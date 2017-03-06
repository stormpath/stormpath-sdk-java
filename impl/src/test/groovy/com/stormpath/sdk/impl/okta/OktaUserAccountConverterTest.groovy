package com.stormpath.sdk.impl.okta

import com.stormpath.sdk.account.AccountList
import com.stormpath.sdk.directory.CustomData
import com.stormpath.sdk.impl.account.DefaultAccount
import com.stormpath.sdk.impl.account.DefaultAccountList
import com.stormpath.sdk.impl.directory.DefaultCustomData
import com.stormpath.sdk.impl.ds.InternalDataStore
import com.stormpath.sdk.resource.Resource
import org.easymock.Capture
import org.easymock.EasyMock
import org.easymock.IAnswer
import org.testng.annotations.Test

import static org.easymock.EasyMock.anyObject
import static org.easymock.EasyMock.anyString
import static org.easymock.EasyMock.capture
import static org.easymock.EasyMock.createStrictMock
import static org.easymock.EasyMock.expect
import static org.easymock.EasyMock.isNull
import static org.easymock.EasyMock.replay
import static org.easymock.EasyMock.verify
import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.Matchers.allOf
import static org.hamcrest.Matchers.hasEntry
import static org.hamcrest.Matchers.is

/**
 * Tests for {@link OktaUserAccountConverter}.
 */
class OktaUserAccountConverterTest {

    @Test
    void baseToAccountTest() {

        // define User map
        Map<String, Object> userMap = [
            id               : "foobar",
            status           : "ACTIVE",
            created          : "2017-03-06T18:28:15.000Z",
            activated        : null,
            statusChanged    : "2017-03-06T18:32:12.000Z",
            lastLogin        : "2017-03-06T18:32:12.000Z",
            lastUpdated      : "2017-03-06T22:16:30.000Z",
            passwordChanged  : "2017-03-06T18:32:12.000Z",
            credentials      : [],
            password         : [],
            recovery_question: [
                    question: "What is the food you least liked as a child?"
            ],
            profile: [
                login: "jcoder@example.com",
                firstName: "Joe",
                lastName: "Coder",
                mobilePhone: null,
                email: "jcoder@example.com",
                secondEmail: null,
                myCustomAttribute: "foobar"
            ],
            provider         : [
                "type": "OKTA",
                "name": "OKTA"
            ]
        ]

        // convert Account map
//        def accountMap = new OktaUserAccountConverter().toAccount(userMap)

        // create Account object
        def internalDataStore = createStrictMock(InternalDataStore)
        def customDataPropsCapture = new Capture<Map>()
        expect(internalDataStore.instantiate(anyObject(CustomData), capture(customDataPropsCapture))).andAnswer(new IAnswer<CustomData>() {
            @Override
            CustomData answer() throws Throwable {
                return new DefaultCustomData(internalDataStore, customDataPropsCapture.value)
            }
        })
        replay internalDataStore

        def account = new DefaultAccount(internalDataStore, userMap)

        // validate
//        verify internalDataStore
        assertThat account.givenName, is("Joe")
        assertThat account.customData, allOf(hasEntry("myCustomAttribute", "foobar"))


    }

}
