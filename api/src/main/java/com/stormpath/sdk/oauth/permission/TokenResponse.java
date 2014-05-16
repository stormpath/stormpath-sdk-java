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
package com.stormpath.sdk.oauth.permission;

/**
 * TokenResponse
 *
 * @since 1.0.RC
 */
public interface TokenResponse {

    /**
     * Returns a base64 (url safe) string that
     *
     * @return
     */
    String getAccessToken();


    /**
     * Returns the space separated collection of scope.
     *
     * @return the space separated collection of scope.
     */
    String getScope();

    /**
     * Returns the type of the accessToken result. Currently only "Bearer" is returned.
     *
     * @return The type of the accessToken result. Currently only "Bearer" is returned.
     */
    String getTokenType();

    /**
     * Returns an string containing the time to live.
     *
     * @return an string containing the time to live.
     */
    String getExpiresIn();

    /**
     *
     * @return
     */
    String getRefreshToken();

    /**
     * Returns all the non-values of this token response as json (body message).
     *
     * @return all the non-values of this token response as json (body message).
     */
    String toJson();
}
