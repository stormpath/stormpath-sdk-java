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
package com.stormpath.sdk.impl.multifactor

import com.google.zxing.BinaryBitmap
import com.google.zxing.DecodeHintType
import com.google.zxing.LuminanceSource
import com.google.zxing.client.j2se.BufferedImageLuminanceSource
import com.google.zxing.common.HybridBinarizer
import com.google.zxing.qrcode.QRCodeReader
import com.stormpath.sdk.challenge.Challenge
import com.stormpath.sdk.client.ClientIT
import com.stormpath.sdk.factor.FactorStatus
import com.stormpath.sdk.factor.FactorType
import com.stormpath.sdk.factor.FactorVerificationStatus
import com.stormpath.sdk.factor.google.GoogleAuthenticatorFactor

import javax.imageio.ImageIO
import java.awt.image.BufferedImage

import static org.testng.AssertJUnit.*
/**
 * Created by mehrshadrafiei on 9/29/16.
 */
abstract class AbstractMultiFactorIT extends ClientIT{

    protected void assertGoogleAuthenticatorFactorFields(GoogleAuthenticatorFactor factor, String expectedIssuer = null, String expectedAccountName = null, boolean enabled = true) {
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

    protected void assertInitialChallengeFields(Challenge challenge, String expectedChallengeStatus = 'CREATED', boolean expectDefaultMessage = true) {
        if (expectDefaultMessage) {
            assertEquals(challenge.message,'Your verification code is ${code}')
        }
        assertEquals(challenge.status as String, expectedChallengeStatus)
        assertNotNull(challenge.factor)
        assertNotNull(challenge.factor.href)
        assertNotNull(challenge.account)
        assertNotNull(challenge.account.href)
    }
}
