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

import com.stormpath.sdk.resource.Resource;

/**
 * SAML-specific Resource to represent the SAML service provider for an Application.
 *
 * @since 1.0.RC8
 */
public interface SamlServiceProvider extends Resource {

    /**
     * Returns the SSO initialization Endpoint for this Saml Service provider
     * @return the String SSO initialization Endpoint for this Saml Service Provider
     */
    String getSsoInitiationEndpoint();
}
