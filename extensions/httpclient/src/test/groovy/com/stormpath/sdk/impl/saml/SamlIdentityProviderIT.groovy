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

import com.stormpath.sdk.account.Account
import com.stormpath.sdk.application.Application
import com.stormpath.sdk.application.webconfig.ApplicationWebConfig
import com.stormpath.sdk.impl.ds.InternalDataStore
import com.stormpath.sdk.query.Options
import com.stormpath.sdk.resource.ResourceException
import com.stormpath.sdk.saml.AttributeStatementMappingRule
import com.stormpath.sdk.saml.AttributeStatementMappingRules
import com.stormpath.sdk.saml.AuthnVerification
import com.stormpath.sdk.saml.AuthnVerificationRequest
import com.stormpath.sdk.saml.CreateSamlResponseRequest
import com.stormpath.sdk.saml.RegisteredSamlServiceProvider
import com.stormpath.sdk.saml.RegisteredSamlServiceProviderList
import com.stormpath.sdk.saml.RegisteredSamlServiceProviders
import com.stormpath.sdk.saml.SamlIdentityProvider
import com.stormpath.sdk.saml.SamlIdentityProviderMetadata
import com.stormpath.sdk.saml.SamlIdentityProviderStatus
import com.stormpath.sdk.saml.SamlIdentityProviders
import com.stormpath.sdk.saml.SamlPolicy
import com.stormpath.sdk.saml.SamlResponse
import com.stormpath.sdk.saml.SamlServiceProviderRegistration
import com.stormpath.sdk.saml.SamlServiceProviderRegistrationList
import com.stormpath.sdk.saml.SamlServiceProviderRegistrationStatus
import com.stormpath.sdk.saml.SamlServiceProviderRegistrations
import org.joda.time.format.DateTimeFormatter
import org.joda.time.format.ISODateTimeFormat
import org.testng.annotations.AfterMethod
import org.testng.annotations.Test

import javax.xml.bind.DatatypeConverter
import java.text.DateFormat
import java.text.SimpleDateFormat

import static org.testng.Assert.assertEquals
import static org.testng.Assert.assertNotEquals
import static org.testng.Assert.assertNull
import static org.testng.Assert.assertNotNull
import static org.testng.Assert.assertTrue

/**
 * @since 1.3.0
 */
class SamlIdentityProviderIT extends AbstractSamlIT {

    @AfterMethod
    public void cleanUp() {
        def list = client.getResource("${client.currentTenant.href}/registeredSamlServiceProviders", RegisteredSamlServiceProviderList)
        List<RegisteredSamlServiceProvider> collection = list.asList()
        for (RegisteredSamlServiceProvider registeredSamlServiceProvider : collection) {
            registeredSamlServiceProvider.delete()
        }
    }

    @Test
    public void samlPoliciesForNewApplicationsShouldHaveSAMLIdentityProviders() {
        getNewSamlIdentityProviderForNewApplication()
    }

    @Test
    public void samlPolicyForTheAdministrorsShouldHaveAnIdentityProvider() {
        getSamlIdentityProviderForAdministratorsApplication()
    }

    @Test
    public void identityProvidersAreEnabledByDefault() {
        def identityProvider = getNewSamlIdentityProviderForNewApplication()
        assertNotNull(identityProvider.status)
        assertEquals(identityProvider.status, SamlIdentityProviderStatus.ENABLED)
    }

    @Test
    public void identityProvidersHaveEmptyAttributeStatementMappings() {
        def identityProvider = getNewSamlIdentityProviderForNewApplication()
        assertNotNull(identityProvider.attributeStatementMappingRules)
        def mappingRules = client.getResource(identityProvider.attributeStatementMappingRules.href, AttributeStatementMappingRules)
        assertEquals(mappingRules.items.size(), 0)
    }

    @Test
    public void testUpdateIdentityProviderAttributeStatementMappings() {
        def identityProvider = getNewSamlIdentityProviderForNewApplication()
        def attributeStatementMappingRules = identityProvider.attributeStatementMappingRules
        assertNotNull(attributeStatementMappingRules)

        attributeStatementMappingRules = client.getResource(attributeStatementMappingRules.href, AttributeStatementMappingRules)
        AttributeStatementMappingRule rule1 = new DefaultAttributeStatementMappingRule("User.FirstName", null, new HashSet(["givenName"]))
        AttributeStatementMappingRule rule2 = new DefaultAttributeStatementMappingRule("User.LastName", null, new HashSet(["surname"]))
        AttributeStatementMappingRule rule3 = new DefaultAttributeStatementMappingRule("User.CustomField", null, new HashSet(["customData.someField"]))

        Set<AttributeStatementMappingRule> rules = new HashSet<>(3);
        rules.add(rule1);
        rules.add(rule2);
        rules.add(rule3);
        attributeStatementMappingRules.setItems(rules)

        attributeStatementMappingRules.save()

        attributeStatementMappingRules = client.getResource(attributeStatementMappingRules.href, AttributeStatementMappingRules)
        assertEquals(attributeStatementMappingRules.items.size(), 3)

        Options options = SamlIdentityProviders.options().withAttributeMappingRules()
        identityProvider = client.getResource(identityProvider.href, SamlIdentityProvider, options)
        assertEquals(identityProvider.attributeStatementMappingRules.items.size(), 3)
    }

    @Test
    public void testUpdateIdentityProviderAttributeStatementMappingsError() {
        def identityProvider = getNewSamlIdentityProviderForNewApplication()
        def attributeStatementMappingRules = identityProvider.attributeStatementMappingRules
        assertNotNull(attributeStatementMappingRules)

        attributeStatementMappingRules = client.getResource(attributeStatementMappingRules.href, AttributeStatementMappingRules)
        AttributeStatementMappingRule rule1 = new DefaultAttributeStatementMappingRule("User.FirstName", null, new HashSet(["givenName"]))
        AttributeStatementMappingRule rule2 = new DefaultAttributeStatementMappingRule("User.LastName", null, new HashSet(["notAnAccountField"]))
        AttributeStatementMappingRule rule3 = new DefaultAttributeStatementMappingRule("User.CustomField", null, new HashSet(["customData.someField"]))

        Set<AttributeStatementMappingRule> rules = new HashSet<>(3);
        rules.add(rule1);
        rules.add(rule2);
        rules.add(rule3);
        attributeStatementMappingRules.setItems(rules)

        updatedSaveableError(attributeStatementMappingRules, 2002)
    }

    @Test
    public void identityProviderForStormpathApplicationShouldBeDisabled() {
        def identityProvider = getSamlIdentityProviderForAdministratorsApplication()
        assertNotNull(identityProvider.status)
        assertEquals(identityProvider.status, SamlIdentityProviderStatus.DISABLED)
    }

    @Test
    void identityProvidersForNonDefaultApplicationsShouldHaveTheCorrectSsoLoginEndpoint() {
        Application application = createTempApp()
        def identityProvider = getSamlIdentityProviderForApplication(application)

        def ssoLoginEndpointHref = identityProvider.ssoLoginEndpoint.href
        def webConfigHref = application.webConfig.href
        def webConfig = client.getResource(webConfigHref, ApplicationWebConfig)
        def domainName = webConfig.domainName
        URI baseUri = new URI(((InternalDataStore) client.getDataStore()).getBaseUrl())
        assertEquals(ssoLoginEndpointHref, baseUri.getScheme() + "://" + domainName + "/saml/sso")
    }

    @Test
    public void identityProviderForAdministratorApplicationShouldHaveNullSsoLoginEndpoint() {
        def identityProvider = getSamlIdentityProviderForAdministratorsApplication()
        assertNull(identityProvider.ssoLoginEndpoint)
    }

    @Test
    void identityProvidersShouldHaveCreatedAtAndModifiedAtTimestamps() {
        DateTimeFormatter dateTimeFormatter = ISODateTimeFormat.dateTime();
        def identityProvider = getNewSamlIdentityProviderForNewApplication()
        assertNotNull(identityProvider.createdAt)
        dateTimeFormatter.parseMillis(new org.joda.time.DateTime(identityProvider.createdAt).toString())
        assertNotNull(identityProvider.modifiedAt)
        dateTimeFormatter.parseMillis(new org.joda.time.DateTime(identityProvider.modifiedAt).toString())
    }

    @Test
    void newIdentityProvidersShouldHaveRSASHA256AsTheSignatureAlgorithm() {
        def identityProvider = getNewSamlIdentityProviderForNewApplication()
        assertNotNull(identityProvider.signatureAlgorithm)
        assertEquals(identityProvider.signatureAlgorithm, "RSA-SHA256")
    }

    @Test
    void identityProvidersShouldHaveX509Certs() {
        def identityProvider = getNewSamlIdentityProviderForNewApplication()
        assertNotNull(identityProvider.getX509SigninCert())
        def x509SigningCertificateHref = identityProvider.getX509SigninCert().href
        assertTrue(x509SigningCertificateHref.startsWith(baseUrl + "/x509certificates"))
    }

    @Test
    void identityProvidersShouldHaveMetadata() {
        def identityProvider = getNewSamlIdentityProviderForNewApplication()
        assertNotNull(identityProvider.getSamlIdentityProviderMetadata())
        def metadataHref = identityProvider.getSamlIdentityProviderMetadata().href
        assertTrue(metadataHref.startsWith(baseUrl + "/samlIdentityProviderMetadatas"))
    }

    @Test
    void identityProvidersShouldHaveRegisteredSamlServiceProvidersCollection() {
        def identityProvider = getNewSamlIdentityProviderForNewApplication()
        def identityProviderHref = identityProvider.href
        assertNotNull(identityProvider.getRegisteredSamlServiceProviders(RegisteredSamlServiceProviders.criteria().orderByDescription()))
        Options options = SamlIdentityProviders.options().withRegisteredSamlServiceProviders()
        identityProvider = client.getResource(identityProviderHref, SamlIdentityProvider, options)

        assertEquals(identityProviderHref, identityProvider.href)

        def registeredSamlServiceProviders = identityProvider.getRegisteredSamlServiceProviders(RegisteredSamlServiceProviders.criteria().orderByName())
        assertEquals(registeredSamlServiceProviders.href, identityProviderHref + "/registeredSamlServiceProviders")

        assertEquals(registeredSamlServiceProviders.offset, 0)
        assertEquals(registeredSamlServiceProviders.size, 0)
        assertEquals(registeredSamlServiceProviders.limit, 25)

        def items = registeredSamlServiceProviders.getProperties().items
        assertNotNull(items)
        assertEquals(items.size, 0)
    }

    @Test
    void identityProvidersShouldHaveSamlServiceProviderRegistrationsCollection() {
        def identityProvider = getNewSamlIdentityProviderForNewApplication()
        assertNotNull(identityProvider.getSamlServiceProviderRegistrations(SamlServiceProviderRegistrations.criteria().orderByStatus()))

        def identityProviderHref = identityProvider.href
        Options options = SamlIdentityProviders.options().withSamlServiceProviderRegistrations()
        identityProvider = client.getResource(identityProviderHref, SamlIdentityProvider, options)

        assertEquals(identityProvider.href, identityProviderHref)

        def samlServiceProviderRegistrations = identityProvider.getSamlServiceProviderRegistrations(SamlServiceProviderRegistrations.criteria().orderByDefaultRelayState())
        assertEquals(samlServiceProviderRegistrations.href, identityProviderHref + "/samlServiceProviderRegistrations")

        assertEquals(samlServiceProviderRegistrations.offset, 0)
        assertEquals(samlServiceProviderRegistrations.size, 0)
        assertEquals(samlServiceProviderRegistrations.limit, 25)

        def items = samlServiceProviderRegistrations.getProperties().items
        assertEquals(items.size, 0)
    }

    @Test
    void testSuccessfulRegistrationCreation() {
        def identityProvider = getNewSamlIdentityProviderForNewApplication()
        def serviceProvider = client.instantiate(RegisteredSamlServiceProvider)
        serviceProvider
                .setName("testName")
                .setAssertionConsumerServiceUrl("https://some.sp.com/saml/sso/post")
                .setEntityId(uniquify("urn:entity:id"))
                .setEncodedX509SigningCert(validX509Cert)

        serviceProvider = client.currentTenant.createRegisterdSamlServiceProvider(serviceProvider)
        def registration = client.instantiate(SamlServiceProviderRegistration)
        registration.setIdentityProvider(identityProvider).setServiceProvider(serviceProvider)

        registration = createAndGetAndAssertNewRegistration(registration)

        assertEquals(registration.status, SamlServiceProviderRegistrationStatus.ENABLED)
    }

    @Test
    void testSuccessfulRegistrationCreationWithSpecifiedStatus() {
        def identityProvider = getNewSamlIdentityProviderForNewApplication()
        def serviceProvider = client.instantiate(RegisteredSamlServiceProvider)
        serviceProvider
                .setName("testName")
                .setAssertionConsumerServiceUrl("https://some.sp.com/saml/sso/post")
                .setEntityId(uniquify("urn:entity:id"))
                .setEncodedX509SigningCert(validX509Cert)

        serviceProvider = client.currentTenant.createRegisterdSamlServiceProvider(serviceProvider)
        def registration = client.instantiate(SamlServiceProviderRegistration)
        registration.setIdentityProvider(identityProvider).setServiceProvider(serviceProvider).setStatus(SamlServiceProviderRegistrationStatus.DISABLED)
        registration = createAndGetAndAssertNewRegistration(registration)

        assertEquals(registration.status, SamlServiceProviderRegistrationStatus.DISABLED)
    }

    @Test
    void testSuccessfulRegistrationCreationWithSpecifiedDefaultRelayState() {
        def identityProvider = getNewSamlIdentityProviderForNewApplication()
        def serviceProvider = client.instantiate(RegisteredSamlServiceProvider)
        serviceProvider
                .setName("testName")
                .setAssertionConsumerServiceUrl("https://some.sp.com/saml/sso/post")
                .setEntityId(uniquify("urn:entity:id"))
                .setEncodedX509SigningCert(validX509Cert)

        serviceProvider = client.currentTenant.createRegisterdSamlServiceProvider(serviceProvider)
        def registration = client.instantiate(SamlServiceProviderRegistration)
        registration.setIdentityProvider(identityProvider).setServiceProvider(serviceProvider).setDefaultRelayState("heho")
        registration = createAndGetAndAssertNewRegistration(registration)

        assertEquals(registration.defaultRelayState, "heho")
    }

    @Test
    void testNewRegistrationPopulatesIdentityProviderCollections() {
        def identityProvider = getNewSamlIdentityProviderForNewApplication()
        def identityProviderHref = identityProvider.href

        def serviceProvider = client.instantiate(RegisteredSamlServiceProvider)
        serviceProvider
                .setName("testName")
                .setAssertionConsumerServiceUrl("https://some.sp.com/saml/sso/post")
                .setEntityId(uniquify("urn:entity:id"))
                .setEncodedX509SigningCert(validX509Cert)

        serviceProvider = client.currentTenant.createRegisterdSamlServiceProvider(serviceProvider)
        def registration = client.instantiate(SamlServiceProviderRegistration)
        registration.setIdentityProvider(identityProvider).setServiceProvider(serviceProvider).setDefaultRelayState("heho")
        registration = createAndGetAndAssertNewRegistration(registration)

        Options options = SamlIdentityProviders.options().withSamlServiceProviderRegistrations().withRegisteredSamlServiceProviders()
        identityProvider = client.getResource(identityProviderHref, SamlIdentityProvider, options)

        def samlServiceProviderRegistrations = identityProvider.samlServiceProviderRegistrations
        assertEquals(samlServiceProviderRegistrations.href, identityProviderHref + "/samlServiceProviderRegistrations")

        assertEquals(samlServiceProviderRegistrations.offset, 0)
        assertEquals(samlServiceProviderRegistrations.size, 1)
        assertEquals(samlServiceProviderRegistrations.limit, 25)

        def registrationItems = samlServiceProviderRegistrations.getProperties().items
        assertEquals(registrationItems.size, 1)
        def registrationInCollection = registrationItems[0]
        assertEquals(registrationInCollection, registration.getProperties())

        def registeredServiceProviders = identityProvider.registeredSamlServiceProviders
        assertEquals(registeredServiceProviders.href, identityProviderHref + "/registeredSamlServiceProviders")

        assertEquals(registeredServiceProviders.offset, 0)
        assertEquals(registeredServiceProviders.size, 1)
        assertEquals(registeredServiceProviders.limit, 25)

        def serviceProviderItems = registeredServiceProviders.getProperties().items
        assertEquals(serviceProviderItems.size, 1)
        def serviceProviderInCollection = serviceProviderItems[0]
        assertEquals(serviceProviderInCollection, serviceProvider.getProperties())
    }

    @Test
    void testRegistrationWithNoServiceProvider() {
        def identityProvider = getNewSamlIdentityProviderForNewApplication()
        def registration = client.instantiate(SamlServiceProviderRegistration)
        registration.setIdentityProvider(identityProvider)
        createNewRegistrationError(registration, 2002)
    }

    @Test
    void testRegistrationWithInvalidServiceProviderHref() {
        def identityProvider = getNewSamlIdentityProviderForNewApplication()
        def serviceProvider = client.instantiate(RegisteredSamlServiceProvider)
        serviceProvider.getProperties().href = "bla"
        def registration = client.instantiate(SamlServiceProviderRegistration)
        registration.setIdentityProvider(identityProvider)
        registration.setServiceProvider(serviceProvider)
        createNewRegistrationError(registration, 2014)
    }

    @Test
    void testRegistrationDeletion() {
        def identityProvider = getNewSamlIdentityProviderForNewApplication()
        def identityProviderHref = identityProvider.href
        def serviceProvider = client.instantiate(RegisteredSamlServiceProvider)
        serviceProvider
                .setName("testName")
                .setAssertionConsumerServiceUrl("https://some.sp.com/saml/sso/post")
                .setEntityId(uniquify("urn:entity:id"))
                .setEncodedX509SigningCert(validX509Cert)

        serviceProvider = client.currentTenant.createRegisterdSamlServiceProvider(serviceProvider)
        def registration = client.instantiate(SamlServiceProviderRegistration)
        registration.setIdentityProvider(identityProvider).setServiceProvider(serviceProvider)

        registration = createAndGetAndAssertNewRegistration(registration)

        registration.delete()

        Throwable e = null;
        try {
            client.getResource(registration.href, SamlServiceProviderRegistration)
        }
        catch (ResourceException re) {
            e = re
            assertEquals(re.status, 404)
            assertEquals(re.getCode(), 404)
        }

        assertTrue(e instanceof ResourceException)

        Options options = SamlIdentityProviders.options().withRegisteredSamlServiceProviders().withSamlServiceProviderRegistrations()
        identityProvider = client.getResource(identityProviderHref, SamlIdentityProvider, options)

        def samlServiceProviderRegistrations = identityProvider.samlServiceProviderRegistrations
        assertEquals(samlServiceProviderRegistrations.href, identityProviderHref + "/samlServiceProviderRegistrations")

        assertEquals(samlServiceProviderRegistrations.offset, 0)
        assertEquals(samlServiceProviderRegistrations.size, 0)
        assertEquals(samlServiceProviderRegistrations.limit, 25)

        def registrationItems = samlServiceProviderRegistrations.getProperties().items
        assertEquals(registrationItems.size, 0)

        def registeredSamlServiceProviders = identityProvider.registeredSamlServiceProviders
        assertEquals(registeredSamlServiceProviders.href, identityProviderHref + "/registeredSamlServiceProviders")

        assertEquals(registeredSamlServiceProviders.offset, 0)
        assertEquals(registeredSamlServiceProviders.size, 0)
        assertEquals(registeredSamlServiceProviders.limit, 25)

        assertNotNull(registeredSamlServiceProviders.getProperties().items)

        def serviceProviderItems = registeredSamlServiceProviders.getProperties().items
        assertEquals(serviceProviderItems.size, 0)
    }

    @Test
    void testUpdateSamlIdentityProvider() {
        Application application = createTempApp()
        def identityProvider = getSamlIdentityProviderForApplication(application)

        identityProvider.setStatus(SamlIdentityProviderStatus.DISABLED)
        identityProvider.save()

        //validate that modifications are persisted
        identityProvider = client.getResource(identityProvider.href, SamlIdentityProvider)
        assertEquals(identityProvider.status, SamlIdentityProviderStatus.DISABLED)
    }

    @Test
    void testDeletingApplicationDeletesSamlIdentityProvider() {
        Application application = createTempApp()
        def identityProvider = getSamlIdentityProviderForApplication(application)

        application.delete()

        getDeletedResourceError(application.href, Application)
        // todo: saml uncomment the snippet below once cascading deletes are supported in SDK
        // Cascading deletes are not supported in SDK for now
        // Following issue will address it: https://github.com/stormpath/stormpath-sdk-java/issues/985
        //getDeletedResourceError(identityProvider.href, SamlIdentityProvider)
    }

    @Test
    void testDeletingSamlIdentityProviderDeletesServiceProvidersAndRegistrations() {
        // todo: saml uncomment the snippet below once cascading deletes are supported in SDK
        // Cascading deletes are not supported in SDK for now
        // Following issue will address it: https://github.com/stormpath/stormpath-sdk-java/issues/985
    }

    @Test
    void testDeletingSamlIdentityProviderDoesNotDeleteOtherIdentityProvidersCollections() {
        // todo: saml uncomment the snippet below once cascading deletes are supported in SDK
        // Cascading deletes are not supported in SDK for now
        // Following issue will address it: https://github.com/stormpath/stormpath-sdk-java/issues/985
    }

    @Test
    void testUpdatingAlgorithmUpdatesCertificateAndFingerprint() {
        def identityProvider = getNewSamlIdentityProviderForNewApplication()
        assertNotNull(identityProvider.shaFingerprint)

        def oldFingerprint = identityProvider.shaFingerprint
        def oldCertHref = identityProvider.getX509SigninCert().href

        identityProvider.setSignatureAlgorithm("RSA-SHA1").save()

        identityProvider = client.getResource(identityProvider.href, SamlIdentityProvider)

        assertNotEquals(oldFingerprint, identityProvider.shaFingerprint)
        assertNotEquals(oldCertHref, identityProvider.getX509SigninCert().href)

        def metedata = client.getResource(identityProvider.samlIdentityProviderMetadata.href, SamlIdentityProviderMetadata)

        assertNotEquals(oldCertHref, metedata.getX509SigninCert().href)
    }

    @Test
    void testSearchingOnCollectionsInIdentityProvider() {
        def serviceProvider = client.instantiate(RegisteredSamlServiceProvider)
        serviceProvider
                .setName("testName")
                .setAssertionConsumerServiceUrl("https://some.sp.com/saml/sso/post")
                .setEntityId(uniquify("urn:entity:id"))
                .setEncodedX509SigningCert(validX509Cert)
        def registeredSamlServiceProviderReturned = client.currentTenant.createRegisterdSamlServiceProvider(serviceProvider)

        def application = createTempApp()
        def samlPolicy = application.samlPolicy
        samlPolicy = client.getResource(samlPolicy.href, SamlPolicy)

        assertNotNull(samlPolicy.getIdentityProvider())

        def identityProviderHref = samlPolicy.getIdentityProvider().href
        assertNotNull(identityProviderHref)

        def identityProvider = client.getResource(identityProviderHref, SamlIdentityProvider)
        assertEquals(identityProvider.href, identityProviderHref)

        def registration = client.instantiate(SamlServiceProviderRegistration)
        registration.setDefaultRelayState("aNiceDefaultRelayState")
        registration.setServiceProvider(registeredSamlServiceProviderReturned)
        registration.setIdentityProvider(identityProvider)
        registration = createAndGetAndAssertNewRegistration(registration)

        assertEquals(registration.getIdentityProvider().href, identityProvider.href)
        assertEquals(registration.getProperties()."serviceProvider".get("href"), serviceProvider.href)
        assertEquals(registration.getDefaultRelayState(), "aNiceDefaultRelayState")

        def list = client.currentTenant.getRegisterdSamlServiceProviders(RegisteredSamlServiceProviders.where(RegisteredSamlServiceProviders.name().eqIgnoreCase("testName")))
        assertEquals(list.size as int, 1)

        list = identityProvider.getSamlServiceProviderRegistrations(SamlServiceProviderRegistrations.where(SamlServiceProviderRegistrations.status().eq(SamlServiceProviderRegistrationStatus.ENABLED)))
        assertEquals(list.size as int, 1)
    }

    @Test
    void testSearchIdentityProviderCollectionOfSamlServiceProviderRegistrationsByDifferentProperties() {
        def application = createTempApp()
        def identityProvider = getSamlIdentityProviderForApplication(application)
        def identityProviderHref = identityProvider.href

        def serviceProvider = client.instantiate(RegisteredSamlServiceProvider)
        serviceProvider
                .setName("testName")
                .setAssertionConsumerServiceUrl("https://some.sp.com/saml/sso/post")
                .setEntityId(uniquify("urn:entity:id"))
                .setEncodedX509SigningCert(validX509Cert)
        def registeredSamlServiceProviderReturned = client.currentTenant.createRegisterdSamlServiceProvider(serviceProvider)

        def registration = client.instantiate(SamlServiceProviderRegistration)
        registration.setDefaultRelayState("aNiceDefaultRelayState")
        registration.setServiceProvider(registeredSamlServiceProviderReturned)
        registration.setIdentityProvider(identityProvider)
        createAndGetAndAssertNewRegistration(registration)

        def registrationList = client.getResource(identityProviderHref + "/samlServiceProviderRegistrations", SamlServiceProviderRegistrationList)
        assertEquals(registrationList.size, 1)

        registrationList = identityProvider.getSamlServiceProviderRegistrations(SamlServiceProviderRegistrations.where(SamlServiceProviderRegistrations.status().eq(SamlServiceProviderRegistrationStatus.ENABLED)))
        assertEquals(registrationList.size, 1)

        registrationList = identityProvider.getSamlServiceProviderRegistrations(SamlServiceProviderRegistrations.where(SamlServiceProviderRegistrations.status().eq(SamlServiceProviderRegistrationStatus.DISABLED)))
        assertEquals(registrationList.size, 0)

        registrationList = identityProvider.getSamlServiceProviderRegistrations(SamlServiceProviderRegistrations.where(SamlServiceProviderRegistrations.defaultRelayState().eq("aNice*")))
        assertEquals(registrationList.size, 1)

        registrationList = identityProvider.getSamlServiceProviderRegistrations(SamlServiceProviderRegistrations.where(SamlServiceProviderRegistrations.defaultRelayState().eq("aNotNice*")))
        assertEquals(registrationList.size, 0)
    }

    @Test
    void testAuthnVerification() {
        def identityProvider = getNewSamlIdentityProviderForNewApplication()

        String cannedX509Cert = '''-----BEGIN CERTIFICATE-----
MIIDBjCCAe4CCQDkkfBwuV3jqTANBgkqhkiG9w0BAQUFADBFMQswCQYDVQQGEwJV
UzETMBEGA1UECBMKU29tZS1TdGF0ZTEhMB8GA1UEChMYSW50ZXJuZXQgV2lkZ2l0
cyBQdHkgTHRkMB4XDTE1MTAxNDIyMDUzOFoXDTE2MTAxMzIyMDUzOFowRTELMAkG
A1UEBhMCVVMxEzARBgNVBAgTClNvbWUtU3RhdGUxITAfBgNVBAoTGEludGVybmV0
IFdpZGdpdHMgUHR5IEx0ZDCCASIwDQYJKoZIhvcNAQEBBQADggEPADCCAQoCggEB
ALuZBSfp4ecigQGFL6zawVi9asVstXHy3cpj3pPXjDx5Xj4QlbBL7KbZhVd4B+j3
Paacetpn8N0g06sYe1fIeddZE7PZeD2vxTLglriOCB8exH9ZAcYNHIGy3pMFdXHY
lS7xXYWb+BNLVU7ka3tJnceDjhviAjICzQJs0JXDVQUeYxB80a+WtqJP+ZMbAxvA
QbPzkcvK8CMctRSRqKkpC4gWSxUAJOqEmyvQVQpaLGrI2zFroD2Bgt0cZzBHN5tG
wC2qgacDv16qyY+90rYgX/WveA+MSd8QKGLcpPlEzzVJp7Z5Boc3T8wIR29jaDtR
cK4bWQ2EGLJiJ+Vql5qaOmsCAwEAATANBgkqhkiG9w0BAQUFAAOCAQEAmCND/4tB
+yVsIZBAQgul/rK1Qj26FlyO0i0Rmm2OhGRhrd9JPQoZ+xCtBixopNICKG7kvUeQ
Sk8Bku6rQ3VquxKtqAjNFeiLykd9Dn2HUOGpNlRcpzFXHtX+L1f34lMaT54qgWAh
PgWkzh8xo5HT4M83DaG+HT6BkaVAQwIlJ26S/g3zJ00TrWRP2E6jlhR5KHLN+8eE
D7/ENlqO5ThU5uX07/Bf+S0q5NK0NPuy0nO2w064kHdIX5/O64ktT1/MgWBV6yV7
mg1osHToeo4WXGz2Yo6+VFMM3IKRqMDbkR7N4cNKd1KvEKrMaRE7vC14H/G5NSOh
yl85oFHAdkguTA==
-----END CERTIFICATE-----
'''

        def serviceProvider = client.instantiate(RegisteredSamlServiceProvider)
        serviceProvider
                .setName("testName")
                .setAssertionConsumerServiceUrl("http://localhost:9191/v1/directories/58RbxGTCdqH9L1ddRxBquy/saml/sso/post")
                .setEntityId("http://localhost:9191/v1/directories/58RbxGTCdqH9L1ddRxBquy")
                .setEncodedX509SigningCert(cannedX509Cert)


        def registeredSamlServiceProvider = client.currentTenant.createRegisterdSamlServiceProvider(serviceProvider)

        def registration = client.instantiate(SamlServiceProviderRegistration)
        registration.setDefaultRelayState("aNiceDefaultRelayState")
        registration.setServiceProvider(registeredSamlServiceProvider)
        registration.setIdentityProvider(identityProvider)
        createAndGetAndAssertNewRegistration(registration)

        String cannedRelayState = "eyJ0aWQiOiI3QXZCMWJqZXJRWTRVM0JzQWtvOEYyIiwic3R0IjoiYXNzZXJ0aW9uIiwiYWxnIjoiSFMyNTYifQ.eyJpcnQiOiIyMmJhYjc5OS01OTM2LTQ2ZjktOTMwNy1mYjM2ZGIxOTBkMmYiLCJhcGlfa2lkIjoiMVNFNUxIODBVU080MEVYTVQ1SVEyVVRXSCIsImNiX3VyaSI6Imh0dHA6Ly9sb2NhbGhvc3Q6OTE5MS91aTIvdmlld3Mvc2FtbC10ZXN0LWNhbGxiYWNrLmh0bWwiLCJhc2giOiJodHRwOi8vbG9jYWxob3N0OjkxOTEvdjEvZGlyZWN0b3JpZXMvNThSYnhHVENkcUg5TDFkZFJ4QnF1eSIsImFwcF9ocmVmIjoiaHR0cDovL2xvY2FsaG9zdDo5MTkxL3YxL2FwcGxpY2F0aW9ucy83RFVXNHlzNU5mM0oyd09NV29SRUZvIiwianRpIjoic3A2UEJLVm5ZZW4xS3ZYN1F0UThQS0RjIiwiaXNzIjoiaHR0cDovL2xvY2FsaG9zdDo5MTkxL3YxL2FwcGxpY2F0aW9ucy83RFVXNHlzNU5mM0oyd09NV29SRUZvL3NhbWwvc3NvL2lkcFJlZGlyZWN0IiwiYXVkIjoiaHR0cDovL2xvY2FsaG9zdDo5MTkxL3YxL2RpcmVjdG9yaWVzLzU4UmJ4R1RDZHFIOUwxZGRSeEJxdXkvc2FtbC9zc28vcG9zdCJ9.fG4Ffp2Udzr2xfEzNrVGzbjc02asVf2UkuzpOLMphHo"

        AuthnVerificationRequest authnVerificationRequest = client.instantiate(AuthnVerificationRequest.class);
        authnVerificationRequest.setSamlRequest("pJJBc9owEIXv/RUe3W0hd0hBg50xMGkYaOOAk2l7U+RNUEaWjFZ2SX59HQNNcmguvUq77715307O95UOWnCorEkIiwYkACNtqcxDQm6Ki3BEztNPExSVjmueNX5r1rBrAH2QIYLz3d7MGmwqcBtwrZJws14lZOt9zSnVVgq9tej5mI0ZbRktlQPprVOAdDha3+2/FrNydzlesbJc76e75om+mFFES+tukQTzzkwZ4fuER91nENo2GD6Cj0RdYyRVhJ1qVQu/jZT9q0GCC+sk9METci80AgkW84RgfZZPl7fmJxi2bH98ufbXo3w5l90v5gJRtfA6j9jAwqAXxickHrCzkMUh+1zEjA+HnMVRPBr8IkHurLfS6qkyhwIbZ7gVqJAbUQFyL/km+7bicTTgd4ch5JdFkYf51aYgwe0JRPwCokNjkB+q/1irPhqT9ECK94ndW4WPBcSJJUn/g9yEvnVPT1fzvbNbzHOrlXwKMq3t75kD4bt+vWugJ1QJ/++ALGL9iyrD+36UQyWUzsrSASKh6dH3/X2mfwAAAP//")
                .setRelayState(cannedRelayState)
                .setQueryString("SAMLRequest=pJJBc9owEIXv/RUe3W0hd0hBg50xMGkYaOOAk2l7U+RNUEaWjFZ2SX59HQNNcmguvUq77715307O95UOWnCorEkIiwYkACNtqcxDQm6Ki3BEztNPExSVjmueNX5r1rBrAH2QIYLz3d7MGmwqcBtwrZJws14lZOt9zSnVVgq9tej5mI0ZbRktlQPprVOAdDha3+2/FrNydzlesbJc76e75om+mFFES+tukQTzzkwZ4fuER91nENo2GD6Cj0RdYyRVhJ1qVQu/jZT9q0GCC+sk9METci80AgkW84RgfZZPl7fmJxi2bH98ufbXo3w5l90v5gJRtfA6j9jAwqAXxickHrCzkMUh+1zEjA+HnMVRPBr8IkHurLfS6qkyhwIbZ7gVqJAbUQFyL/km+7bicTTgd4ch5JdFkYf51aYgwe0JRPwCokNjkB+q/1irPhqT9ECK94ndW4WPBcSJJUn/g9yEvnVPT1fzvbNbzHOrlXwKMq3t75kD4bt+vWugJ1QJ/++ALGL9iyrD+36UQyWUzsrSASKh6dH3/X2mfwAAAP//&RelayState=eyJ0aWQiOiI3QXZCMWJqZXJRWTRVM0JzQWtvOEYyIiwic3R0IjoiYXNzZXJ0aW9uIiwiYWxnIjoiSFMyNTYifQ.eyJpcnQiOiIyMmJhYjc5OS01OTM2LTQ2ZjktOTMwNy1mYjM2ZGIxOTBkMmYiLCJhcGlfa2lkIjoiMVNFNUxIODBVU080MEVYTVQ1SVEyVVRXSCIsImNiX3VyaSI6Imh0dHA6Ly9sb2NhbGhvc3Q6OTE5MS91aTIvdmlld3Mvc2FtbC10ZXN0LWNhbGxiYWNrLmh0bWwiLCJhc2giOiJodHRwOi8vbG9jYWxob3N0OjkxOTEvdjEvZGlyZWN0b3JpZXMvNThSYnhHVENkcUg5TDFkZFJ4QnF1eSIsImFwcF9ocmVmIjoiaHR0cDovL2xvY2FsaG9zdDo5MTkxL3YxL2FwcGxpY2F0aW9ucy83RFVXNHlzNU5mM0oyd09NV29SRUZvIiwianRpIjoic3A2UEJLVm5ZZW4xS3ZYN1F0UThQS0RjIiwiaXNzIjoiaHR0cDovL2xvY2FsaG9zdDo5MTkxL3YxL2FwcGxpY2F0aW9ucy83RFVXNHlzNU5mM0oyd09NV29SRUZvL3NhbWwvc3NvL2lkcFJlZGlyZWN0IiwiYXVkIjoiaHR0cDovL2xvY2FsaG9zdDo5MTkxL3YxL2RpcmVjdG9yaWVzLzU4UmJ4R1RDZHFIOUwxZGRSeEJxdXkvc2FtbC9zc28vcG9zdCJ9.fG4Ffp2Udzr2xfEzNrVGzbjc02asVf2UkuzpOLMphHo&SigAlg=http://www.w3.org/2001/04/xmldsig-more#rsa-sha256&Signature=bv/bFoVCuOlrB8OyrkNMjpDh9s6g2+zppZGeSOd3lSOPzfdJIv7/8/e1S3I+0jDHGSFceFYj1q7HtFMyKxh2VNAeBnt1FjZ3SwbifZzoV5TwFSThTXv2wWiEYPbw9HETv3ol3xthDfFwNy+7mc862XEUwh8vmoilCHdxOJXXTzvuGF0dpF6a4QzHZT4og4GBd9uBTl1u4IKejGzP0CpoBlDrBS0TVuyvJz2kc5CC5NM0Q2LK4WMb3J4HCxZ8SbLBL9O65YQOAzNJwmLRQGhgfeS63a5x0eMtZJAOzAjAaoFOaCSAwsUmQtd5tlGmejSsKQOTeBYe8JMRkSjZ6XnHmw==")

        AuthnVerification authnVerification = identityProvider.createAuthnVerification(authnVerificationRequest)
        assertEquals(authnVerification.relayState, cannedRelayState)
        assertEquals(authnVerification.serviceProvider.href, registeredSamlServiceProvider.href)
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"))
        Date cannedDate = dateFormat.parse("2016-12-13T21:55:12.280Z")
        assertEquals(authnVerification.authnIssueInstant, cannedDate)
    }

    @Test
    void testSamlResponse() {
        def identityProvider = getNewSamlIdentityProviderForNewApplication()

        Account account = client.getAccounts().iterator().next()

        def serviceProvider = client.instantiate(RegisteredSamlServiceProvider)
        serviceProvider
                .setName("testName")
                .setAssertionConsumerServiceUrl("https://some.sp.com/saml/sso/post")
                .setEntityId(uniquify("urn:entity:id"))
                .setEncodedX509SigningCert(validX509Cert)
        def registeredSamlServiceProvider = client.currentTenant.createRegisterdSamlServiceProvider(serviceProvider)

        String requestId = "blargle"

        CreateSamlResponseRequest createSamlResponseRequest = client.instantiate(CreateSamlResponseRequest.class)
        createSamlResponseRequest.setAccount(account)
                createSamlResponseRequest.setAuthnIssueInstant(new Date())
        createSamlResponseRequest.setRequestId(requestId)
                .setServiceProvider(registeredSamlServiceProvider)

        SamlResponse samlResponse = identityProvider.createSamlResponse(createSamlResponseRequest)
        String base64EncodedXml = samlResponse.getValue()

        String xml = new String(DatatypeConverter.parseBase64Binary(base64EncodedXml))

        assertTrue(xml.contains(account.email))
        assertTrue(xml.contains("InResponseTo=\"" + requestId + "\""))
    }

}