package com.stormpath.spring.boot.autoconfigure

import autoconfigure.StormpathWebSecurityAutoConfigurationTestApplication
import com.stormpath.spring.config.TwoAppTenantStormpathTestConfiguration
import com.stormpath.spring.context.CompositeMessageSource
import com.stormpath.spring.context.MessageSourceDefinitionPostProcessor
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.MessageSource
import org.springframework.context.support.ResourceBundleMessageSource
import org.springframework.test.context.TestPropertySource
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests
import org.springframework.test.context.web.WebAppConfiguration
import org.testng.annotations.Test

/**
 * @since 1.1.0
 * @see <a href="https://github.com/stormpath/stormpath-sdk-java/issues/703">Issue 703</a>
 */
@SpringBootTest(classes = [StormpathWebSecurityAutoConfigurationTestApplication.class, TwoAppTenantStormpathTestConfiguration.class])
@WebAppConfiguration
@TestPropertySource(locations = "classpath:test.application.properties")
class SpecifiedMessageSourceIT extends AbstractTestNGSpringContextTests {

    @Autowired
    MessageSource messageSource;

    @Test
    void testDefaultMessageSource() {
        assert messageSource instanceof CompositeMessageSource

        assert messageSource.messageSources.length == 2

        assert messageSource.messageSources[0] instanceof ResourceBundleMessageSource
        assert messageSource.messageSources[0].basenameSet.iterator().next() == 'testMessages'

        assert messageSource.messageSources[1] instanceof ResourceBundleMessageSource
        assert messageSource.messageSources[1].basenameSet.iterator().next() == MessageSourceDefinitionPostProcessor.I18N_PROPERTIES_BASENAME
    }
}
