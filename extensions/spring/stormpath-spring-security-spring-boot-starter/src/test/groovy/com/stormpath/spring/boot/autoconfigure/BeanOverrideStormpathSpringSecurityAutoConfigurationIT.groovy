package com.stormpath.spring.boot.autoconfigure

import com.stormpath.spring.security.provider.GroupGrantedAuthorityResolver
import com.stormpath.spring.security.provider.StormpathAuthenticationProvider
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.SpringApplicationConfiguration
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests
import org.testng.annotations.Test

import static org.testng.Assert.assertTrue

@SpringApplicationConfiguration(classes = BeanOverrideSpringSecurityBootTestApplication.class)
class BeanOverrideStormpathSpringSecurityAutoConfigurationIT extends AbstractTestNGSpringContextTests {

    @Autowired
    GroupGrantedAuthorityResolver groupGrantedAuthorityResolver;

    @Autowired
    StormpathAuthenticationProvider authenticationProvider;

    @Test
    void test() {
        assertTrue groupGrantedAuthorityResolver instanceof CustomTestGroupGrantedAuthorityResolver
        assertTrue authenticationProvider.groupGrantedAuthorityResolver instanceof CustomTestGroupGrantedAuthorityResolver
    }
}
