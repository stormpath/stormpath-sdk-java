package com.stormpath.sdk.impl.saml

import com.stormpath.sdk.impl.idsite.IdSiteClaims
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test

import static org.testng.Assert.assertEquals
import static org.testng.Assert.fail

/**
 * @since 1.0.0
 */
class SamlClaimsTest {

    def samlClaims

    @BeforeMethod
    void setup() {
        samlClaims = new SamlClaims()
    }

    @Test
    void testChain() {
        assertEquals samlClaims.getCallbackUri(), null
        assertEquals samlClaims.getState(), null
        assertEquals samlClaims.getPath(), null
        assertEquals samlClaims.getOrganizationNameKey(), null
        assertEquals samlClaims.getSpToken(), null

        samlClaims
            .setCallbackUri("/my/callback")
            .setState("ENABLED")
            .setPath("/mypath")
            .setOrganizationNameKey("my-org")
            .setSpToken("my-sp-token")

        assertEquals samlClaims.getCallbackUri(), "/my/callback"
        assertEquals samlClaims.getState(), "ENABLED"
        assertEquals samlClaims.getPath(), "/mypath"
        assertEquals samlClaims.getOrganizationNameKey(), "my-org"
        assertEquals samlClaims.getSpToken(), "my-sp-token"
    }
}
