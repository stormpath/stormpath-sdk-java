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
package com.stormpath.sdk.impl.query

import com.fasterxml.jackson.databind.util.ISO8601DateFormat
import com.stormpath.sdk.lang.Duration
import com.stormpath.sdk.query.Criterion
import org.joda.time.DateTime
import org.testng.annotations.Test

import java.text.DateFormat
import java.util.concurrent.TimeUnit

import static org.testng.Assert.assertEquals
import static org.testng.Assert.fail

/**
 * @since 1.0.RC4.6
 */
class DefaultDateExpressionFactoryTest {

    private static final DefaultDateExpressionFactory defaultDateExpressionFactory = new DefaultDateExpressionFactory("test");
    private static final DateFormat df = new ISO8601DateFormat();
    private static final String INCLUSIVE_OPENING = "[";
    private static final String INCLUSIVE_CLOSING = "]";
    private static final String PROPERTY_NAME = "test";
    private static final String OPERATOR = "=";
    private static final String EXCLUSIVE_OPENING = "(";
    private static final String EXCLUSIVE_CLOSING = ")";
    private static final String COMMA = ", ";

    @Test(expectedExceptions = IllegalArgumentException)
    void testGtParamError() {
        defaultDateExpressionFactory.gt(null);
    }

    @Test(expectedExceptions = IllegalArgumentException)
    void testGteParamError() {
        defaultDateExpressionFactory.gte(null);
    }

    @Test(expectedExceptions = IllegalArgumentException)
    void testLtParamError() {
        defaultDateExpressionFactory.lt(null);
    }

    @Test(expectedExceptions = IllegalArgumentException)
    void testLteParamError() {
        defaultDateExpressionFactory.lte(null);
    }

    @Test(expectedExceptions = IllegalArgumentException)
    void testInNullParamsError() {
        defaultDateExpressionFactory.in(null, new Date());
    }

    @Test(expectedExceptions = IllegalArgumentException)
    void testInInvalidDurationParamError() {
        defaultDateExpressionFactory.in(null, new Duration(4, TimeUnit.DAYS));
    }

    @Test
    void testMethodErrors() {
        try {
            Date thisMoment = new Date();
            defaultDateExpressionFactory.in(new Date(), thisMoment);
            fail("Should have thrown")
        } catch (IllegalArgumentException e) {
            assertEquals(e.getMessage(), "begin date needs to be earlier than end date")
        }

        try {
            Date thisMoment = new Date();
            defaultDateExpressionFactory.in(thisMoment, thisMoment);
            fail("Should have thrown")
        } catch (IllegalArgumentException e) {
            assertEquals(e.getMessage(), "begin date needs to be earlier than end date")
        }
    }

    @Test
    void testMethods(){

        Date date = new Date();
        DateTime dateTime = new DateTime(date);

        String iso8601Date1 = dateTime.toString();

        //matches
        String expectedString = PROPERTY_NAME + OPERATOR + iso8601Date1;
        Criterion c = defaultDateExpressionFactory.matches(iso8601Date1);
        assertEquals c.toString(), expectedString

        //gt
        expectedString = PROPERTY_NAME + OPERATOR + EXCLUSIVE_OPENING + iso8601Date1 + COMMA + INCLUSIVE_CLOSING;
        c = defaultDateExpressionFactory.gt(dateTime.toDate());
        assertEquals c.toString(), expectedString

        //gte
        expectedString = PROPERTY_NAME + OPERATOR + INCLUSIVE_OPENING + iso8601Date1 + COMMA + INCLUSIVE_CLOSING;
        c = defaultDateExpressionFactory.gte(dateTime.toDate());
        assertEquals c.toString(), expectedString

        //lt
        expectedString = PROPERTY_NAME + OPERATOR + INCLUSIVE_OPENING + COMMA + iso8601Date1 + EXCLUSIVE_CLOSING;
        c = defaultDateExpressionFactory.lt(dateTime.toDate());
        assertEquals c.toString(), expectedString

        //lte
        expectedString = PROPERTY_NAME + OPERATOR + INCLUSIVE_OPENING + COMMA + iso8601Date1 + INCLUSIVE_CLOSING;
        c = defaultDateExpressionFactory.lte(dateTime.toDate());
        assertEquals c.toString(), expectedString

        Date date2 = new Date();
        DateTime dateTime2 = new DateTime(date2);

        //in
        expectedString = PROPERTY_NAME + OPERATOR + INCLUSIVE_OPENING + iso8601Date1 + COMMA + dateTime2.toString() + EXCLUSIVE_CLOSING;
        c = defaultDateExpressionFactory.in(dateTime.toDate(), dateTime2.toDate());
        assertEquals c.toString(), expectedString

        //in
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, 1);
        date2 = cal.getTime();
        dateTime2 = new DateTime(date2);
        expectedString = PROPERTY_NAME + OPERATOR + INCLUSIVE_OPENING + iso8601Date1 + COMMA + dateTime2.toString() + EXCLUSIVE_CLOSING;
        c = defaultDateExpressionFactory.in(dateTime.toDate(), new Duration(1, TimeUnit.DAYS));
        assertEquals c.toString(), expectedString
    }
}
