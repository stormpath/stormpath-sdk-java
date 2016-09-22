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
import com.stormpath.sdk.challenge.Challenges
import com.stormpath.sdk.client.ClientIT
import com.stormpath.sdk.directory.Directory
import com.stormpath.sdk.factor.sms.SmsFactor
import com.stormpath.sdk.phone.Phone
import com.stormpath.sdk.resource.ResourceException
import org.testng.annotations.Test

import static org.testng.AssertJUnit.*

/**
 * @since 1.1.0
 */
class ChallengeIT extends ClientIT{

    @Test
    void testFailedChallenge() {
        Directory dir = client.instantiate(Directory)
        dir.name = uniquify("Java SDK: ChallengeIT.testFailedChallenge")
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
        phone = phone.setNumber("6123438710").setAccount(account)
        SmsFactor factor = client.instantiate(SmsFactor)
        factor = factor.setPhone(phone)

        factor = account.createFactor(factor)

        def challenge = client.instantiate(Challenge)
        challenge.setMessage("This the message which has no place holder for the code")

        // A 13103 is returned since message does not contain a ${code}

        Throwable e = null
        try {
            factor.createChallenge(challenge)
        }
        catch(ResourceException re){
            e = re
            assertEquals(re.status, 400)
            assertEquals(re.getCode(), 13103)
        }

        assertTrue(e instanceof ResourceException)
        e = null

        // A 13103 is returned since message does not contain a ${code}. This time with CreateChallengeRequest

        def builder = Challenges.newCreateRequestFor(challenge).withResponseOptions(Challenges.options().withAccount())
        try{
            factor.createChallenge(builder.build())
        }
        catch(ResourceException re){
            e = re
            assertEquals(re.status, 400)
            assertEquals(re.getCode(), 13103)
        }
        assertTrue(e instanceof ResourceException)
    }

}
