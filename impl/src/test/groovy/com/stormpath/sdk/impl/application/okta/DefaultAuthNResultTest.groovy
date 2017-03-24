package com.stormpath.sdk.impl.application.okta


import static org.easymock.EasyMock.*
import static org.hamcrest.Matchers.*
import static org.hamcrest.MatcherAssert.*

import org.testng.annotations.Test

/**
 * Tests for {@link DefaultAuthNResult}.
 */
class DefaultAuthNResultTest {

    @Test
    void basicParseTest() {

        def sessionToken = "a_session_token"
        def userId = "a_user_id"
        
        Map<String, Object> map =  [
            sessionToken: sessionToken,
            "_embedded": [
                user: [
                    id: userId
                ]
            ]
        ]

        def underTest = new DefaultAuthNResult(null, map)

        assertThat underTest.sessionToken, equalTo(sessionToken)
        assertThat underTest.userId, equalTo(userId)

    }
}
