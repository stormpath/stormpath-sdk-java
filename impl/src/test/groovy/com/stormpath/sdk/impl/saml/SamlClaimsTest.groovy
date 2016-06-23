/*
 * Copyright 2016 Stormpath, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.stormpath.sdk.impl.saml

import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test

import static org.testng.Assert.assertEquals

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
