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
package com.stormpath.spring.oauth;

import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.servlet.account.AccountResolver;
import com.stormpath.sdk.servlet.filter.HttpFilter;
import com.stormpath.spring.security.token.ProviderAuthenticationToken;
import org.apache.oltu.oauth2.rs.extractor.BearerHeaderTokenExtractor;
import org.apache.oltu.oauth2.rs.extractor.TokenExtractor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * A pre-authentication filter for OAuth2 protected resources. Extracts an access token token from the incoming request and
 * uses it to populate the Spring Security context with an {@link com.stormpath.spring.security.token.ProviderAuthenticationToken ProviderAuthenticationToken}.
 * <p>
 * Most of this code was taken from <a href="https://github.com/spring-projects/spring-security-oauth/blob/master/spring-security-oauth2/src/main/java/org/springframework/security/oauth2/provider/authentication/OAuth2AuthenticationProcessingFilter.java">spring-security-oauth</a>.</p>
 *
 * @since 1.0.RC8.3
 */
public class Oauth2AuthenticationSpringSecurityProcessingFilter extends HttpFilter implements InitializingBean {

    private final static Logger logger = LoggerFactory.getLogger(Oauth2AuthenticationSpringSecurityProcessingFilter.class);

    @Autowired
    private AuthenticationProvider authenticationProvider;

    private boolean stateless = false;

    private TokenExtractor tokenExtractor = new BearerHeaderTokenExtractor();

    /**
     * Flag to say that this filter guards stateless resources (default false). Set this to true if the only way the
     * resource can be accessed is with a token. If false then an incoming cookie can populate the security context and
     * allow access to a caller that isn't an OAuth2 client.
     *
     * @param stateless the flag to set (default true)
     */
    public void setStateless(boolean stateless) {
        this.stateless = stateless;
    }

    public void afterPropertiesSet() {
    }

    public void filter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException,
            ServletException {

        final boolean debug = logger.isDebugEnabled();

        String accessToken = tokenExtractor.getAccessToken(request);

        if (accessToken == null) {
            if (stateless && isAuthenticated()) {
                if (debug) {
                    logger.debug("Clearing security context.");
                }
                SecurityContextHolder.clearContext();
            }
            if (debug) {
                logger.debug("No token in request, will continue chain.");
            }
        } else {
            Account account = AccountResolver.INSTANCE.getAccount(request);
            Authentication authentication = new ProviderAuthenticationToken(account);
            authentication = authenticationProvider.authenticate(authentication);
            SecurityContextHolder.clearContext();
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        chain.doFilter(request, response);
    }

    private boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            return false;
        }
        return true;
    }

    public void destroy() {
    }

}