/*
 * Copyright 2015 Stormpath, Inc.
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

/**
 * @since 1.0.4
 */
class PhoneIT extends ClientIT {

    @Test
    void testCreatePhoneDefaultValues() {

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

        //create a phone:
        def phone = client.instantiate(Phone)
        phone.setNumber("+18883915282").setAccount(account)
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
    }

    @Test
    void testCreatePhone() {
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

        def phoneNumber = "+18883915282"
        def name = "phoneName"
        String description = "This is a good description for a great phone"

        //create a phone:
        def phone = client.instantiate(Phone)
                .setNumber(phoneNumber)
                .setName(name)
                .setDescription(description)
                .setAccount(account)
                .setVerificationStatus(PhoneVerificationStatus.VERIFIED)
                .setStatus(PhoneStatus.DISABLED)
        phone = account.createPhone(phone);

        assertEquals(phone.number ,phoneNumber)
        assertNotNull(phone.name)
        assertNotNull(phone.description)
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

        def phoneNumber = "+18883915282"
        def name = "phoneName"
        String description = "This is a good description for a great phone"

        //create a phone:
        def phone = client.instantiate(Phone)
                .setNumber(phoneNumber)
                .setName(name)
                .setDescription(description)
                .setAccount(account)
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

        try {
            phone.save()
        }
        catch(ResourceException re){
            assertEquals(re.status, 400)
            assertEquals(re.getCode(), 13101)
        }

        //should not be able to post description > 140
        phone.setDescription("this is a long repeated description this is a long repeated description this is a long repeated description this is a long repeated description " +
                "this is a long repeated description this is a long repeated description this is a long repeated description this is a long repeated description " +
                "this is a long repeated description this is a long repeated description this is a long repeated description ")

        try {
            client.getResource(phone.href, Phone)
        }
        catch(ResourceException re){
            assertEquals(re.status, 400)
            assertEquals(re.getCode(), 2008)
        }

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

        try {
            phone.save()
        }
        catch(ResourceException re){
            assertEquals(re.status, 400)
            assertEquals(re.getCode(), 13100)
        }

        //OK if number is the same
        phone.setNumber(newNumber)
        phone.save()
    }

    @Test
    void testAccountPhonesCollection() {
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

        PhoneList phoneList = client.getResource(account.href+"/phones", PhoneList)
        assertEquals(phoneList.size,0)

        //create a phone:
        def phoneNumber = "+18883915282"
        def phone = client.instantiate(Phone)
        phone.setNumber(phoneNumber).setAccount(account)
        account.createPhone(phone);

        def phoneNumber2 = "+18883528249"
        phone = client.instantiate(Phone)
        phone.setNumber(phoneNumber2).setAccount(account)
        account.createPhone(phone);

        phoneList = client.getResource(account.href+"/phones", PhoneList)
        assertEquals(phoneList.size,2)
    }

    @Test
    void testCreatePhoneErrors() {
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

        // Not a number
        def phone = client.instantiate(Phone)
                .setNumber("notANumber")
        try{
            account.createPhone(phone);
        }
        catch(ResourceException re){
            assertEquals(re.status, 400)
            assertEquals(re.getCode(), 13101)
        }

        // Extra numbers
        phone.setNumber("+188839152822222222")
        try{
            account.createPhone(phone);
        }
        catch(ResourceException re){
            assertEquals(re.status, 400)
            assertEquals(re.getCode(), 13101)
        }

        // No number
        phone = client.instantiate(Phone)
        try{
            account.createPhone(phone);
        }
        catch(ResourceException re){
            assertEquals(re.status, 400)
            assertEquals(re.getCode(), 2000)
        }

        // Null number
        phone.setNumber(null)
        try{
            account.createPhone(phone);
        }
        catch(ResourceException re){
            assertEquals(re.status, 400)
            assertEquals(re.getCode(), 2000)
        }

        //try to create duplicate phone numbers for the account
        def phoneNumber = "+18883915282"
        phone = client.instantiate(Phone)
        phone.setNumber(phoneNumber)
        account.createPhone(phone);
        phone = client.instantiate(Phone)
        phone.setNumber(phoneNumber)
        try{
            account.createPhone(phone);
        }
        catch(ResourceException re){
            assertEquals(re.status, 409)
            assertEquals(re.getCode(), 13102)
        }
    }

    @Test
    void testDeleteAccountDeletesPhones() {
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

        def phoneNumber = "2016549571"
        def phone = client.instantiate(Phone)
                .setNumber(phoneNumber)
        phone = account.createPhone(phone);

        def phoneNumber2 = "2026549572"
        def phone2 = client.instantiate(Phone)
                .setNumber(phoneNumber2)

        account.createPhone(phone2);

        account.delete()

        try{
            client.getResource(phone.href, Phone)
        }
        catch(ResourceException re){
            assertEquals(re.status, 404)
            assertEquals(re.getCode(), 404)
        }
    }

    /*
    @Test
    void testDeletePhone() {
        DirectoryResource directoryResource = createRandomDirectoryResource()

        JsonPath account = postRestCall(directoryResource.accountsHref,
                [givenName: "Joe",
                 surname  : "Stormtrooper",
                 email    : makeUniqueEmail(),
                 password : 'Death$tar1'], 201)

        String accountPhonesEndpoint = account.href + "/phones"

        String phoneNumber = "+18883915282"
        String phoneNumber2 = "+18883915283"
        def phoneResult = postRestCall(accountPhonesEndpoint, [number: phoneNumber], 201)
        //create second phone to make sure deletion of one phone doesn't delete all phones in collection (indexes)
        postRestCall(accountPhonesEndpoint, [number: phoneNumber2], 201)

        deleteResource(phoneResult.href)

        makeErrorGetRestCall(phoneResult.href as String, 404, 404)

        def response = getRestCall(account.href + "?expand=phones")

        assertThat(response.phones.size, equalTo(1))
    }
     */

    @Test
    void testDeletePhone() {
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

        try{
            client.getResource(phone.href, Phone)
        }
        catch(ResourceException re){
            assertEquals(re.status, 404)
            assertEquals(re.getCode(), 404)
        }

        PhoneList phoneList = client.getResource(account.href+"/phones", PhoneList)

        assertEquals(phoneList.size, 1)
    }

    @Test
    void testSearchOnPhoneNameDescription() {
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
