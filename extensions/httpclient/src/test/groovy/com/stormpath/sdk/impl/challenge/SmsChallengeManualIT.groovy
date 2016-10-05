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
import com.stormpath.sdk.challenge.Challenge
import com.stormpath.sdk.challenge.ChallengeOptions
import com.stormpath.sdk.challenge.Challenges
import com.stormpath.sdk.challenge.sms.SmsChallenge
import com.stormpath.sdk.client.ClientIT
import com.stormpath.sdk.directory.Directory
import com.stormpath.sdk.factor.sms.SmsFactor
import com.stormpath.sdk.phone.Phone
import org.testng.annotations.Test

import static org.testng.AssertJUnit.*

/**
 * @since 1.1.0
 */
class SmsChallengeManualIT extends ClientIT{

    // This test and the next one (testSuccessfulChallengeVerifyChallenge) require an actual phone to complete
    // Therefore they are disabled and meant to be run manually
    @Test(enabled = false)
    void testSuccessfulChallengeSendCode() {

        // Put your phone number here
        def phoneNumber = "2016589574"

        Directory dir = client.instantiate(Directory)
        dir.name = uniquify("Java SDK: DirectoryIT.testCreateAndDeleteDirectory")
        dir = client.currentTenant.createDirectory(dir);
        Account account = client.instantiate(Account)
        account = account.setGivenName('John')
                .setSurname('DELETEME')
                .setEmail('johndeleteme@nowhere.com')
                .setPassword('Changeme1!')

        deleteOnTeardown(account)
        deleteOnTeardown(dir)

        dir.createAccount(account)

        def phone = client.instantiate(Phone)
        phone = phone.setNumber(phoneNumber)
        SmsFactor factor = client.instantiate(SmsFactor)
        factor = factor.setPhone(phone)

        factor = account.createFactor(factor)

        def challenge = client.instantiate(SmsChallenge)
        challenge.setMessage("This is your owesome code: \${code}")

        challenge = factor.createChallenge(challenge)

        println("Chalenge href: $challenge.href")
    }

    // This test and the previous one (testSuccessfulChallengeSendCode) require an actual phone to complete
    // Therefore they are disabled and meant to be run manually
    @Test(enabled = false)
    void testSuccessfulVerifyChallenge() {

        // Paste the value for challenge href retrieved from previous test (testSuccessfulChallengeSendCode) here
        def href = "https://dev.i.stormpath.com/v1/challenges/5Ybc0WUcv7J0hpgy7UCO4O"
        // Paste the code you received on your phone by running the previous test (testSuccessfulChallengeSendCode) here
        def code = "658063"


        Challenge challenge = client.getResource(href, SmsChallenge)
        boolean challengeValidated = challenge.validate(code)
        challenge = client.getResource(href, SmsChallenge)
        println("\n-----> The status of your challenge is: ${challenge.status}")
        assertTrue(challengeValidated)

        //Test Challenges with ChallengeOptions
        ChallengeOptions challengeOptions = Challenges.SMS.options().withFactor()
        challenge = client.getResource(challenge.href, SmsChallenge.class, challengeOptions)

        assertNotNull(challenge.getAccount().href)
        assertEquals(challenge.getFactor().getAccount().href, challenge.getAccount().href)
    }
}
