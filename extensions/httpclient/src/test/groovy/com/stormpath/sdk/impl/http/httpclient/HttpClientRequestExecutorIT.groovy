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
import com.stormpath.sdk.impl.http.Response
import com.stormpath.sdk.impl.http.authc.BasicRequestAuthenticator
import com.stormpath.sdk.impl.http.authc.RequestAuthenticator
import com.stormpath.sdk.impl.http.authc.RequestAuthenticatorFactory
import com.stormpath.sdk.impl.http.support.DefaultRequest
import com.stormpath.sdk.lang.Strings
import com.stormpath.sdk.oauth.Authenticators
import com.stormpath.sdk.oauth.OAuthPasswordGrantRequestAuthentication
import com.stormpath.sdk.oauth.OAuthRequests
import org.testng.annotations.Test

import java.util.concurrent.*

import static org.testng.Assert.assertEquals
import static org.testng.Assert.assertNotNull

class HttpClientRequestExecutorIT extends ClientIT {

    @Test //asserts https://github.com/stormpath/stormpath-sdk-java/issues/539
    void testNoWaitOnRedirect() throws Exception {

        //We will only run this test in Travis as it is always failing when executed in other environments. It seems that Travis is much faster
        // than other environments. Instead of restricting this test to only work in travis, we could increase the time window so other environments
        // have better chances of fulfilling the operation in the specified time but that will cause this test to become less reliable
        // as we may be failing to detect a redirection loop which is the sole purpose of this test
        def travisEnvVar = System.getenv().get("TRAVIS");
        if (travisEnvVar != null && travisEnvVar.equals("true")) {
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

            // verify the access token in Stormpath
            // fail if it takes longer than 500ms
            // this proves that we are *not* waiting on redirects
            def verifyUri = app.getHref() + "/authTokens/" + result.getAccessTokenString()

            def httpClientRequestExecutor = new HttpClientRequestExecutor(new ApiKeyCredentials(client.getApiKey()), null, AuthenticationScheme.SAUTHC1, null, 2000)
            httpClientRequestExecutor.setNumRetries(0)

            Callable<Response> callable = new Callable<Response>() {

                @Override
                Response call() throws Exception {
                    return httpClientRequestExecutor.executeRequest(new DefaultRequest(HttpMethod.GET, verifyUri))
                }
            }

            ExecutorService executorService = Executors.newCachedThreadPool()
            Future<Response> task = executorService.submit(callable)

            def response = task.get(500, TimeUnit.MILLISECONDS)
            assertEquals response.getHttpStatus(), 200
        }
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
