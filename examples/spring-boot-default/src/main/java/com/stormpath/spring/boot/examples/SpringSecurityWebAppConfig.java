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
package com.stormpath.spring.boot.examples;

import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.api.ApiKey;
import com.stormpath.sdk.application.Application;
import com.stormpath.sdk.authc.AuthenticationRequest;
import com.stormpath.sdk.authc.AuthenticationResult;
import com.stormpath.sdk.authc.AuthenticationResultVisitor;
import com.stormpath.sdk.client.Client;
import com.stormpath.sdk.impl.application.okta.OktaSigningKeyResolver;
import com.stormpath.sdk.impl.ds.InternalDataStore;
import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.lang.Strings;
import com.stormpath.sdk.oauth.AccessTokenResult;
import com.stormpath.sdk.oauth.TokenResponse;
import com.stormpath.sdk.resource.ResourceException;
import com.stormpath.sdk.servlet.application.ApplicationResolver;
import com.stormpath.sdk.servlet.client.ClientResolver;
import com.stormpath.sdk.servlet.filter.account.DefaultJwtAccountResolver;
import com.stormpath.sdk.servlet.filter.account.JwtAccountResolver;
import com.stormpath.sdk.servlet.filter.account.JwtSigningKeyResolver;
import com.stormpath.sdk.servlet.filter.account.OktaJwtAccountResolver;
import com.stormpath.sdk.servlet.http.Saver;
import com.stormpath.sdk.servlet.oauth.impl.JwtTokenSigningKeyResolver;
import com.stormpath.spring.config.OktaLoginSuccessHandler;
import com.stormpath.spring.config.StormpathLoginSuccessHandler;
import com.stormpath.spring.security.provider.AccountGrantedAuthorityResolver;
import com.stormpath.spring.security.provider.AccountPermissionResolver;
import com.stormpath.spring.security.provider.AuthenticationTokenFactory;
import com.stormpath.spring.security.provider.GroupGrantedAuthorityResolver;
import com.stormpath.spring.security.provider.GroupPermissionResolver;
import com.stormpath.spring.security.provider.OktaAuthenticationProvider;
import com.stormpath.spring.security.provider.StormpathAuthenticationProvider;
import com.stormpath.spring.security.token.JwtProviderAuthenticationToken;
import com.stormpath.spring.security.token.ProviderAuthenticationToken;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwsHeader;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SigningKeyResolver;
import io.jsonwebtoken.SigningKeyResolverAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.NullRequestCache;

import javax.crypto.spec.SecretKeySpec;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.Charset;
import java.security.Key;

/**
 * @since 1.0.RC6
 */
@Configuration
public class SpringSecurityWebAppConfig extends WebSecurityConfigurerAdapter {

    @Value("#{ @environment['stormpath.web.login.nextUri'] ?: '/' }")
    protected String loginNextUri;

    @Value("#{ @environment['stormpath.web.produces'] ?: 'application/json, text/html' }")
    protected String produces;

    @Autowired
    @Qualifier("stormpathAuthenticationResultSaver")
    protected Saver<AuthenticationResult> authenticationResultSaver; //provided by stormpath-spring-webmvc

    @Autowired
    protected Client client;

    /**
     * {@inheritDoc}
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // Access to all paths is restricted by default.
        // We want to restrict access to one path and leave all other paths open.
        // Starting with Spring Security 4.2 we do not need to explicitly apply the Stormpath configuration in Spring Boot
        // any more (note that it is still required in regular Spring)
        http
            .authorizeRequests()
            .antMatchers("/restricted").fullyAuthenticated()
            .antMatchers("/**").permitAll();
    }

    @Bean(name = "stormpathAuthenticationProvider" )
    public AuthenticationProvider stormpathAuthenticationProvider(final Application application,
                                                                  GroupGrantedAuthorityResolver stormpathGroupGrantedAuthorityResolver,
                                                                  GroupPermissionResolver stormpathGroupPermissionResolver,
                                                                  AccountGrantedAuthorityResolver stormpathAccountGrantedAuthorityResolver,
                                                                  AccountPermissionResolver stormpathAccountPermissionResolver,
                                                                  AuthenticationTokenFactory stormpathAuthenticationTokenFactory) {

        StormpathAuthenticationProvider provider = new OktaAuthenticationProvider(application);
        provider.setGroupGrantedAuthorityResolver(stormpathGroupGrantedAuthorityResolver);
        provider.setGroupPermissionResolver(stormpathGroupPermissionResolver);
        provider.setAccountGrantedAuthorityResolver(stormpathAccountGrantedAuthorityResolver);
        provider.setAccountPermissionResolver(stormpathAccountPermissionResolver);
        provider.setAuthenticationTokenFactory(stormpathAuthenticationTokenFactory);

        return provider;
    }

    @Bean(name = "stormpathAuthenticationSuccessHandler" )
    public AuthenticationSuccessHandler stormpathAuthenticationSuccessHandler() {

        StormpathLoginSuccessHandler loginSuccessHandler = new OktaLoginSuccessHandler(client, authenticationResultSaver, produces);

        loginSuccessHandler.setDefaultTargetUrl(loginNextUri);
        loginSuccessHandler.setTargetUrlParameter("next");
        loginSuccessHandler.setRequestCache(new NullRequestCache());
        return loginSuccessHandler;
    }

    @Bean(name = "stormpathJwtAccountResolver")
    public JwtAccountResolver stormpathJwtAccountResolver(Client client) {

        return new OktaJwtAccountResolver(new OktaSigningKeyResolver(client.getDataStore()));
    }

}