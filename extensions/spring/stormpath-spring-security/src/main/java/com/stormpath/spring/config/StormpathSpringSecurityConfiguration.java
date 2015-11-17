package com.stormpath.spring.config;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;

import com.stormpath.spring.security.provider.AccountGrantedAuthorityResolver;
import com.stormpath.spring.security.provider.AccountPermissionResolver;
import com.stormpath.spring.security.provider.AuthenticationTokenFactory;
import com.stormpath.spring.security.provider.GroupGrantedAuthorityResolver;
import com.stormpath.spring.security.provider.GroupPermissionResolver;

/**
 * @since 1.0.RC5
 */
@Configuration
public class StormpathSpringSecurityConfiguration extends AbstractStormpathSpringSecurityConfiguration {

    @Bean
    @Conditional(StormpathSecurityEnabled.class)
    @Override
    public GroupGrantedAuthorityResolver stormpathGroupGrantedAuthorityResolver() {
        return super.stormpathGroupGrantedAuthorityResolver();
    }

    @Bean
    @Conditional(StormpathSecurityEnabled.class)
    @Override
    public GroupPermissionResolver stormpathGroupPermissionResolver() {
        return super.stormpathGroupPermissionResolver();
    }

    @Bean
    @Conditional(StormpathSecurityEnabled.class)
    @Override
    public AccountGrantedAuthorityResolver stormpathAccountGrantedAuthorityResolver() {
        return super.stormpathAccountGrantedAuthorityResolver();
    }

    @Bean
    @Conditional(StormpathSecurityEnabled.class)
    @Override
    public AccountPermissionResolver stormpathAccountPermissionResolver() {
        return super.stormpathAccountPermissionResolver();
    }

    @Bean
    @Conditional(StormpathSecurityEnabled.class)
    @Override
    public AuthenticationTokenFactory stormpathAuthenticationTokenFactory() {
        return super.stormpathAuthenticationTokenFactory();
    }

    @Bean
    @Conditional(StormpathSecurityEnabled.class)
    @Override
    public AuthenticationProvider stormpathAuthenticationProvider() {
        return super.stormpathAuthenticationProvider();
    }

    @Bean
    @Conditional(StormpathSecurityEnabled.class)
    public AuthenticationManager stormpathAuthenticationManager() {
        return new ProviderManager(Arrays.asList(stormpathAuthenticationProvider()));
    }
}
