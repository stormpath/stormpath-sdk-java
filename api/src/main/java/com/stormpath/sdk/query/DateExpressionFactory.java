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
 * An {@code DateExpressionFactory} creates a single condition (matches) for resource properties of Date type.
 *
 * @since 1.0.RC4
 */
public interface DateExpressionFactory {

    /**
     * Returns a new equals expression indicating the specified value should match the property value.
     * Multiple values are allowed, for example:
     * .createdAt().matches("[,2014-04-05T12:00:00]"): matches all entities created between the beginning of time and noon of 2014/04/05
     * .createdAt().matches("[2015-01-01, 2015-02-01)"): matches all entities created/modified between 2015/01/01 and 2015/02/01, excluding those created on 2015/02/01
     * .createdAt().matches("[2015-02-01T12:00:00,]"): matches all entities created between noon of 2015-02-01 and now
     * .modifiedAt().matches("2015-01"): is equivalent to "[2015-01-01T00:00:00.000Z,2015-02-01T00:00:00.000Z)" and matches all entities modified during January 2015
     * .modifiedAt().matches("2015-01-03T11:23:14.233"): matches all entities modified only at that exact instant represented by the ISO-8601 Timestamp
     *
     * @param value the value that should equal the property value (ignoring case).
     * @return a new case-insensitive equals expression reflecting the property name and the specified value.
     */
    Criterion matches(String value);

}
