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

import com.stormpath.sdk.application.ApplicationAccountStoreMappingCriteria;
import com.stormpath.sdk.resource.Auditable;
import com.stormpath.sdk.resource.Resource;

/**
 * A {@code SamlServiceProvider} instance represents the information necessary to redirect an end-user to login at
 * a SAML Identity Provider.  This is used when the owning Application acts as a SAML Service Provider.
 *
 * @since 1.0.RC8
 */
public interface SamlServiceProvider extends Resource, Auditable {

    /**
     * Returns the endpoint to where end-users (web browsers) should be redirected when they need to login to a SAML
     * Identity Provider.  This is an application-specific URL in Stormpath.  Once the browser is redirected there,
     * they will transparently be redirected to the appropriate SAML Identity Provider based on the Application's
     * {@link com.stormpath.sdk.application.Application#getApplicationAccountStoreMappings(ApplicationAccountStoreMappingCriteria) accountStoreMappings}.
     * This transparent redirect is what allows Stormpath to handle all SAML protocol specifics so you don't have to
     * worry about SAML concepts at all.
     *
     * @return the endpoint to where end-users (web browsers) should be redirected when they need to login to a SAML
     * Identity Provider.
     */
    String getSsoInitiationEndpoint();
}
