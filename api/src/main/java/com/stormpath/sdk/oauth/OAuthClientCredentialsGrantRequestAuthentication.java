/*
 * Copyright 2016 Stormpath, Inc.
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
package com.stormpath.sdk.oauth;

/**
 * This class represents a request for Stormpath to authenticate an Account and exchange its api key and api secret for a valid OAuth 2.0 access token.
 * Using client_credentials grant type
 *
 * @since 1.1.0
 */
public interface OAuthClientCredentialsGrantRequestAuthentication extends OAuthGrantRequestAuthentication {

    /**
     /**
     * Returns the ApiKey's id string that will be used for the client_credentials grant type authentication attempt.
     *
     * @return the ApiKey's id string that will be used for the client_credentials grant type authentication attempt.
     */
    String getApiKeyId();

    /**
     * Returns the ApiKey's secret string that will be used for the client_credentials grant type authentication attempt.
     *
     * @return the ApiKey's secret string that will be used for the client_credentials grant type authentication attempt.
     */
    String getApiKeySecret();
}
