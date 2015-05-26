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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Date: 5/20/15
 */
@Component
//public class LogoutHandlerWrapper implements LogoutSuccessHandler {
public class LogoutHandler implements org.springframework.security.web.authentication.logout.LogoutHandler {
//public class LogoutHandlerWrapper extends LogoutController implements LogoutSuccessHandler {

    @Autowired
    protected StormpathWebMvcConfiguration stormpathWebMvcConfiguration;

//    @Autowired
//    protected Client stormpathClient;

//    public LogoutHandler() {
//        System.out.println();
//    }

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        do3(request, response, authentication);
    }


    public void do1(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        try {
            //SecurityContextHolder.getContext().setAuthentication(null);

            //stormpathWebMvcConfiguration.stormpathLogoutController().handleRequest(request, response);
            SecurityContextHolder.getContext().setAuthentication(null);

            //response.sendRedirect(stormpathWebMvcConfiguration.stormpathInternalConfig().getLogoutNextUrl());

        } catch (Exception e) {

            //TODO see here XXX

        }
    }

    public void do3(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        try {

            stormpathWebMvcConfiguration.stormpathAuthenticationResultSaver().set(request, response, null);
            //stormpathWebMvcConfiguration.stormpathCookieAuthenticationResultSaver().set(request, response, null);
//            //SecurityContextHolder.getContext().setAuthentication(null);
//
//            Cookie[] cookies = request.getCookies();
//            for(Cookie cookie : cookies){
//                if(cookie.getName().equals("account")){
//                    cookie.setValue("");
//                    cookie.setPath("/");
//                    cookie.setMaxAge(0);
//                    response.addCookie(cookie);
//                    break;
//                }
//            }

            //response.sendRedirect(stormpathWebMvcConfiguration.stormpathInternalConfig().getLogoutNextUrl());

        } catch (Exception e) {

            //TODO see here XXX

        }
    }

    //OK
    public void do2(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        try {

            //request.removeAttribute(Account.class.getName());
            //store under both names - can be convenient depending on how it is accessed:
            //request.removeAttribute(DefaultAccountResolver.REQUEST_ATTR_NAME);
            //request.removeAttribute("account");

            //SecurityContextHolder.getContext().setAuthentication(null);

            //stormpathWebMvcConfiguration.stormpathLogoutController().handleRequest(request, response);
            //SecurityContextHolder.getContext().setAuthentication(null);

            Cookie[] cookies = request.getCookies();
            for(Cookie cookie : cookies){
                if(cookie.getName().equals("account")){
                    cookie.setValue("");
                    cookie.setPath("/");
                    cookie.setMaxAge(0);
                    response.addCookie(cookie);
                    break;
                }
            }

            //response.sendRedirect(stormpathWebMvcConfiguration.stormpathInternalConfig().getLogoutNextUrl());

        } catch (Exception e) {

            //TODO see here XXX

        }
    }

//
//    public void do1(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
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

}
