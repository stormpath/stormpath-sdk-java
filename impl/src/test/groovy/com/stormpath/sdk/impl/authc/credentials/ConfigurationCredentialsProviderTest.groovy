package com.stormpath.sdk.impl.authc.credentials

import com.stormpath.sdk.impl.config.ClientConfiguration
import org.testng.annotations.Test

import static org.testng.Assert.assertEquals


class ConfigurationCredentialsProviderTest {

    @Test
    public void configuredCredentialsReturned() {

        String keyId = UUID.randomUUID().toString()
        String secret = UUID.randomUUID().toString()

        ClientConfiguration clientConfiguration = new ClientConfiguration()
        clientConfiguration.setApiKeyId(keyId)
        clientConfiguration.setApiKeySecret(secret)

        ClientCredentials clientCredentials = new ConfigurationCredentialsProvider(clientConfiguration).getClientCredentials()

        assertEquals(clientCredentials.getId(), keyId)
        assertEquals(clientCredentials.getSecret(), secret)

    }

}
