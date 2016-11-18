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

//todo: saml javadoc
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
     * @param status the samlServiceProviderRegistration's status.
     * {@link SamlServiceProviderRegistrationStatus}
     *
     * @return this instance for method chaining.
     */
    SamlServiceProviderRegistration setStatus(SamlServiceProviderRegistrationStatus status);

    RegisteredSamlServiceProvider getServiceProvider();

    SamlServiceProviderRegistration setServiceProvider(RegisteredSamlServiceProvider registeredSamlServiceProvider);

    SamlIdentityProvider getIdentityProvider();

    SamlServiceProviderRegistration setIdentityProvider(SamlIdentityProvider samlIdentityProvider);

    String getDefaultRelayState();

    SamlServiceProviderRegistration setDefaultRelayState(String defaultRelayState);

}
