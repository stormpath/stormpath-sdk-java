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

import java.util.UUID;

import static org.easymock.EasyMock.expect;
import static org.powermock.api.easymock.PowerMock.mockStatic;
import static org.powermock.api.easymock.PowerMock.replayAll;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

@PrepareForTest(EnvironmentVariableCredentialsProvider.class)
public class EnvironmentVariableCredentialsProviderTest extends PowerMockTestCase {

    @Test
    public void environmentVariableCredentialsReturned() {
        String keyId = UUID.randomUUID().toString();
        String secret = UUID.randomUUID().toString();

        String userHome = System.getProperty("user.home");

        mockStatic(System.class);

        expect(System.getProperty("user.home")).andReturn(userHome).once();
        expect(System.getenv("STORMPATH_API_KEY_ID")).andReturn(keyId).once();
        expect(System.getenv("STORMPATH_API_KEY_SECRET")).andReturn(secret).once();

        replayAll();

        ClientCredentials clientCredentials = new EnvironmentVariableCredentialsProvider().getClientCredentials();

        assertNotNull(clientCredentials);
        assertEquals(clientCredentials.getId(), keyId);
        assertEquals(clientCredentials.getSecret(), secret);

    }

}
