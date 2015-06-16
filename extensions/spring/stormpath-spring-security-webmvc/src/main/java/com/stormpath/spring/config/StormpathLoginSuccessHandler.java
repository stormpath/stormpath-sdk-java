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

import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.authc.AuthenticationResult;
import com.stormpath.sdk.client.Client;
import com.stormpath.sdk.servlet.authc.impl.TransientAuthenticationResult;
import com.stormpath.sdk.servlet.filter.account.AuthenticationResultSaver;
import com.stormpath.spring.security.provider.StormpathUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @since 1.0.RC4.4
 */
@Component
public class StormpathLoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    @Autowired
    AuthenticationResultSaver stormpathAuthenticationResultSaver;

    @Autowired
    protected Client stormpathClient;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, final Authentication authentication) throws IOException, ServletException {
        saveAccount(request, response, authentication);
        super.onAuthenticationSuccess(request, response, authentication);
    }

    protected void saveAccount(HttpServletRequest request, HttpServletResponse response, final Authentication authentication) throws IOException, ServletException {
        Account account = getAccount(authentication);
        AuthenticationResult result = new TransientAuthenticationResult(account);
        stormpathAuthenticationResultSaver.set(request, response, result);
    }

    private Account getAccount(Authentication authentication) {
        String accountHref = ((StormpathUserDetails) authentication.getPrincipal()).getProperties().get("href");
        return stormpathClient.getResource(accountHref, Account.class);
    }

}
