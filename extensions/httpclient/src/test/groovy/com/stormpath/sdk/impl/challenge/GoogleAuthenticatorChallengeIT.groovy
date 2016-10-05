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

import com.stormpath.sdk.account.Account
import com.stormpath.sdk.challenge.ChallengeList
import com.stormpath.sdk.challenge.ChallengeOptions
import com.stormpath.sdk.challenge.Challenges
import com.stormpath.sdk.challenge.google.GoogleAuthenticatorChallenge
import com.stormpath.sdk.directory.Directory
import com.stormpath.sdk.factor.FactorOptions
import com.stormpath.sdk.factor.FactorType
import com.stormpath.sdk.factor.Factors
import com.stormpath.sdk.factor.google.GoogleAuthenticatorFactor
import com.stormpath.sdk.impl.multifactor.AbstractMultiFactorIT
import com.stormpath.sdk.resource.ResourceException
import org.apache.commons.codec.binary.Base32
import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import org.testng.annotations.Test

import static com.stormpath.sdk.impl.challenge.TOTPService.getTotpPassword
import static org.testng.AssertJUnit.*

/**
 * @since 1.1.0
 */
class GoogleAuthenticatorChallengeIT extends AbstractMultiFactorIT{

    @Test
    void testSuccessfulGoogleAuthenticatorChallenge() {
        Directory dir = client.instantiate(Directory)
        dir.name = uniquify("Java SDK: ${this.getClass().getSimpleName()}.${new Object(){}.getClass().getEnclosingMethod().getName()}")
        dir = client.currentTenant.createDirectory(dir);
        deleteOnTeardown(dir)
        Account account = createTempAccountInDir(dir)

        def randomAccountName = uniquify("Random Account Name")
        def randomIssuer = uniquify("Random Issuer")
        def factor = createGoogleAuthenticatorFactor(account, randomIssuer, randomAccountName)
        assertGoogleAuthenticatorFactorFields(factor, randomIssuer, randomAccountName)

        sleepToAvoidCrossingThirtySecondMark()

        assertGoogleAuthenticatorChallengeResponse(factor, getCurrentValidCode(factor), 'SUCCESS')
    }


    @Test
    void testGoogleAuthenticatorChallengeWithGarbageCode() {
        Directory dir = client.instantiate(Directory)
        dir.name = uniquify("Java SDK: ${this.getClass().getSimpleName()}.${new Object(){}.getClass().getEnclosingMethod().getName()}")
        dir = client.currentTenant.createDirectory(dir);
        deleteOnTeardown(dir)
        Account account = createTempAccountInDir(dir)

        def randomAccountName = uniquify("Random Account Name")
        def factor = createGoogleAuthenticatorFactor(account, null, randomAccountName)
        assertGoogleAuthenticatorFactorFields(factor, null, randomAccountName)

        Throwable e = null
        try {
            createChallenge(factor, "bogus")
        } catch (ResourceException re) {
            e = re
            assertEquals(re.status, 400)
            assertEquals(re.getCode(), 2002)
        }
        assertTrue(e instanceof ResourceException)
    }

    @Test
    void testGoogleAuthenticatorChallengeAgainstDisabledFactor() {
        Directory dir = client.instantiate(Directory)
        dir.name = uniquify("Java SDK: ${this.getClass().getSimpleName()}.${new Object(){}.getClass().getEnclosingMethod().getName()}")
        dir = client.currentTenant.createDirectory(dir);
        deleteOnTeardown(dir)
        Account account = createTempAccountInDir(dir)

        def randomAccountName = uniquify("Random Account Name")
        def factor = createGoogleAuthenticatorFactor(account, null, randomAccountName,false)
        assertGoogleAuthenticatorFactorFields(factor, null, randomAccountName, false)

        Throwable e = null
        try {
            createChallenge(factor, "123456")
        } catch (ResourceException re) {
            e = re
            assertEquals(re.status, 400)
            assertEquals(re.getCode(), 13109)
        }
        assertTrue(e instanceof ResourceException)
    }

    @Test
    void testGoogleAuthenticatorChallengeWithInvalidCode() {
        Directory dir = client.instantiate(Directory)
        dir.name = uniquify("Java SDK: ${this.getClass().getSimpleName()}.${new Object(){}.getClass().getEnclosingMethod().getName()}")
        dir = client.currentTenant.createDirectory(dir);
        deleteOnTeardown(dir)
        Account account = createTempAccountInDir(dir)

        def randomAccountName = uniquify("Random Account Name")
        def factor = createGoogleAuthenticatorFactor(account, null, randomAccountName)
        assertGoogleAuthenticatorFactorFields(factor, null, randomAccountName)

        sleepToAvoidCrossingThirtySecondMark()

        assertGoogleAuthenticatorChallengeResponse(factor, getCurrentInvalidCode(factor), 'FAILED', 'UNVERIFIED')
    }


    private void assertGoogleAuthenticatorChallengeResponse(GoogleAuthenticatorFactor factor, String code, String status, String verificationStatus = 'VERIFIED') {
        String factorHref = factor.href
        GoogleAuthenticatorChallenge challenge = createChallenge(factor, code)
        assertInitialChallengeFields(challenge, status, false)

        FactorOptions options = Factors.options().withChallenges().withMostRecentChallenge()
        def retrievedFactor = client.getResource(factorHref, GoogleAuthenticatorFactor.class, options)
        assertNotNull(retrievedFactor.mostRecentChallenge)
        assertNotNull(retrievedFactor.mostRecentChallenge.href)

        FactorOptions factorOptions = Factors.options().withChallenges().withMostRecentChallenge()
        retrievedFactor = client.getResource(factorHref, GoogleAuthenticatorFactor.class, factorOptions)

        assertNotNull(retrievedFactor)
        assertEquals(retrievedFactor.challenges.size, 1)

        ChallengeOptions challengeOptions = Challenges.GOOGLE_AUTHENTICATOR.options().withFactor().withAccount()
        def retrievedChallenge = client.getResource(retrievedFactor.mostRecentChallenge.href, GoogleAuthenticatorChallenge.class, challengeOptions)

        assertEquals(retrievedChallenge.status.name(), status)
        assertEquals(retrievedChallenge.factor.type, FactorType.GOOGLE_AUTHENTICATOR)
        assertNotNull(retrievedChallenge.factor.secret)

        retrievedFactor = client.getResource(factorHref, GoogleAuthenticatorFactor.class)
        assertEquals(retrievedFactor.factorVerificationStatus.name(), verificationStatus)

        ChallengeList challengeList = retrievedFactor.getChallenges(Challenges.criteria().orderByStatus().limitTo(10).descending())
        assertEquals(challengeList.iterator().next().account.materialized, false)
        challengeList = retrievedFactor.getChallenges(Challenges.criteria().withAccount().orderByStatus().limitTo(10).descending())
        assertEquals(challengeList.iterator().next().account.materialized, true)
        Throwable t = null;
        try {
            challengeList.iterator().next().factor
        } catch (IllegalStateException e) {
            t = e;
        }
        assertTrue(t instanceof IllegalStateException)
        challengeList = retrievedFactor.getChallenges(Challenges.criteria().withAccount().withFactor().orderByStatus().limitTo(10).descending())
        assertEquals(challengeList.iterator().next().account.materialized, true)
        assertEquals(challengeList.iterator().next().factor.materialized, true)
        assertEquals(challengeList.toList().size(), 1)
        assertNotNull(challengeList.toList().get(0).account.href)
    }

    private void sleepToAvoidCrossingThirtySecondMark() {
        DateTime now = new DateTime(DateTimeZone.UTC)
        int seconds = now.getSecondOfMinute()
        int secondsToWait
        if ((seconds <= 30) && (seconds > 25)) {
            secondsToWait = 31 - seconds
        } else if ((seconds <= 60) && (seconds > 55)) {
            secondsToWait = 61 - seconds
        }

        sleep(secondsToWait * 1000)
    }

    private GoogleAuthenticatorChallenge createChallenge(GoogleAuthenticatorFactor factor, String code = null) {
        def challenge = client.instantiate(GoogleAuthenticatorChallenge.class)
        challenge.setCode(code)
        ChallengeOptions options = Challenges.GOOGLE_AUTHENTICATOR.options().withFactor()
        return factor.createChallenge(Challenges.GOOGLE_AUTHENTICATOR.newCreateRequestFor(challenge).withResponseOptions(options).build())
    }

    private String getCurrentValidCode(GoogleAuthenticatorFactor factor) {
        return getCurrentCode(factor, true)
    }

    private String getCurrentInvalidCode(GoogleAuthenticatorFactor factor) {
        return getCurrentCode(factor, false)
    }

    private String getCurrentCode(GoogleAuthenticatorFactor factor, boolean valid) {
        String secret = factor.getSecret()
        byte[] bytes = new Base32().decode(secret)
        String currentValidCode = getTotpPassword(bytes, new DateTime(DateTimeZone.UTC).getMillis())

        if (valid) {
            return currentValidCode
        }
        else {
            int badValue = (Integer.valueOf(currentValidCode) + 1) % 1000000
            return String.format("%06d", badValue)
        }
    }
}
