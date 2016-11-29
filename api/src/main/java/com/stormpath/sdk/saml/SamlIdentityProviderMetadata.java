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

import com.stormpath.sdk.application.Application;
import com.stormpath.sdk.resource.Auditable;
import com.stormpath.sdk.resource.Deletable;
import com.stormpath.sdk.resource.Resource;
import com.stormpath.sdk.resource.Saveable;

import java.util.Map;

/**
 * The (read-only) Saml Identity Provider metadata providing meta data for a given Stormpath's {@link Application}
 * as part of its {@link SamlPolicy}

 * @since 1.3.0
 */
public interface SamlIdentityProviderMetadata extends Resource, Saveable, Deletable, Auditable {

    /**
     * Returns the SamlIdentityProvider's entity id.
     *
     * @return the SamlIdentityProvider's entity id.
     */
    String getEntityId();

    /**
     * Returns the SamlIdentityProvider's x509 Signin Certificate.
     *
     * @return the SamlIdentityProvider's x509 Signin Certificate.
     */
    Map<String, String> getX509SigninCert();

    /**
     * Returns the SamlIdentityProvider instance.
     *
     * @return the the SamlIdentityProvider instance.
     */
    SamlIdentityProvider getSamlIdentityProvider();

}
