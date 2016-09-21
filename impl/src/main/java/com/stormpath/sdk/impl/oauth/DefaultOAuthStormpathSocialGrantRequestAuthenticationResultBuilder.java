package com.stormpath.sdk.impl.oauth;

import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.oauth.GrantAuthenticationToken;

/**
 * @since 1.1.0
 */
public class DefaultOAuthStormpathSocialGrantRequestAuthenticationResultBuilder extends DefaultOAuthGrantRequestAuthenticationResultBuilder {

    public DefaultOAuthStormpathSocialGrantRequestAuthenticationResultBuilder(GrantAuthenticationToken grantAuthenticationToken) {
        super(grantAuthenticationToken);
    }

    @Override
    public DefaultOAuthGrantRequestAuthenticationResult build() {
        Assert.notNull(this.grantAuthenticationToken, "grantAuthenticationToken has not been set. It is a required attribute.");

        this.accessToken = grantAuthenticationToken.getAsAccessToken();
        this.accessTokenString = grantAuthenticationToken.getAccessToken();
        this.accessTokenHref = grantAuthenticationToken.getAccessTokenHref();
        this.tokenType = grantAuthenticationToken.getTokenType();
        this.expiresIn = Integer.parseInt(grantAuthenticationToken.getExpiresIn());

        return new DefaultOAuthGrantRequestAuthenticationResult(this);
    }
}
