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
package com.stormpath.sdk.impl.lang

import com.stormpath.sdk.lang.Duration
import org.testng.annotations.Test

import java.util.concurrent.TimeUnit

import static org.testng.Assert.assertEquals
import static org.testng.Assert.assertTrue

/**
 * @since 0.8
 */
class DurationTest {

    @Test
    void testGreaterThan() {

        //1,800,801 millis = 30 minutes + 1 millis
        def duration = new Duration(1800001, TimeUnit.MILLISECONDS);
        def thirtyMin = new Duration(30, TimeUnit.MINUTES);

        assertEquals duration.compareTo(thirtyMin), 1
        assertTrue duration.isGreaterThan(thirtyMin)
    }

    @Test
    void testLessThan() {

        //1,799,999 millis = 30 minutes - 1 millis
        def duration = new Duration(1799999, TimeUnit.MILLISECONDS);
        def thirtyMin = new Duration(30, TimeUnit.MINUTES);

        assertEquals duration.compareTo(thirtyMin), -1
        assertTrue duration.isLessThan(thirtyMin);
    }

    @Test
    void testEqualTo() {

        //1,800,000 millis = 30 minutes
        def duration = new Duration(1800000, TimeUnit.MILLISECONDS);
        def thirtyMin = new Duration(30, TimeUnit.MINUTES);

        assertEquals duration.compareTo(thirtyMin), 0
        assertTrue duration.isEquivalentTo(thirtyMin)
    }

}
