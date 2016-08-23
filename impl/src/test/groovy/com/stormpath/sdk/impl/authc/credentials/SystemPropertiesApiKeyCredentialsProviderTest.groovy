package com.stormpath.sdk.impl.authc.credentials

import org.testng.annotations.Test

import static org.testng.Assert.assertEquals

class SystemPropertiesApiKeyCredentialsProviderTest {

    @Test
    public void systemPropertiesCredentialsReturned() {

        String keyId = UUID.randomUUID().toString()
        String secret = UUID.randomUUID().toString()

        System.setProperty(SystemPropertiesApiKeyCredentialsProvider.API_KEY_ID_SYSTEM_PROPERTY, keyId);
        System.setProperty(SystemPropertiesApiKeyCredentialsProvider.API_KEY_SECRET_SYSTEM_PROPERTY, secret)

        ClientCredentials clientCredentials = new SystemPropertiesApiKeyCredentialsProvider().getClientCredentials()

        assertEquals(clientCredentials.getId(), keyId)
        assertEquals(clientCredentials.getSecret(), secret)

    }

}
