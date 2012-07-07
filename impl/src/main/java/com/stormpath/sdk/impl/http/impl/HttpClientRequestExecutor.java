/*
 * Copyright 2012 Stormpath, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.stormpath.sdk.impl.http.impl;

import com.stormpath.sdk.client.ApiKey;
import com.stormpath.sdk.impl.http.HttpHeaders;
import com.stormpath.sdk.impl.http.*;
import com.stormpath.sdk.impl.http.authc.Sauthc1Signer;
import com.stormpath.sdk.impl.http.authc.Signer;
import com.stormpath.sdk.impl.util.Assert;
import com.stormpath.sdk.impl.util.StringInputStream;
import org.apache.http.*;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.params.AllClientPNames;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.util.Random;

/**
 * @since 0.1
 */
public class HttpClientRequestExecutor implements RequestExecutor {

    private static final Logger log = LoggerFactory.getLogger(HttpClientRequestExecutor.class);

    /**
     * Default HTTP connection timeout.
     */
    private static final int CONNECTION_TIMEOUT = 10000; //10,000 millis = 10 seconds

    /**
     * Maximum exponential back-off time before retrying a request
     */
    private static final int MAX_BACKOFF_IN_MILLISECONDS = 20 * 1000;

    private static final int DEFAULT_MAX_RETRIES = 4;

    private int numRetries = DEFAULT_MAX_RETRIES;

    private final ApiKey apiKey;

    private final Signer signer;

    private DefaultHttpClient httpClient;

    private BackoffStrategy backoffStrategy;

    private HttpClientRequestFactory httpClientRequestFactory;

    //doesn't need to be SecureRandom: only used in backoff strategy, not for crypto:
    private final Random random = new Random();

    public HttpClientRequestExecutor(ApiKey apiKey) {

        this.apiKey = apiKey;

        this.signer = new Sauthc1Signer();

        this.httpClientRequestFactory = new HttpClientRequestFactory();

        PoolingClientConnectionManager connMgr = new PoolingClientConnectionManager();
        connMgr.setDefaultMaxPerRoute(10);

        this.httpClient = new DefaultHttpClient(connMgr);
        httpClient.getParams().setParameter(AllClientPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
        httpClient.getParams().setParameter(AllClientPNames.SO_TIMEOUT, CONNECTION_TIMEOUT);
        httpClient.getParams().setParameter(AllClientPNames.CONNECTION_TIMEOUT, CONNECTION_TIMEOUT);
        httpClient.getParams().setParameter(ClientPNames.HANDLE_REDIRECTS, false);
        httpClient.getParams().setParameter("http.protocol.content-charset", "UTF-8");
    }

    public int getNumRetries() {
        return numRetries;
    }

    public void setNumRetries(int numRetries) {
        this.numRetries = numRetries;
    }

    /**
     * @since 0.3
     */
    public BackoffStrategy getBackoffStrategy() {
        return this.backoffStrategy;
    }

    public void setBackoffStrategy(BackoffStrategy backoffStrategy) {
        this.backoffStrategy = backoffStrategy;
    }

    public void setHttpClient(DefaultHttpClient httpClient) {
        this.httpClient = httpClient;
    }

    @Override
    public Response executeRequest(Request request) throws RestException {

        Assert.notNull(request, "Request argument cannot be null.");

        /*if (requestLog.isDebugEnabled()) {
            requestLog.debug("Sending Request: " + request.toString());
        }*/

        int retryCount = 0;
        URI redirectUri = null;
        HttpEntity entity = null;
        RestException exception = null;

        // Make a copy of the original request params and headers so that we can
        // permute them in the loop and start over with the original every time.
        QueryString originalQuery = new QueryString();
        originalQuery.putAll(request.getQueryString());

        HttpHeaders originalHeaders = new HttpHeaders();
        originalHeaders.putAll(request.getHeaders());

        while (true) {

            if (redirectUri != null) {
                request = new DefaultRequest(
                        request.getMethod(),
                        redirectUri.toString(),
                        null,
                        null,
                        request.getBody(),
                        request.getHeaders().getContentLength()
                );
            }

            if (retryCount > 0) {
                request.setQueryString(originalQuery);
                request.setHeaders(originalHeaders);
            }

            // Sign the request
            if (this.signer != null && this.apiKey != null) {
                this.signer.sign(request, this.apiKey);
            }

            HttpRequestBase httpRequest = this.httpClientRequestFactory.createHttpClientRequest(request, entity);

            if (httpRequest instanceof HttpEntityEnclosingRequest) {
                entity = ((HttpEntityEnclosingRequest) httpRequest).getEntity();
            }


            HttpResponse httpResponse = null;
            try {
                if (retryCount > 0) {
                    pauseExponentially(retryCount, exception);
                    if (entity != null) {
                        InputStream content = entity.getContent();
                        if (content.markSupported()) {
                            content.reset();
                        }
                    }
                }

                exception = null;
                retryCount++;

                long start = System.currentTimeMillis();
                httpResponse = httpClient.execute(httpRequest);
                long end = System.currentTimeMillis();
                //executionContext.getTimingInfo().addSubMeasurement(HTTP_REQUEST_TIME, new TimingInfo(start, end));

                if (isRedirect(httpResponse)) {
                    Header[] locationHeaders = httpResponse.getHeaders("Location");
                    String location = locationHeaders[0].getValue();
                    log.debug("Redirecting to: " + location);
                    redirectUri = URI.create(location);
                    httpRequest.setURI(redirectUri);
                } else {
                    Response response = toSdkResponse(httpResponse);

                    if (!response.isServerError() || retryCount > this.numRetries) {
                        return response;
                    }
                    //otherwise allow the loop to continue to execute a retry request
                }
            } catch (Throwable t) {
                log.warn("Unable to execute HTTP request: " + t.getMessage());

                if (!shouldRetry(httpRequest, t, retryCount)) {
                    throw new RestException("Unable to execute HTTP request: " + t.getMessage(), t);
                }
            } finally {
                try {
                    httpResponse.getEntity().getContent().close();
                } catch (Throwable ignored) {
                }
            }
        }
    }

    private String toString(InputStream is) throws IOException {
        if (is == null) {
            return null;
        }
        try {
            return new java.util.Scanner(is, "UTF-8").useDelimiter("\\A").next();
        } catch (java.util.NoSuchElementException e) {
            return null;
        }
    }

    private boolean isRedirect(org.apache.http.HttpResponse response) {
        int status = response.getStatusLine().getStatusCode();
        return (status == HttpStatus.SC_MOVED_PERMANENTLY ||
                status == HttpStatus.SC_MOVED_TEMPORARILY ||
                status == HttpStatus.SC_TEMPORARY_REDIRECT) &&
                response.getHeaders("Location") != null &&
                response.getHeaders("Location").length > 0;
    }

    /*private boolean isRequestSuccessful(org.apache.http.HttpResponse response) {
        int status = response.getStatusLine().getStatusCode();
        return status >= 200 && status < 300;
    }*/

    /**
     * Exponential sleep on failed request to avoid flooding a service with
     * retries.
     *
     * @param retries           Current retry count.
     * @param previousException Exception information for the previous attempt, if any.
     */
    private void pauseExponentially(int retries, RestException previousException) {
        long delay;
        if (backoffStrategy != null) {
            delay = this.backoffStrategy.getDelayMillis(retries);
        } else {
            long scaleFactor = 300;
            if (isThrottlingException(previousException)) {
                scaleFactor = 500 + random.nextInt(100);
            }
            delay = (long) (Math.pow(2, retries) * scaleFactor);
        }

        delay = Math.min(delay, MAX_BACKOFF_IN_MILLISECONDS);
        if (log.isDebugEnabled()) {
            log.debug("Retryable condition detected, will retry in " + delay + "ms, attempt number: " + retries);
        }

        try {
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RestException(e.getMessage(), e);
        }
    }

    /**
     * Returns true if a failed request should be retried.
     *
     * @param method  The current HTTP method being executed.
     * @param t       The throwable from the failed request.
     * @param retries The number of times the current request has been attempted.
     * @return True if the failed request should be retried.
     */
    private boolean shouldRetry(HttpRequestBase method, Throwable t, int retries) {
        if (retries > this.numRetries) {
            return false;
        }

        if (method instanceof HttpEntityEnclosingRequest) {
            HttpEntity entity = ((HttpEntityEnclosingRequest) method).getEntity();
            if (entity != null && !entity.isRepeatable()) {
                return false;
            }
        }

        if (t instanceof NoHttpResponseException ||
                t instanceof SocketException ||
                t instanceof SocketTimeoutException) {
            if (log.isDebugEnabled()) {
                log.debug("Retrying on " + t.getClass().getName()
                        + ": " + t.getMessage());
            }
            return true;
        }

        if (t instanceof RestException) {
            RestException re = (RestException) t;

            /*
             * Throttling is reported as a 400 error. To try
             * and smooth out an occasional throttling error, we'll pause and
             * retry, hoping that the pause is long enough for the request to
             * get through the next time.
             */
            if (isThrottlingException(re)) return true;
        }

        return false;
    }

    /**
     * Returns {@code true} if the exception resulted from a throttling error, {@code false} otherwise.
     *
     * @param re The exception to test.
     * @return {@code true} if the exception resulted from a throttling error, {@code false} otherwise.
     */
    private boolean isThrottlingException(RestException re) {
        return false;

        //TODO: look at response codes and react accordingly
        /*
        if (re == null) return false;
        return "Throttling".equals(re.getErrorCode())
                || "ThrottlingException".equals(re.getErrorCode())
                || "ProvisionedThroughputExceededException".equals(re.getErrorCode()); */
    }

    protected Response toSdkResponse(HttpResponse httpResponse) throws IOException {

        int httpStatus = httpResponse.getStatusLine().getStatusCode();

        HttpHeaders headers = getHeaders(httpResponse);
        MediaType mediaType = headers.getContentType();

        HttpEntity entity = httpResponse.getEntity();

        InputStream body = entity != null ? entity.getContent() : null;
        long contentLength = entity != null ? entity.getContentLength() : -1;

        //ensure that the content has been fully acquired before closing the http stream
        if (body != null) {
            String stringBody = toString(body);
            body = new StringInputStream(stringBody);
        }

        return new DefaultResponse(httpStatus, mediaType, body, contentLength);
    }

    private HttpHeaders getHeaders(HttpResponse response) {

        HttpHeaders headers = new HttpHeaders();

        Header[] httpHeaders = response.getAllHeaders();

        if (httpHeaders != null) {
            for (Header httpHeader : httpHeaders) {
                headers.add(httpHeader.getName(), httpHeader.getValue());
            }
        }

        return headers;
    }
}
