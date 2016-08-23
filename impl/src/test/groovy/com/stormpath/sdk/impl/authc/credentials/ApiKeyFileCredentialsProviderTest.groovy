package com.stormpath.sdk.impl.authc.credentials

import org.testng.annotations.Test

import static org.testng.Assert.assertEquals
import static org.testng.Assert.assertNotNull

public class ApiKeyFileCredentialsProviderTest {

    @Test
    public void apiKeyCredentialsLoadedFromSpecifiedFile() {

        ApiKeyFileCredentialsProvider credentialsProvider = new ApiKeyFileCredentialsProvider("classpath:credentials.txt");

        ClientCredentials clientCredentials = credentialsProvider.getClientCredentials();

        assertNotNull(clientCredentials);
        assertEquals(clientCredentials.id, "1234")
        assertEquals(clientCredentials.secret, "5678")

    }

}
