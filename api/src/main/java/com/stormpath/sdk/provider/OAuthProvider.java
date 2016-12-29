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
package com.stormpath.sdk.provider;

import com.stormpath.sdk.provider.social.UserInfoMappingRules;

import java.util.List;

/**
 * @since 1.0.0
 */
public interface OAuthProvider extends Provider {

    /**
     * Returns the client ID used to authenticate requests to the 3rd party oauth provider.
     *
     * @return the client ID used to authenticate requests to the 3rd party oauth provider.
     */
    String getClientId();

    /**
     * Returns the client secret used to authenticate requests to the 3rd party oauth provider.
     *
     * @return the client secret used to authenticate requests to the 3rd party oauth provider.
     */
    String getClientSecret();

    /**
     * Returns the list of scopes configured for the oauth provider
     * @return the list of scopes configured for the oauth provider
     * @since 1.2.0
     */
    List<String> getScope();

    /**
     * Returns the userInfoMappingRules configured for the oauth provider
     * @return the userInfoMappingRules configured for the oauth provider
     * @since 1.3.0
     */
    UserInfoMappingRules getUserInfoMappingRules();
}
