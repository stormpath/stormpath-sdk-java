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

import com.stormpath.sdk.application.ExpandOptions;
import com.stormpath.sdk.application.MeProperty;
import com.stormpath.sdk.impl.resource.BooleanProperty;
import com.stormpath.sdk.impl.resource.ObjectProperty;

import java.util.Map;

public class DefaultMeProperty extends ConfigurableProperty implements MeProperty {


    private static ObjectProperty<ExpandOptions> EXPAND = new ObjectProperty<>("uri", ExpandOptions.class);

    private static BooleanProperty ENABLED = new BooleanProperty("enabled");

    public DefaultMeProperty(Map<String, Object> properties) {
        super(properties);
    }

    @Override
    public ExpandOptions getExpandOptions() {
        return getObjectProperty(EXPAND);
    }

    @Override
    public Boolean isEnabled() {
        return getBoolean(ENABLED);
    }

    @Override
    public void setEnabled(Boolean enabled) {
        setProperty(ENABLED, enabled);
    }
}
