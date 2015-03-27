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

import com.fasterxml.jackson.databind.util.ISO8601DateFormat;
import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.lang.Duration;
import com.stormpath.sdk.query.Criterion;
import com.stormpath.sdk.query.DateExpressionFactory;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @since 1.0.RC4
 */
public class DefaultDateExpressionFactory implements DateExpressionFactory {

    private final String propertyName;

    private static final String INCLUSIVE_OPENING = "[";
    private static final String INCLUSIVE_CLOSING = "]";
    private static final String EXCLUSIVE_OPENING = "(";
    private static final String EXCLUSIVE_CLOSING = ")";
    private static final String COMMA = ", ";

    public DefaultDateExpressionFactory(String propertyName) {
        this.propertyName = propertyName;
    }

    /**
     * Convenience method that returns a new equals expression reflecting the specified String value.
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
        Assert.isTrue(date instanceof Date, "date needs to be a valid Date object");
        DateFormat df = new ISO8601DateFormat();
        String value = EXCLUSIVE_OPENING + df.format(date).toString() + COMMA + INCLUSIVE_CLOSING;
        return new SimpleExpression(propertyName, value, Operator.EQUALS);
    }

    @Override
    public Criterion gte(Date date) {
        Assert.isTrue(date instanceof Date, "date needs to be a valid Date object");
        DateFormat df = new ISO8601DateFormat();
        String value = INCLUSIVE_OPENING + df.format(date).toString() + COMMA + INCLUSIVE_CLOSING;
        return new SimpleExpression(propertyName, value, Operator.EQUALS);
    }

    @Override
    public Criterion lt(Date date) {
        Assert.isTrue(date instanceof Date, "date needs to be a valid Date object");
        DateFormat df = new ISO8601DateFormat();
        String value = INCLUSIVE_OPENING + COMMA + df.format(date).toString() + EXCLUSIVE_CLOSING;
        return new SimpleExpression(propertyName, value, Operator.EQUALS);
    }

    @Override
    public Criterion lte(Date date) {
        Assert.isTrue(date instanceof Date, "date needs to be a valid Date object");
        DateFormat df = new ISO8601DateFormat();
        String value = INCLUSIVE_OPENING + COMMA + df.format(date).toString() + INCLUSIVE_CLOSING;
        return new SimpleExpression(propertyName, value, Operator.EQUALS);
    }

    @Override
    public Criterion equals(Date date) {
        Assert.isTrue(date instanceof Date, "date needs to be a valid Date object");
        DateFormat df = new ISO8601DateFormat();
        return new SimpleExpression(propertyName, df.format(date).toString(), Operator.EQUALS);
    }

    @Override
    public Criterion in(Date begin, Date end) {
        Assert.isTrue(begin instanceof Date, "begin needs to be a valid Date object");
        Assert.isTrue(end instanceof Date, "end needs to be a valid Date object");
        Assert.isTrue(begin.before(end), "begin date needs to be earlier than end date");
        DateFormat df = new ISO8601DateFormat();
        String value = INCLUSIVE_OPENING + df.format(begin).toString() + COMMA + df.format(end).toString() + EXCLUSIVE_CLOSING;
        return new SimpleExpression(propertyName, value, Operator.EQUALS);
    }

    @Override
    public Criterion in(Date begin, Duration duration) {
        Assert.isTrue(begin instanceof Date, "begin needs to be a valid Date object");
        Assert.isTrue(duration instanceof Duration, "duration needs to be a valid Duration object");
        DateFormat df = new ISO8601DateFormat();
        Date endDate = this.calculateDateFromDuration(begin, duration);
        String value = INCLUSIVE_OPENING + df.format(begin).toString() + COMMA + df.format(endDate).toString() + EXCLUSIVE_CLOSING;
        return new SimpleExpression(propertyName, value, Operator.EQUALS);
    }

    private Date calculateDateFromDuration(Date begin, Duration duration){
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(begin.getTime() + this.DurationToIntMillis(duration));
        return cal.getTime();
    }

    private long DurationToIntMillis(Duration duration){
        switch (duration.getTimeUnit()){
            case DAYS:
                return TimeUnit.DAYS.toMillis(duration.getValue());
            case HOURS:
                return TimeUnit.HOURS.toMillis(duration.getValue());
            case MINUTES:
                return TimeUnit.MINUTES.toMillis(duration.getValue());
            case MILLISECONDS:
                return duration.getValue();
            case MICROSECONDS:
                return TimeUnit.MICROSECONDS.toMillis(duration.getValue());
            case NANOSECONDS:
                return TimeUnit.NANOSECONDS.toMillis(duration.getValue());
            default:
                return 0;
        }
    }
}
