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
package com.stormpath.sdk.provider.saml;

import com.stormpath.sdk.provider.Provider;
import com.stormpath.sdk.saml.AttributeStatementMappingRules;
import com.stormpath.sdk.saml.SamlServiceProviderMetadata;

/**
 * A {@link com.stormpath.sdk.provider.Provider} Resource that represents a SAML Identity Provider (IdP).  Accounts
 * authenticated at the IdP will automatically be synchronized to the associated Stormpath Directory.
 *
 * @since 1.0.RC8
 */
public interface SamlProvider extends Provider {

    /**
     * Returns the URL at the SAML Identity Provider where end-users should be redirected to login. This is often called
     * an “SSO URL”, “Login URL” or “Sign-in URL”.for the Identity Provider (IdP) SSO Login Endpoint.
     *
     * @return the URL at the SAML Identity Provider where end-users should be redirected to login.
     */
    String getSsoLoginUrl();

    /**
     * Sets the URL at the SAML Identity Provider where end-users should be redirected to login. This is often called
     * an “SSO URL”, “Login URL” or “Sign-in URL”.for the Identity Provider (IdP) SSO Login Endpoint.
     */
    void setSsoLoginUrl(String ssoLoginUrl);

    /**
     * Returns the URL at the SAML Idenity Provider where end-users should be redirected to logout of all applications.
     * This is often called a “Logout URL”, “Global Logout URL” or “Single Logout URL”.
     *
     * @return the URL at the SAML Idenity Provider where end-users should be redirected to logout of all applications.
     */
    String getSsoLogoutUrl();

    /**
     * Sets the URL at the SAML Idenity Provider where end-users should be redirected to logout of all applications.
     * This is often called a “Logout URL”, “Global Logout URL” or “Single Logout URL”.
     */
    void setSsoLogoutUrl(String ssoLogoutUrl);

    /**
     * Returns the algorithm used by the SAML Identity provider to sign SAML assertions.  If signatures are used, this
     * value is usually either {@code RSA-SHA1} or {@code RSA-SHA256}.
     *
     * @return the algorithm used by the SAML Identity provider to sign SAML assertions.
     */
    String getRequestSignatureAlgorithm();

    /**
     * Sets the algorithm used by the SAML Identity provider to sign SAML assertions.  If signatures are used, this
     * value is usually either {@code RSA-SHA1} or {@code RSA-SHA256}.
     */
    void setRequestSignatureAlgorithm(String requestSignatureAlgorithm);

    /**
     * Returns the <a href="https://en.wikipedia.org/wiki/Privacy-enhanced_Electronic_Mail">PEM</a>-formatted
     * {@code X.509} certificate used validate the SAML Identity Provider's signed SAML assertions.
     *
     * @return the <a href="https://en.wikipedia.org/wiki/Privacy-enhanced_Electronic_Mail">PEM</a>-formatted
     * {@code X.509} certificate used validate the SAML Identity Provider's signed SAML assertions.
     */
    String getEncodedX509SigningCert();

    /**
     * Sets the <a href="https://en.wikipedia.org/wiki/Privacy-enhanced_Electronic_Mail">PEM</a>-formatted
     * {@code X.509} certificate used validate the SAML Identity Provider's signed SAML assertions.  This <b>MUST</b>
     * be a valid PEM formatting, otherwise the value will be rejected.
     */
    void setEncodedX509SigningCert(String encodedX509SigningCert);

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
    void setAttributeStatementMappingRules(AttributeStatementMappingRules attributeStatementMappingRules);

    /**
     * Returns the (read-only) Service Provider metadata that can be used to register and/or configure an
     * application with a SAML Identity Provider.
     * <p>This metadata is almost always accessed as an XML document and
     * provided to the SAML Identity Provider when registering with the Identity Provider, and not often accessed
     * in Java code or as JSON.  It is provided as a type-safe resource however should you wish to read the associated
     * values.</p>
     * <p>The returned object is read-only.  Because Stormpath fully automates SAML assertion exchange between the
     * Identity Provider, there is nothing to configure, so there are no mutator (setter) methods necessary.</p>
     *
     * @return the (read-only) Service Provider metadata that can be used to register and/or configure an
     * application with a SAML Identity Provider.
     */
    SamlServiceProviderMetadata getServiceProviderMetadata();
}

