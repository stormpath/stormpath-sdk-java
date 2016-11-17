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

import com.stormpath.sdk.application.Application
import com.stormpath.sdk.application.webconfig.ApplicationWebConfig
import com.stormpath.sdk.query.Options
import com.stormpath.sdk.saml.*
import org.joda.time.format.DateTimeFormatter
import org.joda.time.format.ISODateTimeFormat
import org.testng.annotations.Test

import static org.testng.AssertJUnit.*
/**
 * @since 1.2.0
 */
class SamlIdentityProviderIT extends AbstractSamlIT{

    @Test
    public void samlPoliciesForNewApplicationsShouldHaveSAMLIdentityProviders() {
        getNewSamlIdentityProviderForNewApplication()
    }

    @Test
    public void samlPolicyForTheDefaulApplicationShouldHaveAnIdentityProvider() {
        getSamlIdentityProviderForDefaultApplication()
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
        def identityProvider = getSamlIdentityProviderForDefaultApplication()
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
        assertEquals(ssoLoginEndpointHref, "http://" + domainName + "/saml/sso/login")
    }

    @Test
    public void identityProviderForTheDefaultApplicationShouldHaveNullSsoLoginEndpoint() {
        def identityProvider = getSamlIdentityProviderForDefaultApplication()
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
        assertNotNull(identityProvider.registeredSamlServiceProviders)
        Options options = SamlIdentityProviders.options().withRegisteredSamlServiceProviders()
        identityProvider = client.getResource(identityProviderHref, SamlIdentityProvider, options)

        assertEquals(identityProviderHref, identityProvider.href)

        def registeredSamlServiceProviders = identityProvider.registeredSamlServiceProviders
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
        //todo: saml implement this once registrations created
    }

    @Test
    void testSuccessfulRegistrationCreation() {
        //todo: saml implement this once registrations created
    }

    @Test
    void testSuccessfulRegistrationCreationWithSpecifiedStatus() {
        //todo: saml implement this once registrations created
    }

    @Test
    void testSuccessfulRegistrationCreationWithSpecifiedDefaultRelayState() {
        //todo: saml implement this once registrations created
    }

    @Test
    void testNewRegistrationPopulatesIdentityProviderCollections() {
        //todo: saml implement this once registrations created
    }

    @Test
    void testRegistrationWithNoServiceProvider() {
        //todo: saml implement this once registrations created
    }

    @Test
    void testRegistrationWithNonMapServiceProvider() {
        //todo: saml implement this once registrations created
    }

    @Test
    void testRegistrationWithoutServiceProviderHref() {
        //todo: saml implement this once registrations created
    }

    @Test
    void testRegistrationWithInvalidServiceProviderHref() {
        //todo: saml implement this once registrations created
    }

    @Test
    void testRegistrationDeletion() {
        //todo: saml implement this once registrations created
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
        // todo saml uncomment the snippet below once cascading deletes are supported in SDK
        // Cascading deletes are not supported in SDK for now
        // Following issue will address it: https://github.com/stormpath/stormpath-sdk-java/issues/985
        //getDeletedResourceError(identityProvider.href, SamlIdentityProvider)
    }

    @Test
    void testDeletingSamlIdentityProviderDeletesServiceProvidersAndRegistrations() {
        // todo saml uncomment the snippet below once cascading deletes are supported in SDK
        // Cascading deletes are not supported in SDK for now
        // Following issue will address it: https://github.com/stormpath/stormpath-sdk-java/issues/985
    }

    @Test
    void testDeletingSamlIdentityProviderDoesNotDeleteOtherIdentityProvidersCollections() {
        // todo saml uncomment the snippet below once cascading deletes are supported in SDK
        // Cascading deletes are not supported in SDK for now
        // Following issue will address it: https://github.com/stormpath/stormpath-sdk-java/issues/985
    }

    @Test
    void testAuthNConsumptionAndValidation(){
        // todo saml implement this ?
    }

    @Test
    void testAuthNConsumptionErrors() {
        // todo saml implement this ?
    }

    @Test(enabled = false)
    void testUpdatingAlgorithmUpdatesCertificate() {
        def identityProvider = getNewSamlIdentityProviderForNewApplication()
        def oldCertHref = identityProvider.getX509SigninCert().href
        client.getResource(identityProvider.getX509SigninCert().href, com.stormpath.sdk.cert.X509SigningCert.class)
        // todo saml the above line fails, fix and continue implementing
    }

    @Test
    void testSearchingOnCollectionsInIdentityProvider() {
        // todo saml implement this once registrations created
    }

    @Test
    void testSearchIdentityProviderCollectionOfSamlServiceProviderRegistrationsByDifferentProperties() {
        // todo saml implement this once registrations created
    }
}