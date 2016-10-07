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
package com.stormpath.sdk.impl.application;

import com.stormpath.sdk.application.MeConfiguration;
import com.stormpath.sdk.application.MeExpansionOptions;

public class DefaultMeConfiguration implements MeConfiguration {

    private String uri;
    private Boolean enabled;
    private MeExpansionOptions expansionOptions;

    @Override
    public String getUri() {
        return null;
    }

    @Override
    public MeExpansionOptions getExpansionOptions() {
        return expansionOptions;
    }

    @Override
    public Boolean isEnabled() {
        return null;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public void setExpansionOptions(MeExpansionOptions expansionOptions) {
        this.expansionOptions = expansionOptions;
    }
}
