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
package com.stormpath.sdk.provider;

/**
 * Google-specific {@link ProviderData} Resource.
 *
 * @since 1.0.beta
 */
public interface GoogleProviderData extends ProviderData {

    /**
     * Getter for the Google access token.
     *
     * @return the Google access token.
     */
    String getAccessToken();

    /**
     * Getter for the Google refresh token.
     *
     * @return the Google refresh token.
     */
    String getRefreshToken();

}
