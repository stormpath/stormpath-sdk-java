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
 * Factory class to create {@link OAuthRevocationRequestBuilder}s instances.
 *
 * @since 1.2.0
 */
public interface OAuthTokenRevocationRequestFactory {

    /**
     * Returns a newly created {@link OAuthRevocationRequestBuilder} instance to build  to build {@link OAuthRevocationRequest}s.
     *
     * @return a newly created {@link OAuthRevocationRequestBuilder} instance to build {@link OAuthRevocationRequest}s.
     */
    OAuthRevocationRequestBuilder builder();

}
