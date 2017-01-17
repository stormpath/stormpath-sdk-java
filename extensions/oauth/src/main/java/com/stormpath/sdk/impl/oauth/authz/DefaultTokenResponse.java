package com.stormpath.sdk.impl.oauth.authz;

import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.lang.Strings;
import com.stormpath.sdk.oauth.TokenResponse;
import org.apache.oltu.oauth2.as.response.OAuthASResponse;
import org.apache.oltu.oauth2.common.message.types.TokenType;
import org.json.JSONObject;

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

    private final JSONObject oAuthResponse;
    private final String idToken;

    private DefaultTokenResponse(Builder builder) {
        accessToken = builder.accessToken;
        expiresIn = builder.expiresIn;
        refreshToken = builder.refreshToken;
        idToken = builder.idToken;
        scope = builder.scope;
        tokenType = builder.tokenType;
        applicationHref = builder.applicationHref;

        Assert.hasText(accessToken);
        Assert.hasText(expiresIn);
        Assert.hasText(applicationHref);

        oAuthResponse = new JSONObject();
        initOAuthResponse();
    }

    private void initOAuthResponse() {
        oAuthResponse.put("token_type", tokenType);
        oAuthResponse.put("access_token", accessToken);
        oAuthResponse.put("expires_in", Long.parseLong(expiresIn));
        if (Strings.hasText(refreshToken)) {
            oAuthResponse.put("refresh_token", refreshToken);
        }
        if (Strings.hasText(idToken)) {
            oAuthResponse.put("id_token", idToken);
        }
    }

    @Override
    public String getAccessToken() {
        return accessToken;
    }

    @Override
    public String getIdToken() {
        return idToken;
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
        return oAuthResponse.toString();
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
        private String idToken;

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

        public Builder idToken(String idToken) {
            this.idToken = idToken;
            return this;
        }

        public Builder scope(String scope) {
            this.scope = scope;
            tokenResponseBuilder.setScope(scope);
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
