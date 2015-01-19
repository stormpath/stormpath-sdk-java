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
package com.stormpath.sdk.servlet.config;

import com.stormpath.sdk.servlet.config.impl.DefaultConfigResolver;

import javax.servlet.ServletContext;

/**
 * @since 1.0.RC3
 */
public interface ConfigResolver {

    /**
     * A thread-safe instance to use as desired.  The implementation is a
     * {@link com.stormpath.sdk.servlet.config.impl.DefaultConfigResolver DefaultConfigResolver}.
     */
    public static final ConfigResolver INSTANCE = new DefaultConfigResolver();

    Config getConfig(ServletContext servletContext);
}
