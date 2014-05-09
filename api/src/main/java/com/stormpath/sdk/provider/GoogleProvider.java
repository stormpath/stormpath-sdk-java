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
package com.stormpath.sdk.provider;

/**
 * Google-specific {@link Provider} Resource.
 *
 * @since 1.0.beta
 */
public interface GoogleProvider extends Provider {

    /**
     * Getter for the App ID of the Google application.
     *
     * @return the App ID for of Google application.
     */
    String getClientId();

    /**
     * Setter for the App ID of the Google application.
     *
     * @param clientId the App ID of the Google application.
     * @return this instance for method chaining.
     */
    GoogleProvider setClientId(String clientId);

    /**
     * Getter for the App Secret of the Google application.
     *
     * @return the App Secret of the Google application.
     */
    String getClientSecret();

    /**
     * Setter for the App Secret of the Google application.
     *
     * @param clientSecret the App Secret of the Google application.
     * @return this instance for method chaining.
     */
    GoogleProvider setClientSecret(String clientSecret);

    /**
     * Getter for the Redirect Uri of the Google application.
     *
     * @return the Redirect Uri of the Google application.
     */
    String getRedirectUri();

    /**
     * Setter for the Redirect Uri of the Google application.
     *
     * @param redirectUri the Redirect Uri of the Google application.
     * @return this instance for method chaining.
     */
    GoogleProvider setRedirectUri(String redirectUri);

}
