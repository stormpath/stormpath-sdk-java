package com.stormpath.sdk.impl.authc

import com.stormpath.sdk.account.Account
import com.stormpath.sdk.account.AccountStatus
import com.stormpath.sdk.api.ApiKey
import com.stormpath.sdk.api.ApiKeyOptions
import com.stormpath.sdk.api.ApiKeyStatus
import com.stormpath.sdk.application.Application
import com.stormpath.sdk.impl.ds.InternalDataStore
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test

import javax.crypto.BadPaddingException

import static org.easymock.EasyMock.anyObject
import static org.easymock.EasyMock.createStrictMock
import static org.easymock.EasyMock.eq
import static org.easymock.EasyMock.expect
import static org.easymock.EasyMock.replay
import static org.easymock.EasyMock.verify
import static org.testng.Assert.assertNotNull

/**
 * @since 1.0.4
 */
class BasicApiAuthenticatorTest {

    private static final String keyId = "keyId"
    private static final String keySecret = "keySecret"

    ApiKey mockApiKey
    Application mockApplication
    Account mockAccount
    InternalDataStore mockDataStore

    BasicApiAuthenticator authenticator

    @BeforeMethod
    void setup() {
        mockApiKey = createStrictMock(ApiKey)
        mockApplication = createStrictMock(Application)
        mockAccount = createStrictMock(Account)
        mockDataStore = createStrictMock(InternalDataStore)

        authenticator = new BasicApiAuthenticator(mockDataStore)
    }

    @Test
    void testAuthenticationNormalWorkflow() {
        expect(mockApplication.getApiKey(eq(keyId), anyObject(ApiKeyOptions.class))).andReturn mockApiKey
        expect(mockApiKey.getSecret()).andReturn keySecret
        expect(mockApiKey.getStatus()).andReturn ApiKeyStatus.ENABLED
        expect(mockApiKey.getAccount()).andReturn mockAccount
        expect(mockAccount.getStatus()).andReturn AccountStatus.ENABLED

        replay mockAccount, mockApiKey, mockApplication, mockDataStore

        def result = authenticator.authenticate(mockApplication, keyId, keySecret)
        assertNotNull(result)

        verify mockAccount, mockApiKey, mockApplication, mockDataStore
    }

    @Test
    void testAuthenticationOneFailure() {
        expect(mockApplication.getApiKey(eq(keyId), anyObject(ApiKeyOptions.class))).andThrow new RuntimeException(new BadPaddingException())
        expect(mockApplication.getHref()).andReturn "http://some_href"
        expect(mockApplication.getApiKey(eq(keyId), anyObject(ApiKeyOptions.class))).andReturn mockApiKey
        expect(mockApiKey.getSecret()).andReturn keySecret
        expect(mockApiKey.getStatus()).andReturn ApiKeyStatus.ENABLED
        expect(mockApiKey.getAccount()).andReturn mockAccount
        expect(mockAccount.getStatus()).andReturn AccountStatus.ENABLED

        replay mockAccount, mockApiKey, mockApplication, mockDataStore

        def result = authenticator.authenticate(mockApplication, keyId, keySecret)
        assertNotNull(result)

        verify mockAccount, mockApiKey, mockApplication, mockDataStore
    }

    @Test(expectedExceptions = RuntimeException.class)
    void testAuthenticationFailure() {
        expect(mockApplication.getApiKey(eq(keyId), anyObject(ApiKeyOptions.class))).andThrow(new RuntimeException(new BadPaddingException()))
        expect(mockApplication.getHref()).andReturn("http://some_href")
        expect(mockApplication.getApiKey(eq(keyId), anyObject(ApiKeyOptions.class))).andThrow(new RuntimeException(new BadPaddingException()))
        expect(mockApplication.getHref()).andReturn("http://some_href")
        expect(mockApplication.getApiKey(eq(keyId), anyObject(ApiKeyOptions.class))).andThrow(new RuntimeException(new BadPaddingException()))
        expect(mockApplication.getHref()).andReturn("http://some_href")
        expect(mockApiKey.getSecret()).andReturn keySecret
        expect(mockApiKey.getStatus()).andReturn ApiKeyStatus.ENABLED
        expect(mockApiKey.getAccount()).andReturn mockAccount
        expect(mockAccount.getStatus()).andReturn AccountStatus.ENABLED

        replay mockAccount, mockApiKey, mockApplication, mockDataStore

        authenticator.authenticate(mockApplication, keyId, keySecret)

        verify mockAccount, mockApiKey, mockApplication, mockDataStore
    }
}
