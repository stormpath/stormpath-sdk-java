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
package com.stormpath.spring.boot.autoconfigure;

import com.stormpath.sdk.lang.Assert;

/**
 * Configuration properties common to Stormpath-specific {@link com.stormpath.sdk.servlet.mvc.Controller} implementations.
 *
 * @see com.stormpath.spring.boot.autoconfigure.ViewControllerProperties ViewControllerProperties
 * @since 1.0.RC4
 */
public abstract class ControllerProperties {

    private boolean enabled = true;
    private String uri;

    public ControllerProperties(String defaultUri) {
        Assert.hasText(defaultUri, "defaultUri cannot be null or empty.");
        this.uri = defaultUri;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        Assert.hasText(uri, "uri cannot be null or empty.");
        this.uri = uri;
    }
}
