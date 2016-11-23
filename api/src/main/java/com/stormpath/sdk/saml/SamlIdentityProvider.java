/*
* Copyright 2016 Stormpath, Inc.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package com.stormpath.sdk.saml;

import com.stormpath.sdk.resource.*;

import java.util.Map;

/**
 * A SamlIdentityProvider represents data supporting Stormpath as an saml identity provider.
 *
 * @since 1.2.1
 */
public interface SamlIdentityProvider extends Resource, Saveable, Deletable, Auditable {
    /**
     * Returns the samlIdentityProvider's status.
     * {@link SamlIdentityProviderStatus}
     *
     * @return the samlIdentityProvider's status.
     */
    SamlIdentityProviderStatus getStatus();

    /**
     * Sets the samlIdentityProvider's status.
     * @param status the samlIdentityProvider's status.
     * {@link SamlIdentityProviderStatus}
     *
     * @return this instance for method chaining.
     */
    SamlIdentityProvider setStatus(SamlIdentityProviderStatus status);

    /**
     * Returns the samlIdentityProvider's sso login endpoint.
     *
     * @return the samlIdentityProvider's sso login endpoint.
     */
    Map<String, String> getSsoLoginEndpoint();

    /**
     * Sets the samlIdentityProvider's sso login endpoint.
     *
     * @param ssoLoginEndpoint the samlIdentityProvider's sso login endpoint.
     *
     * @return this instance for method chaining.
     */
    SamlIdentityProvider setSsoLoginEndpoint(String ssoLoginEndpoint);

    /**
     * Returns the samlIdentityProvider's signature algorithm.
     *
     * @return the samlIdentityProvider's signature algorithm.
     */
    String getSignatureAlgorithm();

    /**
     * Sets the samlIdentityProvider's signature algorithm.
     *
     * @param signatureAlgorithm the samlIdentityProvider's signature algorithm..
     *
     * @return this instance for method chaining.
     */
    SamlIdentityProvider setSignatureAlgorithm(String signatureAlgorithm);

    /**
     * Returns the samlIdentityProvider's x509 Signin Certificate.
     *
     * @return the samlIdentityProvider's x509 Signin Certificate.
     */
    Map<String, String> getX509SigninCert();

    /**
     * Sets the samlIdentityProvider's x509 Signin Certificate.
     *
     * @param x509SigninCert the samlIdentityProvider's x509 Signin Certificate.
     *
     * @return this instance for method chaining.
     */
    SamlIdentityProvider setX509SigninCert(Map<String, String> x509SigninCert);

    /**
     * Returns the samlIdentityProvider's SHA fingerprint.
     *
     * @return the samlIdentityProvider's SHA fingerprint.
     */
    String getShaFingerprint();

    /**
     * Sets the samlIdentityProvider's SHA fingerprint.
     *
     * @param shaFingerprint the samlIdentityProvider's SHA fingerprint.
     *
     * @return this instance for method chaining.
     */
    SamlIdentityProvider setShaFingerprint(String shaFingerprint);

    /**
     * Returns the samlIdentityProvider's metadata.
     *
     * @return the samlIdentityProvider's metadata.
     */
    SamlIdentityProviderMetadata getSamlIdentityProviderMetadata();

    /**
     * Returns the rules for mapping SAML Assertion Attributes to Stormpath Account attributes for Accounts created
     * in the associated Stormpath Directory.
     *
     * @return the rules for mapping SAML Assertion Attributes to Stormpath Account attributes for Accounts created
     * in the associated Stormpath Directory.
     */
    AttributeStatementMappingRules getAttributeStatementMappingRules();

    /**
     * Sets the rules for mapping SAML Assertion Attributes to Stormpath Account attributes for Accounts created
     * in the associated Stormpath Directory.
     */
    SamlIdentityProvider setAttributeStatementMappingRules(AttributeStatementMappingRules attributeStatementMappingRules);

    /**
     * Returns the list of {@link RegisteredSamlServiceProvider}s as {@link RegisteredSamlServiceProviderList} for which Stormpath serves as an identity provider.
     *
     * @return the list of {@link RegisteredSamlServiceProvider}s as {@link RegisteredSamlServiceProviderList} for which Stormpath serves as an identity provider.
     */
    RegisteredSamlServiceProviderList getRegisteredSamlServiceProviders();

    /**
     * Returns a paginated list of the samlIdentityProviders's assigned registeredSamlServiceProviders that match the specified query criteria.  The
     * {@link SamlIdentityProviders} utility class is available to help construct
     * the criteria DSL - most modern IDEs can auto-suggest and auto-complete as you type, allowing for an easy
     * query-building experience.  For example:
     * <pre>
     * samlIdentityProvider.getRegisteredSamlServiceProviders(RegisteredSamlServiceProviders.where(
     *     RegisteredSamlServiceProviders.createdAt().equals("2016-01-01")
     *     .offsetBy(20)
     *     .limitTo(25));
     * </pre>
     * or, if you use static imports:
     * <pre>
     *
     * @param criteria the criteria to use when performing a request to the collection.
     * @return a paginated list of the samlIdentityProvider's samlServiceProviderRegistrations that match the specified query criteria.
     * @since 1.2.1
     */
    RegisteredSamlServiceProviderList getRegisteredSamlServiceProviders(RegisteredSamlServiceProviderCriteria criteria);

    /**
     * Creates a {@link SamlServiceProviderRegistration} registering a {@link RegisteredSamlServiceProvider} with this identity provider.
     * @param createSamlServiceProviderRegistrationRequest the {@link CreateSamlServiceProviderRegistrationRequest} which the creation of the {@link SamlServiceProviderRegistration} is based upon.
     *
     * @return the {@link SamlServiceProviderRegistration}.
     */
    SamlServiceProviderRegistration createSamlServiceProviderRegistration(CreateSamlServiceProviderRegistrationRequest createSamlServiceProviderRegistrationRequest);

    /**
     * Creates a {@link SamlServiceProviderRegistration} registering a {@link RegisteredSamlServiceProvider} with this identity provider.
     * @param samlServiceProviderRegistration the {@link SamlServiceProviderRegistration} to be created.
     *
     * @return the {@link SamlServiceProviderRegistration}.
     */
    SamlServiceProviderRegistration createSamlServiceProviderRegistration(SamlServiceProviderRegistration samlServiceProviderRegistration) throws ResourceException;

    /**
     * Returns the list of {@link SamlServiceProviderRegistration}s as {@link SamlServiceProviderRegistrationList} which is held by this identity provider.
     *
     * @return the list of {@link SamlServiceProviderRegistration}s as {@link SamlServiceProviderRegistrationList} which is held by this identity provider.
     */
    SamlServiceProviderRegistrationList getSamlServiceProviderRegistrations();

    /**
     * Returns a paginated list of the samlIdentityProviders's assigned samlServiceProviderRegistrations that match the specified query criteria.  The
     * {@link SamlIdentityProviders} utility class is available to help construct
     * the criteria DSL - most modern IDEs can auto-suggest and auto-complete as you type, allowing for an easy
     * query-building experience.  For example:
     * <pre>
     * samlIdentityProvider.getSamlServiceProviderRegistrations(SamlServiceProviderRegistrations.where(
     *     SamlServiceProviderRegistrations.createdAt().equals("2016-01-01")
     *     .offsetBy(20)
     *     .limitTo(25));
     * </pre>
     * or, if you use static imports:
     * <pre>
     *
     * @param criteria the criteria to use when performing a request to the collection.
     * @return a paginated list of the samlIdentityProvider's samlServiceProviderRegistrations that match the specified query criteria.
     * @since 1.2.1
     */
    SamlServiceProviderRegistrationList getSamlServiceProviderRegistrations(SamlServiceProviderRegistrationCriteria criteria);
}
