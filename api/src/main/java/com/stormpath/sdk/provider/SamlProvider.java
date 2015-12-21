/*
* Copyright 2015 Stormpath, Inc.
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
package com.stormpath.sdk.provider;

import com.stormpath.sdk.saml.AttributeStatementMappingRules;
import com.stormpath.sdk.saml.SamlServiceProviderMetadata;

/**
 * A SAML-specific {@link com.stormpath.sdk.provider.Provider} Resource.
 *
 * @since 1.0.RC8
 */
public interface SamlProvider extends Provider {

    /**
     * Returns the URL for the Identity Provider (IdP) SSO Login Endpoint.
     * @return the URL for the Identity Provider (IdP) SSO Login Endpoint.
     */
    String getSsoLoginUrl();

    /**
     * Returns the URL for the Identity Provider (IdP) SSO Logout Endpoint.
     * @return the URL for the Identity Provider (IdP) SSO Logout Endpoint.
     */
    String getSsoLogoutUrl();

    /**
     * Returns the valid String for the PEM encoded certificate.
     * @return the valid String for the PEM encoded certificate.
     */
    String getEncodedX509SigningCert();

    /**
     * Returns the algorithm used to sign the request.
     * @return the algorithm used to sign the request.
     */
    String getRequestSignatureAlgorithm();

    /**
     * Returns the {@link AttributeStatementMappingRules AttributeStatementMappingRules} instace containing the rules for mapping Stormpath's Account attributes to client application attributes.
     * @return the {@link AttributeStatementMappingRules AttributeStatementMappingRules} resource containing the rules for mapping Stormpath's Account attributes to client application attributes.
     */
    AttributeStatementMappingRules getAttributeStatementMappingRules();

    /**
     *
     * @return
     */
    SamlServiceProviderMetadata getServiceProviderMetadata();
}

