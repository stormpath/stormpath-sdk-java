/*
 * Copyright 2014 Stormpath, Inc.
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
package com.stormpath.sdk.impl.api

import com.stormpath.sdk.api.ApiKeyOptions
import com.stormpath.sdk.api.ApiKeyStatus
import org.testng.annotations.Test

import static org.testng.Assert.*

/**
 * @since 1.0.beta
 */
class ClientApiKeyTest {

    @Test
    void testConstructorIdNull() {
        try {
            new ClientApiKey(null, "secret");
            fail("Should have thrown due to null id.")
        } catch (IllegalArgumentException ex) {
            assertEquals(ex.getMessage(), "API key id cannot be null or empty.")
        }
    }

    @Test
    void testConstructorSecretNull() {
        try {
            new ClientApiKey("id", null);
            fail("Should have thrown due to null secret.")
        } catch (IllegalArgumentException ex) {
            assertEquals(ex.getMessage(), "API key secret cannot be null or empty.")
        }
    }

    @Test
    void testConstructor() {
        def apiKey = new ClientApiKey("fooId", "barSecret");
        org.testng.Assert.assertEquals(apiKey.getId(), "fooId")
        org.testng.Assert.assertEquals(apiKey.getSecret(), "barSecret")
    }

    @Test
    void testToString() {
        def apiKey = new ClientApiKey("fooId", "barSecret");
        org.testng.Assert.assertEquals(apiKey.toString(), "fooId")
    }

    @Test
    void testHashCode() {
        def apiKey = new ClientApiKey("fooId", "barSecret");
        org.testng.Assert.assertEquals(apiKey.hashCode(), 97614977)
    }

    @Test
    void testEquals() {
        def apiKey = new ClientApiKey("fooId", "barSecret");
        def apiKey2 = new ClientApiKey("fooId", "barSecret");
        def apiKey3 = new ClientApiKey("fooId", "nope");
        def apiKey4 = new ClientApiKey("nope", "barSecret");

        assertTrue(apiKey.equals(apiKey))
        assertTrue(apiKey.equals(apiKey2))
        assertFalse(apiKey.equals(apiKey3))
        assertFalse(apiKey.equals(apiKey4))
        assertFalse(apiKey.equals("anything"))
    }

    @Test(expectedExceptions = IllegalAccessError)
    void testApiKeyStatus() {
        def apiKey = new ClientApiKey("fooId", "barSecret")
        apiKey.getStatus()
    }

    @Test(expectedExceptions = IllegalAccessError)
    void testSetStatus() {
        def apiKey = new ClientApiKey("fooId", "barSecret")
        apiKey.setStatus(ApiKeyStatus.ENABLED)
    }

    @Test(expectedExceptions = IllegalAccessError)
    void testGetAccount() {
        def apiKey = new ClientApiKey("fooId", "barSecret")
        apiKey.getAccount()
    }

    @Test(expectedExceptions = IllegalAccessError)
    void testGetTenant() {
        def apiKey = new ClientApiKey("fooId", "barSecret")
        apiKey.getTenant()
    }

    @Test(expectedExceptions = IllegalAccessError)
    void testSaveWithOptions() {
        def apiKey = new ClientApiKey("fooId", "barSecret")
        ApiKeyOptions mock = [] as ApiKeyOptions
        apiKey.save(mock)
    }

    @Test(expectedExceptions = IllegalAccessError)
    void testDelete() {
        def apiKey = new ClientApiKey("fooId", "barSecret")
        apiKey.delete()
    }

    @Test(expectedExceptions = IllegalAccessError)
    void testGetHref() {
        def apiKey = new ClientApiKey("fooId", "barSecret")
        apiKey.getHref()
    }

    @Test(expectedExceptions = IllegalAccessError)
    public void testSave() {
        def apiKey = new ClientApiKey("fooId", "barSecret")
        apiKey.save()
    }

}
