package com.stormpath.sdk.impl.okta

import com.stormpath.sdk.account.AccountList
import com.stormpath.sdk.directory.CustomData
import com.stormpath.sdk.impl.account.DefaultAccount
import com.stormpath.sdk.impl.account.DefaultAccountList
import com.stormpath.sdk.impl.directory.DefaultCustomData
import com.stormpath.sdk.impl.ds.InternalDataStore
import com.stormpath.sdk.impl.group.DefaultGroup
import com.stormpath.sdk.resource.Resource
import org.easymock.Capture
import org.easymock.EasyMock
import org.easymock.IAnswer
import org.joda.time.Instant
import org.testng.annotations.Test

import static org.easymock.EasyMock.*
import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.Matchers.*

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
            created          : "2017-03-06T22:16:30.000Z",
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

        // create Account object
        def internalDataStore = createStrictMock(InternalDataStore)
        def customDataPropsCapture = new Capture<Map>()
        expect(internalDataStore.getBaseUrl()).andReturn("https://api.example.com")
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
        assertThat account.createdAt, is(Instant.parse("2017-03-06T22:16:30.000Z").toDate())
        assertThat account.modifiedAt, is(Instant.parse("2017-03-06T22:16:30.000Z").toDate())

    }

    @Test
    void toStormpathGroupTest() {
        // define Group map
        Map<String, Object> groupMap = [
                id                    : "foobarGroup",
                created               : "2017-03-06T18:28:15.000Z",
                lastUpdated           : "2017-03-06T22:16:30.000Z",
                lastMembershipUpdated : "2017-03-06T22:16:30.000Z",
                created               : "2017-03-06T22:16:30.000Z",
                objectClass           : [
                    "okta:user_group"
                ],
                profile: [
                    name              : "Everyone",
                    description       : "All users in your organization"
                ]
        ]

        // create Account object
        def internalDataStore = createMock(InternalDataStore)
        expect(internalDataStore.getBaseUrl()).andReturn("https://api.example.com")
        expect(internalDataStore.instantiate(eq(CustomData), anyObject(Map))).andAnswer(new IAnswer<CustomData>() {
            @Override
            CustomData answer() throws Throwable {
                return new DefaultCustomData(internalDataStore, null)
            }
        }).anyTimes()

        replay internalDataStore

        def group = new DefaultGroup(internalDataStore, groupMap)
        
        //        verify internalDataStore
        assertThat group.getName(), is("Everyone")
        assertThat group.getDescription(), is("All users in your organization")
        assertThat group.createdAt, is(Instant.parse("2017-03-06T22:16:30.000Z").toDate())
        assertThat group.modifiedAt, is(Instant.parse("2017-03-06T22:16:30.000Z").toDate())
        assertThat group.getHref(), is("https://api.example.com/api/v1/groups/foobarGroup")

    }

}
