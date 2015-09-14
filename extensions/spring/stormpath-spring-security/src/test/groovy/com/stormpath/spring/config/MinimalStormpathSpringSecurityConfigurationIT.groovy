package com.stormpath.spring.config

import com.stormpath.spring.security.provider.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests
import org.testng.annotations.Test

import static org.testng.Assert.assertNotNull
import static org.testng.Assert.assertTrue

/**
 * @since 1.0.RC5
 */
@ContextConfiguration(classes = MinimalSpringSecurityAppConfig.class)
class MinimalStormpathSpringSecurityConfigurationIT extends AbstractTestNGSpringContextTests {

    @Autowired
    StormpathSpringSecurityConfiguration c;

    @Autowired
    GroupGrantedAuthorityResolver groupGrantedAuthorityResolver;

    @Autowired
    GroupPermissionResolver groupPermissionResolver;

    @Autowired
    AccountGrantedAuthorityResolver accountGrantedAuthorityResolver;

    @Autowired
    AccountPermissionResolver accountPermissionResolver;

    @Autowired
    AuthenticationTokenFactory authenticationTokenFactory;

    @Autowired
    AuthenticationProvider authenticationProvider;

    @Test
    void test() {
        assertNotNull c
        assertNotNull groupGrantedAuthorityResolver
        assertNotNull groupPermissionResolver
        assertNotNull accountGrantedAuthorityResolver
        assertNotNull accountPermissionResolver
        assertNotNull authenticationTokenFactory
        assertNotNull authenticationProvider
        assertTrue authenticationProvider instanceof StormpathAuthenticationProvider
    }
}
