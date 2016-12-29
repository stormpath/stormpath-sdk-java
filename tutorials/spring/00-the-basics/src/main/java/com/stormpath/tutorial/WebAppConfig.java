package com.stormpath.tutorial;

import com.stormpath.sdk.application.Application;
import com.stormpath.sdk.client.Client;
import com.stormpath.spring.config.EnableStormpath;
import com.stormpath.spring.config.EnableStormpathWebMvc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Configuration
@EnableWebMvc
@EnableStormpath //Stormpath base beans
@EnableStormpathWebMvc //Stormpath web mvc beans plus out-of-the-box views
@ComponentScan
@PropertySource("classpath:application.properties")
public class WebAppConfig {

    @Autowired
    private Application stormpathApplication; //the REST resource in Stormpath that represents this app

    @Autowired
    private Client client; //can be used to interact with all things in your Stormpath tenant

    /**
     * This bean and the @PropertySource annotation above allow you to configure Stormpath beans with properties
     * prefixed with {@code stormpath.}, i.e. {@code stormpath.application.href}, {@code stormpath.client.apiKey.file}, etc.
     *
     * <p>Combine this with Spring's Profile support to override property values based on specific runtime environments,
     * e.g. development, production, etc.</p>
     *
     * @return the application's PropertySourcesPlaceholderConfigurer that enables property placeholder substitution.
     */
    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }
}
