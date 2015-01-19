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

import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.lang.Classes;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @param <T> the type of object discovered/resolved.
 * @since 1.0.RC3
 */
public class ImplementationClassResolver<T> {

    private final Config CONFIG;
    private final String PROPERTY_NAME_PREFIX;
    private final Class<T> EXPECTED_TYPE;

    public ImplementationClassResolver(Config config, String propertyNamePrefix, Class<T> expectedType) {
        Assert.notNull(config, "config cannot be null.");
        Assert.notNull(expectedType, "expectedType cannot be null.");
        Assert.hasText(propertyNamePrefix, "propertyNamePrefix cannot be null or empty.");
        this.CONFIG = config;
        this.PROPERTY_NAME_PREFIX = propertyNamePrefix;
        this.EXPECTED_TYPE = expectedType;
    }

    public Map<String,Class<T>> findImplementationClasses() {

        Map<String,Class<T>> classes = new LinkedHashMap<String, Class<T>>();

        for (String key : CONFIG.keySet()) {

            if (key.startsWith(PROPERTY_NAME_PREFIX)) {

                String instanceName = key.substring(PROPERTY_NAME_PREFIX.length());

                //if there are any periods in the remainder, then the property is not a class name - it is an
                // instance-specific config property, so just ignore it:
                int i = instanceName.indexOf('.');
                if (i >= 0) {
                    continue;
                }

                String className = CONFIG.get(key);

                try {
                    Class<T> clazz = Classes.forName(className);
                    Assert.isTrue(EXPECTED_TYPE.isAssignableFrom(clazz) ||
                                  Factory.class.isAssignableFrom(clazz));
                    classes.put(instanceName, clazz);
                } catch (Exception e) {
                    String msg = key + " value [" + className + "] is not a valid " + EXPECTED_TYPE.getName() + " class.";
                    throw new IllegalArgumentException(msg, e);
                }
            }
        }

        return classes;
    }
}
