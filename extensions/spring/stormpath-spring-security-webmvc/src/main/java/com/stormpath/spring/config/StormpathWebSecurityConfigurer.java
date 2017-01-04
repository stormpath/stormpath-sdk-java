/*
 * Copyright 2015 Stormpath, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.stormpath.spring.config;

import com.stormpath.sdk.authc.AuthenticationResult;
import com.stormpath.sdk.client.Client;
import com.stormpath.sdk.servlet.filter.ContentNegotiationResolver;
import com.stormpath.sdk.servlet.http.MediaType;
import com.stormpath.sdk.servlet.http.Saver;
import com.stormpath.sdk.servlet.http.UnresolvedMediaTypeException;
import com.stormpath.sdk.servlet.mvc.WebHandler;
import com.stormpath.spring.filter.ContentNegotiationAuthenticationFilter;
import com.stormpath.spring.filter.LoginHandlerFilter;
import com.stormpath.spring.filter.SpringSecurityResolvedAccountFilter;
import com.stormpath.spring.oauth.OAuthAuthenticationSpringSecurityProcessingFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.util.matcher.RequestMatcher;

import javax.servlet.http.HttpServletRequest;

/**
 * @since 1.0.RC5
 */
@Configuration
@EnableStormpathWebSecurity
public class StormpathWebSecurityConfigurer extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {

    private static final Logger log = LoggerFactory.getLogger(StormpathWebSecurityConfigurer.class);

    @Autowired
    OAuthAuthenticationSpringSecurityProcessingFilter oauthAuthenticationSpringSecurityProcessingFilter;

    @Autowired
    SpringSecurityResolvedAccountFilter springSecurityResolvedAccountFilter;

    @Autowired
    AuthenticationEntryPoint stormpathAuthenticationEntryPoint;

    @Autowired
    protected Client client;

    @Autowired
    @Qualifier("stormpathLogoutHandler")
    protected LogoutHandler logoutHandler;

    @Autowired
    @Qualifier("stormpathAuthenticationSuccessHandler")
    protected AuthenticationSuccessHandler successHandler;

    @Autowired
    @Qualifier("stormpathCsrfTokenRepository")
    private CsrfTokenRepository csrfTokenRepository;

    @Autowired
    @Qualifier("stormpathAuthenticationFailureHandler")
    protected AuthenticationFailureHandler failureHandler;

    @Autowired
    @Qualifier("stormpathAuthenticationManager")
    AuthenticationManager stormpathAuthenticationManager; // provided by stormpath-spring-security

    @Autowired(required = false) //required = false when stormpath.web.enabled = false
    @Qualifier("stormpathAuthenticationResultSaver")
    protected Saver<AuthenticationResult> authenticationResultSaver; //provided by stormpath-spring-webmvc

    @Value("#{ @environment['stormpath.web.produces'] ?: 'application/json, text/html' }")
    protected String produces;

    @Value("#{ @environment['stormpath.spring.security.enabled'] ?: true }")
    protected boolean stormpathSecuritybEnabled;

    @Value("#{ @environment['stormpath.web.enabled'] ?: true }")
    protected boolean stormpathWebEnabled;

    @Value("#{ @environment['stormpath.web.login.enabled'] ?: true }")
    protected boolean loginEnabled;

    @Value("#{ @environment['stormpath.web.login.uri'] ?: '/login' }")
    protected String loginUri;

    @Value("#{ @environment['stormpath.web.logout.enabled'] ?: true }")
    protected boolean logoutEnabled;

    @Value("#{ @environment['stormpath.web.logout.uri'] ?: '/logout' }")
    protected String logoutUri;

    @Value("#{ @environment['stormpath.web.logout.nextUri'] ?: '/' }")
    protected String logoutNextUri;

    @Value("#{ @environment['stormpath.web.forgotPassword.enabled'] ?: true }")
    protected boolean forgotEnabled;

    @Value("#{ @environment['stormpath.web.forgotPassword.uri'] ?: '/forgot' }")
    protected String forgotUri;

    @Value("#{ @environment['stormpath.web.changePassword.enabled'] ?: true }")
    protected boolean changeEnabled;

    @Value("#{ @environment['stormpath.web.changePassword.uri'] ?: '/change' }")
    protected String changeUri;

    @Value("#{ @environment['stormpath.web.register.enabled'] ?: true }")
    protected boolean registerEnabled;

    @Value("#{ @environment['stormpath.web.register.uri'] ?: '/register' }")
    protected String registerUri;

    @Value("#{ @environment['stormpath.web.verifyEmail.enabled'] ?: true }")
    protected boolean verifyEnabled;

    @Value("#{ @environment['stormpath.web.verifyEmail.uri'] ?: '/verify' }")
    protected String verifyUri;

    @Value("#{ @environment['stormpath.web.oauth2.enabled'] ?: true }")
    protected boolean accessTokenEnabled;

    @Value("#{ @environment['stormpath.web.oauth2.uri'] ?: '/oauth/token' }")
    protected String accessTokenUri;

    @Value("#{ @environment['stormpath.web.oauth2.revokeOnLogout'] ?: true }")
    protected boolean accessTokenRevokeOnLogout;

    @Value("#{ @environment['stormpath.web.csrf.token.enabled'] ?: true }")
    protected boolean csrfTokenEnabled;

    @Value("#{ @environment['stormpath.web.resendVerification.uri'] ?: '/resendVerification' }")
    protected String resendVerificationUri;

    @Value("#{ @environment['stormpath.spring.security.fullyAuthenticated.enabled'] ?: true }")
    protected boolean fullyAuthenticatedEnabled;

    @Value("#{ @environment['stormpath.web.idSite.enabled'] ?: false }")
    protected boolean idSiteEnabled;

    @Value("#{ @environment['stormpath.web.callback.enabled'] ?: true }")
    protected boolean callbackEnabled;

    @Value("#{ @environment['stormpath.web.idSite.resultUri'] ?: '/idSiteResult' }")
    protected String idSiteResultUri;

    @Value("#{ @environment['stormpath.web.callback.uri'] ?: '/stormpathCallback' }")
    protected String samlResultUri;

    @Value("#{ @environment['stormpath.web.social.google.uri'] ?: '/callbacks/google' }")
    protected String googleCallbackUri;

    @Value("#{ @environment['stormpath.web.social.facebook.uri'] ?: '/callbacks/facebook' }")
    protected String facebookCallbackUri;

    @Value("#{ @environment['stormpath.web.social.linkedin.uri'] ?: '/callbacks/linkedin' }")
    protected String linkedinCallbackUri;

    @Value("#{ @environment['stormpath.web.social.github.uri'] ?: '/callbacks/github' }")
    protected String githubCallbackUri;

    @Value("#{ @environment['stormpath.web.me.enabled'] ?: true }")
    protected boolean meEnabled;

    @Value("#{ @environment['stormpath.web.me.uri'] ?: '/me' }")
    protected String meUri;

    @Autowired(required = false)
    @Qualifier("loginPreHandler")
    protected WebHandler loginPreHandler;

    @Autowired(required = false)
    @Qualifier("loginPostHandler")
    protected WebHandler loginPostHandler;

    /**
     * Extend WebSecurityConfigurerAdapter and configure the {@code HttpSecurity} object using
     * the {@link com.stormpath.spring.config.StormpathWebSecurityConfigurer#stormpath stormpath()} utility method.
     * For example:
     *
     * <pre>
     * import static com.stormpath.spring.config.StormpathWebSecurityConfigurer.stormpath;
     *
     * @Configuration
     * public class SecurityConfig extends WebSecurityConfigurerAdapter {
     *
     *     @Override
     *     public void configure(HttpSecurity http) throws Exception {
     *        http
     *            .apply(stormpath())
     *            //other http config here
     *     }
     * }
     * </pre>
     *
     * @return the StormpathWebSecurityConfigurer object
     */
    public static StormpathWebSecurityConfigurer stormpath() {
        return new StormpathWebSecurityConfigurer();
    }

    /**
     * The pre-defined Stormpath access control settings are defined here.
     *
     * @param http
     *            the {@link HttpSecurity} to be modified
     * @throws Exception
     *             if an error occurs
     */
    @Override
    public void init(HttpSecurity http) throws Exception {

        // autowire this bean
        ApplicationContext context = http.getSharedObject(ApplicationContext.class);
        context.getAutowireCapableBeanFactory().autowireBean(this);
        http.servletApi().rolePrefix(""); //Fix for https://github.com/stormpath/stormpath-sdk-java/issues/325

        // We need to add the springSecurityResolvedAccountFilter whenever we have our login enabled in order to
        // fix https://github.com/stormpath/stormpath-sdk-java/issues/450
        http.addFilterBefore(springSecurityResolvedAccountFilter, AnonymousAuthenticationFilter.class);

        if (loginEnabled) {
            // This filter replaces http.formLogin() so that we can properly handle content negotiation
            // If it's an HTML request, it delegates to the default UsernamePasswordAuthenticationFilter behavior
            // refer to: https://github.com/stormpath/stormpath-sdk-java/issues/682
            http.addFilterBefore(setupContentNegotiationAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

            http.addFilterBefore(preLoginHandlerFilter(), ContentNegotiationAuthenticationFilter.class);
        }

        if (idSiteEnabled && loginEnabled) {
            String permittedResultPath = (idSiteEnabled) ? idSiteResultUri : samlResultUri;

            http
                .authorizeRequests()
                .antMatchers(loginUri).permitAll()
                .antMatchers(permittedResultPath).permitAll()
                .and().exceptionHandling().authenticationEntryPoint(stormpathAuthenticationEntryPoint); //Fix for https://github.com/stormpath/stormpath-sdk-java/issues/714
        } else if (stormpathWebEnabled) {
            if (loginEnabled) {
                // make sure that /login and /login?status=... is permitted
                String loginUriMatch = (loginUri.endsWith("*")) ? loginUri : loginUri + "*";

                http
                    .authorizeRequests()
                    .antMatchers(loginUriMatch).permitAll()
                    .antMatchers(googleCallbackUri).permitAll()
                    .antMatchers(githubCallbackUri).permitAll()
                    .antMatchers(facebookCallbackUri).permitAll()
                    .antMatchers(linkedinCallbackUri).permitAll()
                    .and().exceptionHandling().authenticationEntryPoint(stormpathAuthenticationEntryPoint); //Fix for https://github.com/stormpath/stormpath-sdk-java/issues/714
            }

            if (meEnabled) {
                http
                    .authorizeRequests().antMatchers(meUri).fullyAuthenticated();
            }

            http.authorizeRequests()
                .antMatchers("/assets/css/stormpath.css").permitAll()
                .antMatchers("/assets/css/custom.stormpath.css").permitAll()
                .antMatchers("/assets/js/stormpath.js").permitAll()
                // fix for https://github.com/stormpath/stormpath-sdk-java/issues/822
                .antMatchers("/WEB-INF/jsp/stormpath/**").permitAll();

        }

        if (idSiteEnabled || callbackEnabled || stormpathWebEnabled) {
            if (logoutEnabled) {
                LogoutConfigurer<HttpSecurity> httpSecurityLogoutConfigurer = http
                        .logout()
                        .invalidateHttpSession(true)
                        .logoutUrl(logoutUri);

                if (!idSiteEnabled) {
                    httpSecurityLogoutConfigurer.logoutSuccessUrl(logoutNextUri);
                }

                httpSecurityLogoutConfigurer
                    .addLogoutHandler(logoutHandler)
                    .and().authorizeRequests()
                    .antMatchers(logoutUri).permitAll();
            }

            if (forgotEnabled) {
                http.authorizeRequests().antMatchers(forgotUri).permitAll();
            }
            if (changeEnabled) {
                http.authorizeRequests().antMatchers(changeUri).permitAll();
            }
            if (registerEnabled) {
                http.authorizeRequests().antMatchers(registerUri).permitAll();
            }
            if (verifyEnabled) {
                http.authorizeRequests().antMatchers(verifyUri).permitAll();
            }
            if (accessTokenEnabled) {
                if (!callbackEnabled && !idSiteEnabled && !loginEnabled) {
                    oauthAuthenticationSpringSecurityProcessingFilter.setStateless(true);
                }
                http.authorizeRequests().antMatchers(accessTokenUri).permitAll();
                http.addFilterBefore(oauthAuthenticationSpringSecurityProcessingFilter, AnonymousAuthenticationFilter.class);
                http.authorizeRequests().antMatchers(accessTokenUri).permitAll();
            }

            if (fullyAuthenticatedEnabled) {
                http.authorizeRequests().anyRequest().fullyAuthenticated();
            }

            if (!csrfTokenEnabled) {
                http.csrf().disable();
            } else {
                http.csrf().csrfTokenRepository(csrfTokenRepository);
                if (accessTokenEnabled) {
                    http.csrf().ignoringAntMatchers(accessTokenUri);
                }
                if (logoutEnabled) {
                    http.csrf().ignoringAntMatchers(logoutUri);
                }

                // @since 1.0.0
                // Refer to: https://github.com/stormpath/stormpath-sdk-java/pull/701
                http.csrf().requireCsrfProtectionMatcher(new RequestMatcher() {

                    @Override
                    public boolean matches(HttpServletRequest request) {
                        if ("GET".equals(request.getMethod())) {
                            return false;
                        }
                        try {
                            MediaType mediaType = ContentNegotiationResolver.INSTANCE.getContentType(
                                request, null, MediaType.parseMediaTypes(produces)
                            );
                            // if it's a JSON request, disable csrf
                            return !MediaType.APPLICATION_JSON.equals(mediaType);
                        } catch (UnresolvedMediaTypeException e) {
                            log.error("Couldn't resolve media type: {}", e.getMessage(), e);
                            // default to requiring CSRF
                            return true;
                        }
                    }
                });
            }
        }
    }

    // This sets up the Content Negotiation aware filter and replaces the calls to http.formLogin()
    // refer to: https://github.com/stormpath/stormpath-sdk-java/issues/682
    private ContentNegotiationAuthenticationFilter setupContentNegotiationAuthenticationFilter() {
        ContentNegotiationAuthenticationFilter filter = new ContentNegotiationAuthenticationFilter();

        filter.setSupportedMediaTypes(MediaType.parseMediaTypes(produces));
        filter.setAuthenticationManager(stormpathAuthenticationManager);
        filter.setUsernameParameter("login");
        filter.setPasswordParameter("password");
        filter.setAuthenticationSuccessHandler(successHandler);
        filter.setAuthenticationFailureHandler(failureHandler);

        return filter;
    }

    // Creates the pre login handler filter with the user define handler
    private LoginHandlerFilter preLoginHandlerFilter() {
        return new LoginHandlerFilter(loginPreHandler, loginUri);
    }

    private LoginHandlerFilter postLoginHandlerFilter() {
        return new LoginHandlerFilter(loginPostHandler, loginUri);
    }

}
