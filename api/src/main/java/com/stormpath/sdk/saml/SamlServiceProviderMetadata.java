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
package com.stormpath.sdk.saml;

import com.stormpath.sdk.application.Application;
import com.stormpath.sdk.resource.Resource;

/**
 * The (read-only) Service Provider metadata that can be used to register an application (aka 'Service Provider) with
 * a SAML Identity Provider.
 * <p>This metadata is almost always accessed as an XML document (as described below) and
 * provided to the SAML Identity Provider when registering with the Identity Provider, and not often accessed
 * in Java code or as JSON.  However, it is provided in the Stormpath SDK as a type-safe resource should
 * you wish to read the associated values.</p>
 * <p>This instance is read-only.  Because Stormpath fully automates SAML assertion exchange between the
 * Identity Provider, there is nothing to configure, so there are no mutator (setter) methods necessary.</p>
 * <h5>SAML Metadata XML</h5>
 * <p>As mentioned above, most Identity Providers need the SAML metadata as an XML document.  To obtain that
 * document, simply execute an HTTP {@code GET} request to this resource's {@link #getHref() href} property.
 * For example:</p>
 * <pre><code>
 * String HREF_VALUE_HERE = samlServiceProviderMetadata.getHref();
 * </code></pre>
 * <p>This GET request might look like the following example:</p>
 * <pre><code>
 * GET HREF_VALUE_HERE HTTP/1.1
 * Host: api.stormpath.com
 * Content-Type: application/xml
 * </code></pre>
 * <p>(where HREF_VALUE_HERE is substituted with the actual href value)</p>
 * <p>The metadata will be returned as a SAML metadata XML document.</p>
 *
 * @since 1.0.RC8
 */
public interface SamlServiceProviderMetadata extends Resource {

    /**
     * Returns the Service Provider entity id.  This value is specific to the Stormpath
     * {@link com.stormpath.sdk.directory.Directory Directory} that will persist accounts verified by the SAML
     * Identity Provider.
     *
     * @return the Service Provider entity id.
     */
    String getEntityId();

    /**
     * Returns the {@code X.509} certificate used by Stormpath to sign SAML requests sent to the SAML Identity Provider.
     * This value is specific to the Stormpath
     * {@link com.stormpath.sdk.directory.Directory Directory} that will persist accounts verified by the SAML
     * Identity Provider.
     *
     * @return the {@code X.509} certificate used by the Service Provider to sign SAML requests.
     */
    X509SigningCert getX509SigningCert();

    /**
     * The Stormpath endpoint that will process SAML Assertions sent by the SAML Identity Provider.
     * <p>When Stormpath receives a SAML assertion at this endpoint, the assertion will be converted to a JWT that
     * represents the authenticated Stormpath account.  Stormpath will then relay this JWT to your application by
     * redirecting the user agent (browser) to your Application's
     * {@link Application#getAuthorizedCallbackUris() authorized callback URI}.</p>
     * <p><b>NOTE:</b> this is *not* a REST resource or endpoint - it is specific to the SAML protocol and it does
     * not produce or consume JSON data.  Do not attempt to resolve this href as an SDK or JSON resource.</p>
     *
     * @return Stormpath endpoint that will process SAML Assertions sent by the SAML Identity Provider.
     */
    AssertionConsumerServicePostEndpoint getAssertionConsumerServicePostEndpoint();
}
