/*
 * Copyright 2017 Stormpath, Inc.
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutHandler;

/**
 * @since 1.3.0
 */
public abstract class AbstractStormpathSecurityConfigurerAdapter extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {

    private static final Logger log = LoggerFactory.getLogger(AbstractStormpathSecurityConfigurerAdapter.class);

    @Autowired
    @Qualifier("stormpathLogoutHandler")
    protected LogoutHandler logoutHandler;

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

    @Value("#{ @environment['stormpath.web.csrf.token.enabled'] ?: true }")
    protected boolean csrfTokenEnabled;

    @Value("#{ @environment['stormpath.web.resendVerification.uri'] ?: '/resendVerification' }")
    protected String resendVerificationUri;

    @Value("#{ @environment['stormpath.web.saml.uri'] ?: '/saml' }")
    protected String samlUri;

}
