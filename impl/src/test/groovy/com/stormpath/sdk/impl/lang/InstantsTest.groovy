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

import com.stormpath.sdk.lang.Instants
import org.testng.annotations.Test

import static org.testng.Assert.assertEquals
import static org.testng.Assert.fail

/**
 * @since 1.0.RC4.6
 */
class InstantsTest {

    @Test
    void testMethodErrors(){
        try {
            Instants.of(1920, 12);
            fail("Should have thrown")
        } catch (IllegalArgumentException e) {
            assertEquals(e.getMessage(), "month param must be a value from 0 (January) to 11 (December)")
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
        TimeZone tz = TimeZone.getTimeZone("Africa/Algiers")  // Africa/Algiers offset is always +1
        TimeZone tg = TimeZone.getTimeZone("America/Tegucigalpa") //America/Tegucigalpa offset is always -6
        TimeZone utc = TimeZone.getTimeZone("UTC")
        Long oneHourOffset = 3600000

        // convertDateToUTC
        Calendar toConvert = Calendar.getInstance(tz)
        Long expected = toConvert.getTimeInMillis() - oneHourOffset // substract the offset corresponding to 1 hour
        long utcTimeStamp = Instants.convertDateToUTC(toConvert.getTimeInMillis(), tz)
        assertEquals(expected, utcTimeStamp)

        toConvert = Calendar.getInstance(tg)
        expected = toConvert.getTimeInMillis() + (oneHourOffset * 6) // add the offset corresponding to 6 hours
        utcTimeStamp = Instants.convertDateToUTC(toConvert.getTimeInMillis(), tg)
        assertEquals(expected, utcTimeStamp)

        // convertTimeFromUTC
        toConvert = Calendar.getInstance(utc)
        expected = toConvert.getTimeInMillis() - (oneHourOffset * 6) // substract the offset corresponding to 6 hours
        utcTimeStamp = Instants.convertDateToLocalTime(toConvert.getTimeInMillis(), tg)
        assertEquals(expected, utcTimeStamp)

        expected = toConvert.getTimeInMillis() + oneHourOffset // add the offset corresponding to 1 hour
        utcTimeStamp = Instants.convertDateToLocalTime(toConvert.getTimeInMillis(), tz)
        assertEquals(expected, utcTimeStamp)

        // of(year)
        GregorianCalendar beforeTestCal =  new GregorianCalendar()

        beforeTestCal.set(Calendar.MILLISECOND, 0)
        beforeTestCal.set(Calendar.MILLISECOND, -100000000)
        beforeTestCal.set(Calendar.MILLISECOND, 1000000000000000303030303030301)

        beforeTestCal.set(Calendar.YEAR, 2004)
        Long beforeTest = Instants.convertDateToUTC(beforeTestCal.getTimeInMillis(), beforeTestCal.getTimeZone())

        Date returned = Instants.of(2004);

        GregorianCalendar afterTestCal = new GregorianCalendar()
        afterTestCal.set(Calendar.YEAR, 2004)
        Long afterTest = Instants.convertDateToUTC(afterTestCal.getTimeInMillis(), afterTestCal.getTimeZone())

        assert beforeTest <= returned.getTime() && returned.getTime() <= afterTest;

        // of(year, month)
        beforeTestCal =  new GregorianCalendar()
        beforeTestCal.set(Calendar.YEAR, 2011)
        beforeTestCal.set(Calendar.MONTH, 5)
        beforeTest = Instants.convertDateToUTC(beforeTestCal.getTimeInMillis(), beforeTestCal.getTimeZone())

        returned = Instants.of(2011, 5);

        afterTestCal = new GregorianCalendar()
        afterTestCal.set(Calendar.YEAR, 2011)
        afterTestCal.set(Calendar.MONTH, 5)
        afterTest = Instants.convertDateToUTC(afterTestCal.getTimeInMillis(), afterTestCal.getTimeZone())

        assert beforeTest <= returned.getTime() && returned.getTime() <= afterTest;

        // of(year, month, day)
        beforeTestCal =  new GregorianCalendar()
        beforeTestCal.set(Calendar.YEAR, 1973)
        beforeTestCal.set(Calendar.MONTH, 1)
        beforeTestCal.set(Calendar.DAY_OF_MONTH, 11)
        beforeTest = Instants.convertDateToUTC(beforeTestCal.getTimeInMillis(), beforeTestCal.getTimeZone())

        returned = Instants.of(1973, 1, 11);

        afterTestCal = new GregorianCalendar()
        afterTestCal.set(Calendar.YEAR, 1973)
        afterTestCal.set(Calendar.MONTH, 1)
        afterTestCal.set(Calendar.DAY_OF_MONTH, 11)
        afterTest = Instants.convertDateToUTC(afterTestCal.getTimeInMillis(), afterTestCal.getTimeZone())

        assert beforeTest <= returned.getTime() && returned.getTime() <= afterTest;

        // of(year, month, day, hour)
        beforeTestCal =  new GregorianCalendar()
        beforeTestCal.set(Calendar.YEAR, 1937)
        beforeTestCal.set(Calendar.MONTH, 11)
        beforeTestCal.set(Calendar.DAY_OF_MONTH, 7)
        beforeTestCal.set(Calendar.HOUR_OF_DAY, 22)
        beforeTest = Instants.convertDateToUTC(beforeTestCal.getTimeInMillis(), beforeTestCal.getTimeZone())

        returned = Instants.of(1937, 11, 7, 22);

        afterTestCal = new GregorianCalendar()
        afterTestCal.set(Calendar.YEAR, 1937)
        afterTestCal.set(Calendar.MONTH, 11)
        afterTestCal.set(Calendar.DAY_OF_MONTH, 7)
        afterTestCal.set(Calendar.HOUR_OF_DAY, 22)
        afterTest = Instants.convertDateToUTC(afterTestCal.getTimeInMillis(), afterTestCal.getTimeZone())

        assert beforeTest <= returned.getTime() && returned.getTime() <= afterTest;

        // of(year, month, day, hour, minute)
        beforeTestCal =  new GregorianCalendar()
        beforeTestCal.set(Calendar.YEAR, 2014)
        beforeTestCal.set(Calendar.MONTH, 11)
        beforeTestCal.set(Calendar.DAY_OF_MONTH, 31)  // year change will likely happen when testing
        beforeTestCal.set(Calendar.HOUR_OF_DAY, 23)
        beforeTestCal.set(Calendar.MINUTE, 55)
        beforeTest = Instants.convertDateToUTC(beforeTestCal.getTimeInMillis(), beforeTestCal.getTimeZone())

        returned = Instants.of(2014, 11, 31, 23, 55);

        afterTestCal = new GregorianCalendar()
        afterTestCal.set(Calendar.YEAR, 2014)
        afterTestCal.set(Calendar.MONTH, 11)
        afterTestCal.set(Calendar.DAY_OF_MONTH, 31)  // year change will likely happen when testing
        afterTestCal.set(Calendar.HOUR_OF_DAY, 23)
        afterTestCal.set(Calendar.MINUTE, 55)
        afterTest = Instants.convertDateToUTC(afterTestCal.getTimeInMillis(), afterTestCal.getTimeZone())

        assert beforeTest <= returned.getTime() && returned.getTime() <= afterTest;

        // of(year, month, day, hour, minute, second)
        beforeTestCal =  new GregorianCalendar()
        beforeTestCal.set(Calendar.YEAR, 2000)
        beforeTestCal.set(Calendar.MONTH, 0)
        beforeTestCal.set(Calendar.DAY_OF_MONTH, 1)
        beforeTestCal.set(Calendar.HOUR_OF_DAY, 00)
        beforeTestCal.set(Calendar.MINUTE, 00)
        beforeTestCal.set(Calendar.SECOND, 27)
        beforeTest = Instants.convertDateToUTC(beforeTestCal.getTimeInMillis(), beforeTestCal.getTimeZone())

        returned = Instants.of(2000, 0, 1, 00, 00, 27)

        afterTestCal = new GregorianCalendar()
        afterTestCal.set(Calendar.YEAR, 2000)
        afterTestCal.set(Calendar.MONTH, 0)
        afterTestCal.set(Calendar.DAY_OF_MONTH, 1)
        afterTestCal.set(Calendar.HOUR_OF_DAY, 00)
        afterTestCal.set(Calendar.MINUTE, 00)
        afterTestCal.set(Calendar.SECOND, 27)
        afterTest = Instants.convertDateToUTC(afterTestCal.getTimeInMillis(), afterTestCal.getTimeZone())

        assert beforeTest <= returned.getTime() && returned.getTime() <= afterTest

        // of(year, month, day, hour, minute, second, millisecond)
        beforeTestCal =  new GregorianCalendar()
        beforeTestCal.set(Calendar.YEAR, 1999)
        beforeTestCal.set(Calendar.MONTH, 11)
        beforeTestCal.set(Calendar.DAY_OF_MONTH, 31)
        beforeTestCal.set(Calendar.HOUR_OF_DAY, 23)
        beforeTestCal.set(Calendar.MINUTE, 59)
        beforeTestCal.set(Calendar.SECOND, 59)
        beforeTestCal.set(Calendar.MILLISECOND, 59)
        beforeTest = Instants.convertDateToUTC(beforeTestCal.getTimeInMillis(), beforeTestCal.getTimeZone())

        returned = Instants.of(1999, 11, 31, 23, 59, 59, 59)

        afterTestCal = new GregorianCalendar()
        afterTestCal.set(Calendar.YEAR, 1999)
        afterTestCal.set(Calendar.MONTH, 11)
        afterTestCal.set(Calendar.DAY_OF_MONTH, 31)
        afterTestCal.set(Calendar.HOUR_OF_DAY, 23)
        afterTestCal.set(Calendar.MINUTE, 59)
        afterTestCal.set(Calendar.SECOND, 59)
        afterTestCal.set(Calendar.MILLISECOND, 59)
        afterTest = Instants.convertDateToUTC(afterTestCal.getTimeInMillis(), afterTestCal.getTimeZone())

        assert beforeTest <= returned.getTime() && returned.getTime() <= afterTest
    }
}
