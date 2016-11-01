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
package com.stormpath.spring.mvc;

import org.springframework.beans.factory.annotation.Value;

/**
 * @since 1.2.0
 */
public class AccessTokenControllerConfig {

    private String controllerKey = "accessToken";

    @Value("#{ @environment['stormpath.web.oauth2.enabled'] ?: true }")
    protected boolean enabled;

    @Value("#{ @environment['stormpath.web.oauth2.password.enabled'] ?: true }")
    protected boolean passwordGrantTypeEnabled;

    @Value("#{ @environment['stormpath.web.oauth2.password.validationStrategy'] ?: 'local'}")
    protected String accessTokenValidationStrategy;

    @Value("#{ @environment['stormpath.web.oauth2.client_credentials.enabled'] ?: true }")
    protected boolean clientCredentialsGrantTypeEnabled;

    @Value("#{ @environment['stormpath.web.oauth2.uri'] ?: '/oauth/token' }")
    protected String accessTokenUri;

    @Value("#{ @environment['stormpath.web.oauth2.origin.authorizer.originUris'] }")
    protected String accessTokenAuthorizedOriginUris;

    public String getControllerKey(){
        return controllerKey;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public boolean isPasswordGrantTypeEnabled() {
        return passwordGrantTypeEnabled;
    }

    public String getAccessTokenValidationStrategy() {
        return accessTokenValidationStrategy;
    }

    public boolean isClientCredentialsGrantTypeEnabled() {
        return clientCredentialsGrantTypeEnabled;
    }

    public String getAccessTokenUri() {
        return accessTokenUri;
    }

    public String getAccessTokenAuthorizedOriginUris() {
        return accessTokenAuthorizedOriginUris;
    }

}
