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
package com.stormpath.spring.csrf;

import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.servlet.csrf.CsrfTokenManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRepository;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * A {@link CsrfTokenManager} specific to be used with Spring Security.
 * <p>Spring Security provides its onw protection against Cross Site Request Forgery (CSRF)
 * attacks. This {@link SpringSecurityCsrfTokenManager} can delegate the creation and processing of csrf tokens to Spring Security.</p>
 *
 * @since 1.0.RC5
 */
public class SpringSecurityCsrfTokenManager implements CsrfTokenManager {

    private static final Logger log = LoggerFactory.getLogger(SpringSecurityCsrfTokenManager.class);

    private final CsrfTokenRepository csrfTokenRepository;
    private final String tokenName;

    /**
     * Instantiates a new SpringSecurityCsrfTokenManager.
     *
     * @param csrfTokenRepository the CsrfTokenRepository that this manager will use to store and load tokens.
     * @param tokenName the name that will be used to identify the CSRF token.
     */
    public SpringSecurityCsrfTokenManager(CsrfTokenRepository csrfTokenRepository, String tokenName) {
        Assert.notNull(csrfTokenRepository, "csrfTokenRepository cannot be null.");
        Assert.hasText(tokenName, "tokenName cannot be null or empty.");
        this.csrfTokenRepository = csrfTokenRepository;
        this.tokenName = tokenName;
    }

    @Override
    public String getTokenName() {
        return this.tokenName;
    }

    @Override
    public String createCsrfToken(HttpServletRequest request, HttpServletResponse response) {
        CsrfToken csrfToken = this.csrfTokenRepository.loadToken(request);
        if (csrfToken == null) {
            csrfToken = this.csrfTokenRepository.generateToken(request);
            this.csrfTokenRepository.saveToken(csrfToken, request, response);
        }
        return csrfToken.getToken();
    }

    @Override
    public boolean isValidCsrfToken(HttpServletRequest request, HttpServletResponse response, String csrfToken) {
        CsrfToken loadedCSRFToken = this.csrfTokenRepository.loadToken(request);
        return loadedCSRFToken.getToken().equals(csrfToken);
    }
}
