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
import com.stormpath.sdk.account.AccountOptions
import com.stormpath.sdk.account.Accounts
import com.stormpath.sdk.challenge.Challenge
import com.stormpath.sdk.client.ClientIT
import com.stormpath.sdk.directory.Directory
import com.stormpath.sdk.factor.Factor
import com.stormpath.sdk.factor.FactorStatus
import com.stormpath.sdk.factor.FactorVerificationStatus
import com.stormpath.sdk.factor.Factors
import com.stormpath.sdk.factor.sms.SmsFactor
import com.stormpath.sdk.factor.sms.SmsFactorOptions
import com.stormpath.sdk.impl.factor.sms.DefaultSmsFactor
import com.stormpath.sdk.impl.resource.AbstractResource
import com.stormpath.sdk.phone.Phone
import com.stormpath.sdk.phone.PhoneStatus
import com.stormpath.sdk.resource.ResourceException
import org.testng.annotations.Test

import java.lang.reflect.Field

import static org.testng.AssertJUnit.*

class FactorIT extends ClientIT {

    private static final String VALID_PHONE_NUMBER = "+18883915282"

    @Test
    void testCreateFactorWithNewPhone() {
        Directory dir = client.instantiate(Directory)
        dir.name = uniquify("Java SDK: FactorIT.testCreateFactorWithNewPhone")
        dir = client.currentTenant.createDirectory(dir)

        assertNotNull dir.href

        def email = 'johndeleteme@nowhere.com'

        Account account = client.instantiate(Account)
        account = account.setGivenName('John')
                .setSurname('DELETEME')
                .setEmail(email)
                .setPassword('Changeme1!')

        dir.createAccount(account)

        deleteOnTeardown(account)
        deleteOnTeardown(dir)
        def phone = client.instantiate(Phone)
        phone.setNumber(VALID_PHONE_NUMBER)
        phone = account.createPhone(phone);

        SmsFactor smsFactor = client.instantiate(SmsFactor)
        smsFactor = smsFactor.setPhone(phone)
        smsFactor = account.createFactor(smsFactor);

        SmsFactorOptions smsFactorOptions = Factors.SMS.options().withPhone()
        smsFactor = client.getResource(smsFactor.href, SmsFactor.class, smsFactorOptions)

        assertNotNull smsFactor.href
        assertNotNull phone.href
        assertEquals(smsFactor.status, FactorStatus.ENABLED)
        assertEquals(smsFactor.phone.status, PhoneStatus.ENABLED)
    }

    @Test
    void testCreateFactorWithoutChallenge() {
        Directory dir = client.instantiate(Directory)
        dir.name = uniquify("Java SDK: DirectoryIT.testCreateAndDeleteDirectory")
        dir = client.currentTenant.createDirectory(dir);
        assertNotNull dir.href

        def email = 'johndeleteme@nowhere.com'

        Account account = client.instantiate(Account)
        account = account.setGivenName('John')
                .setSurname('DELETEME')
                .setEmail(email)
                .setPassword('Changeme1!')

        deleteOnTeardown(account)
        deleteOnTeardown(dir)

        dir.createAccount(account)

        AccountOptions accountOptions = Accounts.options().withFactors()
        Account retrievedAccount = client.getResource(account.href, Account.class, accountOptions)
        Map accountProperties = getValue(AbstractResource, retrievedAccount, "properties")
        assertTrue accountProperties.get("factors").size == 0

        def phone = client.instantiate(Phone)
        phone = phone.setNumber(VALID_PHONE_NUMBER)
        def factor = client.instantiate(SmsFactor)
        factor = factor.setPhone(phone)

        factor = account.createFactor(factor)
        assertNotNull factor.href
        assertEquals(factor.factorVerificationStatus, FactorVerificationStatus.UNVERIFIED)
        assertEquals(factor.status, FactorStatus.ENABLED)
        assertNotNull(factor.getAccount())
        assertNotNull(factor.getAccount().href)
        assertNotNull(((SmsFactor) factor).getPhone())
        assertNotNull(((SmsFactor) factor).getPhone().href)

        //test GET works
        SmsFactorOptions smsFactorOptions = Factors.SMS.options().withChallenges()
        factor = client.getResource(factor.href, DefaultSmsFactor.class, smsFactorOptions)

        assertEquals(factor.getFactorVerificationStatus(), FactorVerificationStatus.UNVERIFIED)
        assertEquals(factor.getStatus(), FactorStatus.ENABLED)
        assertNotNull(factor.getAccount())
        assertNotNull(factor.getAccount().href)
        assertNotNull(((SmsFactor) factor).getPhone())
        assertNotNull(((SmsFactor) factor).getPhone().href)
        assertNotNull(factor.getChallenges())

        //test search by factor type
        DefaultFactorList factorList = account.getFactors(["type": "sms"])
        assertEquals(factorList.size, 1)

        //test search by verification status
        DefaultFactorList factorList1 = account.getFactors(["verificationStatus": "UNVERIFIED"])
        assertEquals(factorList1.size, 1)
    }

    @Test
    void testDefaultChallengeOnFactorCreation() {
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
        phone = phone.setNumber(VALID_PHONE_NUMBER)
        SmsFactor factor = client.instantiate(SmsFactor)
        factor = factor.setPhone(phone)

        def builder = Factors.SMS.newCreateRequestFor(factor).createChallenge()
        factor = account.createFactor(builder.build())

        SmsFactorOptions smsFactorOptions = Factors.SMS.options().withMostRecentChallenge()
        factor = client.getResource(factor.href, SmsFactor.class, smsFactorOptions)

        assertEquals(factor.getFactorVerificationStatus(), FactorVerificationStatus.UNVERIFIED)
        assertEquals(factor.getStatus(), FactorStatus.ENABLED)
        assertNotNull(factor.getAccount())
        assertNotNull(factor.getAccount().href)
        assertNotNull(factor.getPhone())
        assertNotNull(factor.getPhone().href)
        assertNotNull(factor.mostRecentChallenge)
        assertNotNull(factor.mostRecentChallenge.href)
        assertNotNull(factor.mostRecentChallenge.message)
    }

    @Test
    void testDefaultChallengeOnFactorCreationFailsForDisabledPhone() {
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
        phone.setNumber(VALID_PHONE_NUMBER).setStatus(PhoneStatus.DISABLED)
        phone = account.createPhone(phone);

        def factor = client.instantiate(SmsFactor)
        factor = factor.setPhone(phone)

        def builder = Factors.SMS.newCreateRequestFor(factor).createChallenge()

        // Since phone is disabled, default challenge on factor creation should fail
        Throwable e = null
        try {
            account.createFactor(builder.build())
        }
        catch (ResourceException re) {
            e = re
            assertEquals(re.status, 400)
            assertEquals(re.getCode(), 13110)
        }

        assertTrue(e instanceof ResourceException)

    }

    @Test
    void testDefaultChallengeOnFactorCreationFailsForDisabledFactor() {
        Directory dir = client.instantiate(Directory)
        dir.name = uniquify("Java SDK: FactorIT.testDefaultChallengeOnFactorCreationFailsForDisabledFactor")
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
        phone.setNumber(VALID_PHONE_NUMBER)
        phone = account.createPhone(phone);

        SmsFactor factor = client.instantiate(SmsFactor)
        factor = factor.setPhone(phone)
        factor = factor.setStatus(FactorStatus.DISABLED)
        factor = account.createFactor(factor)

        factor = client.getResource(factor.href, SmsFactor.class)

        // Since factor is disabled, default challenge on factor creation should fail
        Throwable e = null
        try {
            factor.challenge()
        }
        catch (ResourceException re) {
            e = re
            assertEquals(re.status, 400)
            assertEquals(re.getCode(), 13109)
        }

        assertTrue(e instanceof ResourceException)
    }

    @Test
    void testUpdateFactorStatus() {
        Directory dir = client.instantiate(Directory)
        dir.name = uniquify("Java SDK: FactorIT.testUpdateFactorStatus")
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
        phone.setNumber(VALID_PHONE_NUMBER)
        phone = account.createPhone(phone);

        SmsFactor factor = client.instantiate(SmsFactor)
        factor = factor.setPhone(phone)

        def builder = Factors.SMS.newCreateRequestFor(factor)
        factor = account.createFactor(builder.build())

        factor.setStatus(FactorStatus.DISABLED)
        factor.save()

        // Since factor is disabled, challenging it would fail
        Throwable e = null
        try {
            factor.challenge()
        }
        catch (ResourceException re) {
            e = re
            assertEquals(re.status, 400)
            assertEquals(re.getCode(), 13109)
        }

        assertTrue(e instanceof ResourceException)
    }

    @Test
    void testCustomChallengeOnFactorCreation() {
        Directory dir = client.instantiate(Directory)
        dir.name = uniquify("Java SDK: FactorIT.testCustomChallengeOnFactorCreation")
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
        phone.setNumber(VALID_PHONE_NUMBER)
        SmsFactor factor = client.instantiate(SmsFactor)
        factor = factor.setPhone(phone)
        def challenge = client.instantiate(Challenge)
        //challenge = challenge.setMessage("Your awesome code is \${code}")
        factor = factor.setChallenge(challenge)

        factor = account.createFactor(factor)

        SmsFactorOptions smsFactorOptions = Factors.SMS.options().withMostRecentChallenge()
        factor = client.getResource(factor.href, SmsFactor.class, smsFactorOptions)

        assertEquals(factor.getFactorVerificationStatus(), FactorVerificationStatus.UNVERIFIED)
        assertEquals(factor.getStatus(), FactorStatus.ENABLED)
        assertNotNull(factor.getAccount())
        assertEquals(account.href, factor.getAccount().href)
        assertNotNull(factor.getPhone())
        assertNotNull(factor.getPhone().href)
        assertNotNull(factor.getMostRecentChallenge())
        assertNotNull(factor.getMostRecentChallenge().href)
        assertNotNull(factor.getMostRecentChallenge().message)

        client.getResource(factor.getMostRecentChallenge().href, Challenge.class)

    }

    @Test
    void createFactorWithExistingPhone() {
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
        phone.setNumber(VALID_PHONE_NUMBER)
        phone = account.createPhone(phone);

        assertNotNull(phone.href)

        SmsFactor factor = client.instantiate(SmsFactor)
        factor.setPhone(phone)
        factor = account.createFactor(factor)

        SmsFactorOptions smsFactorOptions = Factors.SMS.options().withPhone()
        factor = client.getResource(factor.href, SmsFactor.class, smsFactorOptions)

        assertEquals(factor.factorVerificationStatus, FactorVerificationStatus.UNVERIFIED)
        assertEquals(factor.status, FactorStatus.ENABLED)
        assertNotNull(factor.getAccount())
        assertEquals(account.href, factor.getAccount().href)
        assertNotNull(factor.href)

        SmsFactor factor2 = client.instantiate(SmsFactor)
        def phone2 = client.instantiate(Phone)
        phone2.setNumber(VALID_PHONE_NUMBER)
        factor2.setPhone(phone2)

        Throwable e = null
        try {
            account.createFactor(factor2)
        }
        catch (ResourceException re) {
            e = re
            assertEquals(re.status, 400)
            assertEquals(re.getCode(), 13105)
        }

        assertTrue(e instanceof ResourceException)
    }

    @Test
    void createFactorWithExistingPhoneByHref() {
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
        phone.setNumber(VALID_PHONE_NUMBER)
        phone = account.createPhone(phone);

        assertNotNull(phone.href)

        def phone2 = client.instantiate(Phone)
        ((AbstractResource)phone2).setProperties(["href":phone.href])
        SmsFactor factor = client.instantiate(SmsFactor)
        factor.setPhone(phone)
        factor = account.createFactor(factor)

        SmsFactorOptions smsFactorOptions = Factors.SMS.options().withPhone()
        factor = client.getResource(factor.href, SmsFactor.class, smsFactorOptions)

        assertEquals(factor.factorVerificationStatus, FactorVerificationStatus.UNVERIFIED)
        assertEquals(factor.status, FactorStatus.ENABLED)
        assertNotNull(factor.getAccount())
        assertNotNull(factor.getAccount().href)
        assertNotNull(factor.href)
        assertEquals(factor.getPhone().number, VALID_PHONE_NUMBER)

        SmsFactor factor2 = client.instantiate(SmsFactor)
        phone2.setNumber(VALID_PHONE_NUMBER)
        factor2.setPhone(phone2)

        Throwable e = null
        try {
            account.createFactor(factor2)
        }
        catch (ResourceException re) {
            e = re
            assertEquals(re.status, 400)
            assertEquals(re.getCode(), 13105)
        }

        assertTrue(e instanceof ResourceException)
    }

    @Test
    void testDeleteFactor() {
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
        phone.setNumber(VALID_PHONE_NUMBER)

        SmsFactor factor = client.instantiate(SmsFactor)
        factor.setPhone(phone)
        factor = account.createFactor(factor)

        client.getResource(factor.href, SmsFactor)

        factor.getPhone().delete()
        factor.delete()


        Throwable e = null
        try{
            client.getResource(factor.href, SmsFactor)
        }
        catch(ResourceException re){
            e = re
            assertEquals(re.status, 404)
            assertEquals(re.getCode(), 404)
        }

        assertTrue(e instanceof ResourceException)

        //should be able to recreate factor & phone
        def phone2 = client.instantiate(Phone)
        phone2.setNumber(VALID_PHONE_NUMBER)
        SmsFactor factor2 = client.instantiate(SmsFactor)
        factor2.setPhone(phone2)
        account.createFactor(factor2)
    }

    @Test(enabled = false)
    // Cascading deletes are not supported in SDK for now
    // Following issue will address it: https://github.com/stormpath/stormpath-sdk-java/issues/985
    // todo: Enable this test once the issue is fixed
    void testDeletePhoneDeletesFactor() {
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
        phone.setNumber(VALID_PHONE_NUMBER)

        SmsFactor factor = client.instantiate(SmsFactor)
        factor.setPhone(phone)
        factor = account.createFactor(factor)

        assertNotNull(factor.phone)
        assertNotNull(factor.phone.href)

        factor.phone.delete()

        Throwable e = null
        try{
            client.getResource(factor.href, SmsFactor)
        }
        catch(ResourceException re){
            e = re
            assertEquals(re.status, 404)
            assertEquals(re.getCode(), 404)
        }

        assertTrue(e instanceof ResourceException)

        //should be able to recreate factor & phone
        factor = client.instantiate(SmsFactor)
        factor.setPhone(phone)
        factor = account.createFactor(factor)

        assertNotNull(factor.href)
        assertNotNull(factor.phone.href)
        assertEquals(factor.account.href, account.href)
    }

    @Test(enabled = false)
    // Cascading deletes are not supported in SDK for now
    // Following issue will address it: https://github.com/stormpath/stormpath-sdk-java/issues/985
    // todo: Enable this test once the issue is fixed
    void deletingFactorDeletesMostRecentChallenge() {
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
        phone.setNumber(VALID_PHONE_NUMBER)

        SmsFactor factor = client.instantiate(SmsFactor)
        factor.setPhone(phone)
        def builder = Factors.SMS.newCreateRequestFor(factor).createChallenge()

        account.createFactor(builder.build())
        SmsFactorOptions smsFactorOptions = Factors.SMS.options().withMostRecentChallenge()
        factor = client.getResource(factor.href, SmsFactor.class, smsFactorOptions)

        assertEquals(factor.factorVerificationStatus, FactorVerificationStatus.UNVERIFIED)
        assertEquals(factor.status, FactorStatus.ENABLED)
        assertNotNull(factor.getAccount())
        assertNotNull(factor.getAccount().href)
        assertNotNull(factor.getPhone())
        assertNotNull(factor.getPhone().href)
        assertNotNull(factor.getMostRecentChallenge())
        assertNotNull(factor.getMostRecentChallenge().href)
        assertNotNull(factor.getMostRecentChallenge().message)

        client.getResource(factor.getMostRecentChallenge().href, Challenge)

        factor.delete()

        Throwable e = null
        try{
            client.getResource(factor.getMostRecentChallenge().href, Challenge)
        }
        catch(ResourceException re){
            e = re
            assertEquals(re.status, 404)
            assertEquals(re.getCode(), 404)
        }

        assertTrue(e instanceof ResourceException)
    }

    @Test
    void testGetFactorsWithDifferentCriteria() {
        Directory dir = client.instantiate(Directory)
        dir.name = uniquify("Java SDK: DirectoryIT.testCreateAndDeleteDirectory")
        dir = client.currentTenant.createDirectory(dir)

        assertNotNull dir.href

        def email = 'johndeleteme@nowhere.com'

        Account account = client.instantiate(Account)
        account = account.setGivenName('John')
                .setSurname('DELETEME')
                .setEmail(email)
                .setPassword('Changeme1!')

        dir.createAccount(account)

        deleteOnTeardown(account)
        deleteOnTeardown(dir)
        def phone = client.instantiate(Phone)
        phone.setNumber(VALID_PHONE_NUMBER)
        phone = account.createPhone(phone);

        def phone2 = client.instantiate(Phone)
        phone2.setNumber("8002346195")
        phone2 = account.createPhone(phone2);

        def phone3 = client.instantiate(Phone)
        phone3.setNumber("8002346155")
        phone3 = account.createPhone(phone3);

        SmsFactor smsFactor = client.instantiate(SmsFactor)
        smsFactor = smsFactor.setPhone(phone).setStatus(FactorStatus.DISABLED)
        account.createFactor(smsFactor);

        SmsFactor smsFactor2 = client.instantiate(SmsFactor)
        smsFactor2 = smsFactor2.setPhone(phone2).setStatus(FactorStatus.ENABLED)
        account.createFactor(smsFactor2);

        SmsFactor smsFactor3 = client.instantiate(SmsFactor)
        smsFactor3 = smsFactor3.setPhone(phone3).setStatus(FactorStatus.DISABLED)
        account.createFactor(smsFactor3);

        def factors = account.getFactors(Factors.SMS.criteria().orderByStatus().ascending());
        assertEquals(factors.getLimit(), 25);
        assertEquals(factors.getProperty("items").size, 3);
        assertEquals(factors.iterator().next().account.materialized, false)
        List<Factor> factorList = factors.toList()
        assertEquals(factorList.get(0).status, FactorStatus.DISABLED)
        assertEquals(factorList.get(1).status, FactorStatus.DISABLED)
        assertEquals(factorList.get(2).status, FactorStatus.ENABLED)
    }

    private Object getValue(Class clazz, Object object, String fieldName) {
        Field field = clazz.getDeclaredField(fieldName)
        field.setAccessible(true)
        return field.get(object)
    }

}
