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

import com.stormpath.sdk.resource.Auditable;
import com.stormpath.sdk.resource.Deletable;
import com.stormpath.sdk.resource.Resource;
import com.stormpath.sdk.resource.Saveable;

/**
 * A SamlServiceProviderRegistration represents the link between a {@link SamlIdentityProvider} and a {@link RegisteredSamlServiceProvider}
 *
 * @since 1.2.0
 */
public interface SamlServiceProviderRegistration extends Resource, Saveable, Deletable, Auditable {
    /**
     * Returns the samlServiceProviderRegistration's status.
     * {@link SamlServiceProviderRegistrationStatus}
     *
     * @return the samlServiceProviderRegistration's status.
     */
    SamlServiceProviderRegistrationStatus getStatus();

    /**
     * Sets the samlServiceProviderRegistration's status.
     *
     * @param status the samlServiceProviderRegistration's status.
     *               {@link SamlServiceProviderRegistrationStatus}
     * @return this instance for method chaining.
     */
    SamlServiceProviderRegistration setStatus(SamlServiceProviderRegistrationStatus status);

    /**
     * Returns the samlServiceProviderRegistration's {@link RegisteredSamlServiceProvider} portion of the link.
     *
     * @return the samlServiceProviderRegistration's {@link RegisteredSamlServiceProvider}.
     */
    RegisteredSamlServiceProvider getServiceProvider();

    /**
     * Sets the samlServiceProviderRegistration's {@link RegisteredSamlServiceProvider} portion of the link.
     *
     * @param registeredSamlServiceProvider the samlServiceProviderRegistration's {@link RegisteredSamlServiceProvider}.
     * @return this instance for method chaining.
     */
    SamlServiceProviderRegistration setServiceProvider(RegisteredSamlServiceProvider registeredSamlServiceProvider);

    /**
     * Returns the samlServiceProviderRegistration's {@link SamlIdentityProvider} portion of the link.
     *
     * @return the samlServiceProviderRegistration's {@link SamlIdentityProvider}.
     */
    SamlIdentityProvider getIdentityProvider();

    /**
     * Sets the samlServiceProviderRegistration's {@link SamlIdentityProvider} portion of the link.
     *
     * @param samlIdentityProvider the samlServiceProviderRegistration's {@link SamlIdentityProvider}.
     * @return this instance for method chaining.
     */
    SamlServiceProviderRegistration setIdentityProvider(SamlIdentityProvider samlIdentityProvider);

    /**
     * Returns the samlServiceProviderRegistration's default relay state.
     *
     * @return the samlServiceProviderRegistration's default relay state.
     */
    String getDefaultRelayState();

    /**
     * Sets the samlServiceProviderRegistration's default relay state.
     *
     * @param defaultRelayState the samlServiceProviderRegistration's default relay state.
     * @return this instance for method chaining.
     */
    SamlServiceProviderRegistration setDefaultRelayState(String defaultRelayState);

}
