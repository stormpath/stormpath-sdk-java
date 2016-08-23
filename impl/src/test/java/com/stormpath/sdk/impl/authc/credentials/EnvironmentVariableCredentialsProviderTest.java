/*
 * Copyright (c) 2016 Stormpath, Inc.  All rights reserved.
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
