package com.stormpath.sdk.oauth.authc;

import com.stormpath.sdk.authc.ApiAuthenticationRequestBuilder;
import com.stormpath.sdk.oauth.permission.ScopeFactory;

/**
 * OauthAuthenticationRequestBuilder
 *
 * @since 1.0.RC
 */
public interface OauthAuthenticationRequestBuilder extends ApiAuthenticationRequestBuilder {

    /**
     * @param scopeFactory
     * @return a new {@link BasicOauthAuthenticationRequestBuilder} instance created with the current state of the
     *         this builder.
     */
    BasicOauthAuthenticationRequestBuilder using(ScopeFactory scopeFactory);

    /**
     * @param locations
     * @return
     */
    BearerOauthAuthenticationRequestBuilder inLocation(BearerLocation... locations);

    /**
     * @return
     */
    OauthAuthenticationResult execute();

}
