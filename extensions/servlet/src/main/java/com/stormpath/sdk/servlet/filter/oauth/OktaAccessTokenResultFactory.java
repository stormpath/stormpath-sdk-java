package com.stormpath.sdk.servlet.filter.oauth;

import com.stormpath.sdk.application.Application;
import com.stormpath.sdk.impl.oauth.authz.DefaultTokenResponse;
import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.oauth.AccessTokenResult;
import com.stormpath.sdk.oauth.OAuthGrantRequestAuthenticationResult;
import com.stormpath.sdk.oauth.TokenResponse;
import org.apache.oltu.oauth2.common.message.types.TokenType;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 */
public class OktaAccessTokenResultFactory implements AccessTokenResultFactory {

    private final Application application;

    public OktaAccessTokenResultFactory(Application application) {
        Assert.notNull(application, "Application argument cannot be null.");
        this.application = application;
    }

    protected Application getApplication() {
        return this.application;
    }

    @Override
    public AccessTokenResult createAccessTokenResult(HttpServletRequest request, HttpServletResponse response, OAuthGrantRequestAuthenticationResult result) {
        final TokenResponse tokenResponse =
                DefaultTokenResponse.tokenType(TokenType.BEARER)
                        .accessToken(result.getAccessTokenString())
                        .refreshToken(result.getRefreshTokenString())
                        .idToken(result.getIdTokenString())
                        .applicationHref(application.getHref())
                        .expiresIn(String.valueOf(result.getExpiresIn())).build();
        return new PasswordGrantAccessTokenResult(result.getAccessToken().getAccount(), tokenResponse);
    }
}
