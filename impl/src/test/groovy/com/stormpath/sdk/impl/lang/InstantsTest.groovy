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
package com.stormpath.sdk.impl.lang

import com.fasterxml.jackson.databind.util.ISO8601DateFormat
import com.stormpath.sdk.lang.Instants
import org.testng.annotations.Test
import java.text.DateFormat
import static org.testng.Assert.assertEquals
import static org.testng.Assert.fail

/**
 * Utility class to create UTC-based dates and perform time conversions from UTC to other {@link TimeZone} timezones and vice versa
 *
 * @since 1.0.RC4
 */
class InstantsTest {

    private static final DateFormat dateFormat = new ISO8601DateFormat();

    @Test
    void testMethodErrors(){
        try {
            Instants.of(1920, 15);
            fail("Should have thrown")
        } catch (IllegalArgumentException e) {
            assertEquals(e.getMessage(), "month param must be a value from 1 (January) to 12 (December)")
        }

        try {
            Instants.of(1920, 3, 0);
            fail("Should have thrown")
        } catch (IllegalArgumentException e) {
            assertEquals(e.getMessage(), "day param must be a value from 1 to 31")
        }

        try {
            Instants.of(1920, 3, 3, 24);
            fail("Should have thrown")
        } catch (IllegalArgumentException e) {
            assertEquals(e.getMessage(), "hour param must be a value from 0 to 23")
        }

        try {
            Instants.of(1920, 3, 3, 20, 60);
            fail("Should have thrown")
        } catch (IllegalArgumentException e) {
            assertEquals(e.getMessage(), "minute param must be a value from 0 to 59")
        }

        try {
            Instants.of(1920, 3, 3, 20, 28, 60);
            fail("Should have thrown")
        } catch (IllegalArgumentException e) {
            assertEquals(e.getMessage(), "second param must be a value from 0 to 59")
        }
    }

    @Test
    void testMethods() {
        TimeZone tz = TimeZone.getTimeZone("Africa/Algiers")
        TimeZone utc = TimeZone.getTimeZone("UTC")

        Calendar date2 = new GregorianCalendar(2014,2,15,10,0,0)
        String expectedDate = "2014-03-15T08:00:00Z"
        date2.setTimeZone(tz)   //for tz the set time is = 2014-03-15T09:00:00Z
        long utcTimeStamp = Instants.convertDateToUTC(date2.getTimeInMillis(), tz)
        Calendar utcCal = new GregorianCalendar(utc)
        utcCal.setTimeInMillis(utcTimeStamp);
        assertEquals(expectedDate, dateFormat.format(utcCal.getTime()))

        expectedDate = "2014-03-15T09:00:00Z"
        long lTime = Instants.convertDateToLocalTime(utcTimeStamp, tz);
        Calendar locCal = new GregorianCalendar(tz)
        locCal.setTimeInMillis(lTime);
        assertEquals(expectedDate, dateFormat.format(locCal.getTime()))

        // of(year)
        expectedDate = "2004-01-01T00:00:00Z"
        Date expected = Instants.of(2004);
        assertEquals(dateFormat.format(expected), expectedDate);

        // of(year, month)
        expectedDate = "2011-11-01T00:00:00Z"
        expected = Instants.of(2011, 11);
        assertEquals(dateFormat.format(expected), expectedDate);

        // of(year, month, day)
        expectedDate = "2009-04-10T00:00:00Z"
        expected = Instants.of(2009, 4, 10);
        assertEquals(dateFormat.format(expected), expectedDate);

        // of(year, month, day, hour)
        expectedDate = "2015-09-10T20:00:00Z"
        expected = Instants.of(2015, 9, 10, 20);
        assertEquals(dateFormat.format(expected), expectedDate);

        // of(year, month, day, hour, minute)
        expectedDate = "2009-04-10T20:45:00Z"
        expected = Instants.of(2009, 4, 10, 20, 45);
        assertEquals(dateFormat.format(expected), expectedDate);

        // of(year, month, day, hour, minute, second)
        expectedDate = "2009-04-10T20:45:23Z"
        expected = Instants.of(2009, 4, 10, 20, 45, 23);
        assertEquals(dateFormat.format(expected), expectedDate);
    }
}
