package com.stormpath.spring.boot.autoconfigure;

import com.stormpath.spring.config.EnableStormpathSecurity;
import com.stormpath.spring.security.provider.GroupGrantedAuthorityResolver;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableAutoConfiguration
@EnableStormpathSecurity
public class BeanOverrideSpringSecurityBootTestApplication {

    @Bean
    public GroupGrantedAuthorityResolver stormpathGroupGrantedAuthorityResolver() {
        return new CustomTestGroupGrantedAuthorityResolver();
    }

    public static void main(String[] args) {
        SpringApplication.run(SpringSecurityBootTestApplication.class, args);
    }
}
