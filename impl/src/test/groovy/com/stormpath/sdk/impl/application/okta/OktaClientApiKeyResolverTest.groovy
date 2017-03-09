package com.stormpath.sdk.impl.application.okta

import com.stormpath.sdk.application.okta.ApplicationCredentials
import com.stormpath.sdk.client.Client
import org.testng.annotations.Test

import static org.easymock.EasyMock.*
import static org.hamcrest.MatcherAssert.*
import static org.hamcrest.Matchers.*

/**
 * Tests for {@link OktaClientApiKeyResolver}.
 */
class OktaClientApiKeyResolverTest {

    @Test
    void basicCreationTest() {

        def resolver = new OktaClientApiKeyResolver()
        def baseHref = "http://example.com/api/v1"
        def applicationId = "the_app_id"
        def appCredentials = new DefaultApplicationCredentials(null, new HashMap<String, Object>())
        appCredentials.setClientId("client_id")
        appCredentials.setClientSecret("client_secret")

        def client = createStrictMock(Client)
        expect(client.getResource(baseHref + "/internal/apps/"+ applicationId +"/settings/clientcreds", ApplicationCredentials)).andReturn(appCredentials)
        replay client

        def apiKey = resolver.getClientApiKey(client, baseHref, applicationId)
        assertThat(apiKey.id, is("client_id"))
        assertThat(apiKey.secret, is("client_secret"))

        verify client

    }
}
