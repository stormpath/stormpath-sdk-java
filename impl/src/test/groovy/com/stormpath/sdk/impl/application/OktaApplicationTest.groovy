package com.stormpath.sdk.impl.application

import com.stormpath.sdk.account.Account
import com.stormpath.sdk.account.AccountStatus
import com.stormpath.sdk.account.Accounts
import com.stormpath.sdk.account.EmailVerificationStatus
import com.stormpath.sdk.account.EmailVerificationToken
import com.stormpath.sdk.api.ApiKey
import com.stormpath.sdk.directory.CustomData
import com.stormpath.sdk.impl.account.DefaultAccount
import com.stormpath.sdk.impl.account.DefaultVerificationEmailRequest
import com.stormpath.sdk.impl.client.DefaultClient
import com.stormpath.sdk.impl.ds.InternalDataStore
import com.stormpath.sdk.impl.okta.OktaUserAccountConverter
import com.stormpath.sdk.impl.resource.DefaultVoidResource
import com.stormpath.sdk.mail.EmailRequest
import com.stormpath.sdk.mail.EmailService
import com.stormpath.sdk.okta.OIDCWellKnownResource
import com.stormpath.sdk.okta.OktaActivateAccountResponse
import com.stormpath.sdk.okta.OktaUserToApplicationMapping
import com.stormpath.sdk.resource.VoidResource
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jws
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.impl.compression.CompressionCodecs
import org.easymock.Capture
import org.easymock.EasyMock
import org.easymock.IAnswer
import org.testng.annotations.Test

import static org.easymock.EasyMock.*
import static org.powermock.api.easymock.PowerMock.createMock
import static org.powermock.api.easymock.PowerMock.verify
import static org.hamcrest.Matchers.*
import static org.hamcrest.MatcherAssert.*
import static org.testng.Assert.*

/**
 * Tests for {@link OktaApplication}.
 */
class OktaApplicationTest {

    @Test
    void sendVerificationEmailTest() {

        def email = "test@example.com"
        def clientId = "test_client_id"
        def apiKeyScret = "api_key_secret"
        def internalDataStore = createMock(InternalDataStore)
        def client = createMock(DefaultClient)
        def emailService = createMock(EmailService)
        def wellKnown = createNiceMock(OIDCWellKnownResource)
        def account = createMock(Account)
        def apiKey = createMock(ApiKey)
        def customData = createMock(CustomData)
        def tokenCapture = new Capture<String>()
        def emailRequestCapture = new Capture<EmailRequest>()

        expect(internalDataStore.getBaseUrl()).andReturn("http://base.example.com").anyTimes()
        expect(internalDataStore.getResource("/oauth2/test_authorization_server_id/.well-known/openid-configuration", OIDCWellKnownResource)).andReturn(wellKnown)
        client.setTenantResolver(anyObject())

        // test specifics
        expect(internalDataStore.getResource("/api/v1/users/"+email, Account)).andReturn(account)
        expect(account.getEmail()).andReturn(email)
        expect(account.getHref()).andReturn("/api/v1/users/uid")
        expect(internalDataStore.getApiKey()).andReturn(apiKey)
        expect(apiKey.getSecret()).andReturn(apiKeyScret)
        expect(account.setEmailVerificationStatus(EmailVerificationStatus.UNVERIFIED)).andReturn(account)
        expect(account.getCustomData()).andReturn(customData)
        expect(customData.put(eq(OktaUserAccountConverter.STORMPATH_EMAIL_VERIFICATION_TOKEN), capture(tokenCapture))).andReturn(null)
        account.save()

        expect(account.getFullName()).andReturn("Joe Coder")
        expect(account.getEmail()).andReturn(email)

        def request = new DefaultVerificationEmailRequest(internalDataStore)
        request.setLogin(email)
        emailService.sendValidationEmail(capture(emailRequestCapture))

        replay internalDataStore, client, wellKnown, emailService, account, apiKey, customData

        def app = new OktaApplication(clientId, internalDataStore)

        def appProps = [
                authorizationServerId: "test_authorization_server_id",
                emailService: emailService,
                registrationWorkflowEnabled: false,
                client: client
        ]
        app.configureWithProperties(appProps)
        app.sendVerificationEmail(request)

        verify internalDataStore, client, wellKnown, emailService, account, apiKey, customData

        // ok, now we can validate the captures
        EmailRequest emailRequestActual = emailRequestCapture.getValue()

        assertThat emailRequestActual.getToAddress(), equalTo(email)
        assertThat emailRequestActual.getToDisplayName(), equalTo("Joe Coder")
        assertThat emailRequestActual.getToken(),  equalTo(tokenCapture.getValue())

        // now for the JWT, pretty easy to validate this one
        Jws<Claims> claim = Jwts.parser()
                                .setSigningKey(apiKeyScret)
                                .requireSubject(email)
                                .require("tokenType", "verify")
                                .require("userHref", "/api/v1/users/uid")
                                .parseClaimsJws(emailRequestActual.getToken())

        assertNotNull claim.getBody().get("verifyToken")
        assertNull claim.getBody().getExpiration() // does not expire
    }


    @Test
    void verifyAccountEmailTest() {

        def email = "test@example.com"
        def clientId = "test_client_id"
        def apiKeyScret = "api_key_secret"
        def internalDataStore = createMock(InternalDataStore)
        def client = createMock(DefaultClient)
        def emailService = createMock(EmailService)
        def wellKnown = createNiceMock(OIDCWellKnownResource)
        def account = createMock(Account)
        def apiKey = createMock(ApiKey)
        def customData = createMock(CustomData)
        def emailVerificationToken = createMock(EmailVerificationToken)

        def jwt =  Jwts.builder()
                .setSubject(email)
                .claim("tokenType", "verify")
                .claim("verifyToken", "this token could be anything")
                .claim("userHref", "/api/v1/users/uid")
                .compressWith(CompressionCodecs.DEFLATE)
                .signWith(SignatureAlgorithm.HS512, apiKeyScret)
                .compact()

        expect(internalDataStore.getBaseUrl()).andReturn("http://base.example.com").anyTimes()
        expect(internalDataStore.getResource("/oauth2/test_authorization_server_id/.well-known/openid-configuration", OIDCWellKnownResource)).andReturn(wellKnown)
        client.setTenantResolver(anyObject())

        // test specifics
        expect(internalDataStore.getResource("/api/v1/users/uid", Account)).andReturn(account)
        expect(internalDataStore.instantiate(OktaActivateAccountResponse)).andReturn(null)
        expect(internalDataStore.create("/api/v1/users/uid/lifecycle/activate?sendEmail=false", null)).andReturn(null)
        expect(internalDataStore.getApiKey()).andReturn(apiKey)
        expect(apiKey.getSecret()).andReturn(apiKeyScret)

        expect(account.getEmailVerificationToken()).andReturn(emailVerificationToken).anyTimes()
        expect(emailVerificationToken.getValue()).andReturn(jwt)
        expect(account.setEmailVerificationStatus(EmailVerificationStatus.VERIFIED)).andReturn(account)
        expect(account.setStatus(AccountStatus.ENABLED)).andReturn(account)
        expect(account.getCustomData()).andReturn(customData)
        expect(customData.put(OktaUserAccountConverter.STORMPATH_EMAIL_VERIFICATION_TOKEN, null)).andReturn(null)
        account.save()

        replay internalDataStore, client, wellKnown, emailService, account, apiKey, customData, emailVerificationToken

        def app = new OktaApplication(clientId, internalDataStore)

        def appProps = [
                authorizationServerId: "test_authorization_server_id",
                emailService: emailService,
                registrationWorkflowEnabled: false,
                client: client
        ]
        app.configureWithProperties(appProps)
        app.verifyAccountEmail(jwt)

        verify internalDataStore, client, wellKnown, emailService, account, apiKey, customData, emailVerificationToken
    }

    @Test
    void verifyAccountEmailInvalidTokenTest() {

        def email = "test@example.com"
        def clientId = "test_client_id"
        def apiKeyScret = "api_key_secret"
        def internalDataStore = createMock(InternalDataStore)
        def client = createMock(DefaultClient)
        def emailService = createMock(EmailService)
        def wellKnown = createNiceMock(OIDCWellKnownResource)
        def account = createMock(Account)
        def apiKey = createMock(ApiKey)
        def customData = createMock(CustomData)
        def emailVerificationToken = createMock(EmailVerificationToken)

        def jwt =  Jwts.builder()
                .setSubject(email)
                .claim("tokenType", "verify")
                .claim("verifyToken", "this token could be anything")
                .claim("userHref", "/api/v1/users/uid")
                .compressWith(CompressionCodecs.DEFLATE)
                .signWith(SignatureAlgorithm.HS512, apiKeyScret)
                .compact()

        expect(internalDataStore.getBaseUrl()).andReturn("http://base.example.com").anyTimes()
        expect(internalDataStore.getResource("/oauth2/test_authorization_server_id/.well-known/openid-configuration", OIDCWellKnownResource)).andReturn(wellKnown)
        client.setTenantResolver(anyObject())

        // test specifics
        expect(internalDataStore.getResource("/api/v1/users/uid", Account)).andReturn(account)

        expect(internalDataStore.getApiKey()).andReturn(apiKey)
        expect(apiKey.getSecret()).andReturn(apiKeyScret)

        expect(account.getEmailVerificationToken()).andReturn(emailVerificationToken).anyTimes()
        expect(emailVerificationToken.getValue()).andReturn(jwt + "_testDifferentToken") // jwt will validate, but token will not match previously saved

        replay internalDataStore, client, wellKnown, emailService, account, apiKey, customData, emailVerificationToken

        def app = new OktaApplication(clientId, internalDataStore)

        def appProps = [
                authorizationServerId: "test_authorization_server_id",
                emailService: emailService,
                registrationWorkflowEnabled: false,
                client: client
        ]
        app.configureWithProperties(appProps)

        try {
            app.verifyAccountEmail(jwt)
            fail("expected ResourceException")
        }
        catch (com.stormpath.sdk.resource.ResourceException e) {
            // expected
            assertThat(e.getStormpathError().getMessage(), equalTo("Invalid Token"))
            assertThat(e.getStormpathError().getDeveloperMessage(), equalTo("Email verification token did NOT match"))
        }

        verify internalDataStore, client, wellKnown, emailService, account, apiKey, customData, emailVerificationToken
    }


    @Test
    void createUserTest() {

        def email = "test@example.com"
        def uid = "uid123"
        def appId = "app123"
        def clientId = "test_client_id"
        def internalDataStore = createMock(InternalDataStore)
        def client = createMock(DefaultClient)
        def wellKnown = createNiceMock(OIDCWellKnownResource)
        def customData = createNiceMock(CustomData)
        def appMapping = createMock(OktaUserToApplicationMapping)

        def accountCapture = new Capture<Account>()

        expect(internalDataStore.getBaseUrl()).andReturn("http://base.example.com").anyTimes()
        expect(internalDataStore.getResource("/oauth2/test_authorization_server_id/.well-known/openid-configuration", OIDCWellKnownResource)).andReturn(wellKnown)
        client.setTenantResolver(anyObject())
        expect(internalDataStore.instantiate(CustomData)).andReturn(customData)
        expect(internalDataStore.create(eq("http://base.example.com/api/v1/users/?activate=true"), capture(accountCapture))).andAnswer(new IAnswer<Account>() {
            @Override
            Account answer() throws Throwable {
                DefaultAccount account = accountCapture.getValue()
                account.setProperty("href", "http://base.example.com/api/v1/users/${uid}".toString())
                return account
            }
        })

        expect(internalDataStore.instantiate(OktaUserToApplicationMapping)).andReturn(appMapping)
        expect(appMapping.setId(uid)).andReturn(appMapping)
        expect(appMapping.setScope("USER")).andReturn(appMapping)
        expect(appMapping.setUsername(email)).andReturn(appMapping)
        expect(internalDataStore.create("/api/v1/apps/${appId}/users".toString(), appMapping)).andReturn(appMapping)

        replay internalDataStore, client, wellKnown, customData, appMapping

        def app = new OktaApplication(clientId, internalDataStore)

        def appProps = [
                applicationId: appId,
                authorizationServerId: "test_authorization_server_id",
                registrationWorkflowEnabled: false,
                client: client
        ]
        app.configureWithProperties(appProps)

        def account = new DefaultAccount(internalDataStore)
                .setEmail(email)
                .setGivenName("Joe")
                .setSurname("Coder")

        app.createAccount(account)

        verify internalDataStore, client, wellKnown, customData, appMapping
    }

    @Test
    void createUserWithGroup() {
        def email = "test@example.com"
        def uid = "uid123"
        def appId = "app123"
        def groupId = "group123"
        def clientId = "test_client_id"
        def internalDataStore = createMock(InternalDataStore)
        def client = createMock(DefaultClient)
        def wellKnown = createNiceMock(OIDCWellKnownResource)
        def customData = createNiceMock(CustomData)
        def appMapping = createMock(OktaUserToApplicationMapping)

        def accountCapture = new Capture<Account>()

        expect(internalDataStore.getBaseUrl()).andReturn("http://base.example.com").anyTimes()
        expect(internalDataStore.getResource("/oauth2/test_authorization_server_id/.well-known/openid-configuration", OIDCWellKnownResource)).andReturn(wellKnown)
        client.setTenantResolver(anyObject())
        expect(internalDataStore.instantiate(CustomData)).andReturn(customData)
        expect(internalDataStore.create(eq("http://base.example.com/api/v1/users/?activate=true"), capture(accountCapture))).andAnswer(new IAnswer<Account>() {
            @Override
            Account answer() throws Throwable {
                DefaultAccount account = accountCapture.getValue()
                account.setProperty("href", "http://base.example.com/api/v1/users/${uid}".toString())
                return account
            }
        })

        internalDataStore.save(anyObject(DefaultVoidResource))

        replay internalDataStore, client, wellKnown, customData, appMapping

        def app = new OktaApplication(clientId, internalDataStore)

        def appProps = [
                applicationId: appId,
                oktaApplicationGroupId: groupId,
                authorizationServerId: "test_authorization_server_id",
                registrationWorkflowEnabled: false,
                client: client
        ]
        app.configureWithProperties(appProps)

        def account = new DefaultAccount(internalDataStore)
                .setEmail(email)
                .setGivenName("Joe")
                .setSurname("Coder")

        app.createAccount(account)

        verify internalDataStore, client, wellKnown, customData, appMapping
    }
}
