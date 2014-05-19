package com.stormpath.sdk.impl.oauth.token;

import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.oauth.permission.TokenResponse;
import org.apache.oltu.oauth2.as.response.OAuthASResponse;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.apache.oltu.oauth2.common.message.OAuthResponse;
import org.apache.oltu.oauth2.common.message.types.TokenType;

import javax.servlet.http.HttpServletResponse;

/**
 * @since 1.0.RC
 */
public class DefaultTokenResponse implements TokenResponse {

    private final String accessToken;

    private final String scope;

    private final String expiresIn;

    private final String refreshToken;

    private final String tokenType;

    private final String applicationHref;

    private final OAuthResponse oAuthResponse;

    private DefaultTokenResponse(Builder builder) {
        accessToken = builder.accessToken;
        expiresIn = builder.expiresIn;
        refreshToken = builder.refreshToken;
        scope = builder.scope;
        tokenType = builder.tokenType;
        applicationHref = builder.applicationHref;

        Assert.hasText(accessToken);
        Assert.hasText(expiresIn);
        Assert.hasText(applicationHref);

        try {
            oAuthResponse = builder.tokenResponseBuilder.buildJSONMessage();
        } catch (OAuthSystemException e) {
            throw new IllegalStateException("Unexpected error when building Json Oauth response.", e);
        }
    }

    @Override
    public String getAccessToken() {
        return accessToken;
    }

    @Override
    public String getScope() {
        return scope;
    }

    @Override
    public String getExpiresIn() {
        return expiresIn;
    }

    @Override
    public String getRefreshToken() {
        return refreshToken;
    }

    @Override
    public String getApplicationHref() {
        return applicationHref;
    }

    @Override
    public String toJson() {
        return oAuthResponse.getBody();
    }

    public static Builder tokenType(TokenType tokenType) {
        return new Builder(tokenType);
    }

    public String getTokenType() {
        return tokenType;
    }

    public static class Builder {

        private String accessToken;
        private String expiresIn;
        private String refreshToken;
        private String scope;
        private String tokenType;
        private String applicationHref;

        private OAuthASResponse.OAuthTokenResponseBuilder tokenResponseBuilder;

        private Builder(TokenType tokenType) {
            Assert.notNull(tokenType);
            this.tokenType = tokenType.toString();
            tokenResponseBuilder = OAuthASResponse.tokenResponse(HttpServletResponse.SC_OK).setTokenType(this.tokenType);
        }

        public Builder accessToken(String accessToken) {
            this.accessToken = accessToken;
            tokenResponseBuilder.setAccessToken(accessToken);
            return this;
        }

        public Builder scope(String scope) {
            this.scope = scope;
            return this;
        }

        public Builder expiresIn(String expiresIn) {
            this.expiresIn = expiresIn;
            tokenResponseBuilder.setExpiresIn(expiresIn);
            return this;
        }

        public Builder refreshToken(String refreshToken) {
            this.refreshToken = refreshToken;
            tokenResponseBuilder.setRefreshToken(refreshToken);
            return this;
        }

        public Builder applicationHref(String applicationHref) {
            this.applicationHref = applicationHref;
            return this;
        }

        public TokenResponse build() {
            return new DefaultTokenResponse(this);
        }
    }
}
