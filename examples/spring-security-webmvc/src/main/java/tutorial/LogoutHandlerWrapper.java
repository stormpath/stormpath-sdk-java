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
import com.stormpath.sdk.authc.AuthenticationResult;
import com.stormpath.sdk.client.Client;
import com.stormpath.sdk.lang.Strings;
import com.stormpath.sdk.servlet.account.DefaultAccountResolver;
import com.stormpath.sdk.servlet.authc.impl.TransientAuthenticationResult;
import com.stormpath.sdk.servlet.mvc.DefaultViewModel;
import com.stormpath.sdk.servlet.mvc.LoginController;
import com.stormpath.sdk.servlet.mvc.LogoutController;
import com.stormpath.spring.config.StormpathWebMvcConfiguration;
import com.stormpath.spring.security.provider.StormpathUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Date: 5/20/15
 */
@Component
//public class LogoutHandlerWrapper implements LogoutSuccessHandler {
//public class LogoutHandlerWrapper extends SimpleUrlLogoutSuccessHandler {
public class LogoutHandlerWrapper extends LogoutController implements LogoutSuccessHandler {

    @Autowired
    protected StormpathWebMvcConfiguration stormpathWebMvcConfiguration;

    @Autowired
    protected Client stormpathClient;

    public LogoutHandlerWrapper() {
        System.out.println();
    }

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        do3(request, response, authentication);
    }


    public void do1(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        try {
            SecurityContextHolder.getContext().setAuthentication(null);

            stormpathWebMvcConfiguration.stormpathLogoutController().handleRequest(request, response);

            response.sendRedirect(stormpathWebMvcConfiguration.stormpathInternalConfig().getLogoutNextUrl());

        } catch (Exception e) {

            //TODO see here XXX

        }
    }

//
//    public void do2(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
//        try {
//            SecurityContextHolder.getContext().setAuthentication(null);
//
//            //stormpathWebMvcConfiguration.stormpathLogoutController().handleRequest(request, response);
//
//            this.handleRequest(request, response);
//
//            response.sendRedirect(stormpathWebMvcConfiguration.stormpathInternalConfig().getLogoutNextUrl());
//
//        } catch (Exception e) {
//
//            //TODO see here XXX
//
//        }
//    }

    private void do3(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        try {
            //SecurityContextHolder.getContext().setAuthentication(null);

            request.removeAttribute(Account.class.getName());
            //store under both names - can be convenient depending on how it is accessed:
            request.removeAttribute(DefaultAccountResolver.REQUEST_ATTR_NAME);
            request.removeAttribute("account");

//            //SecurityContextHolder.getContext().setAuthentication(null);
//            stormpathWebMvcConfiguration.stormpathLogoutController().handleRequest(request, response);

            String next = request.getParameter("next");

            if (!Strings.hasText(next)) {
                next = getNextUri();
            }

            if (!Strings.hasText(next)) {
                next = stormpathWebMvcConfiguration.stormpathInternalConfig().getLogoutNextUrl();
            }

            if (isHtmlPreferred(request)) {
                response.sendRedirect(new DefaultViewModel(next).getViewName());
            } else {
                //probably an ajax or non-browser client - return 200 ok:
                response.setStatus(HttpServletResponse.SC_OK);
            }

            //response.sendRedirect(stormpathWebMvcConfiguration.stormpathInternalConfig().getLogoutNextUrl());

        } catch (Exception e) {

            //TODO see here XXX

        }
    }

}
