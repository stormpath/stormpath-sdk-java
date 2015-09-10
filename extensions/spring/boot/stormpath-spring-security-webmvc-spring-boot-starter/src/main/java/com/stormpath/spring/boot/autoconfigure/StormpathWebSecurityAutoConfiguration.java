package com.stormpath.spring.boot.autoconfigure;

import com.stormpath.sdk.servlet.csrf.CsrfTokenManager;
import com.stormpath.spring.config.AbstractStormpathWebSecurityConfiguration;
import com.stormpath.spring.config.StormpathMethodSecurityConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.web.servlet.DispatcherServlet;

import javax.servlet.Filter;
import javax.servlet.Servlet;

/**
 * @since 1.0.RC4.6
 */
@SuppressWarnings("SpringFacetCodeInspection")
@Configuration
@ConditionalOnProperty(name = {"stormpath.enabled", "stormpath.web.enabled", "stormpath.spring.security.enabled"}, matchIfMissing = true)
@ConditionalOnClass({Servlet.class, Filter.class, DispatcherServlet.class})
@ConditionalOnWebApplication
@EnableWebSecurity
@Import(StormpathMethodSecurityConfiguration.class)
@AutoConfigureAfter({StormpathWebMvcAutoConfiguration.class, StormpathSpringSecurityAutoConfiguration.class})
public class StormpathWebSecurityAutoConfiguration extends AbstractStormpathWebSecurityConfiguration {

    @Bean
    @ConditionalOnMissingBean(name="stormpathAuthenticationSuccessHandler")
    public AuthenticationSuccessHandler stormpathAuthenticationSuccessHandler() {
        return super.stormpathAuthenticationSuccessHandler();
    }

    @Bean
    @ConditionalOnMissingBean(name="stormpathLogoutHandler")
    public LogoutHandler stormpathLogoutHandler() {
        return super.stormpathLogoutHandler();
    }

    @Bean
    @ConditionalOnMissingBean
    public AuthenticationManager getAuthenticationManager() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    @ConditionalOnMissingBean
    public CsrfTokenRepository csrfTokenRepository() {
        return super.csrfTokenRepository();
    }

    @Bean
    @ConditionalOnMissingBean
    public CsrfTokenManager stormpathCsrfTokenManager() {
        return super.stormpathCsrfTokenManager();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        configure(http, stormpathAuthenticationSuccessHandler(), stormpathLogoutHandler());
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        configure(auth, super.stormpathAuthenticationProvider);
    }
}
