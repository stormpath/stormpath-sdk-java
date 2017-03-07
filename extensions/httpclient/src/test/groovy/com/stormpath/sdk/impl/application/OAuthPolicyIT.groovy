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
package com.stormpath.sdk.impl.application

import com.stormpath.sdk.client.ClientIT
import com.stormpath.sdk.impl.resource.AbstractResource
import com.stormpath.sdk.oauth.OAuthPolicies
import com.stormpath.sdk.oauth.OAuthPolicy
import com.stormpath.sdk.oauth.OAuthPolicyOptions
import com.stormpath.sdk.oauth.openidconnect.Scope
import com.stormpath.sdk.oauth.openidconnect.ScopeList
import org.testng.annotations.Test

import java.lang.reflect.Field

import static org.testng.Assert.assertEquals
import static org.testng.Assert.assertNotNull
import static org.testng.Assert.assertFalse
import static org.testng.Assert.assertTrue

/**
 * @since 1.6.0
 */
class OAuthPolicyIT extends ClientIT {

    @Test
    void testPolicyCreatedForNewApplication() {
        def app = createTempApp()

        OAuthPolicy oauthPolicy = app.getOAuthPolicy()
        assertNotNull oauthPolicy
        assertNotNull oauthPolicy.href
        assertEquals oauthPolicy.getApplication().getHref(), app.href
    }

    @Test
    void testNewlyCreatedPolicyGotDefaultScopes(){
        def app = createTempApp()

        OAuthPolicy oauthPolicy = app.getOAuthPolicy()
        assertNotNull oauthPolicy
        assertNotNull oauthPolicy.href
        assertEquals oauthPolicy.getApplication().getHref(), app.href

        def oAuthPolicyScopesEndPoint = oauthPolicy.href + "/scopes"
        def scopes = client.getResource(oAuthPolicyScopesEndPoint, ScopeList.class)
        assertEquals scopes.asList().size(), 3

        assertNotNull scopes.asList().get(0).href
        assertNotNull scopes.asList().get(1).href
        assertNotNull scopes.asList().get(2).href

        for(def currentDefaultScope : scopes.asList()){
            assertNotNull(currentDefaultScope.name)
            assertNotNull(currentDefaultScope.friendlyName)
            assertNotNull(currentDefaultScope.description)
            assertNotNull(currentDefaultScope.attributeMappings)
            assertFalse(currentDefaultScope.attributeMappings.isEmpty())
        }
    }

    @Test
    void testPolicyExpandOnScopes(){
        def app = createTempApp()
        def policyHref = app.getOAuthPolicy().href

        OAuthPolicyOptions options = OAuthPolicies.options().withScopes()

        assertNotNull options
        assertEquals options.expansions.size(), 1

        options = OAuthPolicies.options().withScopes(10)

        assertNotNull options
        assertEquals options.expansions.size(), 1

        options = OAuthPolicies.options().withScopes(10,0)

        assertNotNull options
        assertEquals options.expansions.size(), 1

        def retrieved = client.getResource(policyHref, OAuthPolicy.class, options)
        Map policyProperties = getValue(AbstractResource, retrieved, "properties")
        def scopes = policyProperties.get("scopes").size()
        assertTrue scopes == 5
        assertTrue policyProperties.get("scopes").items.get(0).name != null
        assertTrue policyProperties.get("scopes").items.get(1).name != null
        assertTrue policyProperties.get("scopes").items.get(2).name != null
    }

    @Test
    void testDeleteOAuthPolicyDeletesScopes(){
        // todo: openid connect uncomment the snippet below once cascading deletes are supported in SDK
        // Cascading deletes are not supported in SDK for now
        // Following issue will address it: https://github.com/stormpath/stormpath-sdk-java/issues/985
        //getDeletedResourceError(identityProvider.href, SamlIdentityProvider)
    }

    // todo - currently failing against production @mrafiei
    @Test(enabled = false)
    void testRetrieveAndUpdateOAuthPolicy() {
        def app = createTempApp()

        OAuthPolicy oauthPolicy = app.getOAuthPolicy()
        assertNotNull oauthPolicy
        assertEquals oauthPolicy.getApplication().getHref(), app.href
        assertNotNull oauthPolicy.getTokenEndpoint()
        assertNotNull oauthPolicy.getRevocationEndpoint()
        assertNotNull oauthPolicy.getScopes()
        assertNotNull oauthPolicy.getAccessTokenAttributeMap()
        assertNotNull oauthPolicy.getIdTokenAttributeMap()

        ScopeList scopeList = client.getResource(oauthPolicy.getScopes().getHref(), ScopeList.class)
        assertEquals scopeList.asList().size(), 3

        Scope scope = client.instantiate(Scope.class)
        scope.setName("testScope").setFriendlyName("friendly testing").setDescription("some description").setAttributeMappings(["name":"mapped_name"])
        scope = oauthPolicy.createScope(scope)
        assertEquals scope.getOAuthPolicy().href, oauthPolicy.href

        scopeList = client.getResource(oauthPolicy.getScopes().getHref(), ScopeList.class)
        assertEquals scopeList.asList().size(), 4

        oauthPolicy.setAccessTokenTtl("P8D")
        oauthPolicy.setRefreshTokenTtl("P2D")
        oauthPolicy.setIdTokenTtl("P5D")
        oauthPolicy.setAccessTokenAttributeMap(["atk1":"atv1"])
        oauthPolicy.setIdTokenAttributeMap(["itk1":"itv1", "itk2":"itv2"])
        oauthPolicy.save()

        oauthPolicy = app.getOAuthPolicy()
        assertEquals oauthPolicy.getAccessTokenTtl(), "P8D"
        assertEquals oauthPolicy.getRefreshTokenTtl(), "P2D"
        assertEquals oauthPolicy.getIdTokenTtl(), "P5D"
        assertEquals oauthPolicy.getAccessTokenAttributeMap(), ["atk1":"atv1"]
        assertEquals oauthPolicy.getIdTokenAttributeMap(), ["itk1":"itv1", "itk2":"itv2"]
        assertEquals oauthPolicy.getApplication().getHref(), app.href

        oauthPolicy.setAccessTokenAttributeMap(["atk1":"atv1", "atk2":"atv2"])
        oauthPolicy.setIdTokenAttributeMap(["itk1":"itv1"])
        oauthPolicy.save()

        oauthPolicy = app.getOAuthPolicy()
        assertEquals oauthPolicy.getAccessTokenAttributeMap(), ["atk1":"atv1", "atk2":"atv2"]
        assertEquals oauthPolicy.getIdTokenAttributeMap(), ["itk1":"itv1"]
    }

    @Test
    void testUpdateScopesUpdatesPolicyCollection(){
        def app = createTempApp()

        OAuthPolicy oauthPolicy = app.getOAuthPolicy()
        assertNotNull oauthPolicy
        assertEquals oauthPolicy.getApplication().getHref(), app.href
        assertNotNull oauthPolicy.getScopes()

        def oAuthPolicyScopesEndPoint = oauthPolicy.href + "/scopes"
        def scopes = client.getResource(oAuthPolicyScopesEndPoint, ScopeList.class)

        def items = scopes.asList()

        assertEquals(items.size, 3)
        assertNotNull(items[0].href)
        assertNotNull(items[1].href)
        assertNotNull(items[2].href)

        items[0].setName("updatedName1")
        items[1].setName("updatedName2")
        items[2].setName("updatedName3")

        items[0].save()
        items[1].save()
        items[2].save()

        scopes = client.getResource(oAuthPolicyScopesEndPoint, ScopeList.class)

        items = scopes.asList()

        assertEquals(items.size, 3)
        assertNotNull(items[0].href)
        assertNotNull(items[1].href)
        assertNotNull(items[2].href)
        assertEquals(items[0].name, "updatedName1")
        assertEquals(items[1].name, "updatedName2")
        assertEquals(items[2].name, "updatedName3")
    }

    private Object getValue(Class clazz, Object object, String fieldName) {
        Field field = clazz.getDeclaredField(fieldName)
        field.setAccessible(true)
        return field.get(object)
    }
}
