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

import java.time.*;
import java.util.*;
import java.util.Date;

/**
 * @since 1.0
 */
public class Instants {

    public static final TimeZone UTC_TIMEZONE = TimeZone.getTimeZone("UTC");
    public static final ZoneId UTC_ZONE_ID = UTC_TIMEZONE.toZoneId();

    /**
     * Converts a given time from UTC to the corresponding time in the specified {@link TimeZone}
     * @param time the UTC instant to convert, represented as the number of milliseconds that have elapsed since midnight, January 1, 1970
     * @param to the target {@link TimeZone} for the conversion
     * @return the long representation of the instant converted to the specified {@link TimeZone}
     */
    public static long convertDateToLocalTime(long time, TimeZone to) {
        return convertDate(time, UTC_TIMEZONE, to);
    }

    /**
     * Converts a given time from the specified {@link TimeZone} to the corresponding UTC time
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
     * Creates an UTC-based {@Link Date} from the provided {@code year}
     * @param year  the year to represent, from MIN_YEAR to MAX_YEAR
     * @return the UTC-based {@link Date}
     */
    public static Date of(int year){
        LocalDateTime zonedDateTime = LocalDateTime.of(year, Month.JANUARY, 1, 0, 0);
        return Date.from(zonedDateTime.atZone(UTC_ZONE_ID).toInstant());
    }

    /**
     * Creates an UTC-based {@Link Date} from the provided {@code year} and {@code month}
     * @param year  the year to represent, from MIN_YEAR to MAX_YEAR
     * @param month  the month-of-year to represent, from 1 (January) to 12 (December)
     * @return the UTC-based {@link Date}
     */
    public static Date of(int year, int month){
        Assert.isTrue(1 <= month && month <= 12, "month param must be a value from 1 (January) to 12 (December)");
        LocalDateTime zonedDateTime = LocalDateTime.of(year, Month.of(month), 1, 0, 0);
        return Date.from(zonedDateTime.atZone(UTC_ZONE_ID).toInstant());
    }

    /**
     * Creates an UTC-based {@Link Date} from the provided {@code year}, {@code month} and {@code day}
     * @param year  the year to represent, from MIN_YEAR to MAX_YEAR
     * @param month  the month-of-year to represent, from 1 (January) to 12 (December)
     * @param day  the day-of-month to represent, from 1 to 31
     * @return the UTC-based {@link Date}
     */
    public static Date of(int year, int month, int day){
        Assert.isTrue(1 <= month && month <= 12, "month param must be a value from 1 (January) to 12 (December)");
        Assert.isTrue(1 <= day && day <= 31, "day param must be a value from 1 to 31");
        LocalDateTime zonedDateTime = LocalDateTime.of(year, Month.of(month), day, 0, 0);
        return Date.from(zonedDateTime.atZone(UTC_ZONE_ID).toInstant());
    }

    /**
     * Creates an UTC-based {@Link Date} from the provided {@code year}, {@code month}, {@code day} and {@code hour}
     * @param year  the year to represent, from MIN_YEAR to MAX_YEAR
     * @param month  the month-of-year to represent, from 1 (January) to 12 (December)
     * @param day  the day-of-month to represent, from 1 to 31
     * @param hour  the hour-of-day to represent, from 0 to 23
     * @return the UTC-based {@link Date}
     */
    public static Date of(int year, int month, int day, int hour){
        Assert.isTrue(1 <= month && month <= 12, "month param must be a value from 1 (January) to 12 (December)");
        Assert.isTrue(1 <= day && day <= 31, "day param must be a value from 1 to 31");
        Assert.isTrue(0 <= hour && hour <= 23, "hour param must be a value from 0 to 23");
        LocalDateTime zonedDateTime = LocalDateTime.of(year, Month.of(month), day, hour, 0);
        return Date.from(zonedDateTime.atZone(UTC_ZONE_ID).toInstant());
    }

    /**
     * Creates an UTC-based {@Link Date} from the provided {@code year}, {@code month}, {@code day}, {@code hour} and {@code minute}
     * @param year  the year to represent, from MIN_YEAR to MAX_YEAR
     * @param month  the month-of-year to represent, from 1 (January) to 12 (December)
     * @param day  the day-of-month to represent, from 1 to 31
     * @param hour  the hour-of-day to represent, from 0 to 23
     * @param minute  the minute-of-hour to represent, from 0 to 59
     * @return the UTC-based {@link Date}
     */
    public static Date of(int year, int month, int day, int hour, int minute){
        Assert.isTrue(1 <= month && month <= 12, "month param must be a value from 1 (January) to 12 (December)");
        Assert.isTrue(1 <= day && day <= 31, "day param must be a value from 1 to 31");
        Assert.isTrue(0 <= hour && hour <= 23, "hour param must be a value from 0 to 23");
        Assert.isTrue(0 <= minute && minute <= 59, "minute param must be a value from 0 to 59");
        LocalDateTime zonedDateTime = LocalDateTime.of(year, Month.of(month), day, hour, minute);
        return Date.from(zonedDateTime.atZone(UTC_ZONE_ID).toInstant());
    }

    /**
     * Creates an UTC-based {@Link Date} from the provided {@code year}, {@code month}, {@code day}, {@code hour}, {@code minute} and {@code second}
     * @param year  the year to represent, from MIN_YEAR to MAX_YEAR
     * @param month  the month-of-year to represent, from 1 (January) to 12 (December)
     * @param day  the day-of-month to represent, from 1 to 31
     * @param hour  the hour-of-day to represent, from 0 to 23
     * @param minute  the minute-of-hour to represent, from 0 to 59
     * @param second  the second-of-hour to represent, from 0 to 59
     * @return the UTC-based {@link Date}
     */
    public static Date of(int year, int month, int day, int hour, int minute, int second){
        Assert.isTrue(1 <= month && month <= 12, "month param must be a value from 1 (January) to 12 (December)");
        Assert.isTrue(1 <= day && day <= 31, "day param must be a value from 1 to 31");
        Assert.isTrue(0 <= hour && hour <= 23, "hour param must be a value from 1 to 23");
        Assert.isTrue(0 <= minute && minute <= 59, "minute param must be a value from 0 to 59");
        Assert.isTrue(0 <= second && second <= 59, "second param must be a value from 0 to 59");
        LocalDateTime zonedDateTime = LocalDateTime.of(year, Month.of(month), day, hour, minute, second);
        return Date.from(zonedDateTime.atZone(UTC_ZONE_ID).toInstant());
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
