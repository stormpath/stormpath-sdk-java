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
package com.stormpath.sdk.impl.phone

import com.stormpath.sdk.account.Account
import com.stormpath.sdk.client.ClientIT
import com.stormpath.sdk.directory.Directory
import com.stormpath.sdk.phone.Phone
import com.stormpath.sdk.phone.PhoneList
import com.stormpath.sdk.phone.PhoneStatus
import com.stormpath.sdk.phone.PhoneVerificationStatus
import com.stormpath.sdk.phone.Phones
import com.stormpath.sdk.resource.ResourceException
import org.testng.annotations.Test
import static org.testng.Assert.*
import static org.testng.AssertJUnit.assertEquals
import static org.testng.AssertJUnit.assertNotNull
import static org.testng.AssertJUnit.assertTrue

/**
 * @since 1.1.0
 */
class PhoneIT extends ClientIT {

    @Test
    void testCreatePhoneDefaultValues() {

        Directory dir = client.instantiate(Directory)
        dir.name = uniquify("Java SDK: PhoneIT.testCreatePhoneDefaultValues")
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

        //create a phone:
        def phone = client.instantiate(Phone)
        phone.setNumber("888 391 5282")
        phone = account.createPhone(phone);

        assertNotNull phone.href
        assertEquals phone.number,"+18883915282"
        assertNull phone.name
        assertNull phone.description
        assertEquals phone.verificationStatus, PhoneVerificationStatus.UNVERIFIED
        assertEquals phone.status,PhoneStatus.ENABLED
        assertNotNull phone.createdAt
        assertNotNull phone.modifiedAt
        assertNotNull phone.account
        assertNotNull phone.account.href

        //validate GET on phone endpoint works
        phone = client.getResource(phone.href, Phone)

        //should have same values
        assertNotNull phone.href
        assertEquals phone.number,"+18883915282"
        assertNull phone.name
        assertNull phone.description
        assertEquals phone.verificationStatus, PhoneVerificationStatus.UNVERIFIED
        assertEquals phone.status,PhoneStatus.ENABLED
        assertNotNull phone.createdAt
        assertNotNull phone.modifiedAt
        assertNotNull phone.account
        assertNotNull phone.account.href

        // Create the phone with builder
        def phone2 = client.instantiate(Phone)
        phone2.setNumber("1-888-391-5288")
        def builder = Phones.newCreateRequestFor(phone2).withResponseOptions(Phones.options().withAccount())

        phone2 = account.createPhone(builder.build())

        assertNotNull phone2.href
        assertEquals phone2.number,"+18883915288"
        assertNull phone2.name
        assertNull phone2.description
        assertEquals phone2.verificationStatus, PhoneVerificationStatus.UNVERIFIED
        assertEquals phone2.status,PhoneStatus.ENABLED
        assertNotNull phone2.createdAt
        assertNotNull phone2.modifiedAt
        assertNotNull phone2.account
        assertEquals(phone2.account.fullName, account.fullName)

        //validate GET on phone endpoint works
        phone2 = client.getResource(phone2.href, Phone)

        //should have same values
        assertNotNull phone2.href
        assertEquals phone2.number,"+18883915288"
        assertNull phone2.name
        assertNull phone2.description
        assertEquals phone2.verificationStatus, PhoneVerificationStatus.UNVERIFIED
        assertEquals phone2.status,PhoneStatus.ENABLED
        assertNotNull phone2.createdAt
        assertNotNull phone2.modifiedAt
        assertNotNull phone2.account
        assertNotNull phone2.account.href
    }

    @Test
    void testCreatePhone() {
        Directory dir = client.instantiate(Directory)
        dir.name = uniquify("Java SDK: PhoneIT.testCreatePhone")
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

        def phoneNumber = "+18883915282"
        def name = "phoneName"
        String description = "This is a good description for a great phone"

        //create a phone:
        def phone = client.instantiate(Phone)
                .setNumber(phoneNumber)
                .setName(name)
                .setDescription(description)
                .setVerificationStatus(PhoneVerificationStatus.VERIFIED)
                .setStatus(PhoneStatus.DISABLED)
        phone = account.createPhone(phone);

        assertEquals(phone.number ,phoneNumber)
        assertEquals(phone.name, name)
        assertEquals(phone.description, description)
        assertEquals(phone.verificationStatus,PhoneVerificationStatus.VERIFIED)
        assertEquals(phone.status, PhoneStatus.DISABLED)
        assertNotNull(phone.createdAt)
        assertNotNull(phone.modifiedAt)
        assertNotNull(phone.account)
        assertNotNull(phone.account.href)
    }

    @Test
    void testUpdatePhone() {
        Directory dir = client.instantiate(Directory)
        dir.name = uniquify("Java SDK: PhoneIT.testUpdatePhone")
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

        def phoneNumber = "+18883915282"
        def name = "phoneName"
        String description = "This is a good description for a great phone"

        //create a phone:
        def phone = client.instantiate(Phone)
                .setNumber(phoneNumber)
                .setName(name)
                .setDescription(description)
                .setStatus(PhoneStatus.DISABLED)
        phone = account.createPhone(phone);

        assertEquals(phone.number ,phoneNumber)
        assertNotNull(phone.name)
        assertNotNull(phone.description)
        assertEquals(phone.verificationStatus,PhoneVerificationStatus.UNVERIFIED)
        assertEquals(phone.status, PhoneStatus.DISABLED)
        assertNotNull(phone.createdAt)
        assertNotNull(phone.modifiedAt)
        assertNotNull(phone.account)
        assertNotNull(phone.account.href)

        //should not be able to update to garbage number
        phone.setNumber("bogus")

        Throwable e = null;
        try{
            phone.save()
        }
        catch(ResourceException re){
            e = re
            assertEquals(re.status, 400)
            assertEquals(re.getCode(), 13101)
        }

        assertTrue(e instanceof ResourceException)

        //should be able to post long descriptions
        phone.setDescription("this is a long repeated description this is a long repeated description this is a long repeated description this is a long repeated description " +
                "this is a long repeated description this is a long repeated description this is a long repeated description this is a long repeated description " +
                "this is a long repeated description this is a long repeated description this is a long repeated description ")


        client.getResource(phone.href, Phone)

        def newNumber = "+18888675309"
        def newName = "newName"
        def newDescription = "newDescription"

        phone.setNumber(newNumber)
            .setName(newName)
            .setDescription(newDescription)
            .setStatus(PhoneStatus.ENABLED)

        phone.save()

        //validate GET on phone endpoint works
        phone = client.getResource(phone.href, Phone)

        assertEquals(phone.number, newNumber)
        assertEquals(phone.name, newName)
        assertEquals(phone.description, newDescription)
        assertEquals(phone.status, PhoneStatus.ENABLED)

        //should not be able to modify number after VERIFIED
        phone.setVerificationStatus(PhoneVerificationStatus.VERIFIED)
        phone.save()
        phone = client.getResource(phone.href, Phone)

        assertEquals(phone.getVerificationStatus(), PhoneVerificationStatus.VERIFIED)

        phone.setNumber("+18008675309")

        e = null;
        try{
            phone.save()
        }
        catch(ResourceException re){
            e = re
            assertEquals(re.status, 400)
            assertEquals(re.getCode(), 13100)
        }

        assertTrue(e instanceof ResourceException)

        //OK if number is the same
        phone.setNumber(newNumber)
        phone.save()
    }

    @Test
    void testAccountPhonesCollection() {
        Directory dir = client.instantiate(Directory)
        dir.name = uniquify("Java SDK: PhoneIT.testAccountPhonesCollection")
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

        PhoneList phoneList = client.getResource(account.href+"/phones", PhoneList)
        assertEquals(phoneList.size,0)

        //create a phone:
        def phoneNumber = "+18883915282"
        def phone = client.instantiate(Phone)
        phone.setNumber(phoneNumber)
        account.createPhone(phone);

        def phoneNumber2 = "+18883528249"
        phone = client.instantiate(Phone)
        phone.setNumber(phoneNumber2)
        account.createPhone(phone);

        phoneList = client.getResource(account.href+"/phones", PhoneList)
        assertEquals(phoneList.size,2)
    }

    @Test
    void testCreatePhoneErrors() {
        Directory dir = client.instantiate(Directory)
        dir.name = uniquify("Java SDK: PhoneIT.testCreatePhoneErrors")
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

        // Not a number
        def phone = client.instantiate(Phone)
                .setNumber("notANumber")

        Throwable e = null
        try{
            account.createPhone(phone);
        }
        catch(ResourceException re){
            e=re
            assertEquals(re.status, 400)
            assertEquals(re.getCode(), 13101)
        }

        assertTrue(e instanceof ResourceException)

        // Extra numbers
        phone.setNumber("+188839152822222222")
        e = null
        try{
            account.createPhone(phone);
        }
        catch(ResourceException re){
            e = re
            assertEquals(re.status, 400)
            assertEquals(re.getCode(), 13101)
        }

        assertTrue(e instanceof ResourceException)

        // No number
        phone = client.instantiate(Phone)

        e = null
        try{
            account.createPhone(phone);
        }
        catch(ResourceException re){
            e = re
            assertEquals(re.status, 400)
            assertEquals(re.getCode(), 2000)
        }

        assertTrue(e instanceof ResourceException)

        // Null number
        phone.setNumber(null)

        e = null
        try{
            account.createPhone(phone);
        }
        catch(ResourceException re){
            e = re
            assertEquals(re.status, 400)
            assertEquals(re.getCode(), 2000)
        }

        assertTrue(e instanceof ResourceException)

        //try to create duplicate phone numbers for the same account
        def phoneNumber = "+18883915282"
        phone = client.instantiate(Phone)
        phone.setNumber(phoneNumber)
        account.createPhone(phone);
        phone = client.instantiate(Phone)
        phone.setNumber(phoneNumber)

        e = null
        try{
            account.createPhone(phone);
        }
        catch(ResourceException re){
            e = re
            assertEquals(re.status, 409)
            assertEquals(re.getCode(), 13102)
        }

        assertTrue(e instanceof ResourceException)
    }

    @Test(enabled = false)
    // Cascading deletes are not supported in SDK for now
    // Following issue will address it: https://github.com/stormpath/stormpath-sdk-java/issues/985
    // todo: Enable this test once the issue is fixed
    void testDeleteAccountDeletesPhones() {
        Directory dir = client.instantiate(Directory)
        dir.name = uniquify("Java SDK: PhoneIT.testDeleteAccountDeletesPhones")
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

        def phoneNumber = "2016549571"
        def phone = client.instantiate(Phone)
                .setNumber(phoneNumber)
        phone = account.createPhone(phone);

        def phoneNumber2 = "2026549572"
        def phone2 = client.instantiate(Phone)
                .setNumber(phoneNumber2)

        account.createPhone(phone2);

        account.delete()

        Throwable e = null;
        try{
            client.getResource(account.href, Account)
        }
        catch(ResourceException re){
            e = re
            assertEquals(re.status, 404)
            assertEquals(re.getCode(), 404)
        }

        assertTrue(e instanceof ResourceException)

        e = null;
        try{
            client.getResource(phone.href, Phone)
        }
        catch(ResourceException re){
            e = re
            assertEquals(re.status, 404)
            assertEquals(re.getCode(), 404)
        }

        assertTrue(e instanceof ResourceException)
    }

    @Test
    void testDeletePhone() {
        Directory dir = client.instantiate(Directory)
        dir.name = uniquify("Java SDK: PhoneIT.testDeletePhone")
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

        //create second phone to make sure deletion of one phone doesn't delete all phones in collection
        def phoneNumber = "+18883915282"
        def phoneNumber2 = "+18883915283"

        def phone = client.instantiate(Phone)
                .setNumber(phoneNumber)

        def phone2 = client.instantiate(Phone)
                .setNumber(phoneNumber2)

        phone = account.createPhone(phone);
        account.createPhone(phone2);

        phone.delete()

        Throwable e = null;
        try{
            client.getResource(phone.href, Phone)
        }
        catch(ResourceException re){
            e = re
            assertEquals(re.status, 404)
            assertEquals(re.getCode(), 404)
        }

        assertTrue(e instanceof ResourceException)

        PhoneList phoneList = client.getResource(account.href+"/phones", PhoneList)

        assertEquals(phoneList.size, 1)

        //should be able to recreate phone
        def phone3 = client.instantiate(Phone)
                .setNumber(phoneNumber)
        account.createPhone(phone3);
    }

    @Test
    void testSearchOnPhoneNameDescription() {
        Directory dir = client.instantiate(Directory)
        dir.name = uniquify("Java SDK: PhoneIT.testSearchOnPhoneNameDescription")
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

        //create second phone to make sure deletion of one phone doesn't delete all phones in collection
        def phoneNumber = "+18883915282"

        def phone = client.instantiate(Phone)
                .setNumber(phoneNumber)

        account.createPhone(phone)

        phoneNumber = "+18883915283"
        def name = "Stormtrooper's Phone"
        def description = "Alternate Description"

        def phone2 = client.instantiate(Phone)
                .setNumber(phoneNumber)
                .setName(name)
                .setDescription(description)

        account.createPhone(phone2)

        phoneNumber = "+18883915284"
        description = "Stormtrooper's Description"

        def phone3 = client.instantiate(Phone)
                .setNumber(phoneNumber)
                .setDescription(description)

        account.createPhone(phone3)

        Map<String, Object> queryParams = new HashMap<String, Object>();
        queryParams.put("name", "Storm*");
        PhoneList phoneList = account.getPhones(queryParams)

        assertEquals(phoneList.size, 1)

        queryParams = new HashMap<String, Object>();
        queryParams.put("name", "Stormtrooper's Phone");
        phoneList = account.getPhones(queryParams)

        assertEquals(phoneList.size, 1)

        queryParams = new HashMap<String, Object>();
        queryParams.put("description", "Storm*");
        phoneList = account.getPhones(queryParams)

        assertEquals(phoneList.size, 1)

        queryParams = new HashMap<String, Object>();
        queryParams.put("description", "*Description");
        phoneList = account.getPhones(queryParams)

        assertEquals(phoneList.size, 2)

        queryParams = new HashMap<String, Object>();
        queryParams.put("number", "+18883915282");
        phoneList = account.getPhones(queryParams)

        assertEquals(phoneList.size, 1)
    }

    @Test
    void testGetPhonesWithDifferentCriteria() {
        Directory dir = client.instantiate(Directory)
        dir.name = uniquify("Java SDK: PhoneIT.testGetPhonesWithDifferentCriteria")
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

        //create 2 phones
        def phoneNumber1 = "+18883915281"
        def phoneNumber2 = "+18883915282"

        def phone1 = client.instantiate(Phone)
                .setNumber(phoneNumber1)

        def phone2 = client.instantiate(Phone)
                .setNumber(phoneNumber2)

        phone1 = account.createPhone(phone1)
        phone2 = account.createPhone(phone2)

        deleteOnTeardown(phone1)
        deleteOnTeardown(phone2)

        //Let's check we have 2 phones and the account is not materialized
        def phones = account.getPhones(Phones.criteria().orderByName().ascending());
        assertEquals(phones.getLimit(), 25);
        assertEquals(phones.getProperty("items").size, 2);
        assertEquals(phones.iterator().next().account.materialized, false)

        //Let's retrieve 1 phone per page and confirm that the account is materialized
        phones = account.getPhones(Phones.criteria().limitTo(1).withAccount().orderByName().ascending());
        assertEquals(phones.getLimit(), 1);
        assertEquals(phones.getProperty("items").size, 1);
        assertEquals(phones.getOffset(), 0);
        assertEquals(phones.iterator().next().account.materialized, true)

        Phone firstPhoneWithOffset0 = phones.iterator().next();

        assertNotNull(firstPhoneWithOffset0);
        assertEquals(firstPhoneWithOffset0.getHref(), phone1.getHref());

        //Since we have 2 phones and offset = 1 here, then this page should only have 1 phone, the last one
        phones = account.getPhones(Phones.criteria().offsetBy(1).orderByName().ascending());
        assertEquals(phones.getLimit(), 25);
        assertEquals(phones.getProperty("items").size, 1);
        assertEquals(phones.getOffset(), 1);

        Phone firstPhoneWithOffset1 = phones.iterator().next();

        assertNotNull(firstPhoneWithOffset1);
        assertEquals(firstPhoneWithOffset1.getHref(), phone2.getHref());

        assertTrue(firstPhoneWithOffset0.getHref() != firstPhoneWithOffset1.getHref());
    }
}
