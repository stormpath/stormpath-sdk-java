package com.stormpath.sdk.impl.authc

import com.stormpath.sdk.account.Account
import com.stormpath.sdk.application.okta.OktaTokenRequest
import com.stormpath.sdk.application.okta.OktaTokenResponse
import com.stormpath.sdk.impl.application.okta.OktaSigningKeyResolver
import com.stormpath.sdk.impl.ds.InternalDataStore
import com.stormpath.sdk.impl.http.HttpHeaders
import com.stormpath.sdk.impl.http.MediaType
import io.jsonwebtoken.Claims
import io.jsonwebtoken.JwsHeader
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.impl.DefaultClaims
import io.jsonwebtoken.impl.TextCodec
import org.testng.annotations.Test

import javax.crypto.spec.SecretKeySpec
import java.security.Key

import static org.easymock.EasyMock.*
import static org.hamcrest.Matchers.*
import static org.hamcrest.MatcherAssert.*

/**
 * Tests for {@link OktaAuthNAuthenticator}.
 */
class OktaAuthNAuthenticatorTest {

    @Test
    void authenticationTest() {

        def username = "joe.coder@example.com"
        def password = "test_password"
        def authRequest = new DefaultUsernamePasswordRequest(username, password.chars)
        def key = "somekeyblablabla"

        def accessToken =  Jwts.builder()
            .setClaims(new DefaultClaims([
                    scp: ["one", "two"],
                    uid: "011110"
                ])
                .setSubject(username))
            .signWith(SignatureAlgorithm.HS512, key)
            .compact()

        def mockDataStore = createMock(InternalDataStore)
        def oktaTokenRequest = createMock(OktaTokenRequest)
        def oktaTokenResponse = createMock(OktaTokenResponse)
        def signingKeyResolver = createMock(OktaSigningKeyResolver)
        def account = createMock(Account)

        expect(mockDataStore.instantiate(OktaTokenRequest)).andReturn(oktaTokenRequest)
        expect(oktaTokenRequest.setPassword(password)).andReturn(oktaTokenRequest)
        expect(oktaTokenRequest.setUsername(username)).andReturn(oktaTokenRequest)
        expect(oktaTokenRequest.setGrantType("password")).andReturn(oktaTokenRequest)
        expect(oktaTokenRequest.setScope("offline_access")).andReturn(oktaTokenRequest)

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        expect(mockDataStore.create("/oauth2/v1/token", oktaTokenRequest, OktaTokenResponse, httpHeaders)).andReturn(oktaTokenResponse)
        expect(oktaTokenResponse.getAccessToken()).andReturn(accessToken)

        // TODO: pull this out into own class
        expect(mockDataStore.instantiate(OktaSigningKeyResolver)).andReturn(signingKeyResolver)
        expect(signingKeyResolver.resolveSigningKey(anyObject(JwsHeader), anyObject(Claims))).andReturn(getKey(key))
        expect(mockDataStore.getResource("/api/v1/users/011110", Account)).andReturn(account)


        replay mockDataStore, oktaTokenRequest, oktaTokenResponse, signingKeyResolver

        def authResult = new OktaAuthNAuthenticator(mockDataStore).authenticate(authRequest)
        assertThat authResult.getHref(), nullValue()
        assertThat authResult.getAccount(), sameInstance(account)

        verify mockDataStore, oktaTokenRequest, oktaTokenResponse, signingKeyResolver
    }


    private Key getKey(String key, SignatureAlgorithm algorithm = SignatureAlgorithm.HS512) {
        return new SecretKeySpec(TextCodec.BASE64.decode(key), algorithm.name())
    }

}

