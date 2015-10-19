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
package com.stormpath.spring.boot.autoconfigure;

import com.stormpath.sdk.servlet.csrf.CsrfTokenManager;
//import com.stormpath.spring.config.AbstractStormpathWebSecurityConfigurer;
import com.stormpath.spring.config.AbstractStormpathWebSecurityConfigurer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.authentication.DefaultAuthenticationEventPublisher;
import org.springframework.security.config.annotation.SecurityConfigurer;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.HttpSecurityBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configurers.DefaultLoginPageConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.context.request.async.WebAsyncManagerIntegrationFilter;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.accept.ContentNegotiationStrategy;

import java.util.Collections;

@Component
//@AutoConfigureBefore(WebMvcAutoConfiguration.class)
@Configuration
//@EnableWebSecurity
public class StormpathWebSecurityConfigurer extends AbstractStormpathWebSecurityConfigurer {

    @Autowired
    protected AuthenticationSuccessHandler stormpathAuthenticationSuccessHandler;

    @Autowired
    public LogoutHandler stormpathLogoutHandler;

    @Autowired
    public AuthenticationManager getAuthenticationManager;

    @Autowired
    public CsrfTokenRepository stormpathCsrfTokenRepository;

    @Autowired
    public CsrfTokenManager stormpathCsrfTokenManager;

//    @Bean
////    @Conditional(StormpathSecurityEnabled.class)
////    @ConditionalOnMissingBean
//    public AuthenticationManager getAuthenticationManager() throws Exception {
//        //return super.authenticationManagerBean();
//        return null;
//    }

    /**
     * The pre-defined Stormpath access control settings are defined here.
     * <p>This method has been marked as final in order to avoid users to override this method by mistake and thus removing all this required configuration.
     * Instead, users can extend this class and configure their applications by overriding the {@link #doConfigure(HttpSecurity)} method. This way the configuration
     * can be explicitly modified but not overwritten by mistake.</p>
     *
     * @param http
     *            the {@link HttpSecurity} to be modified
     * @throws Exception
     *             if an error occurs
     * @see #doConfigure(HttpSecurity)
     */
    //@Override
    protected final void configure(HttpSecurity http) throws Exception {
        configure(http, stormpathAuthenticationSuccessHandler, stormpathLogoutHandler);
        doConfigure(http);
    }

    /**
     * Convenience <a href="https://en.wikipedia.org/wiki/Template_method_pattern">Hook Method</a> that will be invoked by {@link #configure(HttpSecurity)} after
     * auto-configuring all the required properties. You can override this method to define app-specific security settings like:
     *
     * <pre>
     * http
     *   .authorizeRequests()
     *   .antMatchers("/account").fullyAuthenticated()
     *   .antMatchers("/admin").hasRole("ADMIN");
     * </pre>
     *
     * @param http
     *            the {@link HttpSecurity} to modify
     * @throws Exception
     *             if an error occurs
     */
    protected void doConfigure(HttpSecurity http) throws Exception {
    }

    /**
     * This method has been marked as final in order to avoid users to skip the <code>stormpathAuthenticationProvider</code> thus removing its required configuration.
     * Instead, users can configure the <code>AuthenticationManagerBuilder</code> by overriding the {@link #doConfigure(AuthenticationManagerBuilder)} method.
     * This way the configuration can be explicitly modified but not overwritten by mistake.
     *
     * @param auth
     *            the {@link AuthenticationManagerBuilder} to use
     * @throws Exception
     *             if an error occurs
     */
    //@Override
    protected final void configure(AuthenticationManagerBuilder auth) throws Exception {
        configure(auth, super.stormpathAuthenticationProvider);
        doConfigure(auth);
    }

    /**
     * Convenience <a href="https://en.wikipedia.org/wiki/Template_method_pattern">Hook Method</a> that will be invoked by {@link #configure(AuthenticationManagerBuilder)} after
     * auto-configuring all the required properties. You can override this method to define app-specific ones.
     *
     * @param auth
     *            the {@link AuthenticationManagerBuilder} to modify
     * @throws Exception
     *             if an error occurs
     */
    protected void doConfigure(AuthenticationManagerBuilder auth) throws Exception {
    }

//    @Override
//    public void init(WebSecurity builder) throws Exception {
//
//        //Collection<SecurityConfigurer<O, securityBuilder<O>>
//
//        Collection<Collection<SecurityConfigurer<HttpSecurity, SecurityBuilder<HttpSecurity>> configurers = builder.getConfigurers(HttpSecurity.class);
//
////        for (SecurityConfigurer configurer : configurers) {
////            configurer.configure((HttpSecurity) this);
////        }
////
////        for(SecurityConfigurer securityConfigurer : builder.getConfigurers(HttpSecurity.class)) {
////            System.out.print(builder);
////        }
//    }

    //WIP HERE
    //@Override
    public void init(WebSecurity builder) throws Exception {
//        SecurityConfigurer configurer = builder.getConfigurer(this.getClass());
//        configurer.configure(builder);
        this.configure(builder);
    }

    /**
     * This method has been marked as final in order to be sure that the required Stormpath configuration is actually applied and not mistakenly overwritten by sub-classes.
     * Instead, users can configure the <code>WebSecurity</code> by overriding the {@link #doConfigure(WebSecurity)} method. This way the configuration can be explicitly modified but not overwritten by mistake.
     *
     * @param web
     *            the {@link WebSecurity} to use
     * @throws Exception
     *             if an error occurs
     */
    @Override
    public final void configure(WebSecurity web) throws Exception {
        super.configure(web);
        doConfigure(web);
    }

    /**
     * Convenience <a href="https://en.wikipedia.org/wiki/Template_method_pattern">Hook Method</a> that will be invoked by {@link #configure(WebSecurity)} after
     * auto-configuring the required Stormpath properties. You can override this method to define app-specific ones.
     *
     * @param web
     *            the {@link WebSecurity} to modify
     * @throws Exception
     *             if an error occurs
     */
    public void doConfigure(WebSecurity web) throws Exception {
    }

//    /**
//     * Creates the {@link HttpSecurity} or returns the current instance
//     *
//     * ] * @return the {@link HttpSecurity}
//     * @throws Exception
//     */
//    public final HttpSecurity getHttpSecurityConfigurer() throws Exception {
//        HttpSecurity http = new HttpSecurity(null, null, null);
////        http.setSharedObject(UserDetailsService.class, userDetailsService());
////        http.setSharedObject(ApplicationContext.class, context);
////        http.setSharedObject(ContentNegotiationStrategy.class, contentNegotiationStrategy);
////        http.setSharedObject(AuthenticationTrustResolver.class, trustResolver);
////        if (!disableDefaults) {
////            // @formatter:off
////            http
////                    .csrf().and()
////                    .addFilter(new WebAsyncManagerIntegrationFilter())
////                    .exceptionHandling().and()
////                    .headers().and()
////                    .sessionManagement().and()
////                    .securityContext().and()
////                    .requestCache().and()
////                    .anonymous().and()
////                    .servletApi().and()
////                    .apply(new DefaultLoginPageConfigurer<HttpSecurity>()).and()
////                    .logout();
////            // @formatter:on
////        }
//        //configure(http);
//        return http;
//    }


}

