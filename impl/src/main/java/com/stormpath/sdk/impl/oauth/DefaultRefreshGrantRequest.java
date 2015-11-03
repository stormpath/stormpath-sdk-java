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

import com.stormpath.sdk.oauth.RefreshGrantRequest;
import com.stormpath.sdk.lang.Assert;

/**
 * @since 1.0.RC6
 */
public class DefaultRefreshGrantRequest implements RefreshGrantRequest {

    private final static String grant_type = "refresh_token";
    private final String refresh_token;


    public DefaultRefreshGrantRequest(String refreshToken) {
        Assert.notNull(refreshToken, "refreshToken argument cannot be null.");
        this.refresh_token = refreshToken;
    }
    @Override
    public String getRefreshToken() {
        return refresh_token;
    }

    @Override
    public String getGrantType() {
        return grant_type;
    }
}
