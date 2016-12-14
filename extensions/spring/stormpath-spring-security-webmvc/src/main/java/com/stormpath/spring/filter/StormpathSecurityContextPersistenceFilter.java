/*
 * Copyright 2016 Stormpath, Inc.
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
package com.stormpath.spring.filter;

import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.client.Client;
import com.stormpath.sdk.servlet.filter.HttpFilter;
import com.stormpath.sdk.servlet.http.Resolver;
import com.stormpath.spring.security.token.ProviderAuthenticationToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * A pre-authentication filter for OAuth2 protected resources. Extracts an access token token from the incoming request and
 * uses it to populate the Spring Security context with an {@link ProviderAuthenticationToken ProviderAuthenticationToken}.
 * <p>
 * Most of this code was taken from <a href="https://github.com/spring-projects/spring-security-oauth/blob/master/spring-security-oauth2/src/main/java/org/springframework/security/oauth2/provider/authentication/OAuth2AuthenticationProcessingFilter.java">spring-security-oauth</a>.</p>
 *
 * @since 1.0.RC8.3
 */
public class StormpathSecurityContextPersistenceFilter extends HttpFilter implements InitializingBean {

    private final static Logger logger = LoggerFactory.getLogger(StormpathSecurityContextPersistenceFilter.class);

    @Autowired
    private Resolver<Account> stormpathCookieAccountResolver;

    @Autowired
    private Client client;

    @Autowired
    private AuthenticationManager authenticationManager;

    public void afterPropertiesSet() {
    }

    private boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            return false;
        }
        return true;
    }

    @Override
    public void filter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {

        if (!isAuthenticated()) {
            request.setAttribute(Client.class.getName(), client);
            Account account = stormpathCookieAccountResolver.get(request, response);
            request.removeAttribute(Client.class.getName());

            if (account != null) {
                SecurityContextHolder.clearContext();
                Authentication authentication = new ProviderAuthenticationToken(account);
                authentication = authenticationManager.authenticate(authentication);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        chain.doFilter(request, response);

    }

    public void destroy() {
    }

}