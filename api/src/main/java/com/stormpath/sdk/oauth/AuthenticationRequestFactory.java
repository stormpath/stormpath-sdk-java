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
package com.stormpath.sdk.oauth;

/**
 *
 * This interface defines the method to create a {@link Oauth2AuthenticationRequestBuilder}s.
 *
 * @param <T> a subclass of {@link Oauth2AuthenticationRequestBuilder} specifying the kind of Oauth2 authentication request
 *           builder that this factory will create.
 *
 * @since 1.0.RC6
 */
public interface AuthenticationRequestFactory<T extends Oauth2AuthenticationRequestBuilder> {

    /**
     * Returns the request builder (sub-class of {@link Oauth2AuthenticationRequestBuilder}) that this factory will create.
     *
     * @return the request builder (sub-class of {@link Oauth2AuthenticationRequestBuilder}) that this factory will create.
     */
    T builder();

}
