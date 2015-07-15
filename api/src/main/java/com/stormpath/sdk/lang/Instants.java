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
package com.stormpath.sdk.lang;

import java.util.*;
import java.util.Date;

/**
 * Utility class to create UTC-based dates and perform time conversions from UTC to other {@link TimeZone timezones} and vice versa
 *
 * @since 1.0.RC4.6
 */
public class Instants {

    public static final TimeZone UTC_TIMEZONE = TimeZone.getTimeZone("UTC");

    /**
     * Converts a given time from UTC (Coordinated Universal Time) to the corresponding time in the specified {@link TimeZone}
     * @param time the UTC instant to convert, represented as the number of milliseconds that have elapsed since midnight, January 1, 1970
     * @param to the target {@link TimeZone} for the conversion
     * @return the long representation of the instant converted to the specified {@link TimeZone}
     */
    public static long convertDateToLocalTime(long time, TimeZone to) {
        return convertDate(time, UTC_TIMEZONE, to);
    }

    /**
     * Converts a given time from the specified {@link TimeZone} to the corresponding UTC (Coordinated Universal Time) time
     * @param time the instant to convert, represented as the number of milliseconds that have elapsed since midnight, January 1, 1970
     * @param from the original {@link TimeZone}
     * @return the UTC instant, represented as the number of milliseconds that have elapsed since midnight, January 1, 1970
     */
    public static long convertDateToUTC(long time, TimeZone from) {
        return convertDate(time, from, UTC_TIMEZONE);
    }

    /**
     * Converts a given time from a {@link TimeZone} to another
     * @param time the instant to convert, represented as the number of milliseconds that have elapsed since midnight, January 1, 1970 in the {@code from} {@link TimeZone}
     * @param from the original {@link TimeZone}
     * @param to the target {@link TimeZone} for the conversion
     * @return the long representation of the instant converted to the specified {@code to} {@link TimeZone}
     */
    public static long convertDate(long time, TimeZone from, TimeZone to) {
        return time + getTimeZoneOffset(time, from, to);
    }

    /**
     * Creates an UTC-based {@Link Date} using the provided {@code year}.
     * Uses the current date and time, sets the specified {@code year} and returns the Date converted to a UTC timestamp.
     *
     * @param year  the year to represent
     * @return the UTC-based {@link Date}
     */
    public static Date of(int year){
        GregorianCalendar cal = new GregorianCalendar();
        cal.set(Calendar.YEAR, year);
        TimeZone fromTimeZone = cal.getTimeZone();
        return new Date(convertDate(cal.getTimeInMillis(), fromTimeZone, UTC_TIMEZONE));
    }

    /**
     * Creates an UTC-based {@Link Date} from the provided {@code year} and {@code month}.
     * Uses the current date and time, sets the specified {@code year} and {@code month}, and returns the Date converted to a UTC timestamp.
     *
     * @param year  the year to represent
     * @param month  the month-of-year to represent, from 0 (January) to 11 (December)
     * @return the UTC-based {@link Date}
     */
    public static Date of(int year, int month){
        Assert.isTrue(0 <= month && month <= 11, "month param must be a value from 0 (January) to 11 (December)");
        GregorianCalendar cal = new GregorianCalendar();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month);
        TimeZone fromTimeZone = cal.getTimeZone();
        return new Date(convertDate(cal.getTimeInMillis(), fromTimeZone, UTC_TIMEZONE));
    }

    /**
     * Creates an UTC-based {@Link Date} from the provided {@code year}, {@code month} and {@code day}
     * Uses the current date and time, sets the specified {@code year}, {@code month} and {@code day}, and returns the Date converted to a UTC timestamp.
     *
     * @param year  the year to represent
     * @param month  the month-of-year to represent, from 0 (January) to 11 (December)
     * @param day  the day-of-month to represent, from 1 to 31
     * @return the UTC-based {@link Date}
     */
    public static Date of(int year, int month, int day){
        Assert.isTrue(0 <= month && month <= 11, "month param must be a value from 0 (January) to 11 (December)");
        Assert.isTrue(1 <= day && day <= 31, "day param must be a value from 1 to 31");
        GregorianCalendar cal = new GregorianCalendar();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month);
        cal.set(Calendar.DAY_OF_MONTH, day);
        TimeZone fromTimeZone = cal.getTimeZone();
        return new Date(convertDate(cal.getTimeInMillis(), fromTimeZone, UTC_TIMEZONE));
    }

    /**
     * Creates an UTC-based {@Link Date} from the provided {@code year}, {@code month}, {@code day} and {@code hour}.
     * Uses the current date and time, sets the specified {@code year}, {@code month}, {@code day} and {@code hour}, and returns the Date converted to a UTC timestamp.
     *
     * @param year  the year to represent
     * @param month  the month-of-year to represent, from 0 (January) to 11 (December)
     * @param day  the day-of-month to represent, from 1 to 31
     * @param hour  the hour-of-day to represent, from 0 to 23
     * @return the UTC-based {@link Date}
     */
    public static Date of(int year, int month, int day, int hour){
        Assert.isTrue(0 <= month && month <= 11, "month param must be a value from 0 (January) to 11 (December)");
        Assert.isTrue(1 <= day && day <= 31, "day param must be a value from 1 to 31");
        Assert.isTrue(0 <= hour && hour <= 23, "hour param must be a value from 0 to 23");
        GregorianCalendar cal = new GregorianCalendar();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month);
        cal.set(Calendar.DAY_OF_MONTH, day);
        cal.set(Calendar.HOUR_OF_DAY, hour);
        TimeZone fromTimeZone = cal.getTimeZone();
        return new Date(convertDate(cal.getTimeInMillis(), fromTimeZone, UTC_TIMEZONE));
    }

    /**
     * Creates an UTC-based {@Link Date} from the provided {@code year}, {@code month}, {@code day}, {@code hour} and {@code minute}.
     * Uses the current date and time, sets the specified {@code year}, {@code month}, {@code day}, {@code hour} and {@code minute}, and returns the Date converted to a UTC timestamp.
     *
     * @param year  the year to represent
     * @param month  the month-of-year to represent, from 0 (January) to 11 (December)
     * @param day  the day-of-month to represent, from 1 to 31
     * @param hour  the hour-of-day to represent, from 0 to 23
     * @param minute  the minute-of-hour to represent, from 0 to 59
     * @return the UTC-based {@link Date}
     */
    public static Date of(int year, int month, int day, int hour, int minute){
        Assert.isTrue(0 <= month && month <= 11, "month param must be a value from 0 (January) to 11 (December)");
        Assert.isTrue(1 <= day && day <= 31, "day param must be a value from 1 to 31");
        Assert.isTrue(0 <= hour && hour <= 23, "hour param must be a value from 0 to 23");
        Assert.isTrue(0 <= minute && minute <= 59, "minute param must be a value from 0 to 59");
        GregorianCalendar cal = new GregorianCalendar();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month);
        cal.set(Calendar.DAY_OF_MONTH, day);
        cal.set(Calendar.HOUR_OF_DAY, hour);
        cal.set(Calendar.MINUTE, minute);
        TimeZone fromTimeZone = cal.getTimeZone();
        return new Date(convertDate(cal.getTimeInMillis(), fromTimeZone, UTC_TIMEZONE));
    }

    /**
     * Creates an UTC-based {@Link Date} from the provided {@code year}, {@code month}, {@code day}, {@code hour}, {@code minute} and {@code second}.
     * Uses the current date and time, sets the specified {@code year}, {@code month}, {@code day}, {@code hour}, {@code minute} and {@code second}, and returns the Date converted to a UTC timestamp.
     *
     * @param year  the year to represent
     * @param month  the month-of-year to represent, from 0 (January) to 11 (December)
     * @param day  the day-of-month to represent, from 1 to 31
     * @param hour  the hour-of-day to represent, from 0 to 23
     * @param minute  the minute-of-hour to represent, from 0 to 59
     * @param second  the second-of-hour to represent, from 0 to 59
     * @return the UTC-based {@link Date}
     */
    public static Date of(int year, int month, int day, int hour, int minute, int second){
        Assert.isTrue(0 <= month && month <= 11, "month param must be a value from 0 (January) to 11 (December)");
        Assert.isTrue(1 <= day && day <= 31, "day param must be a value from 1 to 31");
        Assert.isTrue(0 <= hour && hour <= 23, "hour param must be a value from 1 to 23");
        Assert.isTrue(0 <= minute && minute <= 59, "minute param must be a value from 0 to 59");
        Assert.isTrue(0 <= second && second <= 59, "second param must be a value from 0 to 59");
        GregorianCalendar cal = new GregorianCalendar();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month);
        cal.set(Calendar.DAY_OF_MONTH, day);
        cal.set(Calendar.HOUR_OF_DAY, hour);
        cal.set(Calendar.MINUTE, minute);
        cal.set(Calendar.SECOND, second);
        TimeZone fromTimeZone = cal.getTimeZone();
        return new Date(convertDate(cal.getTimeInMillis(), fromTimeZone, UTC_TIMEZONE));
    }

    /**
     * Creates an UTC-based {@Link Date} from the provided {@code year}, {@code month}, {@code day}, {@code hour}, {@code minute} and {@code second}.
     * Uses the current date and time, sets the specified {@code year}, {@code month}, {@code day}, {@code hour}, {@code minute} and {@code second}, and returns the Date converted to a UTC timestamp.
     *
     * @param year  the YEAR to represent
     * @param month  the MONTH to represent, from 0 (January) to 11 (December)
     * @param day  the DAY_OF_MONTH to represent, from 1 to 31
     * @param hour  the HOUR_OF_DAY to represent, from 0 to 23
     * @param minute  the MINUTE to represent, from 0 to 59
     * @param second  the SECOND to represent, from 0 to 59
     * @param millisecond the MILLISECOND to represent, from 0 to 59
     * @return the UTC-based {@link Date}
     */
    public static Date of(int year, int month, int day, int hour, int minute, int second, int millisecond){
        Assert.isTrue(0 <= month && month <= 11, "month param must be a value from 0 (January) to 11 (December)");
        Assert.isTrue(1 <= day && day <= 31, "day param must be a value from 1 to 31");
        Assert.isTrue(0 <= hour && hour <= 23, "hour param must be a value from 1 to 23");
        Assert.isTrue(0 <= minute && minute <= 59, "minute param must be a value from 0 to 59");
        Assert.isTrue(0 <= second && second <= 59, "second param must be a value from 0 to 59");
        GregorianCalendar cal = new GregorianCalendar();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month);
        cal.set(Calendar.DAY_OF_MONTH, day);
        cal.set(Calendar.HOUR_OF_DAY, hour);
        cal.set(Calendar.MINUTE, minute);
        cal.set(Calendar.SECOND, second);
        cal.set(Calendar.MILLISECOND, millisecond);
        TimeZone fromTimeZone = cal.getTimeZone();
        return new Date(convertDate(cal.getTimeInMillis(), fromTimeZone, UTC_TIMEZONE));
    }

    private static long getTimeZoneOffset(long time, TimeZone from, TimeZone to) {
        int fromOffset = from.getOffset(time);
        int toOffset = to.getOffset(time);
        int diff = 0;

        if (fromOffset >= 0){
            if (toOffset > 0){
                toOffset = -1*toOffset;
            } else {
                toOffset = Math.abs(toOffset);
            }
            diff = (fromOffset+toOffset)*-1;
        } else {
            if (toOffset <= 0){
                toOffset = -1*Math.abs(toOffset);
            }
            diff = (Math.abs(fromOffset)+toOffset);
        }
        return diff;
    }

}
