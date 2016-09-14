package com.stormpath.spring.config;

import com.stormpath.spring.context.MessageSourceDefinitionPostProcessor;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @since 1.1.0
 */
@Configuration
public class StormpathMessageSourceConfiguration {

    @Bean
    public static BeanDefinitionRegistryPostProcessor stormpathMessageSourceDefinitionPostProcessor() {
        return new MessageSourceDefinitionPostProcessor();
    }
}
