package com.stormpath.sdk.servlet.mvc.provider

import com.stormpath.sdk.directory.Directory
import com.stormpath.sdk.provider.GoogleProvider
import com.stormpath.sdk.provider.OAuthProvider
import com.stormpath.sdk.provider.Provider
import com.stormpath.sdk.provider.saml.SamlProvider
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test

import static org.easymock.EasyMock.createNiceMock
import static org.easymock.EasyMock.expect
import static org.easymock.EasyMock.replay
import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.Matchers.equalTo
import static org.hamcrest.Matchers.nullValue
import static org.hamcrest.Matchers.sameInstance

class DefaultAccountStoreModelTest {
    final String AUTHORIZE_BASE_URI = "http://my-app.server.com"
    final String DIR_HREF = "http://api.stormpath.com/v1/directories/abc123"
    final String DIR_NAME = "Directory Name"
    final String PROVIDER_HREF = "${DIR_HREF}/provider"
    final String AUTHORIZE_URI = "${AUTHORIZE_BASE_URI}/authorize?response_type=stormpath_token&account_store_href=${URLEncoder.encode(DIR_HREF, "UTF-8")}"

    Directory directory

    @BeforeMethod
    void setUp() {
        directory = createNiceMock(Directory)
        expect(directory.href).andStubReturn(DIR_HREF)
        expect(directory.name).andStubReturn(DIR_NAME)
        replay(directory)
    }

    @Test
    void testWithDefaultProvider() {
        Provider provider = createNiceMock(Provider)
        expect(provider.href).andStubReturn(PROVIDER_HREF)
        expect(provider.providerId).andStubReturn("stormpath")
        replay(provider)
        ProviderModel providerModel = new DefaultProviderModel(provider)

        DefaultAccountStoreModel modelUT = new DefaultAccountStoreModel(directory, providerModel, AUTHORIZE_BASE_URI)
        assertThat("href", modelUT.href, equalTo(DIR_HREF))
        assertThat("name", modelUT.name, equalTo(DIR_NAME))
        assertThat("provider", modelUT.provider, sameInstance(providerModel as ProviderModel))
        assertThat("authorizeUri", modelUT.authorizeUri, nullValue())
    }

    @Test
    void testWithSamlProvider() {
        Provider provider = createNiceMock(SamlProvider)
        expect(provider.href).andStubReturn(PROVIDER_HREF)
        expect(provider.providerId).andStubReturn("saml")
        replay(provider)
        ProviderModel providerModel = new DefaultProviderModel(provider)

        DefaultAccountStoreModel modelUT = new DefaultAccountStoreModel(directory, providerModel, AUTHORIZE_BASE_URI)
        assertThat("href", modelUT.href, equalTo(DIR_HREF))
        assertThat("name", modelUT.name, equalTo(DIR_NAME))
        assertThat("provider", modelUT.provider, sameInstance(providerModel as ProviderModel))
        assertThat("authorizeUri", modelUT.authorizeUri, nullValue())
    }

    @Test
    void testWithGoogleProvider() {
        Provider provider = createNiceMock(GoogleProvider)
        expect(provider.href).andStubReturn(PROVIDER_HREF)
        expect(provider.providerId).andStubReturn("google")
        expect(provider.clientId).andStubReturn("12345657")
        replay(provider)
        ProviderModel providerModel = new GoogleOAuthProviderModel(provider)

        DefaultAccountStoreModel modelUT = new DefaultAccountStoreModel(directory, providerModel, AUTHORIZE_BASE_URI)
        assertThat("href", modelUT.href, equalTo(DIR_HREF))
        assertThat("name", modelUT.name, equalTo(DIR_NAME))
        assertThat("provider", modelUT.provider, sameInstance(providerModel as ProviderModel))
        assertThat("authorizeUri", modelUT.authorizeUri, equalTo(AUTHORIZE_URI))
    }

    @Test
    void testWithDefaultOAuthProvider() {
        Provider provider = createNiceMock(OAuthProvider)
        expect(provider.href).andStubReturn(PROVIDER_HREF)
        expect(provider.providerId).andStubReturn("facebook")
        expect(provider.clientId).andStubReturn("12345657")
        replay(provider)
        ProviderModel providerModel = new DefaultOAuthProviderModel(provider)

        DefaultAccountStoreModel modelUT = new DefaultAccountStoreModel(directory, providerModel, AUTHORIZE_BASE_URI)
        assertThat("href", modelUT.href, equalTo(DIR_HREF))
        assertThat("name", modelUT.name, equalTo(DIR_NAME))
        assertThat("provider", modelUT.provider, sameInstance(providerModel as ProviderModel))
        assertThat("authorizeUri", modelUT.authorizeUri, equalTo(AUTHORIZE_URI))
    }

    @Test
    void testWithDefaultProviderAndMalformedBaseAuthorizeUri() {
        Provider provider = createNiceMock(Provider)
        expect(provider.href).andStubReturn(PROVIDER_HREF)
        expect(provider.providerId).andStubReturn("stormpath")
        replay(provider)
        ProviderModel providerModel = new DefaultProviderModel(provider)

        DefaultAccountStoreModel modelUT = new DefaultAccountStoreModel(directory, providerModel, "this is not a uri")
        assertThat("href", modelUT.href, equalTo(DIR_HREF))
        assertThat("name", modelUT.name, equalTo(DIR_NAME))
        assertThat("provider", modelUT.provider, sameInstance(providerModel as ProviderModel))
        assertThat("authorizeUri", modelUT.authorizeUri, nullValue())
    }

    @Test
    void testWithSamlProviderAndMalformedBaseAuthorizeUri() {
        Provider provider = createNiceMock(SamlProvider)
        expect(provider.href).andStubReturn(PROVIDER_HREF)
        expect(provider.providerId).andStubReturn("saml")
        replay(provider)
        ProviderModel providerModel = new DefaultProviderModel(provider)

        DefaultAccountStoreModel modelUT = new DefaultAccountStoreModel(directory, providerModel, "this is not a uri")
        assertThat("href", modelUT.href, equalTo(DIR_HREF))
        assertThat("name", modelUT.name, equalTo(DIR_NAME))
        assertThat("provider", modelUT.provider, sameInstance(providerModel as ProviderModel))
        assertThat("authorizeUri", modelUT.authorizeUri, nullValue())
    }

    @Test(expectedExceptions = IllegalArgumentException)
    void testWithGoogleProviderAndMalformedBaseAuthorizeUri() {
        Provider provider = createNiceMock(GoogleProvider)
        expect(provider.href).andStubReturn(PROVIDER_HREF)
        expect(provider.providerId).andStubReturn("google")
        expect(provider.clientId).andStubReturn("12345657")
        replay(provider)
        ProviderModel providerModel = new GoogleOAuthProviderModel(provider)

        new DefaultAccountStoreModel(directory, providerModel, "this is not a uri")
    }

    @Test(expectedExceptions = IllegalArgumentException)
    void testWithDefaultOAuthProviderAndMalformedBaseAuthorizeUri() {
        Provider provider = createNiceMock(OAuthProvider)
        expect(provider.href).andStubReturn(PROVIDER_HREF)
        expect(provider.providerId).andStubReturn("facebook")
        expect(provider.clientId).andStubReturn("12345657")
        replay(provider)
        ProviderModel providerModel = new DefaultOAuthProviderModel(provider)

        new DefaultAccountStoreModel(directory, providerModel, "this is not a uri")
    }


}
