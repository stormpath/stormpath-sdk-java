package com.stormpath.sdk.impl.factor

import com.stormpath.sdk.account.Account
import com.stormpath.sdk.account.AccountOptions
import com.stormpath.sdk.account.Accounts
import com.stormpath.sdk.challenge.Challenge
import com.stormpath.sdk.client.ClientIT
import com.stormpath.sdk.directory.Directory
import com.stormpath.sdk.factor.FactorStatus
import com.stormpath.sdk.factor.Factors
import com.stormpath.sdk.factor.FactorVerificationStatus
import com.stormpath.sdk.factor.sms.SmsFactor
import com.stormpath.sdk.factor.sms.SmsFactorOptions
import com.stormpath.sdk.impl.factor.sms.DefaultSmsFactor
import com.stormpath.sdk.impl.resource.AbstractResource
import com.stormpath.sdk.phone.Phone
import com.stormpath.sdk.phone.PhoneStatus
import com.stormpath.sdk.resource.ResourceException
import org.testng.annotations.Test
import java.lang.reflect.Field
import static org.testng.Assert.assertEquals
import static org.testng.Assert.assertNull
import static org.testng.Assert.assertTrue
import static org.testng.AssertJUnit.assertNotNull

class FactorIT extends ClientIT {

    private static final String VALID_PHONE_NUMBER = "+15005550006"

    @Test
    void testCreateFactorWithNewPhone() {
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
        phone.setNumber(VALID_PHONE_NUMBER).setAccount(account)
        phone = account.createPhone(phone);

        SmsFactor smsFactor = client.instantiate(DefaultSmsFactor)
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
        phone = phone.setNumber(VALID_PHONE_NUMBER).setAccount(retrievedAccount)
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
        assertNull(((SmsFactor) factor).challenge)

        //test GET works
        SmsFactorOptions smsFactorOptions = Factors.SMS.options().withChallenges()
        factor = client.getResource(factor.href, DefaultSmsFactor.class, smsFactorOptions)

        assertEquals(factor.getFactorVerificationStatus(), FactorVerificationStatus.UNVERIFIED)
        assertEquals(factor.getStatus(), FactorStatus.ENABLED)
        assertNotNull(factor.getAccount())
        assertNotNull(factor.getAccount().href)
        assertNotNull(((SmsFactor) factor).getPhone())
        assertNotNull(((SmsFactor) factor).getPhone().href)
        assertNull(((SmsFactor) factor).challenge)
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
        phone = phone.setNumber(VALID_PHONE_NUMBER).setAccount(account)
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
        phone.setNumber(VALID_PHONE_NUMBER).setAccount(account).setStatus(PhoneStatus.DISABLED)
        phone = account.createPhone(phone);

        def factor = client.instantiate(SmsFactor)
        factor = factor.setPhone(phone)

        def builder = Factors.SMS.newCreateRequestFor(factor).createChallenge()

        try {
            account.createFactor(builder.build())
        }
        catch (ResourceException re) {
            assertEquals(re.status, 400)
            assertEquals(re.getCode(), 13110)
        }

    }

    @Test
    void testDefaultChallengeOnFactorCreationFailsForDisabledFactor() {
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
        phone.setNumber(VALID_PHONE_NUMBER).setAccount(account)
        phone = account.createPhone(phone);

        SmsFactor factor = client.instantiate(SmsFactor)
        factor = factor.setPhone(phone)
        factor = factor.setStatus(FactorStatus.DISABLED)
        factor = account.createFactor(factor)

        factor = client.getResource(factor.href, SmsFactor.class)

        try {
            factor.challenge()
        }
        catch (ResourceException re) {
            assertEquals(re.status, 400)
            assertEquals(re.getCode(), 13109)
        }
    }

    @Test
    void testUpdateFactorStatus() {
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
        phone.setNumber(VALID_PHONE_NUMBER).setAccount(account)
        phone = account.createPhone(phone);

        SmsFactor factor = client.instantiate(SmsFactor)
        factor = factor.setPhone(phone)

        def builder = Factors.SMS.newCreateRequestFor(factor)
        factor = account.createFactor(builder.build())

        factor.setStatus(FactorStatus.DISABLED)
        factor.save()

        try {
            factor.challenge()
        }
        catch (ResourceException re) {
            assertEquals(re.status, 400)
            assertEquals(re.getCode(), 13109)
        }
    }

    @Test
    void testCustomChallengeOnFactorCreation() {
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
        phone.setNumber(VALID_PHONE_NUMBER).setAccount(account)
        SmsFactor factor = client.instantiate(SmsFactor)
        factor = factor.setPhone(phone)
        def challenge = client.instantiate(Challenge)
        challenge = challenge.setMessage("Your awesome code is \${code}")
        factor = factor.setChallenge(challenge)

        factor = account.createFactor(factor)

        SmsFactorOptions smsFactorOptions = Factors.SMS.options().withMostRecentChallenge()
        factor = client.getResource(factor.href, SmsFactor.class, smsFactorOptions)

        assertEquals(factor.getFactorVerificationStatus(), FactorVerificationStatus.UNVERIFIED)
        assertEquals(factor.getStatus(), FactorStatus.ENABLED)
        assertNotNull(factor.getAccount())
        assertNotNull(factor.getAccount().href)
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
        phone.setNumber(VALID_PHONE_NUMBER).setAccount(account)
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
        assertNotNull(factor.getAccount().href)
        assertNotNull(factor.href)
        assertEquals(factor.getPhone().number, VALID_PHONE_NUMBER)
        assertNull(factor.getChallenge())

        SmsFactor factor2 = client.instantiate(SmsFactor)
        def phone2 = client.instantiate(Phone)
        phone2.setNumber(VALID_PHONE_NUMBER)
        factor2.setPhone(phone2)
        try {
            account.createFactor(factor2)
        }
        catch (ResourceException re) {
            assertEquals(re.status, 400)
            assertEquals(re.getCode(), 13105)
        }
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
        phone.setNumber(VALID_PHONE_NUMBER).setAccount(account)
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
        assertNull(factor.getChallenge())

        SmsFactor factor2 = client.instantiate(SmsFactor)
        phone2.setNumber(VALID_PHONE_NUMBER)
        factor2.setPhone(phone2)
        try {
            account.createFactor(factor2)
        }
        catch (ResourceException re) {
            assertEquals(re.status, 400)
            assertEquals(re.getCode(), 13105)
        }
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
        phone.setNumber(VALID_PHONE_NUMBER).setAccount(account)

        SmsFactor factor = client.instantiate(SmsFactor)
        factor.setPhone(phone)
        factor = account.createFactor(factor)

        client.getResource(factor.href, SmsFactor)

        factor.delete()

        try{
            client.getResource(factor.href, SmsFactor)
        }
        catch(ResourceException re){
            assertEquals(re.status, 404)
            assertEquals(re.getCode(), 404)
        }
    }

    @Test
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
        phone.setNumber(VALID_PHONE_NUMBER).setAccount(account)

        SmsFactor factor = client.instantiate(SmsFactor)
        factor.setPhone(phone)
        factor = account.createFactor(factor)

        assertNotNull(factor.phone)
        assertNotNull(factor.phone.href)

        factor.phone.delete()

        try{
            client.getResource(factor.href, SmsFactor)
        }
        catch(ResourceException re){
            assertEquals(re.status, 404)
            assertEquals(re.getCode(), 404)
        }
    }

    @Test
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
        phone.setNumber(VALID_PHONE_NUMBER).setAccount(account)

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

        try{
            client.getResource(factor.getMostRecentChallenge().href, Challenge)
        }
        catch(ResourceException re){
            assertEquals(re.status, 404)
            assertEquals(re.getCode(), 404)
        }
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
        phone.setNumber(VALID_PHONE_NUMBER).setAccount(account)
        phone = account.createPhone(phone);

        SmsFactor smsFactor = client.instantiate(DefaultSmsFactor)
        smsFactor = smsFactor.setPhone(phone)
        smsFactor = account.createFactor(smsFactor);

        def factors = account.getFactors(Factors.SMS.criteria().orderByStatus().ascending());
        assertEquals(factors.getLimit(), 25);
        assertEquals(factors.getProperty("items").size, 1);
        assertEquals(factors.iterator().next().account.materialized, false)
    }

    private Object getValue(Class clazz, Object object, String fieldName) {
        Field field = clazz.getDeclaredField(fieldName)
        field.setAccessible(true)
        return field.get(object)
    }

}
