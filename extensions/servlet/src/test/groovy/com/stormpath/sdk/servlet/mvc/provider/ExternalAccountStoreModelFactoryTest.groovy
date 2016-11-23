package com.stormpath.sdk.servlet.mvc.provider

import com.stormpath.sdk.application.Application
import com.stormpath.sdk.application.ApplicationAccountStoreMapping
import com.stormpath.sdk.application.ApplicationAccountStoreMappingCriteria
import com.stormpath.sdk.application.ApplicationAccountStoreMappingList
import com.stormpath.sdk.application.webconfig.ApplicationWebConfig
import com.stormpath.sdk.application.webconfig.LoginConfig
import com.stormpath.sdk.directory.AccountStoreVisitor
import com.stormpath.sdk.directory.Directory
import com.stormpath.sdk.provider.FacebookProvider
import com.stormpath.sdk.provider.GoogleProvider
import com.stormpath.sdk.provider.Provider
import com.stormpath.sdk.provider.saml.SamlProvider
import org.easymock.IAnswer
import org.hamcrest.Matchers
import org.springframework.mock.web.MockHttpServletRequest
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test

import static org.easymock.EasyMock.anyObject
import static org.easymock.EasyMock.createNiceMock
import static org.easymock.EasyMock.expect
import static org.easymock.EasyMock.expectLastCall
import static org.easymock.EasyMock.getCurrentArguments
import static org.easymock.EasyMock.replay
import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.Matchers.containsString
import static org.hamcrest.Matchers.instanceOf
import static org.hamcrest.Matchers.nullValue
import static org.hamcrest.Matchers.startsWith

class ExternalAccountStoreModelFactoryTest {
    static final String WEB_CONFIG_DOMAIN_NAME = "blah-blah.apps.stormpath.io"
    public static final String FACEBOOK_ACCOUNT_STORE_HREF = "http://facebook-account-store"
    public static final String GOOGLE_ACCOUNT_STORE_HREF = "http://google-account-store"
    public static final String SAML_ACCOUNT_STORE_HREF = "http://saml-account-store"

    MockHttpServletRequest request
    Application application
    ApplicationWebConfig applicationWebConfig
    LoginConfig loginConfig
    boolean loginConfigEnabled

    ExternalAccountStoreModelFactory factoryUT
    ApplicationAccountStoreMappingList applicationAccountStoreMappingList
    List<ApplicationAccountStoreMapping> accountStoreMappings
    Directory cloudAccountStore
    Directory facebookAccountStore
    Directory googleAccountStore
    Directory samlAccountStore

    @BeforeMethod
    void setUp() {
        initAccountStores()
        accountStoreMappings = []
        applicationAccountStoreMappingList = createNiceMock(ApplicationAccountStoreMappingList)
        expect(applicationAccountStoreMappingList.iterator()).andStubAnswer(new IAnswer<Iterator<ApplicationAccountStoreMapping>>() {
            @Override
            Iterator<ApplicationAccountStoreMapping> answer() throws Throwable {
                return accountStoreMappings.iterator()
            }
        })

        loginConfigEnabled = false
        loginConfig = createNiceMock(LoginConfig)
        expect(loginConfig.isEnabled()).andStubAnswer(new IAnswer<Boolean>() {
            @Override
            Boolean answer() throws Throwable {
                return loginConfigEnabled
            }
        })
        applicationWebConfig = createNiceMock(ApplicationWebConfig)
        expect(applicationWebConfig.login).andStubReturn(loginConfig)
        expect(applicationWebConfig.domainName).andStubReturn(WEB_CONFIG_DOMAIN_NAME)

        application = createNiceMock(Application)
        expect(application.getWebConfig()).andStubReturn(applicationWebConfig)
        expect(application.getAccountStoreMappings(anyObject() as ApplicationAccountStoreMappingCriteria))
                .andStubReturn(applicationAccountStoreMappingList)

        request = new MockHttpServletRequest()
        request.setAttribute(Application.class.name, application)

        replay(applicationWebConfig, loginConfig, application, applicationAccountStoreMappingList)

        factoryUT = new ExternalAccountStoreModelFactory()
    }

    def initAccountStores() {
        Provider cloudProvider = createNiceMock(Provider)
        expect(cloudProvider.href).andStubReturn("http://cloud-provider")
        cloudAccountStore = createNiceMock(Directory)
        expect(cloudAccountStore.href).andStubReturn("http://cloud-account-store")
        expect(cloudAccountStore.provider).andStubReturn(cloudProvider)
        initAcceptVisitor(cloudAccountStore)

        Provider googleProvider = createNiceMock(GoogleProvider)
        expect(googleProvider.href).andStubReturn("http://google-provider")
        expect(googleProvider.providerId).andStubReturn("google")
        expect(googleProvider.clientId).andStubReturn("google-client-id")
        googleAccountStore = createNiceMock(Directory)
        expect(googleAccountStore.href).andStubReturn(GOOGLE_ACCOUNT_STORE_HREF)
        expect(googleAccountStore.provider).andStubReturn(googleProvider)
        initAcceptVisitor(googleAccountStore)

        Provider facebookProvider = createNiceMock(FacebookProvider)
        expect(facebookProvider.href).andStubReturn("http://google-provider")
        expect(facebookProvider.providerId).andStubReturn("google")
        expect(facebookProvider.clientId).andStubReturn("google-client-id")
        facebookAccountStore = createNiceMock(Directory)
        expect(facebookAccountStore.href).andStubReturn(FACEBOOK_ACCOUNT_STORE_HREF)
        expect(facebookAccountStore.provider).andStubReturn(facebookProvider)
        initAcceptVisitor(facebookAccountStore)

        Provider samlProvider = createNiceMock(SamlProvider)
        expect(samlProvider.href).andStubReturn("http://saml-provider")
        expect(samlProvider.providerId).andStubReturn("saml")
        samlAccountStore = createNiceMock(Directory)
        expect(samlAccountStore.href).andStubReturn(SAML_ACCOUNT_STORE_HREF)
        expect(samlAccountStore.provider).andStubReturn(samlProvider)
        initAcceptVisitor(samlAccountStore)

        replay(cloudProvider, cloudAccountStore,
                googleProvider, googleAccountStore,
                facebookProvider, facebookAccountStore,
                samlProvider, samlAccountStore
        )
    }

    static def initAcceptVisitor(Directory accountStore) {
        accountStore.accept(anyObject() as AccountStoreVisitor)
        expectLastCall().andStubAnswer(new IAnswer<Void>() {
            @Override
            Void answer() throws Throwable {
                //noinspection GroovyAssignabilityCheck
                AccountStoreVisitor visitor = getCurrentArguments()[0] as AccountStoreVisitor
                visitor.visit(accountStore)
                return null
            }
        })
    }

    @Test
    void testEmptyAccountStoreMappings() {
        def actual = factoryUT.getAccountStores(request)
        assertThat(actual, Matchers.hasSize(0))
    }

    @Test
    void testCloudAccountStoreMapping() {
        addMapping(cloudAccountStore)

        def actual = factoryUT.getAccountStores(request)
        assertThat(actual, Matchers.hasSize(0))

    }

    @Test
    void testGoogleAccountStoreMapping() {
        addMapping(googleAccountStore)
        def actual = factoryUT.getAccountStores(request)

        assertThat(actual, Matchers.hasSize(1))
        def accountStoreModel = actual[0]
        assertThat(accountStoreModel.provider, instanceOf(GoogleOAuthProviderModel))
        assertThat("authorizeUri", accountStoreModel.authorizeUri, nullValue())
    }

    @Test
    void testOAuthAccountStoreMapping() {
        addMapping(facebookAccountStore)
        def actual = factoryUT.getAccountStores(request)

        assertThat(actual, Matchers.hasSize(1))
        def accountStoreModel = actual[0]
        assertThat(accountStoreModel.provider, instanceOf(DefaultOAuthProviderModel))
        assertThat("authorizeUri", accountStoreModel.authorizeUri, nullValue())
    }

    @Test
    void testSamlAccountStoreMapping() {
        addMapping(samlAccountStore)
        def actual = factoryUT.getAccountStores(request)

        assertThat(actual, Matchers.hasSize(1))
        def accountStoreModel = actual[0]
        assertThat(accountStoreModel.provider, instanceOf(DefaultProviderModel))
        assertThat("authorizeUri", accountStoreModel.authorizeUri, nullValue())
    }

    @Test
    void testOAuthAccountStoreMappingWithWebConfigLoginEnabled() {
        loginConfigEnabled = true
        addMapping(facebookAccountStore)
        def actual = factoryUT.getAccountStores(request)

        assertThat(actual, Matchers.hasSize(1))
        def accountStoreModel = actual[0]
        assertThat(accountStoreModel.provider, instanceOf(DefaultOAuthProviderModel))
        assertThat("authorizeUri", accountStoreModel.authorizeUri, containsString("response_type=stormpath_token"))
        assertThat("authorizeUri", accountStoreModel.authorizeUri, containsString("account_store_href=${URLEncoder.encode(FACEBOOK_ACCOUNT_STORE_HREF, "UTF-8")}"))
        assertThat("authorizeUri", accountStoreModel.authorizeUri, startsWith("https://${WEB_CONFIG_DOMAIN_NAME}/authorize"))
    }

    private ApplicationAccountStoreMapping addMapping(Directory directory) {
        ApplicationAccountStoreMapping mapping = createNiceMock(ApplicationAccountStoreMapping)
        expect(mapping.application).andStubReturn(application)
        expect(mapping.accountStore).andStubReturn(directory)
        accountStoreMappings.add(mapping)
        replay(mapping)
        mapping
    }

}
