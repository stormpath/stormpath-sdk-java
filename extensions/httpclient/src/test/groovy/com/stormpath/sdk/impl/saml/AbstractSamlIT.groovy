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

import com.stormpath.sdk.application.Application
import com.stormpath.sdk.application.Applications
import com.stormpath.sdk.client.ClientIT
import com.stormpath.sdk.resource.ResourceException
import com.stormpath.sdk.saml.SamlIdentityProvider
import com.stormpath.sdk.saml.SamlPolicy
import com.stormpath.sdk.saml.SamlServiceProviderRegistration
import com.stormpath.sdk.saml.SamlServiceProviderRegistrations

import static org.testng.Assert.assertEquals
import static org.testng.Assert.assertNotNull
import static org.testng.Assert.assertTrue

/**
 * @since 1.3.0
 */
abstract class AbstractSamlIT extends ClientIT {
    public static String validX509Cert = '''-----BEGIN CERTIFICATE-----
MIIDBjCCAe4CCQDkkfBwuV3jqTANBgkqhkiG9w0BAQUFADBFMQswCQYDVQQGEwJV
UzETMBEGA1UECBMKU29tZS1TdGF0ZTEhMB8GA1UEChMYSW50ZXJuZXQgV2lkZ2l0
cyBQdHkgTHRkMB4XDTE1MTAxNDIyMDUzOFoXDTE2MTAxMzIyMDUzOFowRTELMAkG
A1UEBhMCVVMxEzARBgNVBAgTClNvbWUtU3RhdGUxITAfBgNVBAoTGEludGVybmV0
IFdpZGdpdHMgUHR5IEx0ZDCCASIwDQYJKoZIhvcNAQEBBQADggEPADCCAQoCggEB
ALuZBSfp4ecigQGFL6zawVi9asVstXHy3cpj3pPXjDx5Xj4QlbBL7KbZhVd4B+j3
Paacetpn8N0g06sYe1fIeddZE7PZeD2vxTLglriOCB8exH9ZAcYNHIGy3pMFdXHY
lS7xXYWb+BNLVU7ka3tJnceDjhviAjICzQJs0JXDVQUeYxB80a+WtqJP+ZMbAxvA
QbPzkcvK8CMctRSRqKkpC4gWSxUAJOqEmyvQVQpaLGrI2zFroD2Bgt0cZzBHN5tG
wC2qgacDv16qyY+90rYgX/WveA+MSd8QKGLcpPlEzzVJp7Z5Boc3T8wIR29jaDtR
cK4bWQ2EGLJiJ+Vql5qaOmsCAwEAATANBgkqhkiG9w0BAQUFAAOCAQEAmCND/4tB
+yVsIZBAQgul/rK1Qj26FlyO0i0Rmm2OhGRhrd9JPQoZ+xCtBixopNICKG7kvUeQ
Sk8Bku6rQ3VquxKtqAjNFeiLykd9Dn2HUOGpNlRcpzFXHtX+L1f34lMaT54qgWAh
PgWkzh8xo5HT4M83DaG+HT6BkaVAQwIlJ26S/g3zJ00TrWRP2E6jlhR5KHLN+8eE
D7/ENlqO5ThU5uX07/Bf+S0q5NK0NPuy0nO2w064kHdIX5/O64ktT1/MgWBV6yV7
mg1osHToeo4WXGz2Yo6+VFMM3IKRqMDbkR7N4cNKd1KvEKrMaRE7vC14H/G5NSOh
yl85oFHAdkguTA==
-----END CERTIFICATE-----
'''

    public static String validX509Cert2 = '''-----BEGIN CERTIFICATE-----
MIIDpDCCAoygAwIBAgIGAVCruH3KMA0GCSqGSIb3DQEBBQUAMIGSMQswCQYDVQQGEwJVUzETMBEG
A1UECAwKQ2FsaWZvcm5pYTEWMBQGA1UEBwwNU2FuIEZyYW5jaXNjbzENMAsGA1UECgwET2t0YTEU
MBIGA1UECwwLU1NPUHJvdmlkZXIxEzARBgNVBAMMCmRldi03MDMxMjQxHDAaBgkqhkiG9w0BCQEW
DWluZm9Ab2t0YS5jb20wHhcNMTUxMDI3MjM1MjI2WhcNMjUxMDI3MjM1MzI2WjCBkjELMAkGA1UE
BhMCVVMxEzARBgNVBAgMCkNhbGlmb3JuaWExFjAUBgNVBAcMDVNhbiBGcmFuY2lzY28xDTALBgNV
BAoMBE9rdGExFDASBgNVBAsMC1NTT1Byb3ZpZGVyMRMwEQYDVQQDDApkZXYtNzAzMTI0MRwwGgYJ
KoZIhvcNAQkBFg1pbmZvQG9rdGEuY29tMIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA
vP1oEOO2DtsK0fR4A7lPVYfMEtUh10yOTlnoEyuz9+kxhvSj5vCUFOQwyH8v+MOIdRxZqV4yKeng
pMvRy2NxpKbl9mk6d9FY8rWJShpDPh660/GNoaMsmi/Eh9FHSeFZLT7KCr/GYVt9VF/Dxfo4g+s8
wSNOhGb97r8HRgCEvo9XOZgQno1Z1GXnCyAEYsQoW/+eH+KJHvcTlPR00Pysg7ALT3NHlpky/fER
VYOhM+d8v481klhNBvVWPYQzNiI4bKjNlV/1/Rzqerd5RX/t2OIPq+DLALhYfKiICfZG+ELsh/US
nx2OOlxRlTSQEpmL/PAUi27KozQs3rZLyRLveQIDAQABMA0GCSqGSIb3DQEBBQUAA4IBAQCgB1B2
WZ8/Rs7zApninp4QjodUcPLakZGFJW6ipJVPYogUTNj5D8xWvpXLVbFQnqErqSf/Rt4njV6uaRYx
E/Om+IFIv8en/EMh/Em5oNWpEubQ1znMD/aXeiPdtV8lFZml7T5tlkN7pgOzkFY2Y1NXNwTWYu6o
h4LqMywUWPZk/Ccs/q/VUG9rsxGAqCrOvcjl8IoiS2LPbTpEBowMytDM4PEXkyvCz9sqsZqBnpL+
Ks+sF7ATR2ffb/Xg2NBScPcRdmHffQHavZuh44uygeAfSV/Lx4kb8/MdqOkPKRy0QrB/ZY/vRIL8
xu/vQr6stjuzJIsDNAtW1FlG8WALOMjV
-----END CERTIFICATE-----
'''


    protected SamlIdentityProvider getNewSamlIdentityProviderForNewApplication() {
        def app = createTempApp()
        return getSamlIdentityProviderForApplication(app)
    }

    protected SamlIdentityProvider getSamlIdentityProviderForApplication(Application app) {
        def samlPolicy = client.getResource(app.getSamlPolicy().href, SamlPolicy)
        samlPolicy.getIdentityProvider()
        return client.getResource(samlPolicy.getIdentityProvider().href, SamlIdentityProvider)
    }

    protected SamlIdentityProvider getSamlIdentityProviderForAdministratorsApplication() {
        def app = client.currentTenant.getApplications(Applications.where(Applications.name().eqIgnoreCase("Stormpath"))).asList().get(0)
        return getSamlIdentityProviderForApplication(app)
    }

    protected SamlServiceProviderRegistration createAndGetAndAssertNewRegistration(SamlServiceProviderRegistration registration) {
        def identityProviderHref = registration.getIdentityProvider().href
        def builder = SamlServiceProviderRegistrations.newCreateRequestFor(registration)
        registration = registration.getIdentityProvider().createSamlServiceProviderRegistration(builder.build())
        assertNotNull(registration)

        def registrationHref = registration.href

        assertTrue(registrationHref.startsWith(baseUrl + "/samlServiceProviderRegistrations/"))
        assertEquals(registration.identityProvider.href, identityProviderHref)

        return registration
    }

    protected void createNewRegistrationError(SamlServiceProviderRegistration registration, int expectedErrorCode) {
        def builder = SamlServiceProviderRegistrations.newCreateRequestFor(registration)

        Throwable e = null;
        try {
            registration.getIdentityProvider().createSamlServiceProviderRegistration(builder.build())
        }
        catch (ResourceException re) {
            e = re
            assertEquals(re.status, 400)
            assertEquals(re.getCode(), expectedErrorCode)
        }

        assertTrue(e instanceof ResourceException)
    }
}


