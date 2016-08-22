/*
 * Copyright 2015 Stormpath, Inc.
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
package com.stormpath.sdk.impl.http.httpclient;

import com.stormpath.sdk.client.AuthenticationScheme;
import com.stormpath.sdk.authc.StormpathCredentials;
import com.stormpath.sdk.client.Proxy;
import com.stormpath.sdk.impl.http.HttpHeaders;
import com.stormpath.sdk.impl.http.MediaType;
import com.stormpath.sdk.impl.http.QueryString;
import com.stormpath.sdk.impl.http.Request;
import com.stormpath.sdk.impl.http.RequestExecutor;
import com.stormpath.sdk.impl.http.Response;
import com.stormpath.sdk.impl.http.RestException;
import com.stormpath.sdk.impl.http.authc.DefaultRequestAuthenticatorFactory;
import com.stormpath.sdk.impl.authc.RequestAuthenticator;
import com.stormpath.sdk.impl.authc.RequestAuthenticatorFactory;
import com.stormpath.sdk.impl.http.support.BackoffStrategy;
import com.stormpath.sdk.impl.http.support.DefaultRequest;
import com.stormpath.sdk.impl.http.support.DefaultResponse;
import com.stormpath.sdk.lang.Assert;
import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.NoHttpResponseException;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.entity.GzipDecompressingEntity;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.params.AllClientPNames;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.util.Random;

/**
 * {@code RequestExecutor} implementation that uses the
 * <a href="http://hc.apache.org/httpcomponents-client-ga">Apache HttpClient</a> implementation to
 * execute http requests.
 *
 * @since 0.1
 */
public class HttpClientRequestExecutor implements RequestExecutor {

    private static final Logger log = LoggerFactory.getLogger(HttpClientRequestExecutor.class);

    /**
     * Maximum exponential back-off time before retrying a request
     */
    private static final int MAX_BACKOFF_IN_MILLISECONDS = 20 * 1000;

    private static final int DEFAULT_MAX_RETRIES = 4;

    private static final int DEFAULT_MAX_CONNECTIONS_PER_ROUTE = Integer.MAX_VALUE/2;
    private static final String MAX_CONNECTIONS_PER_ROUTE_PROPERTY_KEY = "com.stormpath.sdk.impl.http.httpclient.HttpClientRequestExecutor.connPoolControl.maxPerRoute";
    private static final int MAX_CONNECTIONS_PER_ROUTE;

    private static final int DEFAULT_MAX_CONNECTIONS_TOTAL = Integer.MAX_VALUE;
    private static final String MAX_CONNECTIONS_TOTAL_PROPERTY_KEY = "com.stormpath.sdk.impl.http.httpclient.HttpClientRequestExecutor.connPoolControl.maxTotal";
    private static final int MAX_CONNECTIONS_TOTAL;

    private int numRetries = DEFAULT_MAX_RETRIES;

    private final StormpathCredentials stormpathCredentials;

    private final RequestAuthenticator requestAuthenticator;

    private DefaultHttpClient httpClient;

    private BackoffStrategy backoffStrategy;

    private HttpClientRequestFactory httpClientRequestFactory;

    private final RequestAuthenticatorFactory requestAuthenticatorFactory;

    //doesn't need to be SecureRandom: only used in backoff strategy, not for crypto:
    private final Random random = new Random();

    static {
        int connectionMaxPerRoute = DEFAULT_MAX_CONNECTIONS_PER_ROUTE;
        String connectionMaxPerRouteString = System.getProperty(MAX_CONNECTIONS_PER_ROUTE_PROPERTY_KEY);
        if (connectionMaxPerRouteString != null) {
            try {
                connectionMaxPerRoute = Integer.parseInt(connectionMaxPerRouteString);
            } catch (NumberFormatException nfe) {
                log.warn(
                    "Bad max connection per route value: {}. Using default: {}.",
                    connectionMaxPerRouteString, DEFAULT_MAX_CONNECTIONS_PER_ROUTE, nfe
                );
            }
        }
        MAX_CONNECTIONS_PER_ROUTE = connectionMaxPerRoute;

        int connectionMaxTotal = DEFAULT_MAX_CONNECTIONS_TOTAL;
        String connectionMaxTotalString = System.getProperty(MAX_CONNECTIONS_TOTAL_PROPERTY_KEY);
        if (connectionMaxTotalString != null) {
            try {
                connectionMaxTotal = Integer.parseInt(connectionMaxTotalString);
            } catch (NumberFormatException nfe) {
                log.warn(
                    "Bad max connection total value: {}. Using default: {}.",
                    connectionMaxTotalString, DEFAULT_MAX_CONNECTIONS_TOTAL, nfe
                );
            }
        }
        MAX_CONNECTIONS_TOTAL = connectionMaxTotal;
    }

    /**
     * Creates a new {@code HttpClientRequestExecutor} using the specified {@code ApiKey} and optional {@code Proxy}
     * configuration.
     * @param stormpathCredentials the Stormpath account API Key that will be used to authenticate the client with Stormpath's API sever
     * @param proxy the HTTP proxy to be used when communicating with the Stormpath API server (can be null)
     * @param authenticationScheme the HTTP authentication scheme to be used when communicating with the Stormpath API server.
     *                             If null, then Sauthc1 will be used.
     */
    public HttpClientRequestExecutor(StormpathCredentials stormpathCredentials, Proxy proxy, AuthenticationScheme authenticationScheme, RequestAuthenticatorFactory requestAuthenticatorFactory, Integer connectionTimeout) {
        Assert.notNull(stormpathCredentials, "stormpathCredentials argument is required.");
        Assert.isTrue(connectionTimeout >= 0, "Timeout cannot be a negative number.");

        this.stormpathCredentials = stormpathCredentials;

        this.requestAuthenticatorFactory = (requestAuthenticatorFactory != null)
                ? requestAuthenticatorFactory
                : new DefaultRequestAuthenticatorFactory();

        this.requestAuthenticator = this.requestAuthenticatorFactory.create(authenticationScheme, stormpathCredentials);

        this.httpClientRequestFactory = new HttpClientRequestFactory();

        PoolingClientConnectionManager connMgr = new PoolingClientConnectionManager();
        if (MAX_CONNECTIONS_TOTAL >= MAX_CONNECTIONS_PER_ROUTE) {
            connMgr.setDefaultMaxPerRoute(MAX_CONNECTIONS_PER_ROUTE);
            connMgr.setMaxTotal(MAX_CONNECTIONS_TOTAL);
        } else {
            connMgr.setDefaultMaxPerRoute(DEFAULT_MAX_CONNECTIONS_PER_ROUTE);
            connMgr.setMaxTotal(DEFAULT_MAX_CONNECTIONS_TOTAL);

            log.warn(
                "{} ({}) is less than {} ({}). " +
                "Reverting to defaults: connectionMaxTotal ({}) and connectionMaxPerRoute ({}).",
                MAX_CONNECTIONS_TOTAL_PROPERTY_KEY, MAX_CONNECTIONS_TOTAL,
                MAX_CONNECTIONS_PER_ROUTE_PROPERTY_KEY, MAX_CONNECTIONS_PER_ROUTE,
                DEFAULT_MAX_CONNECTIONS_TOTAL, DEFAULT_MAX_CONNECTIONS_PER_ROUTE
            );
        }

        // The connectionTimeout value is specified in seconds in Stormpath configuration settings.
        // Therefore, multiply it by 1000 to be milliseconds since DefaultHttpClient expects milliseconds.
        int connectionTimeoutAsMilliseconds = connectionTimeout * 1000;

        this.httpClient = new DefaultHttpClient(connMgr);
        httpClient.getParams().setParameter(AllClientPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
        httpClient.getParams().setParameter(AllClientPNames.SO_TIMEOUT, connectionTimeoutAsMilliseconds);
        httpClient.getParams().setParameter(AllClientPNames.CONNECTION_TIMEOUT, connectionTimeoutAsMilliseconds);
        httpClient.getParams().setParameter(ClientPNames.HANDLE_REDIRECTS, false);
        httpClient.getParams().setParameter("http.protocol.content-charset", "UTF-8");

        if (proxy != null) {
            //We have some proxy setting to use!
            HttpHost httpProxyHost = new HttpHost(proxy.getHost(), proxy.getPort());
            httpClient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, httpProxyHost);

            if (proxy.isAuthenticationRequired()) {
                httpClient.getCredentialsProvider().setCredentials(
                        new AuthScope(proxy.getHost(), proxy.getPort()),
                        new UsernamePasswordCredentials(proxy.getUsername(), proxy.getPassword()));
            }

        }
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
            this.requestAuthenticator.authenticate(request);

            HttpRequestBase httpRequest = this.httpClientRequestFactory.createHttpClientRequest(request, entity);

            if (httpRequest instanceof HttpEntityEnclosingRequest) {
                entity = ((HttpEntityEnclosingRequest) httpRequest).getEntity();
            }


            HttpResponse httpResponse = null;
            try {
                // We don't want to treat a redirect like a retry,
                // so if redirectUri is not null, we won't pause
                // before executing the request below.
                if (retryCount > 0 && redirectUri == null) {
                    pauseExponentially(retryCount, exception);
                    if (entity != null) {
                        InputStream content = entity.getContent();
                        if (content.markSupported()) {
                            content.reset();
                        }
                    }
                }

                // reset redirectUri so that if there is an exception, we will pause on retry
                redirectUri = null;
                exception = null;
                retryCount++;

                httpResponse = httpClient.execute(httpRequest);

                if (isRedirect(httpResponse)) {
                    Header[] locationHeaders = httpResponse.getHeaders("Location");
                    String location = locationHeaders[0].getValue();
                    log.debug("Redirecting to: {}", location);
                    redirectUri = URI.create(location);
                    httpRequest.setURI(redirectUri);
                } else {

                    Response response = toSdkResponse(httpResponse);

                    int httpStatus = response.getHttpStatus();

                    if (httpStatus == 429) {
                        throw new RestException("HTTP 429: Too Many Requests.  Exceeded request rate limit in the allotted amount of time.");
                    }
                    if ((httpStatus == 503 || httpStatus == 504) && retryCount <= this.numRetries) {
                        //allow the loop to continue to execute a retry request
                        continue;
                    }

                    return response;
                }
            } catch (Throwable t) {
                log.warn("Unable to execute HTTP request: ", t.getMessage(), t);

                if (t instanceof RestException) {
                    exception = (RestException)t;
                }

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

    private boolean isRedirect(org.apache.http.HttpResponse response) {
        int status = response.getStatusLine().getStatusCode();
        return (status == HttpStatus.SC_MOVED_PERMANENTLY ||
                status == HttpStatus.SC_MOVED_TEMPORARILY ||
                status == HttpStatus.SC_TEMPORARY_REDIRECT) &&
                response.getHeaders("Location") != null &&
                response.getHeaders("Location").length > 0;
    }

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
            if (previousException != null && isThrottlingException(previousException)) {
                scaleFactor = 500 + random.nextInt(100);
            }
            delay = (long) (Math.pow(2, retries) * scaleFactor);
        }

        delay = Math.min(delay, MAX_BACKOFF_IN_MILLISECONDS);
        log.debug("Retryable condition detected, will retry in {}ms, attempt number: {}", delay, retries);

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
                t instanceof SocketTimeoutException ||
                t instanceof ConnectTimeoutException) {
            log.debug("Retrying on {}: {}", t.getClass().getName(), t.getMessage());
            return true;
        }

        if (t instanceof RestException) {
            RestException re = (RestException) t;

            /*
             * Throttling is reported as a 429 error. To try
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
        String msg = re.getMessage();
        return msg != null && msg.contains("HTTP 429");
    }

    protected byte[] toBytes(HttpEntity entity) throws IOException {
        return EntityUtils.toByteArray(entity);
    }

    protected Response toSdkResponse(HttpResponse httpResponse) throws IOException {

        int httpStatus = httpResponse.getStatusLine().getStatusCode();

        HttpHeaders headers = getHeaders(httpResponse);
        MediaType mediaType = headers.getContentType();

        HttpEntity entity = getHttpEntity(httpResponse);

        InputStream body = entity != null ? entity.getContent() : null;
        long contentLength = entity != null ? entity.getContentLength() : -1;

        //ensure that the content has been fully acquired before closing the http stream
        if (body != null) {
            byte[] bytes = toBytes(entity);

            if(bytes != null) {
                body = new ByteArrayInputStream(bytes);
            }  else {
                body = null;
            }
        }

        Response response = new DefaultResponse(httpStatus, mediaType, body, contentLength);

        response.getHeaders().add(HttpHeaders.STORMPATH_REQUEST_ID, headers.getStormpathRequestId());

        return response;
    }

    private HttpEntity getHttpEntity(HttpResponse response) {

        HttpEntity entity = response.getEntity();
        if (entity != null) {
            Header contentEncodingHeader = entity.getContentEncoding();
            if (contentEncodingHeader != null) {
                for (HeaderElement element : contentEncodingHeader.getElements()) {
                    if (element.getName().equalsIgnoreCase("gzip")) {
                        return new GzipDecompressingEntity(response.getEntity());
                    }
                }
            }
        }
        return entity;
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
