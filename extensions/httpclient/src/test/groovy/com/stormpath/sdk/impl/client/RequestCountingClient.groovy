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
package com.stormpath.sdk.impl.client

import com.stormpath.sdk.cache.CacheManager
import com.stormpath.sdk.client.AuthenticationScheme
import com.stormpath.sdk.client.Proxy
import com.stormpath.sdk.impl.authc.credentials.ClientCredentials
import com.stormpath.sdk.ds.DataStore
import com.stormpath.sdk.impl.authc.credentials.ApiKeyCredentials
import com.stormpath.sdk.impl.http.Request
import com.stormpath.sdk.impl.http.RequestExecutor
import com.stormpath.sdk.impl.http.Response
import com.stormpath.sdk.impl.http.RestException

import java.util.concurrent.atomic.AtomicInteger

/**
 * A DefaultClient subclass that will count every request delegated to the RequestExecutor, useful for integration
 * tests that need to assert traffic profiles to the server.
 *
 * This implementation DOES NOT count HTTP redirects since the default RequestExecutor will execute them transparently.
 *
 * @since 1.0.RC4.3
 */
public class RequestCountingClient extends DefaultClient {

    private AtomicInteger count = new AtomicInteger();

    public RequestCountingClient(ApiKeyCredentials apiKeyCredentials, String baseUrl, Proxy proxy, CacheManager cacheManager, AuthenticationScheme authenticationScheme, int connectionTimeout) {
        super(apiKeyCredentials, baseUrl, proxy, cacheManager, authenticationScheme, null, connectionTimeout)
    }

    @Override
    protected DataStore createDataStore(final RequestExecutor requestExecutor, String baseUrl, ClientCredentials clientCredentials, CacheManager cacheManager) {

        RequestExecutor countingExecutor = new RequestExecutor() {
            @Override
            Response executeRequest(Request request) throws RestException {
                count.incrementAndGet();
                return requestExecutor.executeRequest(request);
            }
        };

        return super.createDataStore(countingExecutor, baseUrl, clientCredentials, cacheManager)
    }

    /**
     * Returns the number of NON-REDIRECT requests sent to the server since the client was instantiated or since the
     * last time {@link #resetRequestCount()} was invoked.
     *
     * @return the number of NON-REDIRECT requests sent to the server since the client was instantiated or since the
     * last time {@link #resetRequestCount()} was invoked.
     */
    public int getRequestCount() {
        return count.get();
    }

    /**
     * Sets the request count to zero.
     */
    public void resetRequestCount() {
        count.set(0);
    }
}
