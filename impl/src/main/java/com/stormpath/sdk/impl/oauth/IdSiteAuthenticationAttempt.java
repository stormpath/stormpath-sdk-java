/*
* Copyright 2015 Stormpath, Inc.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package com.stormpath.sdk.impl.oauth;

import com.stormpath.sdk.resource.Resource;

/**
 * @since 1.0.RC8.1
 */
public interface IdSiteAuthenticationAttempt extends Resource {

    /**
     * Sets the Id Site JWT that will be used for the token exchange request.
     * @param token The String representation of an ID Site provided JWT.
     */
    void setToken(String token);

    /**
     * Method used to set the Authentication Grant Type that will be used for the token exchange request.
     * Currently only "stormpath_token" grant type is supported for this operation.
     * @param grantType the Authentication Grant Type that will be used for the token exchange request.
     */
    void setGrantType(String grantType);
}