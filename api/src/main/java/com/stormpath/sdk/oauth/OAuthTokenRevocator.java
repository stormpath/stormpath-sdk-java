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
 * Interface to expose the OAuth 2.0 revoke operattion.
 *
 * @since 1.2.0
 */
public interface OAuthTokenRevocator {

    /**
     * Executes the OAuth 2.0 token revocation operation of the {@link OAuthRevocationRequest} passed as parameter.
     *
     * @param oAuthRevocationRequest to executed by {@code this} instance.
     */
    void revoke(OAuthRevocationRequest oAuthRevocationRequest);

}
