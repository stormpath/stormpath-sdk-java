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
     * Creates a new instance of {@link BearerOauthAuthenticationRequestBuilder} based on the current state
     * of the builder.
     *
     * @param ttl
     * @return
     */
    BasicOauthAuthenticationRequestBuilder withTtl(long ttl);

    /**
     * Creates a new instance of {@link BearerOauthAuthenticationRequestBuilder} based on the current state
     * of the builder.
     *
     * @param locations - An array of {@link BearerLocation} where the
     * @return
     */
    BearerOauthAuthenticationRequestBuilder inLocation(BearerLocation... locations);


    /**
     * @return
     */
    OauthAuthenticationResult execute();

}
