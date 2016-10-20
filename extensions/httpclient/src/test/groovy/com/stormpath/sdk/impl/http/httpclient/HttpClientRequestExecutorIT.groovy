package com.stormpath.sdk.impl.http.httpclient

import com.stormpath.sdk.account.Account
import com.stormpath.sdk.api.ApiKey
import com.stormpath.sdk.client.AuthenticationScheme
import com.stormpath.sdk.client.ClientIT
import com.stormpath.sdk.http.HttpMethod
import com.stormpath.sdk.impl.api.ClientApiKey
import com.stormpath.sdk.impl.authc.credentials.ApiKeyCredentials
import com.stormpath.sdk.impl.authc.credentials.ClientCredentials
import com.stormpath.sdk.impl.client.DefaultClientBuilder
import com.stormpath.sdk.impl.http.authc.BasicRequestAuthenticator
import com.stormpath.sdk.impl.http.authc.RequestAuthenticator
import com.stormpath.sdk.impl.http.authc.RequestAuthenticatorFactory
import com.stormpath.sdk.impl.http.support.BackoffStrategy
import com.stormpath.sdk.impl.http.support.DefaultRequest
import com.stormpath.sdk.oauth.Authenticators
import com.stormpath.sdk.oauth.OAuthPasswordGrantRequestAuthentication
import com.stormpath.sdk.oauth.OAuthRequests
import org.testng.annotations.Test

import static org.testng.Assert.assertEquals
import static org.testng.Assert.assertNotNull

class HttpClientRequestExecutorIT extends ClientIT {

    @Test//asserts https://github.com/stormpath/stormpath-sdk-java/issues/539
    void testNoWaitOnRedirect() {
        // create an temp application
        def app = createTempApp()

        // create an account
        def email = uniquify('testCreateToken+') + '@nowhere.com'

        Account account = client.instantiate(Account)
        account.givenName = 'John'
        account.surname = 'DELETEME'
        account.email = email
        account.password = 'Change&45+me1!'

        def created = app.createAccount(account)
        assertNotNull created.href
        deleteOnTeardown(created)

        // create an access token
        OAuthPasswordGrantRequestAuthentication createRequest = OAuthRequests.OAUTH_PASSWORD_GRANT_REQUEST.builder().setLogin(email).setPassword("Change&45+me1!").build()
        def result = Authenticators.OAUTH_PASSWORD_GRANT_REQUEST_AUTHENTICATOR.forApplication(app).authenticate(createRequest)

        // verify the access token in Stormpath - when the token is valid the response is a 302 (redirect) that will be followed by the HttpExecutor.
        def verifyUri = app.getHref() + "/authTokens/" + result.getAccessTokenString()

        def httpClientRequestExecutor = new HttpClientRequestExecutor(new ApiKeyCredentials(client.getApiKey()), null, AuthenticationScheme.SAUTHC1, null, 20)
        httpClientRequestExecutor.setNumRetries(0)
        // By setting the BackoffStrategy to an instance that throws an exception if this test pass we can be sure that the redirect does not pause.
        httpClientRequestExecutor.setBackoffStrategy(new BackoffStrategy() {
            @Override
            long getDelayMillis(int retryCount) {
                throw new IllegalStateException("should not be called.");
            }
        })

        def response = httpClientRequestExecutor.executeRequest(new DefaultRequest(HttpMethod.GET, verifyUri))

        assertEquals response.getHttpStatus(), 200

    }

    @Test
    void testConfigureRequestAuthenticatorFactory(){

        def id = UUID.randomUUID().toString()
        def secret = UUID.randomUUID().toString()

        ApiKey apiKey = new ClientApiKey(id, secret)
        def creds = new ApiKeyCredentials(apiKey)
        def requestAuthenticator = new BasicRequestAuthenticator(creds)

        def requestAuthenticatorFactory = new RequestAuthenticatorFactory() {
            @Override
            RequestAuthenticator create(AuthenticationScheme scheme, ClientCredentials clientCredentials) {
                return requestAuthenticator
            }
        }

        def builder = new DefaultClientBuilder()
        builder.setClientCredentials(creds)
        builder.setRequestAuthenticatorFactory(requestAuthenticatorFactory)
        def testClient = builder.build()

        assertEquals(testClient.dataStore.requestExecutor.requestAuthenticator, requestAuthenticator)
    }

}
