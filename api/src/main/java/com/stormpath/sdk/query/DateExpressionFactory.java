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

import com.stormpath.sdk.lang.Duration;

import java.util.Date;

/**
 * An {@code DateExpressionFactory} creates a single condition (matches) for resource properties of Date type.
 *
 * @since 1.0.RC4.6
 */
public interface DateExpressionFactory {

    /**
     * Convenience method that returns a new equals expression as a String expression, indicating the specified value should match the property value.
     * Multiple values are allowed, for example:
     * <pre>
     * account.createdAt().matches("[,2014-04-05T12:00:00]"): matches all Accounts created between the beginning of time and noon of 2014/04/05
     * account.createdAt().matches("[2015-01-01, 2015-02-01)"): matches all Accounts created/modified between 2015/01/01 and 2015/02/01, excluding those created on 2015/02/01
     * account.createdAt().matches("[2015-02-01T12:00:00,]"): matches all Accounts created between noon of 2015-02-01 and now
     * account.modifiedAt().matches("2015-01"): is equivalent to "[2015-01-01T00:00:00.000Z,2015-02-01T00:00:00.000Z)" and matches all Accounts modified during January 2015
     * account.modifiedAt().matches("2015-01-03T11:23:14.233"): matches all Accounts modified only at that exact instant represented by the ISO-8601 Timestamp
     * </pre>
     *
     * @param value the value that should equal the property value (ignoring case).
     * @return a new case-insensitive equals expression reflecting the property name and the specified value.
     */
    Criterion matches(String value);

    /**
     * Returns a new expression indicating the property value must be greater than the specified date.
     * For example:
     * <pre>
     * application.createdAt().gt(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse("2014-01-01T12:00:00")): matches only those Applications created after 2014/01/01 at noon
     * </pre>
     *
     * @param date the {@link Date} to compare the property value
     * @return a new case-insensitive expression reflecting the property name and the specified value.
     */
    Criterion gt(Date date);

    /**
     * Returns a new expression indicating the property value must be greater than or equal to the specified date.
     * For example:
     * <pre>
     * application.createdAt().gt(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse("2014-01-01T12:00:00")): matches those applications created after or exactly at noon of 2014/01/01
     * </pre>
     *
     * @param date the {@link Date} to compare the property value
     * @return a new case-insensitive expression reflecting the property name and the specified value.
     */
    Criterion gte(Date date);

    /**
     * Returns a new expression indicating the property value must be less than the specified date.
     * For example:
     * <pre>
     * application.createdAt().lt(new Date()): matches only those applications created before the instant specified by {@code new Date()}
     * </pre>
     *
     * @param date the {@link Date} to compare the property value
     * @return a new case-insensitive expression reflecting the property name and the specified value.
     */
    Criterion lt(Date date);

    /**
     * Returns a new expression indicating the specified date must be greater than or equal to the property value.
     * For example:
     * <pre>
     * application.createdAt().gte(new Date()): matches those applications created before or at the exact instant specified by {@code new Date()}
     * </pre>
     *
     * @param date the {@link Date} to compare the property value
     * @return a new case-insensitive expression reflecting the property name and the specified value.
     */
    Criterion lte(Date date);

    /**
     * Returns a new expression indicating the specified date must be equal to the property value.
     * For example:
     * <pre>
     * application.createdAt().gte(new Date()): matches only those applications created at the exact instant specified by {@code new Date()}
     * </pre>
     *
     * @param date the {@link Date} to compare the property value
     * @return a new case-insensitive expression reflecting the property name and the specified value.
     */
    Criterion equals(Date date);

    /**
     * Returns a new expression indicating the property value must belong to the range specified by the {@code begin} and {@end} dates
     * Using "in" is equivalent to gte(begin).lt(end) where begin time is inclusive and end time is exclusive.
     * For example:
     * <pre>
     * application.modifiedAt().in(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse("2014-01-01T12:00:00"), new Date()) matches those applications modified after or exactly at the noon of 2014/01/01 and before the exact instant represented by {@code new Date()}
     * </pre>
     *
     * @param begin the {@link Date} to use as the range start
     * @param end a {@link Date}  to use as the range end
     * @return a new case-insensitive expression reflecting the property name and the specified value.
     */
    Criterion in(Date begin, Date end);

    /**
     * This is a convenience method used to calculate the end timestamp for the range based on the {@code begin} date and the specified {@link Duration}
     *
     * @param begin the {@link Date} to use as the range start
     * @param duration the {@link Duration} used to calculate the end timestamp based on the value specified by {@code begin}
     * @return
     */
    Criterion in(Date begin, Duration duration);
}
