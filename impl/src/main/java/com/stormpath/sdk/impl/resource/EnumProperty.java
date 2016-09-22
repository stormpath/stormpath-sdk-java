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

/**
 * @since 0.8
 */
public class EnumProperty<T extends Enum> extends NonStringProperty<T> {

    public EnumProperty(Class<T> clazz) {
        super("status", clazz);
    }

    /**
     * @since 1.0.RC4
     */
    public EnumProperty(String propertyName, Class<T> clazz) {
        super(propertyName, clazz);
    }
}
