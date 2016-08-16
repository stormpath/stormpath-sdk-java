package com.stormpath.sdk.impl.http.httpclient

import com.stormpath.sdk.account.Account
import com.stormpath.sdk.client.AuthenticationScheme
import com.stormpath.sdk.client.ClientIT
import com.stormpath.sdk.http.HttpMethod
import com.stormpath.sdk.impl.api.ApiKeyCredentials
import com.stormpath.sdk.impl.http.Response
import com.stormpath.sdk.impl.http.support.DefaultRequest
import com.stormpath.sdk.oauth.Authenticators
import com.stormpath.sdk.oauth.OAuthRequests
import com.stormpath.sdk.oauth.OAuthPasswordGrantRequestAuthentication
import org.testng.annotations.Test

import java.util.concurrent.Callable
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit

import static org.testng.Assert.assertEquals
import static org.testng.Assert.assertNotNull

class HttpClientRequestExecutorIT extends ClientIT {

    @Test //asserts https://github.com/stormpath/stormpath-sdk-java/issues/539
    void testNoWaitOnRedirect() throws Exception {

        // create an temp application
        def app = createTempApp()

        // create an account
        def email = uniquify('testCreateToken+') + '@nowhere.com'

        Account account = client.instantiate(Account)
        account.givenName = 'John'
        account.surname = 'DELETEME'
        account.email =  email
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
