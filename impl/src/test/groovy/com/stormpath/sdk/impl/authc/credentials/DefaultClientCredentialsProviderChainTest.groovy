package com.stormpath.sdk.impl.authc.credentials

import com.stormpath.sdk.impl.config.ClientConfiguration
import org.testng.annotations.Test

import static org.testng.Assert.assertEquals
import static org.testng.Assert.assertFalse
import static org.testng.Assert.assertNotEquals
import static org.testng.Assert.assertNotNull
import static org.testng.Assert.assertTrue

public class DefaultClientCredentialsProviderChainTest {

    @Test
    public void testDefaultClientCredentialsProviderChainOrder() {

        List<ClientCredentialsProvider> chain = new DefaultClientCredentialsProviderChain(new ClientConfiguration()).clientCredentialsProviders

        assertNotNull(chain)
        assertFalse(chain.isEmpty())
        assertTrue(chain.get(0) instanceof ConfigurationCredentialsProvider)
        assertTrue(chain.get(1) instanceof ApiKeyFileCredentialsProvider)
        assertNotEquals(chain.get(1).apiKeyPropertiesFileLocation, AbstractApiKeyCredentialsProvider.DEFAULT_API_KEY_PROPERTIES_FILE_LOCATION)
        assertTrue(chain.get(2) instanceof SystemPropertiesApiKeyCredentialsProvider)
        assertTrue(chain.get(3) instanceof SystemPropertyFileCredentialsProvider)
        assertTrue(chain.get(4) instanceof EnvironmentVariableCredentialsProvider)
        assertTrue(chain.get(5) instanceof EnvironmentVariableFileCredentialsProvider)
        assertTrue(chain.get(6) instanceof ApiKeyFileCredentialsProvider)
        assertEquals(chain.get(6).apiKeyPropertiesFileLocation, AbstractApiKeyCredentialsProvider.DEFAULT_API_KEY_PROPERTIES_FILE_LOCATION)

    }

}
