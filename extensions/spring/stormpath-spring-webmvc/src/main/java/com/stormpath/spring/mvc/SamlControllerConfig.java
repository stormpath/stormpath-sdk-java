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
 * @since 1.5.0
 */
public class SamlControllerConfig extends AbstractSpringControllerConfig {

    @Value("#{ @environment['stormpath.web.saml.uri'] ?: '/saml' }")
    protected String samlUri;

    public SamlControllerConfig() {
        super("saml");
    }

    // SAML config is only relevant in the context of the LoginController,
    // so it does not have a view of its own
    @Override
    public String getView() {
        return null;
    }

    @Override
    public String getUri() {
        return samlUri;
    }

    // SAML config is only relevant in the context of the LoginController,
    // so it does not have a nextUri of its own
    @Override
    public String getNextUri() {
        return null;
    }

    // SAML config is only relevant in the context of the LoginController.
    // It's enabled (or not) based on the account stores mapped to the application.
    @Override
    public boolean isEnabled() {
        return true;
    }
}
