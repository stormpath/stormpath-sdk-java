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
package com.stormpath.sdk.impl.challenge

import com.google.zxing.BinaryBitmap
import com.google.zxing.DecodeHintType
import com.google.zxing.LuminanceSource
import com.google.zxing.client.j2se.BufferedImageLuminanceSource
import com.google.zxing.common.HybridBinarizer
import com.google.zxing.qrcode.QRCodeReader
import com.stormpath.sdk.account.Account
import com.stormpath.sdk.client.ClientIT
import com.stormpath.sdk.directory.Directory
import com.stormpath.sdk.factor.FactorStatus
import com.stormpath.sdk.factor.FactorType
import com.stormpath.sdk.factor.FactorVerificationStatus
import com.stormpath.sdk.factor.google.GoogleAuthenticatorFactor
import org.testng.annotations.Test

import javax.imageio.ImageIO
import java.awt.image.BufferedImage

import static org.testng.AssertJUnit.assertEquals
import static org.testng.AssertJUnit.assertNotNull
import static org.testng.AssertJUnit.assertNull

class GoogleAuthenticatorChallengeIT extends ClientIT{

    @Test
    void testSuccessfulGoogleAuthenticatorChallenge() {
        Directory dir = client.instantiate(Directory)
        dir.name = uniquify("Java SDK: GoogleAuthenticatorChallengeIT.testSuccessfulGoogleAuthenticatorChallenge")
        dir = client.currentTenant.createDirectory(dir);
        Account account = client.instantiate(Account)
        account = account.setGivenName('John')
                .setSurname('DELETEME')
                .setEmail('johndeleteme@nowhere.com')
                .setPassword('Changeme1!')

        deleteOnTeardown(account)
        deleteOnTeardown(dir)

        dir.createAccount(account)

        def randomAccountName = uniquify("Random Account Name")
        def randomIssuer = uniquify("Random Issuer")
        def factor = client.instantiate(GoogleAuthenticatorFactor.class)
        factor.accountName = randomAccountName
        factor.issuer = randomIssuer

        factor = account.createFactor(factor)

        assertGoogleAuthenticatorFactorFields(factor, randomIssuer, randomAccountName)

    }

    private void assertGoogleAuthenticatorFactorFields(GoogleAuthenticatorFactor factor, String expectedIssuer = null, String expectedAccountName = null, boolean enabled = true) {
        assertNotNull factor.href
        assertEquals(factor.type, FactorType.GOOGLE_AUTHENTICATOR)
        assertEquals(factor.factorVerificationStatus, FactorVerificationStatus.UNVERIFIED)
        if(enabled) {
            assertEquals(factor.status, FactorStatus.ENABLED)
        }
        else{
            assertEquals(factor.status, FactorStatus.DISABLED)
        }

        assertEquals(factor.accountName, expectedAccountName)
        assertEquals(factor.issuer, expectedIssuer)
        assertNotNull(factor.secret)

        String actualKeyUri = factor.getKeyUri()

        String expectedKeyUri

        if (expectedIssuer == null) {
            if (expectedAccountName == null) {
                expectedKeyUri = "otpauth://totp/?secret=" + factor.secret
            } else {
                String urlEncodedExpectedAccountName = URLEncoder.encode(expectedAccountName, "UTF-8")
                expectedKeyUri = "otpauth://totp/" + urlEncodedExpectedAccountName + "?secret=" + factor.secret
            }
        } else {
            String urlEncodedExpectedIssuer = URLEncoder.encode(expectedIssuer, "UTF-8")
            if (expectedAccountName == null) {
                expectedKeyUri = "otpauth://totp/?secret=" + factor.secret + "&issuer=" + urlEncodedExpectedIssuer
            } else {
                String urlEncodedExpectedAccountName = URLEncoder.encode(expectedAccountName, "UTF-8")
                expectedKeyUri = "otpauth://totp/" + urlEncodedExpectedIssuer + ":" + urlEncodedExpectedAccountName + "?secret=" + factor.secret + "&issuer=" + urlEncodedExpectedIssuer
            }
        }

        assertEquals(actualKeyUri, expectedKeyUri)
        assertNotNull(factor.getBase64QrImage())
        assertBase64EncodedQRCodeEncodesString(factor.getBase64QrImage(), expectedKeyUri)

        assertNotNull(factor.getAccount())
        assertNotNull(factor.getAccount().href)

        assertNull(factor.getMostRecentChallenge())

        assertNotNull(factor.getChallenges())
        assertNotNull(factor.getChallenges().href)
    }

    protected void assertBase64EncodedQRCodeEncodesString(String base64EncodedQRCode, String expectedString) {
        assertNotNull(base64EncodedQRCode)
        try {
            Map hints = [:]
            hints.put(DecodeHintType.PURE_BARCODE, true)
            byte[] qrImageBytes = Base64.getDecoder().decode(base64EncodedQRCode)
            BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(qrImageBytes))
            LuminanceSource source = new BufferedImageLuminanceSource(bufferedImage)
            BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source))

            String stringFromImage = new QRCodeReader().decode(bitmap, hints).getText()

            assertEquals(stringFromImage, expectedString)
        } catch (Exception e) {
            println "Base64String: ${base64EncodedQRCode}, expectedString: ${expectedString}"
            assert "MFA2IT EXCEPTION: " + e
        }
    }

}
