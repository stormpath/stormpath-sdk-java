/*
 * Copyright 2014 Stormpath, Inc.
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
package com.stormpath.sdk.http;

import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.lang.Classes;

import java.lang.reflect.Constructor;
import java.util.Map;

/**
 * Utility factory class for creating {@link HttpRequest} instances for SDK users that do not already depend on the
 * {@code HttpServletRequest} API.
 *
 * <p>Once you obtain either a {@code HttpServletRequest} or construct a {@link HttpRequest}, either may be
 * authenticated (presumably to assert an identity for calls to your REST API)
 * using {@link com.stormpath.sdk.application.Application#authenticateApiRequest(Object)
 * application.authenticateApiRequest(httpRequest)} or
 * {@link com.stormpath.sdk.application.Application#authenticateOauthRequest(Object)
 * application.authenticateOauthRequest(httpRequest)}.</p>
 *
 * @see com.stormpath.sdk.application.Application#authenticateApiRequest(Object)
 * @see com.stormpath.sdk.application.Application#authenticateOauthRequest(Object)
 * @since 1.0.RC
 */
public final class HttpRequests {

    private static final String PARAMETERS = "parameters";

    private static final String HEADERS = "headers";

    private static final Class<HttpRequestBuilder> HTTP_REQUEST_BUILDER =
        Classes.forName("com.stormpath.sdk.impl.http.DefaultHttpRequestBuilder");

    /**
     * Creates and returns a new {@link HttpRequestBuilder} that builds request instances that will be used for request
     * authentication via {@link com.stormpath.sdk.application.Application#authenticateApiRequest(Object)
     * application.authenticateApiRequest(httpRequest)} or
     * {@link com.stormpath.sdk.application.Application#authenticateOauthRequest(Object)
     * application.authenticateOauthRequest(httpRequest)}.
     *
     * <p>This method is only useful for SDK users that do not depend on the {@code HttpServletRequest} API, as
     * the {@code application.authenticate*} methods accept that type natively.</p>
     *
     * @param method the source request's http method.
     * @return a new HttpRequestBuilder that can be used to construct a new {@link HttpRequest} instance.
     * @throws IllegalArgumentException if the method argument is {@code null}.
     */
    public static HttpRequestBuilder method(HttpMethod method) throws IllegalArgumentException {
        Assert.notNull(method, "method argument is required.");

        Constructor<HttpRequestBuilder> ctor = Classes.getConstructor(HTTP_REQUEST_BUILDER, HttpMethod.class);
        return Classes.instantiate(ctor, method);
    }

    /**
     * Creates and returns a new {@link HttpRequestBuilder} that builds request instances that will be used for request
     * authentication via {@link com.stormpath.sdk.application.Application#authenticateApiRequest(Object)
     * application.authenticateApiRequest(httpRequest)} or
     * {@link com.stormpath.sdk.application.Application#authenticateOauthRequest(Object)
     * application.authenticateOauthRequest(httpRequest)}.
     *
     * @param name the name of the HTTP header that will be present in the resulting HTTP request instance.
     * @param value the value of the header represented as a {@code String} array
     * @return a new HttpRequestBuilder that can be used to construct a new {@link HttpRequest} instance.
     * @throws IllegalArgumentException if the name or value arguments are {@code null}.
     *
     * @since 1.0.RC4.6
     */
    public static HttpRequestBuilder header(String name, String[] value) throws IllegalArgumentException{
       Assert.notNull(name, "name argument is required.");
       Assert.notNull(value, "value argument is required.");

       Constructor<HttpRequestBuilder> ctor = Classes.getConstructor(HTTP_REQUEST_BUILDER, String.class, String[].class, String.class);
       return Classes.instantiate(ctor, name, value, HEADERS);
    }

    /**
     * Creates and returns a new {@link HttpRequestBuilder} that builds request instances that will be used for request
     * authentication via {@link com.stormpath.sdk.application.Application#authenticateApiRequest(Object)
     * application.authenticateApiRequest(httpRequest)} or
     * {@link com.stormpath.sdk.application.Application#authenticateOauthRequest(Object)
     * application.authenticateOauthRequest(httpRequest)}.
     *
     * @param name the name of the HTTP parameter that will be present in the resulting HTTP request instance.
     * @param value the value of the parameter represented as a {@code String} array
     * @return a new HttpRequestBuilder that can be used to construct a new {@link HttpRequest} instance.
     * @throws IllegalArgumentException if the name or value arguments are {@code null}.
     *
     * @since 1.0.RC4.6
     */
    public static HttpRequestBuilder parameter(String name, String[] value) throws IllegalArgumentException{
        Assert.notNull(name, "name argument is required.");
        Assert.notNull(value, "value argument is required.");

        Constructor<HttpRequestBuilder> ctor = Classes.getConstructor(HTTP_REQUEST_BUILDER, String.class, String[].class, String.class);
        return Classes.instantiate(ctor, name, value, PARAMETERS);
    }
}
