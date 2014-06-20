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
package com.stormpath.sdk.idsite;

/**
 * Builder for creating <a href="http://openid.net/specs/draft-jones-json-web-token-07.html#anchor3">JSON Web Token</a>
 * encoded SSO URL.
 *
 * @since 1.0.RC2
 */
public interface IdSiteUrlBuilder {

    /**
     * Setter for the final destination where to get the browser redirected.
     * <p/>
     * This is a mandatory value.
     *
     * @param callbackUri the final destination where to get the browser redirected.
     * @return this instance for method chaining.
     */
    IdSiteUrlBuilder setCallbackUri(String callbackUri);

    /**
     * Setter for the client-specified state.
     *
     * @param state any client-specified state.
     * @return this instance for method chaining.
     */
    IdSiteUrlBuilder setState(String state);

    /**
     * The initial user-interface path that should be displayed upon UI launch.
     * If unspecified, this defaults to <code>/</code>
     *
     * @param path initial user-interface path that should be displayed upon UI launch.
     * @return this instance for method chaining.
     */
    IdSiteUrlBuilder setPath(String path);

    /**
     * Constructs the JWT-encoded SSO URL based on the current builder state.
     *
     * @return the JWT-encoded SSO URL based on the current builder state.
     */
    String build();
}
