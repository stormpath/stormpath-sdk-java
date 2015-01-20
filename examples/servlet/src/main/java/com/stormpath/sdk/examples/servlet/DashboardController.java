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
import com.stormpath.sdk.directory.CustomData;
import com.stormpath.sdk.lang.Strings;
import com.stormpath.sdk.servlet.account.AccountResolver;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class DashboardController extends HttpServlet {

    private static final String VIEW_TEMPLATE_PATH = "/WEB-INF/jsp/dashboard.jsp";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String birthday = "";
        String color = "";

        Account account = AccountResolver.INSTANCE.getRequiredAccount(req);
        if (account != null) {
            CustomData data = account.getCustomData();
            birthday = (String)data.get("birthday");
            color = (String)data.get("color");
        }

        req.setAttribute("birthday", birthday);
        req.setAttribute("color", color);
        req.getRequestDispatcher(VIEW_TEMPLATE_PATH).forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String birthday = req.getParameter("birthday");
        String color = req.getParameter("color");

        //get the currently-logged-in account:
        Account account = AccountResolver.INSTANCE.getRequiredAccount(req);
        if (account != null) {

            CustomData data = account.getCustomData();

            if (Strings.hasText(birthday)) {
                data.put("birthday", birthday);
            } else {
                data.remove("birthday");
            }

            if (Strings.hasText(color)) {
                data.put("color", color);
            } else {
                data.remove("color");
            }

            data.save();
        }

        req.setAttribute("birthday", birthday);
        req.setAttribute("color", color);
        req.getRequestDispatcher(VIEW_TEMPLATE_PATH).forward(req, resp);
    }
}
