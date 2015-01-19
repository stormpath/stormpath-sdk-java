/*
 * Copyright 2014 Stormpath, Inc.
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
package com.stormpath.sdk.impl.http.support

import com.stormpath.sdk.lang.Assert
import org.testng.annotations.Test

/**
 * @since 1.0.RC3
 */
class UserAgentTest {

    private static final String VERSION_SEPARATOR = "/";
    private static final String ENTRY_SEPARATOR = " ";

    @Test
    void testGetUserAgentString() {
        String userAgent = UserAgent.getUserAgentString()
        Assert.hasText(userAgent)
        Assert.isTrue(userAgent.contains("stormpath-sdk-java" + VERSION_SEPARATOR + Version.getClientVersion() + ENTRY_SEPARATOR))
        Assert.isTrue(userAgent.contains("java" + VERSION_SEPARATOR + System.getProperty("java.version") + ENTRY_SEPARATOR))
    }
}
