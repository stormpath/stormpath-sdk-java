/*
 * Copyright 2013 Stormpath, Inc.
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
package com.stormpath.sdk.impl.resource;

import com.stormpath.sdk.lang.Assert;

/**
 * @since 0.8
 */
public abstract class Property<T> {

    protected final String name;
    protected final Class<T> type;
    protected final boolean required;

    protected Property(String name, Class<T> type, boolean required) {
        Assert.notNull(name, "name is required.");
        Assert.notNull(type, "type is required.");
        this.name = name;
        this.type = type;
        this.required = required;
    }

    public String getName() {
        return this.name;
    }

    public Class<T> getType() {
        return type;
    }

    public boolean isRequired() {
        return this.required;
    }
}
