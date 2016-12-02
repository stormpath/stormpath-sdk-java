package com.stormpath.sdk.impl.provider;

import com.stormpath.sdk.provider.AccessTokenType;
import com.stormpath.sdk.provider.CreateProviderRequest;
import com.stormpath.sdk.provider.OAuth2CreateProviderRequestBuilder;

import java.util.Map;

/**
 * @since 1.3.0
 */
public class DefaultOAuth2CreateProviderRequestBuilder extends AbstractCreateProviderRequestBuilder<OAuth2CreateProviderRequestBuilder> implements OAuth2CreateProviderRequestBuilder {

    private String providerId;

    private String authorizationEndpoint;

    private String tokenEndpoint;

    private String resourceEndpoint;

    private AccessTokenType accessTokenType;

    @Override
    public OAuth2CreateProviderRequestBuilder setAuthorizationEndpoint(String authorizationEndpoint) {
        this.authorizationEndpoint = authorizationEndpoint;
        return this;
    }

    @Override
    public OAuth2CreateProviderRequestBuilder setTokenEndpoint(String tokenEndpoint) {
        this.tokenEndpoint = tokenEndpoint;
        return this;
    }

    @Override
    public OAuth2CreateProviderRequestBuilder setResourceEndpoint(String resourceEndpoint) {
        this.resourceEndpoint = resourceEndpoint;
        return this;
    }

    @Override
    public OAuth2CreateProviderRequestBuilder setAccessTokenType(AccessTokenType accessTokenType) {
        this.accessTokenType = accessTokenType;
        return this;
    }

    @Override
    public OAuth2CreateProviderRequestBuilder setProviderId(String providerId) {
        this.providerId = providerId;
        return this;
    }

    public String getProviderId() {
        return this.providerId;
    }

    @Override
    protected String getConcreteProviderId() {
        return getProviderId();
    }

    @Override
    protected CreateProviderRequest doBuild(Map<String, Object> map) {
        DefaultOAuth2Provider provider = new DefaultOAuth2Provider(null, map);
        provider.setClientId(super.clientId);
        provider.setClientSecret(super.clientSecret);
        provider.setAuthorizationEndpoint(this.authorizationEndpoint);
        provider.setTokenEndpoint(this.tokenEndpoint);
        provider.setResourceEndpoint(this.resourceEndpoint);
        provider.setAccessTokenType(this.accessTokenType);
        if(super.userInfoMappingRules != null) {
            provider.setUserInfoMappingRules(super.userInfoMappingRules);
        }

        return new DefaultCreateProviderRequest(provider);
    }
}
