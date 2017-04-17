package com.stormpath.sdk.authc;

import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.oauth.AccessToken;
import com.stormpath.sdk.oauth.OAuthGrantRequestAuthenticationResult;
import com.stormpath.sdk.oauth.OAuthRefreshTokenRequestAuthenticator;
import com.stormpath.sdk.okta.TokenIntrospectResponse;
import com.stormpath.sdk.provider.ProviderAccountRequest;
import com.stormpath.sdk.provider.ProviderAccountResult;
import com.stormpath.sdk.resource.Resource;

/**
 *
 */
public interface OktaAuthNAuthenticator extends Resource {

    AuthenticationResult authenticate(AuthenticationRequest request);
    
    void assertValidAccessToken(String accessToken);

    ProviderAccountResult getAccount(ProviderAccountRequest providerAccountRequesst);

    Account getAccountByToken(String accountToken);

    TokenIntrospectResponse resolveAccessToken(String accessToken);

    OAuthGrantRequestAuthenticationResult resolveRefreshToken(String refreshToken, OAuthRefreshTokenRequestAuthenticator refreshTokenAuthenticator);
}
