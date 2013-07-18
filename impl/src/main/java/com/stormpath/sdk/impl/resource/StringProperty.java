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

import com.stormpath.sdk.impl.query.LikeExpression;
import com.stormpath.sdk.impl.query.MatchLocation;
import com.stormpath.sdk.impl.query.Operator;
import com.stormpath.sdk.impl.query.SimpleExpression;

/**
 * @since 0.8
 */
public class StringProperty extends Property<String> {

    public StringProperty(String name) {
        this(name, false);
    }

    public StringProperty(String name, boolean required) {
        super(name, String.class, required);
    }

    /**
     * Returns a new case-insensitive equals expression reflecting the property and the specified value.
     * The 'i' prefix to the method name indicates case-insensitivity.
     *
     * @param value the value that should equal the property value (ignoring case).
     * @return a new case-insensitive equals expression reflecting the current property name and the specified value.
     */
    public SimpleExpression eqIgnoreCase(String value) {
        return new SimpleExpression(this.name, value, Operator.EQUALS);
    }

    /**
     * Returns a new case-insensitive like expression reflecting that the specified value
     * should be at the beginning of the corresponding property value.  The 'i' prefix to the method name indicates
     * case-insensitivity.
     *
     * @param value the value that should be at the beginning of the property value.
     * @return a new case-insensitive like expression reflecting the current property name and that the specified value
     *         should be at the beginning of the corresponding property value.
     */
    public LikeExpression startsWithIgnoreCase(String value) {
        return new LikeExpression(this.name, value, MatchLocation.BEGIN);
    }

    /**
     * a new case-insensitive like expression reflecting that the specified value
     * should be at the end of the corresponding property value.  The 'i' prefix to the method name indicates
     * case-insensitivity.
     *
     * @param value the value that should be at the end of the property value.
     * @return a new case-insensitive like expression reflecting the current property name and that the specified value
     *         should be at the end of the corresponding property value.
     */
    public LikeExpression endsWithIgnoreCase(String value) {
        return new LikeExpression(this.name, value, MatchLocation.END);
    }

    /**
     * Returns a new case-insensitive like expression reflecting the property and the specified value.
     * The 'i' prefix to the method name indicates case-insensitivity.
     *
     * @param value the value that should be contained anywhere in the property value.
     * @return a new case-insensitive like expression reflecting the current property name and the specified value.
     */
    public LikeExpression containsIgnoreCase(String value) {
        return new LikeExpression(this.name, value);
    }
}
