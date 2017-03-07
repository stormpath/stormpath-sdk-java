/*
 * Copyright 2017 Stormpath, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
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
package com.stormpath.sdk.impl.openidconnect

import com.stormpath.sdk.client.ClientIT
import com.stormpath.sdk.oauth.OAuthPolicy
import com.stormpath.sdk.oauth.openidconnect.Scope
import com.stormpath.sdk.oauth.openidconnect.ScopeList
import com.stormpath.sdk.resource.ResourceException
import org.testng.annotations.Test

import static org.testng.Assert.assertEquals
import static org.testng.Assert.assertNotNull
import static org.testng.Assert.assertNull
import static org.testng.Assert.assertTrue

/**
 * @since 1.6.0
 */
class ScopeIT extends ClientIT {

    @Test
    void testGetPolicyScopes() {

        def app = createTempApp()

        OAuthPolicy oauthPolicy = app.getOAuthPolicy()
        assertNotNull oauthPolicy

        ScopeList scopeList = client.getResource(oauthPolicy.getScopes().getHref(), ScopeList.class)
        assertEquals app.getOAuthPolicy().scopes.href, scopeList.href
        assertEquals scopeList.asList().size(), 3
    }

    @Test
    void testCreateScope() {
        def app = createTempApp()
        assertNotNull app.getOAuthPolicy().href
        assertNotNull app.getOAuthPolicy().scopes.href

        OAuthPolicy oAuthPolicy = app.getOAuthPolicy()
        assertNotNull oAuthPolicy.href

        def oAuthPolicyScopesEndPoint = oAuthPolicy.href + "/scopes"
        def scopeList = client.getResource(oAuthPolicyScopesEndPoint, ScopeList.class)
        assertNotNull scopeList.href

        Scope scope = client.instantiate(Scope.class)
        scope.setDescription("description")

        Throwable e = null
        try{
            oAuthPolicy.createScope(scope)
        }
        catch(ResourceException re){
            e = re
            assertEquals(re.status, 400)
            assertEquals(re.getCode(), 2000)
        }
        assertTrue(e instanceof ResourceException)

        scope.setName("name").setAttributeMappings(["attribute1":"value1"])
        scope = oAuthPolicy.createScope(scope)
        scope = client.getResource(scope.href, Scope.class)

        assertNotNull(scope.href)
        assertNotNull(scope.name)
        assertNull(scope.friendlyName)
        assertNotNull(scope.description)
        assertNotNull(scope.attributeMappings)
        assertNotNull(scope.createdAt)
        assertNotNull(scope.modifiedAt)
        assertNotNull(scope.getOAuthPolicy())
        assertNotNull(scope.getOAuthPolicy().href)
    }

    // todo - currently failing against production @mrafiei
    @Test(enabled = false)
    void testUpdateScope() {
        def app = createTempApp()
        assertNotNull app.getOAuthPolicy().href
        assertNotNull app.getOAuthPolicy().scopes.href

        OAuthPolicy oAuthPolicy = app.getOAuthPolicy()
        assertNotNull oAuthPolicy.href

        Scope scope = client.instantiate(Scope.class)
        scope.setName("myScopeName").setAttributeMappings(["attribute1":"value1"])
        scope.setDescription("myScopeDescription")

        scope = oAuthPolicy.createScope(scope)
        scope = client.getResource(scope.href, Scope.class)

        assertNotNull(scope.href)
        assertEquals scope.name, "myScopeName"
        assertNull(scope.friendlyName)
        assertEquals scope.description, "myScopeDescription"
        assertNotNull(scope.attributeMappings)
        assertEquals scope.attributeMappings, ["attribute1":"value1"]
        assertNotNull(scope.createdAt)
        assertNotNull(scope.modifiedAt)
        assertNotNull(scope.getOAuthPolicy())
        assertNotNull(scope.getOAuthPolicy().href)

        scope.setFriendlyName("myFriendlyName")
        scope.setAttributeMappings(["attribute1":"value1", "attribute2":"value2"])
        scope.save()
        scope = client.getResource(scope.href, Scope.class)

        assertNotNull(scope.href)
        assertNotNull(scope.name)
        assertEquals scope.friendlyName, "myFriendlyName"
        assertNotNull(scope.description)
        assertEquals scope.attributeMappings, ["attribute1":"value1", "attribute2":"value2"]
        assertNotNull(scope.createdAt)
        assertNotNull(scope.modifiedAt)
        assertNotNull(scope.getOAuthPolicy())
        assertNotNull(scope.getOAuthPolicy().href)

        scope.setAttributeMappings(["attribute1":"value1"])
        scope.save()
        scope = client.getResource(scope.href, Scope.class)

        assertNotNull(scope.href)
        assertNotNull(scope.name)
        assertEquals scope.friendlyName, "myFriendlyName"
        assertNotNull(scope.description)
        assertEquals scope.attributeMappings, ["attribute1":"value1"]
        assertNotNull(scope.createdAt)
        assertNotNull(scope.modifiedAt)
        assertNotNull(scope.getOAuthPolicy())
        assertNotNull(scope.getOAuthPolicy().href)
    }

    @Test
    void testDeleteScope() {
        def app = createTempApp()
        assertNotNull app.getOAuthPolicy().href
        assertNotNull app.getOAuthPolicy().scopes.href

        OAuthPolicy oAuthPolicy = app.getOAuthPolicy()
        assertNotNull oAuthPolicy.href

        Scope scope = client.instantiate(Scope.class)
        scope.setName("myScopeName").setAttributeMappings(["attribute1":"value1"])
        scope.setDescription("myScopeDescription")

        oAuthPolicy.createScope(scope)

        def oAuthPolicyScopesEndPoint = oAuthPolicy.href + "/scopes"

        def scopes = client.getResource(oAuthPolicyScopesEndPoint, ScopeList.class)
        assertEquals scopes.size, 4

        scope.delete()

        Throwable e = null
        try{
            client.getResource(scope.href, Scope.class)
        }
        catch(ResourceException re){
            e = re
            assertEquals(re.status, 404)
            assertEquals(re.getCode(), 404)
        }
        assertTrue(e instanceof ResourceException)

        scopes = client.getResource(oAuthPolicyScopesEndPoint, ScopeList.class)
        assertEquals scopes.size, 3
    }

}
