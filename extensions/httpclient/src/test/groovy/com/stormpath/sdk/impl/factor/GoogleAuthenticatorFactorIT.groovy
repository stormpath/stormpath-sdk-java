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
package com.stormpath.sdk.impl.factor

import com.stormpath.sdk.account.Account
import com.stormpath.sdk.challenge.Challenges
import com.stormpath.sdk.challenge.google.GoogleAuthenticatorChallenge
import com.stormpath.sdk.directory.Directory
import com.stormpath.sdk.factor.Factor
import com.stormpath.sdk.factor.FactorList
import com.stormpath.sdk.factor.Factors
import com.stormpath.sdk.factor.google.GoogleAuthenticatorFactor
import com.stormpath.sdk.factor.sms.SmsFactor
import com.stormpath.sdk.impl.multifactor.AbstractMultiFactorIT
import com.stormpath.sdk.resource.ResourceException
import org.testng.annotations.Test

import static org.testng.AssertJUnit.*

/**
 * @since 1.1.0
 */
class GoogleAuthenticatorFactorIT extends AbstractMultiFactorIT{

    @Test
    void testQueryMultipleFactorsWithPotentiallyMissingProperties() {
        Directory dir = client.instantiate(Directory)
        dir.name = uniquify("Java SDK: ${this.getClass().getSimpleName()}.${new Object(){}.getClass().getEnclosingMethod().getName()}")
        dir = client.currentTenant.createDirectory(dir);
        deleteOnTeardown(dir)
        Account account = createTempAccountInDir(dir)

        def randomAccountName = uniquify("Random AccountName")
        def factor = createGoogleAuthenticatorFactor(account, null, randomAccountName)
        assertGoogleAuthenticatorFactorFields(factor, null, randomAccountName)

        createSmsFactor(account, VALID_PHONE_NUMBER)

        def factors = account.getFactors(Factors.criteria().limitTo(50).offsetBy(0).orderByCreatedAt());
        assertEquals(factors.iterator().next().account.materialized, false)

        factors = account.getFactors(Factors.SMS.criteria().withPhone().withAccount().limitTo(50).offsetBy(0).orderByCreatedAt());
        assertEquals(factors.iterator().next().account.materialized, true)
        assertEquals(factors.iterator().next().phone.materialized, true)
        assertEquals(factors.getProperty("items").size, 1);

        List<Factor> factorsAsList = factors.asList()
        for(Factor currentFactor : factorsAsList){
            if(currentFactor instanceof SmsFactor){
                assertEquals(currentFactor.phone.number,VALID_PHONE_NUMBER)
            }
        }

        factors = account.getFactors(Factors.SMS.criteria().withPhone().limitTo(50).offsetBy(0).orderByCreatedAt());
        assertEquals(factors.getProperty("items").size,1)
        assertEquals(factors.getProperty("items").get(0).phone.number, VALID_PHONE_NUMBER)

        factors = account.getFactors(Factors.GOOGLE_AUTHENTICATOR.criteria().limitTo(50).offsetBy(0).orderByCreatedAt());
        assertEquals(factors.getProperty("items").size,1)
        assertNotNull(factors.getProperty("items").get(0).secret)
    }

    @Test
    void testCreateAndGetFactorWithNulIssuerAndNullAccountName() {
        Directory dir = client.instantiate(Directory)
        dir.name = uniquify("Java SDK: ${this.getClass().getSimpleName()}.${new Object(){}.getClass().getEnclosingMethod().getName()}")
        dir = client.currentTenant.createDirectory(dir);
        deleteOnTeardown(dir)
        Account account = createTempAccountInDir(dir)

        Throwable e = null
        try {
            createGoogleAuthenticatorFactor(account, null, null)
        }
        catch (ResourceException re) {
            e = re
            assertEquals(re.status, 400)
            assertEquals(re.getCode(), 2000)
        }
        assertTrue(e instanceof ResourceException)
    }

    @Test
    void testCreateAndGetFactorWithNullIssuerAndNoAccountName() {
        Directory dir = client.instantiate(Directory)
        dir.name = uniquify("Java SDK: ${this.getClass().getSimpleName()}.${new Object(){}.getClass().getEnclosingMethod().getName()}")
        dir = client.currentTenant.createDirectory(dir);
        deleteOnTeardown(dir)
        Account account = createTempAccountInDir(dir)

        def factor = client.instantiate(GoogleAuthenticatorFactor.class)
        factor.setIssuer(null)
        factor = account.createFactor(factor)
        assertGoogleAuthenticatorFactorFields(factor, null, account.username)

        factor = client.getResource(factor.getHref(), GoogleAuthenticatorFactor.class)
        assertGoogleAuthenticatorFactorFields(factor, null, account.username)
    }

    @Test
    void testCreateAndGetFactorWithIssuerAndNullAccountName() {
        Directory dir = client.instantiate(Directory)
        dir.name = uniquify("Java SDK: ${this.getClass().getSimpleName()}.${new Object(){}.getClass().getEnclosingMethod().getName()}")
        dir = client.currentTenant.createDirectory(dir);
        deleteOnTeardown(dir)
        Account account = createTempAccountInDir(dir)

        def randomIssuer = uniquify("Random Issuer")
        Throwable e = null
        try {
            createGoogleAuthenticatorFactor(account, randomIssuer, null)
        }
        catch (ResourceException re) {
            e = re
            assertEquals(re.status, 400)
            assertEquals(re.getCode(), 2000)
        }
        assertTrue(e instanceof ResourceException)
    }

    @Test
    void testCreateAndGetFactorWithIssuerAndNoAccountName() {
        Directory dir = client.instantiate(Directory)
        dir.name = uniquify("Java SDK: ${this.getClass().getSimpleName()}.${new Object(){}.getClass().getEnclosingMethod().getName()}")
        dir = client.currentTenant.createDirectory(dir);
        deleteOnTeardown(dir)
        Account account = createTempAccountInDir(dir)

        def randomIssuer = uniquify("Random Issuer")
        def factor = client.instantiate(GoogleAuthenticatorFactor.class)
        factor.setIssuer(randomIssuer)
        factor = account.createFactor(factor)
        assertGoogleAuthenticatorFactorFields(factor, randomIssuer, account.username)

        factor = client.getResource(factor.getHref(), GoogleAuthenticatorFactor.class)
        assertGoogleAuthenticatorFactorFields(factor, randomIssuer, account.username)
    }

    @Test
    void testCreateAndGetFactorWithInvalidIssuer() {
        Directory dir = client.instantiate(Directory)
        dir.name = uniquify("Java SDK: ${this.getClass().getSimpleName()}.${new Object(){}.getClass().getEnclosingMethod().getName()}")
        dir = client.currentTenant.createDirectory(dir);
        deleteOnTeardown(dir)
        Account account = createTempAccountInDir(dir)

        def randomAccountName = uniquify("Random AccountName")
        def randomIssuer = "No:colons!"
        Throwable e = null
        try {
            createGoogleAuthenticatorFactor(account, randomIssuer, randomAccountName)
        }
        catch (ResourceException re) {
            e = re
            assertEquals(re.status, 400)
            assertEquals(re.getCode(), 2002)
        }
        assertTrue(e instanceof ResourceException)
    }

    @Test
    void testCreateAndGetFactorWithDifficultIssuer() {
        Directory dir = client.instantiate(Directory)
        dir.name = uniquify("Java SDK: ${this.getClass().getSimpleName()}.${new Object(){}.getClass().getEnclosingMethod().getName()}")
        dir = client.currentTenant.createDirectory(dir);
        deleteOnTeardown(dir)
        Account account = createTempAccountInDir(dir)

        def randomAccountName = uniquify("Random AccountName")
        String difficultIssuer = "Di//icult Company?"
        def factor = createGoogleAuthenticatorFactor(account, difficultIssuer, randomAccountName)
        assertGoogleAuthenticatorFactorFields(factor, difficultIssuer, randomAccountName)

        factor = client.getResource(factor.getHref(), GoogleAuthenticatorFactor.class)
        assertGoogleAuthenticatorFactorFields(factor, difficultIssuer, randomAccountName)
    }

    @Test
    void testCreateAndGetFactorWithInvalidAccountName() {
        Directory dir = client.instantiate(Directory)
        dir.name = uniquify("Java SDK: ${this.getClass().getSimpleName()}.${new Object(){}.getClass().getEnclosingMethod().getName()}")
        dir = client.currentTenant.createDirectory(dir);
        deleteOnTeardown(dir)
        Account account = createTempAccountInDir(dir)

        def invalidAccountName = "No:colons!"
        Throwable e = null
        try {
            createGoogleAuthenticatorFactor(account, "validIssuer", invalidAccountName)
        }
        catch (ResourceException re) {
            e = re
            assertEquals(re.status, 400)
            assertEquals(re.getCode(), 2002)
        }
        assertTrue(e instanceof ResourceException)
    }

    @Test
    void testCreateAndGetFactorWithDifficultAccountName() {
        Directory dir = client.instantiate(Directory)
        dir.name = uniquify("Java SDK: ${this.getClass().getSimpleName()}.${new Object(){}.getClass().getEnclosingMethod().getName()}")
        dir = client.currentTenant.createDirectory(dir);
        deleteOnTeardown(dir)
        Account account = createTempAccountInDir(dir)

        def difficultAccountName = "Di//icult Account!name"
        def factor = createGoogleAuthenticatorFactor(account, null, difficultAccountName)
        assertGoogleAuthenticatorFactorFields(factor, null, difficultAccountName)

        factor = client.getResource(factor.getHref(), GoogleAuthenticatorFactor.class)
        assertGoogleAuthenticatorFactorFields(factor, null, difficultAccountName)
    }

    @Test
    void testCreateAndGetFactorWithAccountNameAndNullIssuer() {
        Directory dir = client.instantiate(Directory)
        dir.name = uniquify("Java SDK: ${this.getClass().getSimpleName()}.${new Object(){}.getClass().getEnclosingMethod().getName()}")
        dir = client.currentTenant.createDirectory(dir);
        deleteOnTeardown(dir)
        Account account = createTempAccountInDir(dir)

        def randomAccountName = uniquify("Random AccountName")
        def factor = createGoogleAuthenticatorFactor(account, null, randomAccountName)
        assertGoogleAuthenticatorFactorFields(factor, null, randomAccountName)

        factor = client.getResource(factor.getHref(), GoogleAuthenticatorFactor.class)
        assertGoogleAuthenticatorFactorFields(factor, null, randomAccountName)
    }

    @Test
    void testCreateAndGetFactorWithBothIssuerAndAccountName() {
        Directory dir = client.instantiate(Directory)
        dir.name = uniquify("Java SDK: ${this.getClass().getSimpleName()}.${new Object(){}.getClass().getEnclosingMethod().getName()}")
        dir = client.currentTenant.createDirectory(dir);
        deleteOnTeardown(dir)
        Account account = createTempAccountInDir(dir)

        def randomAccountName = uniquify("Random AccountName")
        def randomIssuer = uniquify("Random Issuer")
        def factor = createGoogleAuthenticatorFactor(account, randomIssuer, randomAccountName)
        assertGoogleAuthenticatorFactorFields(factor, randomIssuer, randomAccountName)

        factor = client.getResource(factor.getHref(), GoogleAuthenticatorFactor.class)
        assertGoogleAuthenticatorFactorFields(factor, randomIssuer, randomAccountName)
    }

    @Test
    void testSearchForFactor() {
        Directory dir = client.instantiate(Directory)
        dir.name = uniquify("Java SDK: ${this.getClass().getSimpleName()}.${new Object(){}.getClass().getEnclosingMethod().getName()}")
        dir = client.currentTenant.createDirectory(dir);
        deleteOnTeardown(dir)
        Account account = createTempAccountInDir(dir)

        account = createGoogleAuthenticatorFactorAndSmsFactor("issuer", "accountName", account)
        FactorList factors = account.getFactors()
        assertEquals(factors.size, 2)

        factors = account.getFactors(["type":"google-authenticator"])
        assertEquals(factors.size, 1)

        factors = account.getFactors(["verificationStatus":"UNVERIFIED"])
        assertEquals(factors.size, 2)

        factors = account.getFactors(["issuer":"issuer"])
        assertEquals(factors.size, 1)

        factors = account.getFactors(["issuer":"iss*"])
        assertEquals(factors.size, 1)

        factors = account.getFactors(["accountName":"accountName*"])
        assertEquals(factors.size, 1)

        factors = account.getFactors(["accountName":"account*"])
        assertEquals(factors.size, 1)
    }

    @Test(enabled = false)
    // Cascading deletes are not supported in SDK for now
    // Following issue will address it: https://github.com/stormpath/stormpath-sdk-java/issues/985
    // todo: Enable this test once the issue is fixed
    void testDeleteFactorDeletesChallenges() {
        Directory dir = client.instantiate(Directory)
        dir.name = uniquify("Java SDK: ${this.getClass().getSimpleName()}.${new Object(){}.getClass().getEnclosingMethod().getName()}")
        dir = client.currentTenant.createDirectory(dir);
        deleteOnTeardown(dir)
        Account account = createTempAccountInDir(dir)
        def randomAccountName = uniquify("Random AccountName")
        GoogleAuthenticatorFactor factor = createGoogleAuthenticatorFactor(account, null, randomAccountName)
        List<GoogleAuthenticatorChallenge> challenges = []

        def challenge = client.instantiate(GoogleAuthenticatorChallenge.class)
        challenge.setCode("000000")
        challenges.add(factor.createChallenge(Challenges.GOOGLE_AUTHENTICATOR.newCreateRequestFor(challenge).build()))
        challenges.add(factor.createChallenge(Challenges.GOOGLE_AUTHENTICATOR.newCreateRequestFor(challenge).build()))
        challenges.add(factor.createChallenge(Challenges.GOOGLE_AUTHENTICATOR.newCreateRequestFor(challenge).build()))
        challenges.add(factor.createChallenge(Challenges.GOOGLE_AUTHENTICATOR.newCreateRequestFor(challenge).build()))
        challenges.add(factor.createChallenge(Challenges.GOOGLE_AUTHENTICATOR.newCreateRequestFor(challenge).build()))

        factor.delete()

        for(def currentChallenge : challenges) {
            Throwable e = null
            try {
                client.getResource(currentChallenge.href, GoogleAuthenticatorChallenge.class)
            }
            catch (ResourceException re) {
                e = re
                assertEquals(re.status, 404)
                assertEquals(re.getCode(), 404)
            }
            assertTrue(e instanceof ResourceException)
        }

        factor.delete()
    }

    @Test(enabled = false)
    // Cascading deletes are not supported in SDK for now
    // Following issue will address it: https://github.com/stormpath/stormpath-sdk-java/issues/985
    // todo: Enable this test once the issue is fixed
    void testDeleteAccountDeletesFactorsAndChallenges() {
        Directory dir = client.instantiate(Directory)
        dir.name = uniquify("Java SDK: ${this.getClass().getSimpleName()}.${new Object(){}.getClass().getEnclosingMethod().getName()}")
        dir = client.currentTenant.createDirectory(dir);
        deleteOnTeardown(dir)
        Account account = createTempAccountInDir(dir)

        def randomAccountName = uniquify("Random AccountName")
        GoogleAuthenticatorFactor factor = createGoogleAuthenticatorFactor(account, null, randomAccountName)
        List<GoogleAuthenticatorChallenge> challenges = []

        def challenge = client.instantiate(GoogleAuthenticatorChallenge.class)
        challenge.setCode("000000")
        challenges.add(factor.createChallenge(Challenges.GOOGLE_AUTHENTICATOR.newCreateRequestFor(challenge).build()))
        challenges.add(factor.createChallenge(Challenges.GOOGLE_AUTHENTICATOR.newCreateRequestFor(challenge).build()))
        challenges.add(factor.createChallenge(Challenges.GOOGLE_AUTHENTICATOR.newCreateRequestFor(challenge).build()))
        challenges.add(factor.createChallenge(Challenges.GOOGLE_AUTHENTICATOR.newCreateRequestFor(challenge).build()))
        challenges.add(factor.createChallenge(Challenges.GOOGLE_AUTHENTICATOR.newCreateRequestFor(challenge).build()))

        account.delete()

        Throwable e = null
        try {
            client.getResource(factor.href, GoogleAuthenticatorFactor.class)
        }
        catch (ResourceException re) {
            e = re
            assertEquals(re.status, 404)
            assertEquals(re.getCode(), 404)
        }
        assertTrue(e instanceof ResourceException)

        for(def currentChallenge : challenges) {
            e = null
            try {
                client.getResource(currentChallenge.href, GoogleAuthenticatorChallenge.class)
            }
            catch (ResourceException re) {
                e = re
                assertEquals(re.status, 404)
                assertEquals(re.getCode(), 404)
            }
            assertTrue(e instanceof ResourceException)
        }
    }

}
