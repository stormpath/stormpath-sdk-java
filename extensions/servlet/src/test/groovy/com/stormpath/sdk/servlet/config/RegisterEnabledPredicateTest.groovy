package com.stormpath.sdk.servlet.config

import com.stormpath.sdk.application.Application
import org.testng.annotations.Test

import static org.easymock.EasyMock.*
import static org.testng.Assert.*

/**
 * @since 1.0.0
 */
class RegisterEnabledPredicateTest {

    @Test
    void testApplicationIsNotCalledWhenDisabled() {

        Application app = createMock(Application)

        replay app

        assertFalse new RegisterEnabledPredicate().test(false, app);

        verify app
    }
}
