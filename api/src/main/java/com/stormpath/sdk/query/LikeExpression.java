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

import com.stormpath.sdk.lang.Assert;

/**
 * @since 0.9
 */
public class LikeExpression extends SimpleExpression {

    private final MatchLocation matchLocation;

    public LikeExpression(String propertyName, Object value) {
        this(propertyName, value, MatchLocation.ANYWHERE);
    }

    public LikeExpression(String propertyName, Object value, MatchLocation matchLocation) {
        super(propertyName, value, Operator.ILIKE);
        Assert.notNull(matchLocation, "matchLocation must not be null.");
        this.matchLocation = matchLocation;
    }

    public MatchLocation getMatchLocation() {
        return matchLocation;
    }

    @Override
    public String toString() {
        return getPropertyName() + " " + getOp().getSymbol() + " " + getMatchLocation().toMatchString(String.valueOf(getValue()));
    }

}
