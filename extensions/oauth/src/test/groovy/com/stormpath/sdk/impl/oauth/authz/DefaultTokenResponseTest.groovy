package com.stormpath.sdk.impl.oauth.authz

import com.stormpath.sdk.oauth.TokenResponse
import org.apache.oltu.oauth2.common.message.types.TokenType
import org.hamcrest.Matchers
import org.json.JSONObject
import org.testng.annotations.Test

import static Matchers.is
import static org.hamcrest.MatcherAssert.assertThat

class DefaultTokenResponseTest {

    public static final String APP_HREF = "http://test.app.href.com"
    public static final String ACCESS_TOKEN = "testAccessToken"
    public static final String REFRESH_TOKEN = "testRefreshToken"
    public static final String ID_TOKEN = "testIdToken"
    public static final String SCOPE = "test scope"
    public static final String EXPIRES_IN = "3600"
    public static final String TOKEN_TYPE = "Bearer"

    @Test
    void testBuildCompleteResponse() {
        TokenResponse tokenResponse = DefaultTokenResponse.tokenType(TokenType.BEARER)
                .accessToken(ACCESS_TOKEN)
                .refreshToken(REFRESH_TOKEN)
                .idToken(ID_TOKEN)
                .scope(SCOPE)
                .expiresIn(EXPIRES_IN)
                .applicationHref(APP_HREF)
                .build()
        assertThat(tokenResponse.tokenType, is(TOKEN_TYPE))
        assertThat(tokenResponse.accessToken, is(ACCESS_TOKEN))
        assertThat(tokenResponse.refreshToken, is(REFRESH_TOKEN))
        assertThat(tokenResponse.idToken, is(ID_TOKEN))
        assertThat(tokenResponse.scope, is(SCOPE))
        assertThat(tokenResponse.expiresIn, is(EXPIRES_IN))
        assertThat(tokenResponse.applicationHref, is(APP_HREF))
    }

    @Test
    void testJsonWithOnlyAccessToken() {
        TokenResponse tokenResponse = DefaultTokenResponse.tokenType(TokenType.BEARER)
                .accessToken(ACCESS_TOKEN)
                .expiresIn(EXPIRES_IN)
                .applicationHref(APP_HREF)
                .build()

        String json = tokenResponse.toJson()
        JSONObject actual = new JSONObject(json)
        assertField(actual, "token_type", TOKEN_TYPE)
        assertField(actual, "access_token", ACCESS_TOKEN)
        assertField(actual, "expires_in", EXPIRES_IN)
        assertNoField(actual, "refresh_token")
        assertNoField(actual, "id_token")
    }

    @Test
    void testJsonWithAccessAndRefreshTokens() {
        TokenResponse tokenResponse = DefaultTokenResponse.tokenType(TokenType.BEARER)
                .accessToken(ACCESS_TOKEN)
                .refreshToken(REFRESH_TOKEN)
                .expiresIn(EXPIRES_IN)
                .applicationHref(APP_HREF)
                .build()

        String json = tokenResponse.toJson()
        JSONObject actual = new JSONObject(json)
        assertField(actual, "token_type", TOKEN_TYPE)
        assertField(actual, "access_token", ACCESS_TOKEN)
        assertField(actual, "refresh_token", REFRESH_TOKEN)
        assertField(actual, "expires_in", EXPIRES_IN)
        assertNoField(actual, "id_token")
    }

    @Test
    void testJsonWithAccessAndRefreshAndIdTokens() {
        TokenResponse tokenResponse = DefaultTokenResponse.tokenType(TokenType.BEARER)
                .accessToken(ACCESS_TOKEN)
                .refreshToken(REFRESH_TOKEN)
        .idToken(ID_TOKEN)
                .expiresIn(EXPIRES_IN)
                .applicationHref(APP_HREF)
                .build()

        String json = tokenResponse.toJson()
        JSONObject actual = new JSONObject(json)
        assertField(actual, "token_type", TOKEN_TYPE)
        assertField(actual, "access_token", ACCESS_TOKEN)
        assertField(actual, "refresh_token", REFRESH_TOKEN)
        assertField(actual, "id_token", ID_TOKEN)
        assertField(actual, "expires_in", EXPIRES_IN)
    }

    private static void assertField(JSONObject actual, String field, String expected) {
        assertThat("${field} in ${actual.toString(2)}", actual.optString(field), is(expected))
    }

    private static void assertNoField(JSONObject actual, String field) {
        assertThat("${field} present in ${actual.toString(2)}", actual.has(field), is(false))
    }
}
