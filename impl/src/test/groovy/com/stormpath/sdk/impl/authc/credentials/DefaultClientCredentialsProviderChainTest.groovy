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
