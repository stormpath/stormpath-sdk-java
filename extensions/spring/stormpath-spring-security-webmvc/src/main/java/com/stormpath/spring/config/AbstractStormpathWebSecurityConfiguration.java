package com.stormpath.spring.config;

import com.stormpath.sdk.authc.AuthenticationResult;
import com.stormpath.sdk.client.Client;
import com.stormpath.sdk.servlet.http.Saver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutHandler;

/**
 * @since 1.0.RC6
 */
public abstract class AbstractStormpathWebSecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Autowired
    protected Client client;

    @Autowired
    @Qualifier("stormpathAuthenticationProvider")
    protected AuthenticationProvider stormpathAuthenticationProvider; //provided by stormpath-spring-security

    @Autowired
    @Qualifier("stormpathAuthenticationResultSaver")
    protected Saver<AuthenticationResult> authenticationResultSaver; //provided by stormpath-spring-webmvc

    public AuthenticationSuccessHandler stormpathAuthenticationSuccessHandler() {
        return new StormpathLoginSuccessHandler(client, authenticationResultSaver);
    }

    public LogoutHandler stormpathLogoutHandler() {
        return new StormpathLogoutHandler(authenticationResultSaver);
    }

    /**
     * This method has been marked as final in order to avoid users to skip the <code>stormpathAuthenticationProvider</code> thus removing all this required configuration.
     * Instead, users can configure the <code>AuthenticationManagerBuilder</code> by overriding the {@link #doConfigure(org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder) config(AuthenticationManagerBuilder)} method.
     * This way the configuration can be explicitly modified but not overwritten by mistake.
     *
     * @param auth the {@link AuthenticationManagerBuilder} to use
     * @throws Exception if an error occurs
     */
    @Override
    protected final void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(stormpathAuthenticationProvider);
        doConfigure(auth);
    }

    protected void doConfigure(AuthenticationManagerBuilder auth) throws Exception {
    }
}