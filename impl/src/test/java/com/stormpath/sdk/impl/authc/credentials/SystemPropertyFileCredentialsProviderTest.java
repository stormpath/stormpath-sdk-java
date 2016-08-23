/*
 * Copyright (c) 2016 Stormpath, Inc.  All rights reserved.
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

@PrepareForTest(SystemPropertyFileCredentialsProvider.class)
public class SystemPropertyFileCredentialsProviderTest extends PowerMockTestCase {

    @Test
    public void credentialsReadFromSystemPropertyFileLocation() {

        String userHome = System.getProperty("user.home");

        mockStatic(System.class);

        expect(System.getProperty("user.home")).andReturn(userHome).anyTimes();
        expect(System.getProperty("stormpath.client.apiKey.file")).andReturn("classpath:credentials.txt").anyTimes();

        replayAll();

        ClientCredentials clientCredentials = new SystemPropertyFileCredentialsProvider().getClientCredentials();

        assertNotNull(clientCredentials);
        assertEquals(clientCredentials.getId(), "1234");
        assertEquals(clientCredentials.getSecret(), "5678");

    }


}
