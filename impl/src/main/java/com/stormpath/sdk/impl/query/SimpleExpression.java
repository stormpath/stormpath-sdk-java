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
package com.stormpath.sdk.impl.query;

import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.query.Criterion;

/**
 * @since 0.9
 */
public class SimpleExpression implements Criterion {

    private final String propertyName;
    private final Object value;
    private final Operator op;

    public SimpleExpression(String propertyName, Object value, Operator op) {
        Assert.hasText(propertyName, "propertyName must be a text value.");
        Assert.notNull(value, "value must not be null.");
        Assert.notNull(op, "operator must not be null");
        this.propertyName = propertyName;
        this.value = value;
        this.op = op;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public Object getValue() {
        return value;
    }

    public Operator getOp() {
        return op;
    }

    @Override
    public String toString() {
        return propertyName + getOp().getSymbol() + value;
    }

}
