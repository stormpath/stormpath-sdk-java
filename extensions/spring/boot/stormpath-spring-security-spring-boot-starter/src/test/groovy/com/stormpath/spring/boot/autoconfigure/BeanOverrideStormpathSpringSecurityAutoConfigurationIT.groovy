package com.stormpath.spring.boot.autoconfigure

import com.stormpath.spring.config.TwoAppTenantStormpathConfiguration
import com.stormpath.spring.security.provider.GroupGrantedAuthorityResolver
import com.stormpath.spring.security.provider.StormpathAuthenticationProvider
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.SpringApplicationConfiguration
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests
import org.testng.annotations.Test

import static org.testng.Assert.assertTrue

/**
 * @since 1.0.RC5
 */
@SpringApplicationConfiguration(classes = [BeanOverrideSpringSecurityBootTestApplication.class, TwoAppTenantStormpathConfiguration.class])
class BeanOverrideStormpathSpringSecurityAutoConfigurationIT extends AbstractTestNGSpringContextTests {

    @Autowired
    GroupGrantedAuthorityResolver stormpathGroupGrantedAuthorityResolver;

    @Autowired
    StormpathAuthenticationProvider authenticationProvider;

    @Test
    void test() {
        assertTrue stormpathGroupGrantedAuthorityResolver instanceof CustomTestGroupGrantedAuthorityResolver
        assertTrue authenticationProvider.groupGrantedAuthorityResolver instanceof CustomTestGroupGrantedAuthorityResolver
    }
}
