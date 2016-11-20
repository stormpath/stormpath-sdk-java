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
package com.stormpath.sdk.impl.saml

import com.stormpath.sdk.query.Options
import com.stormpath.sdk.resource.ResourceException
import com.stormpath.sdk.saml.RegisteredSamlServiceProvider
import com.stormpath.sdk.saml.RegisteredSamlServiceProviderList
import com.stormpath.sdk.saml.RegisteredSamlServiceProviders
import com.stormpath.sdk.tenant.Tenant
import com.stormpath.sdk.tenant.Tenants
import org.testng.annotations.AfterMethod
import org.testng.annotations.Test

import static org.testng.AssertJUnit.*
/**
 * @since 1.2.0
 */
class RegisteredSamlServiceProviderIT extends AbstractSamlIT {

    @AfterMethod
    public void cleanUp() {
        def list = client.getResource("${client.currentTenant.href}/registeredSamlServiceProviders", RegisteredSamlServiceProviderList)
        List<RegisteredSamlServiceProvider> collection = list.asList()
        for (RegisteredSamlServiceProvider registeredSamlServiceProvider : collection) {
            registeredSamlServiceProvider.delete()
        }
    }

    @Test
    void testCreateAndGetRegisteredSAMLServiceProvider() {

        //create a RegisteredSamlServiceProvider
        def registeredSamlServiceProvider = client.instantiate(RegisteredSamlServiceProvider)
        registeredSamlServiceProvider
                .setName("testName")
                .setDescription("testDescription")
                .setAssertionConsumerServiceUrl("https://some.sp.com/saml/sso/post")
                .setEntityId(uniquify("urn:entity:id"))
                .setEncodedX509SigningCert(validX509Cert)

        def registeredSamlServiceProviderReturned = client.currentTenant.createRegisterdSamlServiceProvider(registeredSamlServiceProvider)

        assertRegisteredSAMLServiceProviderFields(registeredSamlServiceProviderReturned, registeredSamlServiceProvider)

        registeredSamlServiceProviderReturned = client.getResource(registeredSamlServiceProviderReturned.href, RegisteredSamlServiceProvider)

        assertRegisteredSAMLServiceProviderFields(registeredSamlServiceProviderReturned, registeredSamlServiceProvider)

        cleanUp()
    }

    @Test
    void testUniquenessOfRegisteredSamlServiceProviders() {
        def registeredSamlServiceProvider = client.instantiate(RegisteredSamlServiceProvider)
        registeredSamlServiceProvider
                .setName("testName")
                .setAssertionConsumerServiceUrl("https://some.sp.com/saml/sso/post")
                .setEntityId(uniquify("urn:entity:id"))
                .setEncodedX509SigningCert(validX509Cert)

        def registeredSamlServiceProviderReturned = client.currentTenant.createRegisterdSamlServiceProvider(registeredSamlServiceProvider)

        assertRegisteredSAMLServiceProviderFields(registeredSamlServiceProviderReturned, registeredSamlServiceProvider)

        registeredSamlServiceProvider.setEntityId(registeredSamlServiceProvider.getEntityId())
        updatedSaveableError(registeredSamlServiceProvider, 10110)
    }

    @Test
    void testUpdateRegisteredSAMLServiceProviderNameAndDescription() {
        def registeredSamlServiceProvider = client.instantiate(RegisteredSamlServiceProvider)
        registeredSamlServiceProvider
                .setName("testName")
                .setAssertionConsumerServiceUrl("https://some.sp.com/saml/sso/post")
                .setEntityId(uniquify("urn:entity:id"))
                .setEncodedX509SigningCert(validX509Cert)

        def registeredSamlServiceProviderReturned = client.currentTenant.createRegisterdSamlServiceProvider(registeredSamlServiceProvider)

        assertRegisteredSAMLServiceProviderFields(registeredSamlServiceProviderReturned, registeredSamlServiceProvider)

        registeredSamlServiceProviderReturned.setName("name")
        registeredSamlServiceProviderReturned.setDescription("some description")

        registeredSamlServiceProviderReturned.save()

        def registeredSamlServiceProviderUpdated = client.getResource(registeredSamlServiceProviderReturned.href, RegisteredSamlServiceProvider)

        assertRegisteredSAMLServiceProviderFields(registeredSamlServiceProviderUpdated, registeredSamlServiceProviderReturned)
    }


    @Test
    void testUpdateRegisteredSAMLServiceProviderACSUrl() {
        def registeredSamlServiceProvider = client.instantiate(RegisteredSamlServiceProvider)
        registeredSamlServiceProvider
                .setName("testName")
                .setAssertionConsumerServiceUrl("https://some.sp.com/saml/sso/post")
                .setEntityId(uniquify("urn:entity:id"))
                .setEncodedX509SigningCert(validX509Cert)

        def registeredSamlServiceProviderReturned = client.currentTenant.createRegisterdSamlServiceProvider(registeredSamlServiceProvider)

        assertRegisteredSAMLServiceProviderFields(registeredSamlServiceProviderReturned, registeredSamlServiceProvider)

        registeredSamlServiceProviderReturned.setAssertionConsumerServiceUrl("https://some.sp-updatedacs.com/saml/sso/post")

        registeredSamlServiceProviderReturned.save()

        def registeredSamlServiceProviderUpdated = client.getResource(registeredSamlServiceProviderReturned.href, RegisteredSamlServiceProvider)

        assertRegisteredSAMLServiceProviderFields(registeredSamlServiceProviderUpdated, registeredSamlServiceProviderReturned)
    }

    @Test
    void testUpdateRegisteredSAMLServiceProviderEntityId() {
        def registeredSamlServiceProvider = client.instantiate(RegisteredSamlServiceProvider)
        registeredSamlServiceProvider
                .setName("testName")
                .setAssertionConsumerServiceUrl("https://some.sp.com/saml/sso/post")
                .setEntityId(uniquify("urn:entity:id"))
                .setEncodedX509SigningCert(validX509Cert)

        def registeredSamlServiceProviderReturned = client.currentTenant.createRegisterdSamlServiceProvider(registeredSamlServiceProvider)

        assertRegisteredSAMLServiceProviderFields(registeredSamlServiceProviderReturned, registeredSamlServiceProvider)

        registeredSamlServiceProviderReturned.setEntityId("urn:different:entity:id")

        registeredSamlServiceProviderReturned.save()

        def registeredSamlServiceProviderUpdated = client.getResource(registeredSamlServiceProviderReturned.href, RegisteredSamlServiceProvider)

        assertRegisteredSAMLServiceProviderFields(registeredSamlServiceProviderUpdated, registeredSamlServiceProviderReturned)
    }

    @Test
    void testCreateRegisteredSAMLServiceProviderWithNameIdFormat() {
        def registeredSamlServiceProvider = client.instantiate(RegisteredSamlServiceProvider)
        registeredSamlServiceProvider
                .setName("testName")
                .setAssertionConsumerServiceUrl("https://some.sp.com/saml/sso/post")
                .setEntityId(uniquify("urn:entity:id"))
                .setEncodedX509SigningCert(validX509Cert)
                .setNameIdFormat("persistent")

        def registeredSamlServiceProviderReturned = client.currentTenant.createRegisterdSamlServiceProvider(registeredSamlServiceProvider)

        assertRegisteredSAMLServiceProviderFields(registeredSamlServiceProviderReturned, registeredSamlServiceProvider)

        registeredSamlServiceProviderReturned = client.getResource(registeredSamlServiceProviderReturned.href, RegisteredSamlServiceProvider)

        assertRegisteredSAMLServiceProviderFields(registeredSamlServiceProviderReturned, registeredSamlServiceProvider)
    }

    @Test
    void testUpdateRegisteredSAMLServiceProviderNameIdFormat() {
        def registeredSamlServiceProvider = client.instantiate(RegisteredSamlServiceProvider)
        registeredSamlServiceProvider
                .setName("testName")
                .setAssertionConsumerServiceUrl("https://some.sp.com/saml/sso/post")
                .setEntityId(uniquify("urn:entity:id"))
                .setEncodedX509SigningCert(validX509Cert)

        def registeredSamlServiceProviderReturned = client.currentTenant.createRegisterdSamlServiceProvider(registeredSamlServiceProvider)

        assertRegisteredSAMLServiceProviderFields(registeredSamlServiceProviderReturned, registeredSamlServiceProvider)

        registeredSamlServiceProviderReturned.setNameIdFormat("persistent")

        registeredSamlServiceProviderReturned.save()

        def registeredSamlServiceProviderUpdated = client.getResource(registeredSamlServiceProviderReturned.href, RegisteredSamlServiceProvider)

        assertRegisteredSAMLServiceProviderFields(registeredSamlServiceProviderUpdated, registeredSamlServiceProviderReturned)
    }

    @Test
    void testUpdateRegisteredSAMLServiceProviderCert() {
        def registeredSamlServiceProvider = client.instantiate(RegisteredSamlServiceProvider)
        registeredSamlServiceProvider
                .setName("testName")
                .setAssertionConsumerServiceUrl("https://some.sp.com/saml/sso/post")
                .setEntityId(uniquify("urn:entity:id"))
                .setEncodedX509SigningCert(validX509Cert)

        def registeredSamlServiceProviderReturned = client.currentTenant.createRegisterdSamlServiceProvider(registeredSamlServiceProvider)

        assertRegisteredSAMLServiceProviderFields(registeredSamlServiceProviderReturned, registeredSamlServiceProvider)

        registeredSamlServiceProviderReturned.setEncodedX509SigningCert(validX509Cert2)

        registeredSamlServiceProviderReturned.save()

        def registeredSamlServiceProviderUpdated = client.getResource(registeredSamlServiceProviderReturned.href, RegisteredSamlServiceProvider)

        assertRegisteredSAMLServiceProviderFields(registeredSamlServiceProviderUpdated, registeredSamlServiceProviderReturned)
    }

    @Test
    void testGetAndDeleteRegisteredSAMLServiceProvidersOffTenant() {
        def tenant = client.currentTenant
        assertTrue(tenant.getRegisterdSamlServiceProviders().href.endsWith("/registeredSamlServiceProviders"))

        def list = client.getResource("${tenant.href}/registeredSamlServiceProviders", RegisteredSamlServiceProviderList)
        assertEquals(list.asList().size(), 0)

        def registeredSamlServiceProvider = client.instantiate(RegisteredSamlServiceProvider)
        registeredSamlServiceProvider
                .setName("testName")
                .setAssertionConsumerServiceUrl("https://some.sp.com/saml/sso/post")
                .setEntityId(uniquify("urn:entity:id"))
                .setEncodedX509SigningCert(validX509Cert)

        def registeredSamlServiceProviderReturned = client.currentTenant.createRegisterdSamlServiceProvider(registeredSamlServiceProvider)

        registeredSamlServiceProviderReturned = client.getResource(registeredSamlServiceProviderReturned.href, RegisteredSamlServiceProvider)

        assertRegisteredSAMLServiceProviderFields(registeredSamlServiceProviderReturned, registeredSamlServiceProvider)

        list = client.getResource("${tenant.href}/registeredSamlServiceProviders", RegisteredSamlServiceProviderList)
        List<RegisteredSamlServiceProvider> registeredSamlServiceProviders = list.asList()
        assertEquals(registeredSamlServiceProviders.size(), 1)
        assertTrue(registeredSamlServiceProviders.get(0).href.contains("/registeredSamlServiceProviders/"))

        registeredSamlServiceProviders.get(0).delete()
        registeredSamlServiceProviders.get(0).delete()

        getRegisteredSAMLServiceProviderError(registeredSamlServiceProviders.get(0))

        list = client.getResource("${tenant.href}/registeredSamlServiceProviders", RegisteredSamlServiceProviderList)
        assertEquals(list.asList().size(), 0)
    }

    @Test
    void testGetAndDeleteRegisteredSAMLServiceProvidersOffTenantWithExpansion() {
        def tenant = client.currentTenant

        Options options = Tenants.options().withRegisteredSamlServiceProviders()
        tenant = client.getResource("${tenant.href}", Tenant.class, options)
        assertEquals(tenant.registerdSamlServiceProviders.size(), 0)

        def registeredSamlServiceProvider = client.instantiate(RegisteredSamlServiceProvider)
        registeredSamlServiceProvider
                .setName("testName")
                .setAssertionConsumerServiceUrl("https://some.sp.com/saml/sso/post")
                .setEntityId(uniquify("urn:entity:id"))
                .setEncodedX509SigningCert(validX509Cert)

        client.currentTenant.createRegisterdSamlServiceProvider(registeredSamlServiceProvider)

        tenant = client.getResource("${tenant.href}", Tenant.class, options)
        assertEquals(tenant.registerdSamlServiceProviders.size(), 1)
        assertTrue(tenant.getRegisterdSamlServiceProviders().asList().get(0).href.contains("/registeredSamlServiceProviders/"))

        tenant.getRegisterdSamlServiceProviders().asList().get(0).delete()
        tenant.getRegisterdSamlServiceProviders().asList().get(0).delete()

        getRegisteredSAMLServiceProviderError(tenant.getRegisterdSamlServiceProviders().asList().get(0))

        tenant = client.getResource("${tenant.href}", Tenant.class, options)
        assertEquals(tenant.registerdSamlServiceProviders.size(), 0)
    }

    @Test
    void testInvalidValues() {
        def registeredSamlServiceProvider = client.instantiate(RegisteredSamlServiceProvider)

        //missing acs url
        registeredSamlServiceProvider
                .setName("testName")
                .setEntityId(uniquify("urn:entity:id"))
                .setEncodedX509SigningCert(validX509Cert)
        createRegisteredSAMLServiceProviderError(registeredSamlServiceProvider, 2000)

        //invalid acs url
        registeredSamlServiceProvider.setAssertionConsumerServiceUrl("wwwwww.some.sp.com/saml/sso/post")
        createRegisteredSAMLServiceProviderError(registeredSamlServiceProvider, 2006)

        //missing entityId
        registeredSamlServiceProvider.setEntityId(null)
        createRegisteredSAMLServiceProviderError(registeredSamlServiceProvider, 2000)
        registeredSamlServiceProvider.setEntityId(uniquify("urn:entity:id"))

        //missing cert
        registeredSamlServiceProvider.setAssertionConsumerServiceUrl("https://some.sp.com/saml/sso/post")
        registeredSamlServiceProvider.setEncodedX509SigningCert(null)
        createRegisteredSAMLServiceProviderError(registeredSamlServiceProvider, 2000)

        //invalid cert
        registeredSamlServiceProvider.setEncodedX509SigningCert("invalid cert")
        createRegisteredSAMLServiceProviderError(registeredSamlServiceProvider, 2006)

        //null nameId
        registeredSamlServiceProvider.setEncodedX509SigningCert(validX509Cert)
        registeredSamlServiceProvider.setNameIdFormat(null)
        createRegisteredSAMLServiceProviderError(registeredSamlServiceProvider, 2002)

        //invalid nameId
        registeredSamlServiceProvider.setNameIdFormat("BLAH")
        createRegisteredSAMLServiceProviderError(registeredSamlServiceProvider, 2002)
    }


    @Test
    void testSearchTenantsCollectionOfRegisteredSamlServiceProvidersByDifferentProperties() {

        def registeredSamlServiceProvider = client.instantiate(RegisteredSamlServiceProvider)
        registeredSamlServiceProvider
                .setName("testName")
                .setAssertionConsumerServiceUrl("https://some.sp.com/saml/sso/post")
                .setEntityId(uniquify("urn:entity:id"))
                .setEncodedX509SigningCert(validX509Cert)

        def registeredSamlServiceProviderReturned = client.currentTenant.createRegisterdSamlServiceProvider(registeredSamlServiceProvider)

        def registeredSamlServiceProvider2 = client.instantiate(RegisteredSamlServiceProvider)
        registeredSamlServiceProvider2
                .setName("testName2")
                .setAssertionConsumerServiceUrl("https://some.sp.com/saml/sso/post2")
                .setEntityId(uniquify("urn:entity:id2"))
                .setEncodedX509SigningCert(validX509Cert)

        def registeredSamlServiceProviderReturned2 = client.currentTenant.createRegisterdSamlServiceProvider(registeredSamlServiceProvider2)

        assertRegisteredSAMLServiceProviderFields(registeredSamlServiceProviderReturned, registeredSamlServiceProvider)
        assertRegisteredSAMLServiceProviderFields(registeredSamlServiceProviderReturned2, registeredSamlServiceProvider2)

        RegisteredSamlServiceProviderList list = client.currentTenant.getRegisterdSamlServiceProviders(RegisteredSamlServiceProviders.where(RegisteredSamlServiceProviders.assertionConsumerServiceUrl().eqIgnoreCase("https://some.sp.com/saml/sso/post")))
        assertEquals(list.asList().size(), 1)

        list = client.currentTenant.getRegisterdSamlServiceProviders(RegisteredSamlServiceProviders.where(RegisteredSamlServiceProviders.name().eqIgnoreCase("some wrong name")))
        assertEquals(list.asList().size(), 0)

        registeredSamlServiceProviderReturned.setName("This is an epic name").setDescription("This is an epic description").save()

        list = client.currentTenant.getRegisterdSamlServiceProviders(RegisteredSamlServiceProviders.where(RegisteredSamlServiceProviders.name().eqIgnoreCase("This is an epic name")))
        assertEquals(list.asList().size(), 1)

        list = client.currentTenant.getRegisterdSamlServiceProviders(RegisteredSamlServiceProviders.where(RegisteredSamlServiceProviders.description().eqIgnoreCase("This is an epic description")))
        assertEquals(list.asList().size(), 1)

        list = client.currentTenant.getRegisterdSamlServiceProviders(RegisteredSamlServiceProviders.where(RegisteredSamlServiceProviders.description().startsWithIgnoreCase("This is")))
        assertEquals(list.asList().size(), 1)

        list = client.currentTenant.getRegisterdSamlServiceProviders(RegisteredSamlServiceProviders.where(RegisteredSamlServiceProviders.description().endsWithIgnoreCase("epic description")))
        assertEquals(list.asList().size(), 1)

        list = client.currentTenant.getRegisterdSamlServiceProviders(RegisteredSamlServiceProviders.where(RegisteredSamlServiceProviders.entityId().startsWithIgnoreCase("urn")))
        assertEquals(list.asList().size(), 2)

        registeredSamlServiceProvider.setNameIdFormat("persistent").save()
        registeredSamlServiceProvider2.setNameIdFormat("persistent").save()

        list = client.currentTenant.getRegisterdSamlServiceProviders(RegisteredSamlServiceProviders.where(RegisteredSamlServiceProviders.nameIdFormat().eqIgnoreCase("persistent")))
        assertEquals(list.asList().size(), 2)

        list = client.currentTenant.getRegisterdSamlServiceProviders(RegisteredSamlServiceProviders.where(RegisteredSamlServiceProviders.encodedX509Certificate().startsWithIgnoreCase("-----BEGIN CERTIFICATE")))
        assertEquals(list.asList().size(), 2)
    }

    @Test
    void testSearchIdentityProviderCollectionOfRegisteredSamlServiceProvidersByDifferentProperties(){
        //todo: saml javadoc implement this oce identityProvider is ready
    }

    @Test
    void testDeletion() {
        def registeredSamlServiceProvider = client.instantiate(RegisteredSamlServiceProvider)
        registeredSamlServiceProvider
                .setName("testName")
                .setAssertionConsumerServiceUrl("https://some.sp.com/saml/sso/post")
                .setEntityId(uniquify("urn:entity:id"))
                .setEncodedX509SigningCert(validX509Cert)

        def registeredSamlServiceProviderReturned = client.currentTenant.createRegisterdSamlServiceProvider(registeredSamlServiceProvider)

        assertNotNull(registeredSamlServiceProviderReturned.href)

        registeredSamlServiceProviderReturned.delete()

        getRegisteredSAMLServiceProviderError(registeredSamlServiceProviderReturned)

        //todo: saml javadoc elaborate more once identityProvider becomes available
    }

    void assertRegisteredSAMLServiceProviderFields(RegisteredSamlServiceProvider response, RegisteredSamlServiceProvider input) {
        assertNotNull(response.href)
        assertEquals(response.name, input.name)
        assertEquals(response.description, input.description)
        assertEquals(response.getAssertionConsumerServiceUrl(), input.assertionConsumerServiceUrl)
        assertEquals(response.entityId, input.entityId)
        assertEquals(response.encodedX509SigningCert, input.encodedX509SigningCert)
        if (input.nameIdFormat == null) {
            input.nameIdFormat = "urn:oasis:names:tc:SAML:1.1:nameid-format:emailAddress"
        }
        assertEquals(response.nameIdFormat, input.nameIdFormat)
        assertNotNull(response.createdAt)
        assertNotNull(response.modifiedAt)
        assertNotNull(response.tenant)
        assertNotNull(response.tenant.href)
    }

    void createRegisteredSAMLServiceProviderError(RegisteredSamlServiceProvider input, int expectedErrorCode) {
        Throwable e = null;
        try {
            client.currentTenant.createRegisterdSamlServiceProvider(input)
        }
        catch (ResourceException re) {
            e = re
            assertEquals(re.status, 400)
            assertEquals(re.getCode(), expectedErrorCode)
        }

        assertTrue(e instanceof ResourceException)
    }

    void getRegisteredSAMLServiceProviderError(RegisteredSamlServiceProvider input) {
        Throwable e = null;
        try {
            client.getResource(input.href, RegisteredSamlServiceProvider)
        }
        catch (ResourceException re) {
            e = re
            assertEquals(re.status, 404)
            assertEquals(re.getCode(), 404)
        }

        assertTrue(e instanceof ResourceException)
    }
}
