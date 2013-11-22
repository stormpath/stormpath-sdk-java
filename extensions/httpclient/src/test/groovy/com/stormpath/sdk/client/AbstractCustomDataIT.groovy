/*
 * Copyright 2013 Stormpath, Inc.
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
package com.stormpath.sdk.client

import com.stormpath.sdk.account.Account
import com.stormpath.sdk.application.Application
import com.stormpath.sdk.application.Applications
import com.stormpath.sdk.directory.CustomData
import com.stormpath.sdk.directory.Directory
import com.stormpath.sdk.group.Group
import com.stormpath.sdk.tenant.Tenant

import static com.stormpath.sdk.directory.Directories.name
import static com.stormpath.sdk.directory.Directories.where
import static org.testng.Assert.assertEquals
import static org.testng.Assert.assertNotNull

/**
 * @since 0.9
 */
class AbstractCustomDataIT extends ClientIT {

    //assumes an app that was created with an auto-created, auto-named directory:
    protected Directory retrieveAppDirectory(Application app) {
        client.getCurrentTenant().getDirectories(
                where(name().startsWithIgnoreCase(app.getName()))
        )
        .iterator().next()
    }

    protected Application createApplication() {
        //Setup an application
        Tenant tenant = client.getCurrentTenant()

        Application application = client.instantiate(Application)

        application.setName(uniquify("My CustomData app"))

        return tenant.createApplication(Applications.newCreateRequestFor(application).createDirectory().build())
    }

    Map createComplexData() {

        def m = [
                nullProperty: null,
                emptyStringProperty: "",
                whitespaceOnlyStringProperty: "     ",
                simpleStringProperty: "Hello, world!",
                japaneseCharacters: "アナリストは「Apacheの史郎は、今日日本のソフトウェアエンジニアのた",
                spanishCharacters: "El niño está usando Stormpath para su aplicación.",
                trueProperty: true,
                falseProperty: false,

                minIntegerProperty: Integer.MIN_VALUE,
                zeroIntegerProperty: 0,
                integerProperty: 1001,
                maxIntegerProperty: Integer.MAX_VALUE,

                minLongProperty: Long.MIN_VALUE,
                zeroLongProperty: 0l,
                longProperty: 3323493203l,
                maxLongProperty: Long.MAX_VALUE,

                minFloatProperty: new BigDecimal(Float.MIN_VALUE), //seen as a decimal
                zeroFloatProperty: new BigDecimal("0.00"), //seen as a decimal by JSON
                floatProperty: new BigDecimal(3.14f), //seen as a decimal by JSON
                maxFloatProperty: new BigDecimal(Float.MAX_VALUE).toBigInteger(),

                minDoubleProperty: new BigDecimal(Double.MIN_VALUE),
                zeroDoubleProperty: new BigDecimal("0.0"),
                doubleProperty: new BigDecimal(3.14159d),
                maxDoubleProperty: new BigDecimal(Double.MAX_VALUE).toBigInteger(),

                negativeBigIntegerProperty: new BigInteger("-1415899999999998826183400524314492940902709960937514158999999999988261834005243144929409027099609375"),
                positiveBigIntegerProperty: new BigInteger("1415899999999998826183400524314492940902709960937514158999999999988261834005243144929409027099609375"),

                negativeBigDecimalProperty: new BigDecimal("-3141589999999999882600524314492941834005200524314492940902709960937.999999999988261839294090270996093754005243144929409027099609375"),
                positiveBigDecimalProperty: new BigDecimal("3141589999999999882600524314492941834005200524314492940902709960937.999999999988261839294090270996093754005243144929409027099609375"),
        ]
        m.listProperty = m.values().toList()
        m.mapProperty = m.clone()

        return m;
    }

    Map createDataForUpdate() {

        def m = [
                integerProperty: (int) (Integer.MAX_VALUE * Math.random()),
                longProperty: 1234567890l,
                uniqueString: uniquify("this is a unique random string"),
                doubleProperty: new BigDecimal(1234567890.0987654321)
        ]

        m.listProperty = m.values().toList()

        return m
    }

    protected void assertValidCustomData(String expectedHref, Map submittedProperties, CustomData responseData) {
        assertValidCustomData(expectedHref, submittedProperties, responseData, true)
    }

    protected void assertValidCustomData(String expectedHref, Map submittedProperties, CustomData responseData, boolean isResponseExpanded) {
        assertEquals expectedHref, responseData.href

        if (isResponseExpanded) {
            //when saved, we add 3 properties: href, createdAt and modifiedAt.  Verify the server did this:
            assertEquals  responseData.size(), submittedProperties.size() + 3
            assertNotNull responseData.getCreatedAt()
            assertNotNull responseData.getModifiedAt()

            assertContains(responseData, submittedProperties)
        } else {
            assertEquals 1, responseData.size()
        }
    }

    private void assertContains(Map superset, Map subset) {
        //all other properties should be equal:
        for (def entry : subset.entrySet()) {
            def clientValue = entry.value;
            def serverValue = superset.get(entry.key)

            assertEquals clientValue.toString(), serverValue.toString()
        }
    }

    protected def newAccountData() {

        def account = client.instantiate(Account)

        account.givenName = Util.randomFirstName().trim()
        account.middleName = "IT Test"
        account.surname = Util.randomLastName().trim()
        account.username = account.givenName.toLowerCase() + '-' + account.surname.toLowerCase() + '-' + UUID.randomUUID()
        account.email = account.username + '@mailinator.com'
        account.password = "changeMe1!"

        return account
    }

    protected def newGroupData() {
        def group = client.instantiate(Group)
        group.name = uniquify("My Group")
        return group
    }

    protected Account updateAccount(Account account, Map initialCustomData, Map newCustomData, boolean expand) {

        account.setMiddleName(uniquify("Middle"))

        account.customData.putAll(newCustomData)

        account.save(expand)

        initialCustomData.putAll(newCustomData)

        assertValidCustomData(account.href + "/customData", initialCustomData, account.customData, expand)

        return account
    }

    protected Group updateGroup(Group group, Map initialCustomData, Map newCustomData, boolean expand) {

        group.setDescription(uniquify("this is a unique description."))

        group.customData.putAll(newCustomData)

        group.save(expand)

        initialCustomData.putAll(newCustomData)

        assertValidCustomData(group.href + "/customData", initialCustomData, group.customData, expand)

        return group
    }
}
