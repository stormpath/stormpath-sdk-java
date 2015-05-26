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
package tutorial;

import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.api.ApiAuthenticationResult;
import com.stormpath.sdk.api.ApiKey;
import com.stormpath.sdk.authc.AuthenticationResult;
import com.stormpath.sdk.authc.AuthenticationResultVisitor;
import com.stormpath.sdk.authc.UsernamePasswordRequest;
import com.stormpath.sdk.client.Client;
import com.stormpath.sdk.servlet.authc.impl.TransientAuthenticationResult;
import com.stormpath.sdk.servlet.filter.account.config.AuthenticationResultSaverFactory;
import com.stormpath.sdk.servlet.mvc.DefaultViewModel;
import com.stormpath.sdk.servlet.mvc.LoginController;
import com.stormpath.spring.config.StormpathWebMvcConfiguration;
import com.stormpath.spring.security.provider.StormpathAuthenticationProvider;
import com.stormpath.spring.security.provider.StormpathUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Date: 5/20/15
 */
@Component
public class HandlerWrapper extends LoginController implements AuthenticationSuccessHandler {

    @Autowired
    protected StormpathWebMvcConfiguration stormpathWebMvcConfiguration;

    @Autowired
    protected Client stormpathClient;

    public HandlerWrapper() {
        System.out.println();
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, final Authentication authentication) throws IOException, ServletException {

        do1(request, response, authentication);

    }

    private void do1(HttpServletRequest request, HttpServletResponse response, final Authentication authentication) throws IOException, ServletException {

        String accountHref = ((StormpathUserDetails) authentication.getPrincipal()).getProperties().get("href");
        Account account = stormpathClient.getResource(accountHref, Account.class);

        AuthenticationResult result = new TransientAuthenticationResult(account);
//        saveResult(request, response, result);

//        AuthenticationResult authencationResult = new AuthenticationResult() {
//            @Override
//            public Account getAccount() {
//                String accountHref = ((StormpathUserDetails) authentication.getPrincipal()).getProperties().get("href");
//                Account account = stormpathClient.getResource(accountHref, Account.class);
//                return account;
//            }
//
//            @Override
//            public void accept(AuthenticationResultVisitor visitor) {
//            }
//
//            @Override
//            public String getHref() {
//                return getAccount().getHref();
//            }
//        };

        //this.saveResult(request, response, result);
//        if (request.getSession().getAttribute(Client.class.getName()) == null) {
//            request.getSession().setAttribute(Client.class.getName(), stormpathClient);
//        }

        request.setAttribute(Client.class.getName(), stormpathClient);

        stormpathWebMvcConfiguration.stormpathAuthenticationResultSaver().set(request, response, result);

        String next = stormpathWebMvcConfiguration.stormpathInternalConfig().getLoginNextUrl();
        response.sendRedirect(new DefaultViewModel(next).getViewName());

    }

    private void do2(HttpServletRequest request, HttpServletResponse response, final Authentication authentication) throws IOException, ServletException {

        String accountHref = ((StormpathUserDetails) authentication.getPrincipal()).getProperties().get("href");
        Account account = stormpathClient.getResource(accountHref, Account.class);

        AuthenticationResult result = new TransientAuthenticationResult(account);
        request.setAttribute(Client.class.getName(), stormpathClient);
        this.setCsrfTokenManager(stormpathWebMvcConfiguration.stormpathCsrfTokenManager());

        //stormpathWebMvcConfiguration.stormpathAuthenticationResultSaver().set(request, response, result);
        try {
            this.handleRequest(request, response);
        } catch (Exception e) {
            ///
        }

//        String next = stormpathWebMvcConfiguration.stormpathInternalConfig().getLoginNextUrl();
//        return new DefaultViewModel(next).setRedirect(true);
    }

}
