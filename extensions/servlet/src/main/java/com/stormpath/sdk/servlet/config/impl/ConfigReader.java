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
package com.stormpath.sdk.servlet.config.impl;

/**
 * @since 1.0.RC3
 */
public interface ConfigReader {

    String getString(String name);

    int getInt(String name);

    /**
     * Returns the value of a long property identified by {@code name} or an {@code IllegalArgumentException} if
     * the value doesn't evaluate to a long.
     *
     * @param name name of the long property to retrieve
     * @return the long value corresponding to the property identified by {@code name}
     */
    long getLong(String name);

    boolean getBoolean(String name);

}
