package com.stormpath.spring.config;

import com.stormpath.sdk.authc.AuthenticationResult;
import com.stormpath.sdk.client.Client;
import com.stormpath.sdk.servlet.http.Saver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutHandler;

/**
 * @since 1.0.RC4.6
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

    @Value("#{ @environment['stormpath.web.login.enabled'] ?: true }")
    protected boolean loginEnabled;

    @Value("#{ @environment['stormpath.web.login.uri'] ?: '/login' }")
    protected String loginUri;

    @Value("#{ @environment['stormpath.web.login.nextUri'] ?: '/' }")
    protected String loginNextUri;

    @Value("#{ @environment['stormpath.web.logout.enabled'] ?: true }")
    protected boolean logoutEnabled;

    @Value("#{ @environment['stormpath.web.logout.uri'] ?: '/logout' }")
    protected String logoutUri;

    @Value("#{ @environment['stormpath.web.logout.nextUri'] ?: '/login?status=logout' }")
    protected String logoutNextUri;

    //Standard Spring Security config property - we just read it here as well:
    @Value("#{ @environment['stormpath.web.csrfProtection.enabled'] ?: true }")
    protected boolean csrfProtectionEnabled;

    public AuthenticationSuccessHandler stormpathAuthenticationSuccessHandler() {
        return new StormpathLoginSuccessHandler(client, authenticationResultSaver);
    }

    public LogoutHandler stormpathLogoutHandler() {
        return new StormpathLogoutHandler(authenticationResultSaver);
    }

    /**
     * The pre-defined Stormpath access control settings are defined here.
     * <p>This method has been marked as final in order to avoid users to override this method by mistake and thus removing all this required configuration.
     * Instead, users can extend this class and configure their applications by overriding the {@link #doConfigure(org.springframework.security.config.annotation.web.builders.HttpSecurity)
     * config(HttpSecurity)} method. This way the configuration can be explicitly modified but not overwritten by mistake.</p>
     *
     * @param http the {@link HttpSecurity} to be modified
     * @throws Exception if an error occurs
     * @see #doConfigure(org.springframework.security.config.annotation.web.builders.HttpSecurity)
     */
    protected final void configure(HttpSecurity http, AuthenticationSuccessHandler successHandler, LogoutHandler logoutHandler)
            throws Exception {

        if (loginEnabled) {
            http
                    .formLogin()
                    .loginPage(loginUri)
                    .defaultSuccessUrl(loginNextUri)
                    .successHandler(successHandler)
                    .usernameParameter("login")
                    .passwordParameter("password");
        }

        if (logoutEnabled) {
            http
                    .logout()
                    .invalidateHttpSession(true)
                    .logoutUrl(logoutUri)
                    .logoutSuccessUrl(logoutNextUri)
                    .addLogoutHandler(logoutHandler);

        }

        if (!csrfProtectionEnabled) {
            http.csrf().disable();
        }

        doConfigure(http);
    }

    /**
     * Override this method to define app-specific security settings like:
     * <p>
     * <pre>
     * http
     *   .authorizeRequests()
     *   .antMatchers("/account").fullyAuthenticated()
     *   .antMatchers("/admin").hasRole("ADMIN");
     * </pre>
     *
     * @param http the {@link HttpSecurity} to modify
     * @throws Exception if an error occurs
     */
    protected void doConfigure(HttpSecurity http) throws Exception {
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