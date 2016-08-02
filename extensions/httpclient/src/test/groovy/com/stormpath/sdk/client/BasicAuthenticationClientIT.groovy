package com.stormpath.sdk.client

import com.stormpath.sdk.impl.http.authc.BasicRequestAuthenticator
import org.testng.annotations.Test

import static org.testng.Assert.assertNotNull
import static org.testng.Assert.assertTrue

/**
 * @since 0.9.3 asserts HTTP BASIC authentication works (used only in environments that are forced to use it).
 */
class BasicAuthenticationClientIT extends ClientIT {

    @Test
    void testCreateAppGroupWithBasicAuthentication() {

        //We are creating a new client with BasicAuthentication
        def client = buildClient(AuthenticationSchemes.BASIC)

        def authenticationScheme = client.dataStore.requestExecutor.authenticationScheme
        assertTrue authenticationScheme instanceof BasicRequestAuthenticator

        def tenant = client.currentTenant
        assertNotNull tenant
        assertNotNull tenant.href
    }
}
