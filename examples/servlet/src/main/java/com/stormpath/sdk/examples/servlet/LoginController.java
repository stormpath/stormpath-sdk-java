/*
 * Copyright 2014 Stormpath, Inc.
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
package com.stormpath.sdk.examples.servlet;

import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.servlet.account.RequestAccountResolver;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class LoginController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("/login.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            login(req, resp);
        } catch (ServletException e) {
            String error = e.getMessage();
            req.setAttribute("error", error);
            req.getRequestDispatcher("/login.jsp").forward(req, resp);
        }
    }

    protected Account getAccount(HttpServletRequest req) {
        return RequestAccountResolver.INSTANCE.getAccount(req);
    }

    protected void login(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String usernameOrEmail = req.getParameter("email");
        String password = req.getParameter("password");

        req.login(usernameOrEmail, password);

        //Login was successful - get the Account that just logged in:
        Account account = getAccount(req);

        //put the account in the session for easy retrieval later:
        req.getSession().setAttribute("account", account);

        //Now show the user their account/dashboard view:
        req.getRequestDispatcher("/dashboard.jsp").forward(req, resp);
    }
}
