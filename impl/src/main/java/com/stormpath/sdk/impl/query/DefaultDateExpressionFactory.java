/*
* Copyright 2015 Stormpath, Inc.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package com.stormpath.sdk.impl.query;

import com.stormpath.sdk.lang.Duration;
import com.stormpath.sdk.query.Criterion;
import com.stormpath.sdk.query.DateExpressionFactory;

import java.util.Date;

/**
 * @since 1.0.RC4
 */
public class DefaultDateExpressionFactory implements DateExpressionFactory {

    private final String propertyName;

    public DefaultDateExpressionFactory(String propertyName) {
        this.propertyName = propertyName;
    }

    /**
     * Returns a new equals expression reflecting the specified value.
     *
     * @param value the value that should equal the property value.
     * @return a new equals expression reflecting the current property name and the specified value.
     */
    @Override
    public SimpleExpression matches(String value) {
        return new SimpleExpression(propertyName, value, Operator.EQUALS);
    }

    @Override
    public Criterion gt(Date date) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Criterion gte(Date date) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Criterion lt(Date date) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Criterion lte(Date date) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Criterion equals(Date date) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Criterion in(Date begin, Date end) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Criterion in(Date begin, Duration duration) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
