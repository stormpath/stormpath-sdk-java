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

import com.stormpath.sdk.resource.Auditable;
import com.stormpath.sdk.resource.Deletable;
import com.stormpath.sdk.resource.Resource;
import com.stormpath.sdk.resource.Saveable;
import com.stormpath.sdk.tenant.Tenant;

/**
 * A registeredSamlServiceProvider is associated with an {@link Tenant} and represents a service provider for Stormpath as an identity provider flow.
 *
 * @since 1.2.1
 */
public interface RegisteredSamlServiceProvider extends Resource, Saveable, Deletable, Auditable {
    /**
     * Returns the registeredSamlServiceProvider's name.
     *
     * @return the registeredSamlServiceProvider's name.
     */
    String getName();

    /**
     * Sets the registeredSamlServiceProvider's name.
     *
     * @param name the registeredSamlServiceProvider's name.
     * @return this instance for method chaining.
     */
    RegisteredSamlServiceProvider setName(String name);

    /**
     * Returns the registeredSamlServiceProvider's description.
     *
     * @return the registeredSamlServiceProvider's description.
     */
    String getDescription();

    /**
     * Sets the registeredSamlServiceProvider's description.
     *
     * @param description the registeredSamlServiceProvider's description.
     * @return this instance for method chaining.
     */
    RegisteredSamlServiceProvider setDescription(String description);

    /**
     * Returns the registeredSamlServiceProvider's assertion consumer service url.
     *
     * @return the registeredSamlServiceProvider's assertion consumer service url.
     */
    String getAssertionConsumerServiceUrl();

    /**
     * Sets the registeredSamlServiceProvider's assertion consumer service url.
     *
     * @param assertionConsumerServiceUrl the registeredSamlServiceProvider's assertion consumer service url.
     * @return this instance for method chaining.
     */
    RegisteredSamlServiceProvider setAssertionConsumerServiceUrl(String assertionConsumerServiceUrl);

    /**
     * Returns the registeredSamlServiceProvider's entity id.
     *
     * @return the registeredSamlServiceProvider's entity id.
     */
    String getEntityId();

    /**
     * Sets the registeredSamlServiceProvider's entity id.
     *
     * @param entityId the registeredSamlServiceProvider's entity id.
     * @return this instance for method chaining.
     */
    RegisteredSamlServiceProvider setEntityId(String entityId);

    /**
     * Returns the registeredSamlServiceProvider's name id format.
     *
     * @return the registeredSamlServiceProvider's name id format.
     */
    String getNameIdFormat();

    /**
     * Sets the registeredSamlServiceProvider's name id format.
     *
     * @param nameIdFormat the registeredSamlServiceProvider's name id format.
     * @return this instance for method chaining.
     */
    RegisteredSamlServiceProvider setNameIdFormat(String nameIdFormat);

    /**
     * Returns the registeredSamlServiceProvider's encoded X 509 signing certificate.
     *
     * @return the registeredSamlServiceProvider's encoded X 509 signing certificate.
     */
    String getEncodedX509SigningCert();

    /**
     * Sets the registeredSamlServiceProvider's encoded X 509 signing certificate.
     *
     * @param x509SigningCert the registeredSamlServiceProvider's encoded X 509 signing certificate.
     * @return this instance for method chaining.
     */
    RegisteredSamlServiceProvider setEncodedX509SigningCert(String x509SigningCert);

    /**
     * Returns the Tenant to which this RegisteredSamlServiceProvider belongs.
     *
     * @return the Tenant to which this RegisteredSamlServiceProvider belongs.
     */
    Tenant getTenant();

}
