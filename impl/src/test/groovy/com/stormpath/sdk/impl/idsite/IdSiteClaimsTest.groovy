package com.stormpath.sdk.impl.idsite

import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test

import static org.testng.Assert.assertEquals
import static org.testng.Assert.fail

/**
 * @since 1.0.RC9
 */
class IdSiteClaimsTest {

    def idSiteClaims

    @BeforeMethod
    void setup() {
        idSiteClaims = new IdSiteClaims()
    }

    @Test
    void testChain() {
        assertEquals idSiteClaims.getCallbackUri(), null
        assertEquals idSiteClaims.getState(), null
        assertEquals idSiteClaims.getPath(), null
        assertEquals idSiteClaims.getOrganizationNameKey(), null
        assertEquals idSiteClaims.getUseSubdomain(), null
        assertEquals idSiteClaims.getShowOrganizationField(), null
        assertEquals idSiteClaims.getSpToken(), null

        idSiteClaims
            .setCallbackUri("/my/callback")
            .setState("ENABLED")
            .setPath("/mypath")
            .setOrganizationNameKey("my-org")
            .setUseSubdomain(false)
            .setShowOrganizationField(true)
            .setSpToken("my-sp-token")

        assertEquals idSiteClaims.getCallbackUri(), "/my/callback"
        assertEquals idSiteClaims.getState(), "ENABLED"
        assertEquals idSiteClaims.getPath(), "/mypath"
        assertEquals idSiteClaims.getOrganizationNameKey(), "my-org"
        assertEquals idSiteClaims.getUseSubdomain(), false
        assertEquals idSiteClaims.getShowOrganizationField(), true
        assertEquals idSiteClaims.getSpToken(), "my-sp-token"
    }

    @Test
    void testBoolean() {
        assertEquals idSiteClaims.getUseSubdomain(), null

        idSiteClaims.setUseSubdomain(false)
        assertEquals idSiteClaims.getUseSubdomain(), false

        idSiteClaims.setValue(IdSiteClaims.USE_SUBDOMAIN, "TrUe")
        assertEquals idSiteClaims.getUseSubdomain(), true

        idSiteClaims.setValue(IdSiteClaims.USE_SUBDOMAIN, "fAlSe")
        assertEquals idSiteClaims.getUseSubdomain(), false

        try {
            idSiteClaims.setValue(IdSiteClaims.USE_SUBDOMAIN, "blarg")
            idSiteClaims.getUseSubdomain()
            fail "shouldn't be here"
        } catch (IllegalStateException e) {
            assertEquals e.getMessage(), "Cannot convert 'usd' value [blarg] to Boolean instance."
        }
    }
}
