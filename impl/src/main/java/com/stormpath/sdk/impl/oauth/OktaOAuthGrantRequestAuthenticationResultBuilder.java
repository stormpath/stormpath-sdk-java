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
package com.stormpath.sdk.impl.oauth;

import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.application.Application;
import com.stormpath.sdk.authc.OktaAuthNAuthenticator;
import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.oauth.AccessToken;
import com.stormpath.sdk.oauth.GrantAuthenticationToken;
import com.stormpath.sdk.oauth.OAuthGrantRequestAuthenticationResult;
import com.stormpath.sdk.oauth.OAuthRefreshTokenRequestAuthenticator;
import com.stormpath.sdk.oauth.OAuthRequestAuthentication;
import com.stormpath.sdk.oauth.OAuthRevocationRequest;
import com.stormpath.sdk.oauth.OAuthTokenRevocator;
import com.stormpath.sdk.oauth.RefreshToken;
import com.stormpath.sdk.oauth.TokenTypeHint;
import com.stormpath.sdk.okta.TokenIntrospectResponse;
import com.stormpath.sdk.tenant.Tenant;

import java.util.Map;

/**
 * @since 2.0.0
 */
public class OktaOAuthGrantRequestAuthenticationResultBuilder extends DefaultOAuthGrantRequestAuthenticationResultBuilder {

    private final OktaAuthNAuthenticator authenticator;
    private final OAuthTokenRevocator tokenRevocator;
    private final OAuthRefreshTokenRequestAuthenticator refreshTokenAuthenticator;
    private final Application application;

    public OktaOAuthGrantRequestAuthenticationResultBuilder(GrantAuthenticationToken grantAuthenticationToken, OktaAuthNAuthenticator authenticator, OAuthTokenRevocator tokenRevocator, OAuthRefreshTokenRequestAuthenticator refreshTokenAuthenticator, Application application) {
        super(grantAuthenticationToken);
        this.authenticator = authenticator;
        this.tokenRevocator = tokenRevocator;
        this.refreshTokenAuthenticator = refreshTokenAuthenticator;
        this.application = application;
    }
    @Override
    public DefaultOAuthGrantRequestAuthenticationResult build() {
        Assert.notNull(this.grantAuthenticationToken, "grantAuthenticationToken has not been set. It is a required attribute.");

        this.accessTokenString = grantAuthenticationToken.getAccessToken();
        this.accessToken = toOktaAccessToken(accessTokenString);
        this.idTokenString = grantAuthenticationToken.getIdToken();
        this.refreshTokenString = grantAuthenticationToken.getRefreshToken();
        this.accessTokenHref = grantAuthenticationToken.getAccessTokenHref();
        this.tokenType = grantAuthenticationToken.getTokenType();
        this.expiresIn = Integer.parseInt(grantAuthenticationToken.getExpiresIn());

        if (refreshTokenString != null) {
            // FIXME: this creates a _recursive_ loop, and the refresth token is NOT a JWT, so ignoring for now
            // a related IT does pass when this is commented out, but needs more investigation.
//            this.refreshToken = toOktaRefreshToken(refreshTokenString);
        }
        return new DefaultOAuthGrantRequestAuthenticationResult(this);
    }

    private AccessToken toOktaAccessToken(final String accessToken) {

        return new SimpleIntrospectAccessToken(accessToken, authenticator.getAccountByToken(accessToken), application) {

            @Override
            public void revoke() {
                OAuthRevocationRequest request = new DefaultOAuthRevocationRequest(getJwt(), TokenTypeHint.ACCESS_TOKEN);
                tokenRevocator.revoke(request);
            }
        };
    }

    private RefreshToken toOktaRefreshToken(final String refreshToken) {

        if (refreshTokenAuthenticator == null) {
            return null;
        }

        OAuthRequestAuthentication authenticationRequest = new DefaultOAuthRefreshTokenRequestAuthentication(refreshToken);
        final OAuthGrantRequestAuthenticationResult authResult = refreshTokenAuthenticator.authenticate(authenticationRequest);

        return new RefreshToken() {
            @Override
            public String getJwt() {
                // TODO: NOT a JWT
                return refreshToken;
            }

            @Override
            public Account getAccount() {
                return authResult.getAccessToken().getAccount();
            }

            @Override
            public Application getApplication() {
                return application;
            }

            @Override
            public Tenant getTenant() {
                return application.getTenant();
            }

            @Override
            public Map<String, Object> getExpandedJwt() {
                return null;
            }

            @Override
            public void revoke() {
                throw new UnsupportedOperationException("revoke() has not been implemented");
            }

            @Override
            public void delete() {
                throw new UnsupportedOperationException("delete() has not been implemented");
            }

            @Override
            public String getHref() {
                return null;
            }
        };

    }
}
