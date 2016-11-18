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

//todo: saml javadoc
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

    Map<String, String> getSsoLoginEndpoint();

    SamlIdentityProvider setSsoLoginEndpoint(String ssoLoginEndpoint);

    String getSignatureAlgorithm();

    SamlIdentityProvider setSignatureAlgorithm(String signatureAlgorithm);

    Map<String, String> getX509SigninCert();

    SamlIdentityProvider setX509SigninCert(Map<String, String> x509SigninCert);

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

    RegisteredSamlServiceProviderList getRegisteredSamlServiceProviders();

    SamlServiceProviderRegistration createSamlServiceProviderRegistration(CreateSamlServiceProviderRegistrationRequest createSamlServiceProviderRegistrationRequest);

    SamlServiceProviderRegistration createSamlServiceProviderRegistration(SamlServiceProviderRegistration samlServiceProviderRegistration) throws ResourceException;

    SamlServiceProviderRegistrationList getSamlServiceProviderRegistrations();

    SamlServiceProviderRegistrationList getSamlServiceProviderRegistrations(SamlServiceProviderRegistrationCriteria criteria);
}
