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
