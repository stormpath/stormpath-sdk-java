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
package com.stormpath.sdk.query;

/**
 * @since 0.9
 */
public enum MatchLocation {

    /**
     * Match the search text at the beginning of the field or fields being searched.
     */
    BEGIN {
        @Override
        public String toMatchString(String pattern, String delimiter) {
            return pattern + delimiter;
        }
    },

    /**
     * Match the search text at the end of the field or fields being searched.
     */
    END {
        @Override
        public String toMatchString(String pattern, String delimiter) {
            return delimiter + pattern;
        }
    },

    /**
     * Match the search text anywhere in the field or fields being searched.
     */
    ANYWHERE {
        @Override
        public String toMatchString(String pattern, String delimiter) {
            return delimiter + pattern + delimiter;
        }
    };

    public String toMatchString(String pattern) {
        return toMatchString(pattern, "*");
    }

    public abstract String toMatchString(String pattern, String delimiter);

}
