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
import com.stormpath.sdk.application.Application;
import com.stormpath.sdk.servlet.application.ApplicationResolver;
import com.stormpath.sdk.servlet.client.ClientResolver;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class RegisterController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("/register.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            onPost(req, resp);
        } catch (Exception e) {
            String error = e.getMessage();
            req.setAttribute("error", error);
            req.getRequestDispatcher("/register.jsp").forward(req, resp);
        }
    }

    protected void onPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String email = req.getParameter("email");
        String password = req.getParameter("password");

        //Create a new Account instance that will represent the submitted user information:
        Account account = ClientResolver.INSTANCE.getClient(req.getServletContext()).instantiate(Account.class);
        account.setEmail(email);
        account.setPassword(password);

        //Stormpath requires a givenName and surname on its account objects, but we don't need that for this simple
        //sample app - just create dummy values:
        account.setGivenName("UNSPECIFIED");
        account.setSurname("UNSPECIFIED");

        //Get the Stormpath Application instance corresponding to this web app:
        Application app = ApplicationResolver.INSTANCE.getApplication(req.getServletContext());

        //now persist the new account, and ensure our account reference points to the newly created/returned instance:
        account = app.createAccount(account);

        //store the account in the Session for now:
        req.getSession().setAttribute("account", account);

        //login was successful, show the dashboard:
        resp.sendRedirect(req.getContextPath() + "/dashboard");
    }
}
