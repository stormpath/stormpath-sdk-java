/*
 *
 *  * Copyright 2014 Stormpath, Inc.
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *     http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */
package com.stormpath.sdk.api;

import com.stormpath.sdk.authc.ApiAuthenticationRequestBuilder;
import com.stormpath.sdk.http.HttpRequest;
import com.stormpath.sdk.lang.Classes;
import com.stormpath.sdk.oauth.authc.OauthAuthenticationRequestBuilder;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Constructor;

/**
 * Static utility/helper methods for working with {@link ApiKey} resources.  Most methods are
 * <a href="http://en.wikipedia.org/wiki/Factory_method_pattern">factory method</a>s used for forming
 * ApiKey-specific <a href="http://en.wikipedia.org/wiki/Fluent_interface">fluent DSL</a> queries. for example:
 * <pre>
 * <b>ApiKeys.criteria()</b>
 *     .offsetBy(50)
 *     .limitTo(25)
 *     .withTenant()
 *     .withAccount();
 * </pre>
 * or, if using static imports:
 * <pre>
 * import static com.stormpath.sdk.api.ApiKeys.*;
 *
 * ...
 *
 *  <b>criteria()</b>
 *     .offsetBy(50)
 *     .limitTo(25)
 *     .withTenant()
 *     .withAccount();
 * </pre>
 *
 * @since 1.0.RC
 */
public final class ApiKeys {

    private static final Class<ApiAuthenticationRequestBuilder> API_AUTHENTICATION_REQUEST_BUILDER_CLASS =
            Classes.forName("com.stormpath.sdk.impl.authc.DefaultApiAuthenticationRequestBuilder");


    private static final Class<OauthAuthenticationRequestBuilder> OAUTH_AUTHENTICATION_REQUEST_BUILDER_CLASS =
            Classes.forName("com.stormpath.sdk.impl.oauth.authc.DefaultOauthAuthenticationRequestBuilder");

    /**
     * Returns a new {@link ApiKeyOptions} instance, used to customize how one or more {@link ApiKey}s are retrieved.
     *
     * @return a new {@link ApiKeyOptions} instance, used to customize how one or more {@link ApiKey}s are retrieved.
     */
    public static ApiKeyOptions<ApiKeyOptions> options() {
        return (ApiKeyOptions) Classes.newInstance("com.stormpath.sdk.impl.api.DefaultApiKeyOptions");
    }

    /**
     * Returns a new {@link ApiKeyCriteria} instance to use to formulate an ApiKey query.
     * <p/>
     * For example:
     * <pre>
     * ApiKeyCriteria criteria = ApiKeys.criteria()
     *     .offsetBy(50)
     *     .limitTo(25)
     *     .withTenant()
     *     .withAccount();
     * </pre>
     *
     * @return a new {@link ApiKeyCriteria} instance to use to formulate an ApiKey query.
     */
    public static ApiKeyCriteria criteria() {
        return (ApiKeyCriteria) Classes.newInstance("com.stormpath.sdk.impl.api.DefaultApiKeyCriteria");
    }

    /**
     * Creates a new {@link ApiAuthenticationRequestBuilder ApiAuthenticationRequestBuilder}. The builder can be used to
     * customize an {@code Api} authentication.
     *
     * @return a new {@link ApiAuthenticationRequestBuilder ApiAuthenticationRequestBuilder}.
     * @throws IllegalArgumentException - If {@code httpServletRequest} is null.
     * @see ApiKeys#authenticate(com.stormpath.sdk.http.HttpRequest)
     * @see ApiKeys#authenticateOauth(javax.servlet.http.HttpServletRequest)
     * @see ApiKeys#authenticateOauth(com.stormpath.sdk.http.HttpRequest)
     */
    public static ApiAuthenticationRequestBuilder authenticate(HttpServletRequest httpServletRequest) {
        Constructor<ApiAuthenticationRequestBuilder> ctor = Classes.getConstructor(API_AUTHENTICATION_REQUEST_BUILDER_CLASS, HttpServletRequest.class);
        return Classes.instantiate(ctor, httpServletRequest);
    }

    /**
     * Creates a new {@link ApiAuthenticationRequestBuilder ApiAuthenticationRequestBuilder}. The builder can be used to
     * customize an {@code Api} authentication.
     *
     * @return a new {@link ApiAuthenticationRequestBuilder ApiAuthenticationRequestBuilder}.
     * @throws IllegalArgumentException - If {@code httpRequest} is null.
     * @see ApiKeys#authenticate(javax.servlet.http.HttpServletRequest)
     * @see ApiKeys#authenticate(com.stormpath.sdk.http.HttpRequest)
     * @see ApiKeys#authenticateOauth(javax.servlet.http.HttpServletRequest)
     */
    public static ApiAuthenticationRequestBuilder authenticate(HttpRequest httpRequest) {
        Constructor<ApiAuthenticationRequestBuilder> ctor = Classes.getConstructor(API_AUTHENTICATION_REQUEST_BUILDER_CLASS, HttpRequest.class);
        return Classes.instantiate(ctor, httpRequest);
    }

    /**
     * Creates a new {@link OauthAuthenticationRequestBuilder OauthAuthenticationRequestBuilder}. The builder can be used to
     * customize an {@code Api} authentication.
     * <pre>
     * <b>ApiKeys.authenticateOauth(httpServletRequest)</b>
     *     .forApplication(<b>application</b>)
     *     .execute()
     * </pre>
     *
     * @return a new {@link ApiAuthenticationRequestBuilder OauthAuthenticationRequestBuilder}.
     * @throws IllegalArgumentException - If {@code httpServletRequest} is null.
     * @see ApiKeys#authenticate(javax.servlet.http.HttpServletRequest)
     * @see ApiKeys#authenticate(com.stormpath.sdk.http.HttpRequest)
     * @see ApiKeys#authenticateOauth(com.stormpath.sdk.http.HttpRequest)
     */
    public static OauthAuthenticationRequestBuilder authenticateOauth(HttpServletRequest httpServletRequest) {
        Constructor<OauthAuthenticationRequestBuilder> ctor = Classes.getConstructor(OAUTH_AUTHENTICATION_REQUEST_BUILDER_CLASS, HttpServletRequest.class);
        return Classes.instantiate(ctor, httpServletRequest);
    }

    /**
     * Creates a new {@link OauthAuthenticationRequestBuilder OauthAuthenticationRequestBuilder}. The builder can be used to
     * customize an {@code Api} authentication.
     *
     * @return a new {@link ApiAuthenticationRequestBuilder OauthAuthenticationRequestBuilder}.
     * @throws IllegalArgumentException - If {@code httpRequest} is null.
     * @see ApiKeys#authenticate(com.stormpath.sdk.http.HttpRequest)
     * @see ApiKeys#authenticate(javax.servlet.http.HttpServletRequest)
     * @see ApiKeys#authenticateOauth(javax.servlet.http.HttpServletRequest)
     */
    public static OauthAuthenticationRequestBuilder authenticateOauth(HttpRequest httpRequest) {
        Constructor<OauthAuthenticationRequestBuilder> ctor = Classes.getConstructor(OAUTH_AUTHENTICATION_REQUEST_BUILDER_CLASS, HttpRequest.class);
        return Classes.instantiate(ctor, httpRequest);
    }
}
