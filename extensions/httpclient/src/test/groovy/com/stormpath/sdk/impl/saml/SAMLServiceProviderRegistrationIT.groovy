/*
 * Copyright (c) 2016 Stormpath, Inc.  All rights reserved.
 */
package com.stormpath.sdk.impl.saml

import com.stormpath.sdk.application.Application
import com.stormpath.sdk.query.Options
import com.stormpath.sdk.saml.*
import org.testng.annotations.AfterMethod
import org.testng.annotations.Test

import static org.testng.AssertJUnit.assertEquals
import static org.testng.AssertJUnit.assertNotNull

class SamlServiceProviderRegistrationIT extends AbstractSamlIT {

    @AfterMethod
    public void cleanUp() {
        def list = client.getResource("${client.currentTenant.href}/registeredSamlServiceProviders", RegisteredSamlServiceProviderList)
        List<RegisteredSamlServiceProvider> collection = list.asList()
        for (RegisteredSamlServiceProvider registeredSamlServiceProvider : collection) {
            registeredSamlServiceProvider.delete()
        }
    }

    @Test
    void testCreateAndGetSamlServiceProviderRegistration() {
        def serviceProvider = client.instantiate(RegisteredSamlServiceProvider)
        serviceProvider
                .setName("testName")
                .setAssertionConsumerServiceUrl("https://some.sp.com/saml/sso/post")
                .setEntityId(uniquify("urn:entity:id"))
                .setEncodedX509SigningCert(validX509Cert)

        serviceProvider = client.currentTenant.createRegisterdSamlServiceProvider(serviceProvider)
        Application application = createTempApp()
        SamlPolicy samlPolicy = application.getSamlPolicy()

        assertNotNull(samlPolicy.identityProvider)

        def identityProviderHref = samlPolicy.identityProvider.href
        assertNotNull(identityProviderHref)

        def identityProvider = client.getResource(identityProviderHref, SamlIdentityProvider)
        assertEquals(identityProvider.href, identityProviderHref)

        def registration = client.instantiate(SamlServiceProviderRegistration)
        registration.setIdentityProvider(identityProvider).setServiceProvider(serviceProvider).setDefaultRelayState("aNiceDefaultRelayState")

        registration = createAndGetAndAssertNewRegistration(registration)
        assertEquals(registration.status, SamlServiceProviderRegistrationStatus.ENABLED)

        assertEquals(registration.identityProvider.href, identityProvider.href)
        assertEquals(registration.serviceProvider.href, serviceProvider.href)
        assertEquals(registration.defaultRelayState, "aNiceDefaultRelayState")

        identityProvider = client.getResource(identityProvider.href, SamlIdentityProvider)
        def serviceProvidersHref = identityProvider.registeredSamlServiceProviders.href
        def registrationsHref = identityProvider.samlServiceProviderRegistrations.href

        assertEquals(client.getResource(serviceProvidersHref, RegisteredSamlServiceProviderList).getProperties().items.size, 1)
        assertEquals(client.getResource(registrationsHref, SamlServiceProviderRegistrationList).getProperties().items.size, 1)
    }

    @Test
    void testCreateAndGetSamlServiceProviderRegistrationWithInitialStatus() {
        def serviceProvider = client.instantiate(RegisteredSamlServiceProvider)
        serviceProvider
                .setName("testName")
                .setAssertionConsumerServiceUrl("https://some.sp.com/saml/sso/post")
                .setEntityId(uniquify("urn:entity:id"))
                .setEncodedX509SigningCert(validX509Cert)

        serviceProvider = client.currentTenant.createRegisterdSamlServiceProvider(serviceProvider)
        Application application = createTempApp()
        def samlPolicy = application.getSamlPolicy()

        assertNotNull(samlPolicy.identityProvider)

        def identityProviderHref = samlPolicy.identityProvider.href
        assertNotNull(identityProviderHref)

        def identityProvider = client.getResource(identityProviderHref, SamlIdentityProvider)
        assertEquals(identityProvider.href, identityProviderHref)

        def registration = client.instantiate(SamlServiceProviderRegistration)
        registration.setIdentityProvider(identityProvider).setServiceProvider(serviceProvider)
                .setDefaultRelayState("aNiceDefaultRelayState").setStatus(SamlServiceProviderRegistrationStatus.DISABLED)

        registration = createAndGetAndAssertNewRegistration(registration)
        assertEquals(registration.status, SamlServiceProviderRegistrationStatus.DISABLED)

        assertEquals(registration.identityProvider.href, identityProvider.href)
        assertEquals(registration.serviceProvider.href, serviceProvider.href)
        assertEquals(registration.defaultRelayState, "aNiceDefaultRelayState")

        identityProvider = client.getResource(identityProvider.href, SamlIdentityProvider)
        def serviceProvidersHref = identityProvider.registeredSamlServiceProviders.href
        def registrationsHref = identityProvider.samlServiceProviderRegistrations.href

        assertEquals(client.getResource(serviceProvidersHref, RegisteredSamlServiceProviderList).getProperties().items.size, 1)
        assertEquals(client.getResource(registrationsHref, SamlServiceProviderRegistrationList).getProperties().items.size, 1)
    }

    @Test
    void testCreateAndGetSamlServiceProviderRegistrationAvoidDuplicateRegistration() {
        def serviceProvider = client.instantiate(RegisteredSamlServiceProvider)
        serviceProvider
                .setName("testName")
                .setAssertionConsumerServiceUrl("https://some.sp.com/saml/sso/post")
                .setEntityId(uniquify("urn:entity:id"))
                .setEncodedX509SigningCert(validX509Cert)

        serviceProvider = client.currentTenant.createRegisterdSamlServiceProvider(serviceProvider)

        Application application = createTempApp()

        def samlPolicy = application.getSamlPolicy()
        assertNotNull(samlPolicy.identityProvider)

        def identityProviderHref = samlPolicy.identityProvider.href
        assertNotNull(identityProviderHref)

        def identityProvider = client.getResource(identityProviderHref, SamlIdentityProvider)
        assertEquals(identityProvider.href, identityProviderHref)

        def registration = client.instantiate(SamlServiceProviderRegistration)
        registration.setIdentityProvider(identityProvider).setServiceProvider(serviceProvider)
                .setDefaultRelayState("aNiceDefaultRelayState")
        createAndGetAndAssertNewRegistration(registration)

        registration.setServiceProvider(registration.getServiceProvider())
        createNewRegistrationError(registration, 10111)
    }

    @Test
    void testCreateAndUpdateSamlServiceProviderRegistrationWithMapping() {
        def acsUrl = "https://some.sp.com/saml/sso/post"
        def name = "testName"
        def serviceProvider = client.instantiate(RegisteredSamlServiceProvider)
        serviceProvider
                .setName(name)
                .setAssertionConsumerServiceUrl(acsUrl)
                .setEntityId(uniquify("urn:entity:id"))
                .setEncodedX509SigningCert(validX509Cert)

        serviceProvider = client.currentTenant.createRegisterdSamlServiceProvider(serviceProvider)

        Application application = createTempApp()

        def samlPolicy = application.getSamlPolicy()
        assertNotNull(samlPolicy.identityProvider)

        def identityProviderHref = samlPolicy.identityProvider.href
        assertNotNull(identityProviderHref)

        def identityProvider = client.getResource(identityProviderHref, SamlIdentityProvider)
        assertEquals(identityProvider.href, identityProviderHref)

        def registration = client.instantiate(SamlServiceProviderRegistration)
        registration.setIdentityProvider(identityProvider).setServiceProvider(serviceProvider)
                .setDefaultRelayState("aNiceDefaultRelayState")
        createAndGetAndAssertNewRegistration(registration)

        assertEquals(registration.identityProvider.href, identityProvider.href)
        assertEquals(registration.serviceProvider.href, serviceProvider.href)
        assertEquals(registration.defaultRelayState, "aNiceDefaultRelayState")

        registration.setStatus(SamlServiceProviderRegistrationStatus.DISABLED).setDefaultRelayState("newDefaultRelayState")
        registration.save()

        registration = client.getResource(registration.href, SamlServiceProviderRegistration)
        assertEquals(registration.status, SamlServiceProviderRegistrationStatus.DISABLED)
        assertEquals(registration.defaultRelayState, "newDefaultRelayState")

        Options options = SamlIdentityProviders.options().withSamlServiceProviderRegistrations()
        identityProvider = client.getResource(identityProviderHref, SamlIdentityProvider, options)

        assertEquals(identityProvider.samlServiceProviderRegistrations.asList().get(0).status, SamlServiceProviderRegistrationStatus.DISABLED)
        assertEquals(identityProvider.samlServiceProviderRegistrations.asList().get(0).defaultRelayState, "newDefaultRelayState")
    }

    @Test
    void testExpandSamlServiceProviderRegistrationFields() {
        def acsUrl = "https://some.sp.com/saml/sso/post"
        def name = "testName"
        def serviceProvider = client.instantiate(RegisteredSamlServiceProvider)
        serviceProvider
                .setName(name)
                .setAssertionConsumerServiceUrl(acsUrl)
                .setEntityId(uniquify("urn:entity:id"))
                .setEncodedX509SigningCert(validX509Cert)

        serviceProvider = client.currentTenant.createRegisterdSamlServiceProvider(serviceProvider)

        Application application = createTempApp()

        def samlPolicy = application.getSamlPolicy()
        assertNotNull(samlPolicy.identityProvider)

        def identityProviderHref = samlPolicy.identityProvider.href
        assertNotNull(identityProviderHref)

        def identityProvider = client.getResource(identityProviderHref, SamlIdentityProvider)
        assertEquals(identityProvider.href, identityProviderHref)

        def registration = client.instantiate(SamlServiceProviderRegistration)
        registration.setIdentityProvider(identityProvider).setServiceProvider(serviceProvider)
                .setDefaultRelayState("aNiceDefaultRelayState")
        createAndGetAndAssertNewRegistration(registration)

        assertEquals(registration.identityProvider.href, identityProvider.href)
        assertEquals(registration.serviceProvider.href, serviceProvider.href)
        assertEquals(registration.defaultRelayState, "aNiceDefaultRelayState")

        Options options = SamlServiceProviderRegistrations.options().withServiceProvider().withIdentityProvider()
        registration = client.getResource(registration.href, SamlServiceProviderRegistration, options)

        assertNotNull(registration.serviceProvider.assertionConsumerServiceUrl)
        assertNotNull(registration.identityProvider.ssoLoginEndpoint)
    }

    @Test
    void testRegistrationErrors() {
        def acsUrl = "https://some.sp.com/saml/sso/post"
        def name = "testName"
        def serviceProvider = client.instantiate(RegisteredSamlServiceProvider)
        serviceProvider
                .setName(name)
                .setAssertionConsumerServiceUrl(acsUrl)
                .setEntityId(uniquify("urn:entity:id"))
                .setEncodedX509SigningCert(validX509Cert)

        serviceProvider = client.currentTenant.createRegisterdSamlServiceProvider(serviceProvider)
        serviceProvider.getProperties().href = serviceProvider.getProperties().href + "invalid"

        Application application = createTempApp()

        def samlPolicy = application.getSamlPolicy()
        assertNotNull(samlPolicy.identityProvider)

        def identityProviderHref = samlPolicy.identityProvider.href
        assertNotNull(identityProviderHref)

        def identityProvider = client.getResource(identityProviderHref, SamlIdentityProvider)
        assertEquals(identityProvider.href, identityProviderHref)

        def registration = client.instantiate(SamlServiceProviderRegistration)
        registration.setIdentityProvider(identityProvider).setServiceProvider(serviceProvider)
                .setDefaultRelayState("aNiceDefaultRelayState")

        createNewRegistrationError(registration, 2014)
    }
}
