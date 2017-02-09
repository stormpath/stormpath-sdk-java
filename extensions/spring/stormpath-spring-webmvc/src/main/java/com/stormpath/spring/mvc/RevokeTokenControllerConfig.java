/*
 * Copyright 2017 Stormpath, Inc.
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
 * https://github.com/stormpath/stormpath-sdk-java/issues/1247
 *
 * @since 1.5.0
 */
public class RevokeTokenControllerConfig {

    private String controllerKey = "revokeToken";

    @Value("#{ @environment['stormpath.web.oauth2.enabled'] ?: true }")
    protected boolean enabled;

    @Value("#{ @environment['stormpath.web.oauth2.revoke.uri'] ?: '/oauth/revoke' }")
    protected String revokeTokenUri;

    public String getControllerKey(){
        return controllerKey;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public String getRevokeTokenUri() {
        return revokeTokenUri;
    }

}
