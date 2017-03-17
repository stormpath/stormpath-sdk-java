/*
* Copyright 2017 Stormpath, Inc.
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
package com.stormpath.sdk.oauth.openidconnect;

import com.stormpath.sdk.oauth.OAuthPolicy;
import com.stormpath.sdk.resource.Auditable;
import com.stormpath.sdk.resource.Deletable;
import com.stormpath.sdk.resource.Resource;
import com.stormpath.sdk.resource.Saveable;

import java.util.Map;

/**
 * A Scope resource is used to configure different openid connect scopes
 * with an {@link OAuthPolicy OAuthPolicy}
 *
 * @since 1.0.RC7
 */
public interface Scope extends Resource, Saveable, Deletable, Auditable {
    /**
     * Returns the scope's name.
     *
     * @return the scope's name.
     */
    String getName();

    /**
     * Sets the scope's name.
     * @param name the scope's name.
     *
     * @return this instance for method chaining.
     */
    Scope setName(String name);

    /**
     * Returns the scope's friendly name.
     *
     * @return the scope's friendly name.
     */
    String getFriendlyName();

    /**
     * Sets the scope's friendly name.
     *
     * @param name the scope's friendly name.
     * @return this instance for method chaining.
     */
    Scope setFriendlyName(String name);

    /**
     * Returns the scope's description.
     *
     * @return the scope's description.
     */
    String getDescription();

    /**
     * Sets the scope's description.
     * @param description the scope's description.
     *
     * @return this instance for method chaining.
     */
    Scope setDescription(String description);


    /**
     * Returns the scope's attribute mappings.
     *
     * @return the scope's attribute mappings.
     */
    Map<String, String> getAttributeMappings();

    /**
     * Sets the scope's attribute mappings.
     * @param attributeMappings the scope's attribute mappings.
     *
     * @return this instance for method chaining.
     */
    Scope setAttributeMappings(Map<String, String> attributeMappings);

    /**
     * Returns the {@link OAuthPolicy} associated with this scope.
     *
     * @return the {@link OAuthPolicy} associated with this scope.
     */
    OAuthPolicy getOAuthPolicy();

    /**
     * Sets the {@link OAuthPolicy} associated with this scope.
     * @param oAuthPolicy the {@link OAuthPolicy} associated with this scope.
     *
     * @return this instance for method chaining.
     */
    Scope setOAuthPolicy(OAuthPolicy oAuthPolicy);
}
