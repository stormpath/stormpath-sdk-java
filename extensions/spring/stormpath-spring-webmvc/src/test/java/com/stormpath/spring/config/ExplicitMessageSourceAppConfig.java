package com.stormpath.spring.config;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;

/**
 * @since 1.1.0
 * @see <a href="https://github.com/stormpath/stormpath-sdk-java/issues/703">Issue 703</a>
 */
@Configuration
@EnableStormpathWebMvc
public class ExplicitMessageSourceAppConfig {

    @Bean
    public MessageSource messageSource() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename("testMessages");
        messageSource.setDefaultEncoding("UTF-8");
        return messageSource;
    }
}
