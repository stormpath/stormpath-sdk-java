package com.stormpath.sdk.impl.application.okta

import com.stormpath.sdk.impl.application.okta.DefaultApplicationCredentials
import org.testng.annotations.Test

import static org.hamcrest.MatcherAssert.*
import static org.hamcrest.Matchers.*

/**
 *Tests for {@link DefaultApplicationCredentials}.
 */
class DefaultApplicationCredentialsTest {

    @Test
    void buildFromMapTest() {

        def map = [
            client_id                  : "the_client_id",
            client_secret              : "the_client_secret",
            token_endpoint_auth_method : "client_secret_basic"
        ]

        def applicationCredentials = new DefaultApplicationCredentials(null, map);

        assertThat(applicationCredentials.clientId, is("the_client_id"))
        assertThat(applicationCredentials.clientSecret, is("the_client_secret"))
    }
}
