package com.stormpath.spring.config

import com.stormpath.spring.context.CompositeMessageSource
import com.stormpath.spring.context.MessageSourceDefinitionPostProcessor
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.MessageSource
import org.springframework.context.support.ResourceBundleMessageSource
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests
import org.springframework.test.context.web.WebAppConfiguration
import org.testng.annotations.Test

/**
 * @since 1.1.0
 * @see <a href="https://github.com/stormpath/stormpath-sdk-java/issues/703">Issue 703</a>
 */
@ContextConfiguration(classes = [ExplicitMessageSourceAppConfig.class, TwoAppTenantStormpathTestConfiguration.class])
@WebAppConfiguration
public class ExistingMessageSourceIT extends AbstractTestNGSpringContextTests {

    @Autowired
    MessageSource messageSource;

    @Test
    void testCompositeMessageSource() {
        assert messageSource instanceof CompositeMessageSource

        assert messageSource.messageSources.length == 2

        assert messageSource.messageSources[0] instanceof ResourceBundleMessageSource
        assert messageSource.messageSources[0].basenameSet.iterator().next() == 'testMessages'

        assert messageSource.messageSources[1] instanceof ResourceBundleMessageSource
        assert messageSource.messageSources[1].basenameSet.iterator().next() == MessageSourceDefinitionPostProcessor.I18N_PROPERTIES_BASENAME
    }

}
