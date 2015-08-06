package com.stormpath.spring.boot.autoconfigure;

import com.stormpath.sdk.application.Application;
import com.stormpath.sdk.client.Client;
import com.stormpath.spring.security.provider.GroupGrantedAuthorityResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @since 1.0.RC4.6
 */

@Configuration
@EnableAutoConfiguration
public class BeanOverrideSpringSecurityBootTestApplication {

    @Autowired
    private Client client;

    @Bean
    public GroupGrantedAuthorityResolver stormpathGroupGrantedAuthorityResolver() {
        return new CustomTestGroupGrantedAuthorityResolver();
    }

    @Bean
    public Application stormpathApplication() {

        //purposefully return the Stormpath admin console app.
        //Real apps would never do this, but this is an easy way to test that bean overrides work:

        for (Application app : client.getApplications()) {
            if (app.getName().equalsIgnoreCase("Stormpath")) { //return the admin app
                return app;
            }
        }

        throw new IllegalStateException("Stormpath application is always available in Stormpath.");
    }

    public static void main(String[] args) {
        SpringApplication.run(SpringSecurityBootTestApplication.class, args);
    }
}
