package com.stormpath.spring.config;

import com.stormpath.spring.security.provider.AccountGrantedAuthorityResolver;
import com.stormpath.spring.security.provider.AccountPermissionResolver;
import com.stormpath.spring.security.provider.AuthenticationTokenFactory;
import com.stormpath.spring.security.provider.GroupGrantedAuthorityResolver;
import com.stormpath.spring.security.provider.GroupPermissionResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;

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
}
