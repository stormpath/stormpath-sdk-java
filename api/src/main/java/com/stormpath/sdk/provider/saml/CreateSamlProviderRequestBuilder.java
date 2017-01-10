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
package com.stormpath.sdk.provider.saml;

import com.stormpath.sdk.provider.CreateProviderRequest;
import com.stormpath.sdk.saml.AttributeStatementMappingRules;

/**
 * A Builder to construct Saml-specific {@link com.stormpath.sdk.provider.CreateProviderRequest}s.
 * A simple SAML create provider request creation looks like:
 *
 * CreateProviderRequest request = Providers.SAML.builder()
 *      .setEncodedX509SigningCert(validX509Cert)
 *      .setRequestSignatureAlgorithm("RSA-SHA256")
 *      .setSsoLoginUrl(validLoginURL)
 *      .setSsoLogoutUrl(validLogoutURL)
 *      .build();
 *
 * @since 1.0.RC8
 */
public interface CreateSamlProviderRequestBuilder {

    /**
     * Sets the URL at the SAML Identity Provider where end-users should be redirected to login. This is often called
     * an “SSO URL”, “Login URL” or “Sign-in URL”.for the Identity Provider (IdP) SSO Login Endpoint.
     *
     * @return this instance for method chaining.
     */
    CreateSamlProviderRequestBuilder setSsoLoginUrl(String ssoLoginUrl);

    /**
     * Sets the URL at the SAML Idenity Provider where end-users should be redirected to logout of all applications.
     * This is often called a “Logout URL”, “Global Logout URL” or “Single Logout URL”.
     *
     * @return this instance for method chaining.
     */
    CreateSamlProviderRequestBuilder setSsoLogoutUrl(String ssoLogoutUrl);

    /**
     * Sets the algorithm used by the SAML Identity provider to sign SAML assertions.  If signatures are used, this
     * value is usually either {@code RSA-SHA1} or {@code RSA-SHA256}.
     *
     * @return this instance for method chaining.
     */
    CreateSamlProviderRequestBuilder setRequestSignatureAlgorithm(String requestSignatureAlgorithm);

    /**
     * Sets the <a href="https://en.wikipedia.org/wiki/Privacy-enhanced_Electronic_Mail">PEM</a>-formatted
     * {@code X.509} certificate used validate the SAML Identity Provider's signed SAML assertions.  This <b>MUST</b>
     * be a valid PEM formatting, otherwise the value will be rejected.
     *
     * @return this instance for method chaining.
     */
    CreateSamlProviderRequestBuilder setEncodedX509SigningCert(String encodedX509SigningCert);

    /**
     * Sets the rules for mapping SAML Assertion Attributes to Stormpath Account attributes for Accounts created
     * in the associated Stormpath Directory.
     *
     * @return this instance for method chaining.
     */
    CreateSamlProviderRequestBuilder setAttributeStatementMappingRules(AttributeStatementMappingRules attributeStatementMappingRules);

    /**
     * Sets the boolean value for the forceAuthn attribute of the SamlProvider.
     *
     * @return this instance for method chaining.
     * @since 1.3.0
     */
    CreateSamlProviderRequestBuilder setForceAuthn(boolean forceAuthn);

    /**
     * Builds a {@link com.stormpath.sdk.provider.CreateProviderRequest} based on the current state of the builder.
     *
     * @return a {@code CreateProviderRequest} instance
     */
    CreateProviderRequest build();
}