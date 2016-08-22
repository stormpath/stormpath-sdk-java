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

import com.stormpath.sdk.http.HttpMethod;
import com.stormpath.sdk.impl.http.QueryString;
import com.stormpath.sdk.impl.http.Request;
import com.stormpath.sdk.impl.http.RestException;
import com.stormpath.sdk.impl.util.RequestUtils;
import com.stormpath.sdk.lang.Strings;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.params.CoreProtocolPNames;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.List;
import java.util.Map;

/**
 * Responsible for creating Apache HttpClient 4 request objects.
 *
 * @since 0.1
 */
class HttpClientRequestFactory {

    /**
     * Creates an HttpClient method object based on the specified request and
     * populates any parameters, headers, etc. from the original request.
     *
     * @param request        The request to convert to an HttpClient method object.
     * @param previousEntity The optional, previous HTTP entity to reuse in the new request.
     * @return The converted HttpClient method object with any parameters,
     *         headers, etc. from the original request set.
     */
    HttpRequestBase createHttpClientRequest(Request request, HttpEntity previousEntity) {

        HttpMethod method = request.getMethod();
        URI uri = getFullyQualifiedUri(request);
        InputStream body = request.getBody();
        long contentLength = request.getHeaders().getContentLength();

        HttpRequestBase base;

        switch (method) {
            case DELETE:
                base = new HttpDelete(uri);
                break;
            case GET:
                base = new HttpGet(uri);
                break;
            case HEAD:
                base = new HttpHead(uri);
                break;
            case POST:
                base = new HttpPost(uri);
                ((HttpEntityEnclosingRequestBase)base).setEntity(new RepeatableInputStreamEntity(request));
                break;
            case PUT:
                base = new HttpPut(uri);

                // Enable 100-continue support for PUT operations, since this is  where we're potentially uploading
                // large amounts of data and want to find out as early as possible if an operation will fail. We
                // don't want to do this for all operations since it will cause extra latency in the network
                // interaction.
                base.getParams().setParameter(CoreProtocolPNames.USE_EXPECT_CONTINUE, true);

                if (previousEntity != null) {
                    ((HttpEntityEnclosingRequestBase)base).setEntity(previousEntity);
                } else if (body != null) {
                    HttpEntity entity = new RepeatableInputStreamEntity(request);
                    if (contentLength < 0) {
                        entity = newBufferedHttpEntity(entity);
                    }
                    ((HttpEntityEnclosingRequestBase)base).setEntity(entity);
                }
                break;
            default:
                throw new IllegalArgumentException("Unrecognized HttpMethod: " + method);
        }

        applyHeaders(base, request);

        return base;
    }

    /**
     * Configures the headers in the specified Apache HTTP request.
     */
    private void applyHeaders(HttpRequestBase httpRequest, Request request) {
        /*
         * Apache HttpClient omits the port number in the Host header (even if
         * we explicitly specify it) if it's the default port for the protocol
         * in use. To ensure that we use the same Host header in the request and
         * in the calculated string to sign (even if Apache HttpClient changed
         * and started honoring our explicit host with endpoint), we follow this
         * same behavior here and in the RequestAuthenticator.
         */
        URI endpoint = request.getResourceUrl();
        String hostHeader = endpoint.getHost();
        if (!RequestUtils.isDefaultPort(endpoint)) {
            hostHeader += ":" + endpoint.getPort();
        }
        httpRequest.addHeader("Host", hostHeader);
        httpRequest.addHeader("Accept-Encoding", "gzip");

        // Copy over any other headers already in our request
        for (Map.Entry<String, List<String>> entry : request.getHeaders().entrySet()) {
            String key = entry.getKey();
            List<String> value = entry.getValue();
            /*
             * HttpClient4 fills in the Content-Length header and complains if
             * it's already present, so we skip it here. We also skip the Host
             * header to avoid sending it twice, which will interfere with some
             * signing schemes.
             */
            if (!"Content-Length".equalsIgnoreCase(key) && !"Host".equalsIgnoreCase(key)) {
                String delimited = Strings.collectionToCommaDelimitedString(value);
                httpRequest.addHeader(key, delimited);
            }
        }
    }

    private URI getFullyQualifiedUri(Request request) {
        StringBuilder sb = new StringBuilder();
        sb.append(request.getResourceUrl().normalize());
        QueryString query = request.getQueryString();
        if (query != null && !query.isEmpty()) {
            sb.append("?").append(query.toString());
        }

        return URI.create(sb.toString());
    }

    /**
     * Utility function for creating a new BufferedEntity and wrapping any errors
     * as a RestException.
     *
     * @param entity The HTTP entity to wrap with a buffered HTTP entity.
     * @return A new BufferedHttpEntity wrapping the specified entity.
     */
    private HttpEntity newBufferedHttpEntity(HttpEntity entity) {
        try {
            return new BufferedHttpEntity(entity);
        } catch (IOException e) {
            throw new RestException("Unable to create HTTP entity: " + e.getMessage(), e);
        }
    }
}
