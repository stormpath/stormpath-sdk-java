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

@PrepareForTest(SystemPropertiesApiKeyCredentialsProvider.class)
public class SystemPropertiesApiKeyCredentialsProviderTest extends PowerMockTestCase {

    @Test
    public void systemPropertiesCredentialsReturned() {

        String keyId = UUID.randomUUID().toString();
        String secret = UUID.randomUUID().toString();

        String userHome = System.getProperty("user.home");

        mockStatic(System.class);

        expect(System.getProperty("user.home")).andReturn(userHome).once();
        expect(System.getProperty("stormpath.client.apiKey.id")).andReturn(keyId).once();
        expect(System.getProperty("stormpath.client.apiKey.secret")).andReturn(secret).once();

        replayAll();

        ClientCredentials clientCredentials = new SystemPropertiesApiKeyCredentialsProvider().getClientCredentials();

        assertNotNull(clientCredentials);
        assertEquals(clientCredentials.getId(), keyId);
        assertEquals(clientCredentials.getSecret(), secret);

    }


}
