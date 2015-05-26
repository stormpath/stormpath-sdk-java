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
import com.stormpath.sdk.servlet.account.AccountResolver;
import com.stormpath.sdk.servlet.account.DefaultAccountResolver;
import com.stormpath.sdk.servlet.authc.SuccessfulAuthenticationRequestEvent;
import com.stormpath.sdk.servlet.authc.impl.TransientAuthenticationResult;
import com.stormpath.sdk.servlet.http.impl.StormpathHttpServletRequest;
import com.stormpath.sdk.servlet.mvc.DefaultViewModel;
import com.stormpath.sdk.servlet.mvc.LoginController;
import com.stormpath.spring.config.StormpathWebMvcConfiguration;
import com.stormpath.spring.security.provider.StormpathUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Date: 5/20/15
 */
@Component
//public class LoginSuccessHandler extends LoginController implements AuthenticationSuccessHandler {
public class LoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    private RequestCache requestCache = new HttpSessionRequestCache();

    @Autowired
    protected StormpathWebMvcConfiguration stormpathWebMvcConfiguration;

    @Autowired
    protected Client stormpathClient;

//    public LoginSuccessHandler() {
//        System.out.println();
//    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, final Authentication authentication) throws IOException, ServletException {

        do1(request, response, authentication);
        //super.onAuthenticationSuccess(request, response, authentication);


        SavedRequest savedRequest = requestCache.getRequest(request, response);

        if (savedRequest == null) {
            super.onAuthenticationSuccess(request, response, authentication);

            return;
        }

        //String targetUrlParameter = getTargetUrlParameter();
        String targetUrlParameter = savedRequest.getRedirectUrl();
        if (isAlwaysUseDefaultTargetUrl() || (targetUrlParameter != null && StringUtils.hasText(request.getParameter(targetUrlParameter)))) {
            requestCache.removeRequest(request, response);
            super.onAuthenticationSuccess(request, response, authentication);

            return;
        }

        clearAuthenticationAttributes(request);

//        // Use the DefaultSavedRequest URL
//        String targetUrl = savedRequest.getRedirectUrl();
//        logger.debug("Redirecting to DefaultSavedRequest Url: " + targetUrl);
//        getRedirectStrategy().sendRedirect(request, response, targetUrl);

        response.sendRedirect(targetUrlParameter);


    }

    private void do1(HttpServletRequest request, HttpServletResponse response, final Authentication authentication) throws IOException, ServletException {

        //String accountHref = ((StormpathUserDetails) authentication.getPrincipal()).getProperties().get("href");
        //Account account = stormpathClient.getResource(accountHref, Account.class);

        //Account account = AccountResolver.INSTANCE.getRequiredAccount(request);

        //request.login(((StormpathUserDetails) authentication.getPrincipal()).getProperties().get("email"), password);

        Account account = getAccount(authentication);

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

        if (! (request.getAttribute(Client.class.getName()) instanceof Client)) {
            request.setAttribute(Client.class.getName(), stormpathClient);
        }

        //request.setAttribute(DefaultAccountResolver.REQUEST_ATTR_NAME, account);
        //request.setAttribute("account", account);
        stormpathWebMvcConfiguration.stormpathAuthenticationResultSaver().set(request, response, result);

        //request.setAttribute(StormpathHttpServletRequest.AUTH_TYPE_REQUEST_ATTRIBUTE_NAME, "LOGIN_METHOD");

//        String next = (String) request.getSession().getAttribute("SPRING_SECURITY_SAVED_REQUEST");
//        if (next == null) {
//            next = stormpathWebMvcConfiguration.stormpathInternalConfig().getLoginNextUrl();
//            //request.setAttribute("next", next);
//        }
//
//        response.sendRedirect(next);
//
//        //response.sendRedirect(new DefaultViewModel(next).getViewName());
//        SecurityContextHolder.

    }

//    private void do2(HttpServletRequest request, HttpServletResponse response, final Authentication authentication) throws IOException, ServletException {
//
//        String accountHref = ((StormpathUserDetails) authentication.getPrincipal()).getProperties().get("href");
//        Account account = stormpathClient.getResource(accountHref, Account.class);
//
//        AuthenticationResult result = new TransientAuthenticationResult(account);
//        request.setAttribute(Client.class.getName(), stormpathClient);
//        this.setCsrfTokenManager(stormpathWebMvcConfiguration.stormpathCsrfTokenManager());
//
//        //stormpathWebMvcConfiguration.stormpathAuthenticationResultSaver().set(request, response, result);
//        try {
//            this.handleRequest(request, response);
//        } catch (Exception e) {
//            ///
//        }
//
////        String next = stormpathWebMvcConfiguration.stormpathInternalConfig().getLoginNextUrl();
////        return new DefaultViewModel(next).setRedirect(true);
//    }

    protected Account getAccount(Authentication authentication) {
        String accountHref = ((StormpathUserDetails) authentication.getPrincipal()).getProperties().get("href");
        return stormpathClient.getResource(accountHref, Account.class);
    }

}
