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
import com.stormpath.sdk.impl.resource.AbstractPropertyRetriever;
import com.stormpath.sdk.impl.resource.BooleanProperty;
import com.stormpath.sdk.impl.resource.ParentAwareObjectProperty;

import java.util.Map;

public class DefaultMeProperty extends ConfigurableProperty implements MeProperty {

    private static final ParentAwareObjectProperty<DefaultExpandOptions, AbstractPropertyRetriever> EXPAND;
    private static final BooleanProperty ENABLED = new BooleanProperty("enabled");

    static {
        EXPAND = new ParentAwareObjectProperty<>("expand", DefaultExpandOptions.class, AbstractPropertyRetriever.class);
    }

    public DefaultMeProperty(String name, Map<String, Object> properties, AbstractPropertyRetriever parent) {
        super(name, properties, parent);
    }

    @Override
    public ExpandOptions getExpand() {
        return getParentAwareObjectProperty(EXPAND);
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
