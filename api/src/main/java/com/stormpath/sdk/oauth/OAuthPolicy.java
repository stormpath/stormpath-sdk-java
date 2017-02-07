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
package com.stormpath.sdk.oauth;

import com.stormpath.sdk.application.Application;
import com.stormpath.sdk.oauth.openidconnect.Scope;
import com.stormpath.sdk.oauth.openidconnect.ScopeList;
import com.stormpath.sdk.resource.Resource;
import com.stormpath.sdk.resource.ResourceException;
import com.stormpath.sdk.resource.Saveable;
import com.stormpath.sdk.tenant.Tenant;

import java.util.Map;

/**
 * An OAuthPolicy resource is used to configure different aspects of the OAuth tokens associated
 * with an {@link Application Application}
 *
 * @since 1.0.RC7
 */
public interface OAuthPolicy extends Resource, Saveable {

    /**
     * Returns the Time To Live for the tokens created for the parent {@link Application Application} expressed in a period of time format, for example: PT1H.
     *
     * @return the String representation of the Time To Live for the tokens created for the parent {@link Application Application}
     */
    String getAccessTokenTtl();

    /**
     * Returns the Time To Live for the refresh tokens created for the parent {@link Application Application} expressed in a period of time format, for example: PT1H.
     *
     * @return the String representation of the Time To Live for the refresh tokens created for the parent {@link Application Application}
     */
    String getRefreshTokenTtl();

    /**
     * Returns the Time To Live for the id tokens created for the parent {@link Application Application} expressed in a period of time format, for example: PT1H.
     *
     * @return the String representation of the Time To Live for the id tokens created for the parent {@link Application Application}
     * @since 1.6.0
     */
    String getIdTokenTtl();

    /**
     * The href corresponding to the Endpoint for Access Tokens created for the parent {@link Application Application}
     *
     * @return the href corresponding to the Token Endpoint for Access Tokens created for the parent {@link Application Application}
     */
    String getTokenEndpoint();

    /**
     * The href corresponding to the Endpoint for Tokens (access_tokens or refresh) revocation for the parent {@link Application Application}
     *
     * @return the href corresponding to the Endpoint for Tokens (access_tokens or refresh) revocation for the parent {@link Application Application}
     */
    String getRevocationEndpoint();

    /**
     * Sets the Time To Live for the tokens created for the parent {@link Application Application} expressed in a period of time format, for example: PT1H.
     *
     * @return this instance for method chaining.
     */
    OAuthPolicy setAccessTokenTtl(String accessTokenTtl);

    /**
     * Sets the Time To Live for the refresh tokens created for the parent {@link Application Application} expressed in a period of time format, for example: PT1H.
     * <p>Since Refresh tokens are optional, if you would like to disable the refresh token from being generated, set a zero duration value (PT0M, PT0S, etc).</p>
     *
     * @return this instance for method chaining.
     */
    OAuthPolicy setRefreshTokenTtl(String refreshTokenTtl);

    /**
     * Sets the Time To Live for the id tokens created for the parent {@link Application Application} expressed in a period of time format, for example: PT1H.
     *
     * @return this instance for method chaining.
     * @since 1.6.0
     */
    OAuthPolicy setIdTokenTtl(String idTokenTtl);

    /**
     * Creates a new {@link Scope} assigned to this oauthPolicy in the Stormpath server and returns the created resource.
     * The scope is used for openid connect flows.
     *
     * @param scope {@link Scope} pojo to hold necessary data to send to the back-end to create a {@link Scope}.
     * @return the newly created {@link Scope}.
     *
     * @since 1.6.0
     */
    Scope createScope(Scope scope) throws ResourceException;

    /**
     * Returns a paginated list of all the scopes that belong to the oAuthPolicy.
     *
     * @return a paginated list of all the oAuthPolicy's scopes.
     *
     * @since 1.6.0
     */
    ScopeList getScopes();

    /**
     * Returns access token attribute mappings.
     * Open Id provider (OP) would enter any custom mappings used for their internal purposes in this map.
     * Authorization server would then add all these mappings as part of the access token upon its generation.
     *
     * @return access token attribute mappings.
     *
     * @since 1.6.0
     */
    Map<String,String> getAccessTokenAttributeMap();

    /**
     * Sets access token attribute mappings to be inserted into access tokens.
     *
     * @param accessTokenAttributeMap access token attribute mappings to be inserted into access tokens
     * Open Id provider (OP) would enter any custom mappings used for their internal purposes in this map.
     * Authorization server would then add all these mappings as part of the access token upon its generation.
     * @return this instance for method chaining.
     *
     * @since 1.6.0
     */
    OAuthPolicy setAccessTokenAttributeMap(Map<String,String> accessTokenAttributeMap);

    /**
     * Returns id token attribute mappings.
     * Open Id provider (OP) would enter any custom mappings used for their internal purposes in this map.
     * Authorization server would then add all these mappings as part of the id token upon its generation.
     *
     * @return id token attribute mappings.
     *
     * @since 1.6.0
     */
    Map<String,String> getIdTokenAttributeMap();

    /**
     * Sets id token attribute mappings to be inserted into id tokens.
     *
     * @param idTokenAttributeMap id token attribute mappings to be inserted into id tokens
     * Open Id provider (OP) would enter any custom mappings used for their internal purposes in this map.
     * Authorization server would then add all these mappings as part of the id token upon its generation.
     * @return this instance for method chaining.
     *
     * @since 1.6.0
     */
    OAuthPolicy setIdTokenAttributeMap(Map<String, String> idTokenAttributeMap);

    /**
     * Returns the {@link Application Application} associated to this {@link OAuthPolicy OAuthPolicy}
     *
     * @return the {@link Application Application} associated to this {@link OAuthPolicy OAuthPolicy}
     */
    Application getApplication();

    /**
     * Returns the parent {@link Tenant Tenant} of the {@link Application Application} associated to this {@link OAuthPolicy OauthPolicy}
     *
     * @return the parent {@link Tenant Tenant} of the {@link Application Application} associated to this {@link OAuthPolicy OauthPolicy}
     */
    Tenant getTenant();
}
