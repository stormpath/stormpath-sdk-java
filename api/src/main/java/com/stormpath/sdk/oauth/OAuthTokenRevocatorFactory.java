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

import com.stormpath.sdk.application.Application;

/**
 * Factory class to create {@link OAuthTokenRevocator}s instances.
 *
 * @since 1.2.0
 */
public interface OAuthTokenRevocatorFactory {

    /**
     * Defines the {@link Application} that will be used to operate with the OAuth Revocator tha this factory will create.
     *
     * @param application the application that will internally be used by the {@link OAuthTokenRevocator}
     * @return the concrete {@link OAuthTokenRevocator} that this factory will create.
     */
    OAuthTokenRevocator forApplication(Application application);

}
