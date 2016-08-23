/*
 * Copyright 2016 Stormpath, Inc.
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
package com.stormpath.sdk.impl.authc.credentials;

import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.testng.PowerMockTestCase;
import org.testng.annotations.Test;

import static org.easymock.EasyMock.expect;
import static org.powermock.api.easymock.PowerMock.mockStatic;
import static org.powermock.api.easymock.PowerMock.replayAll;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

@PrepareForTest(EnvironmentVariableFileCredentialsProvider.class)
public class EnvironmentVariableFileCredentialsProviderTest extends PowerMockTestCase {

    @Test
    public void credentialsReadFromEnvirionmentVariableFileLocation() {
        String userHome = System.getProperty("user.home");

        mockStatic(System.class);

        expect(System.getProperty("user.home")).andReturn(userHome).anyTimes();
        expect(System.getenv("STORMPATH_API_KEY_FILE")).andReturn("classpath:credentials.txt").anyTimes();

        replayAll();

        ClientCredentials clientCredentials = new EnvironmentVariableFileCredentialsProvider().getClientCredentials();

        assertNotNull(clientCredentials);
        assertEquals(clientCredentials.getId(), "1234");
        assertEquals(clientCredentials.getSecret(), "5678");
    }
}
