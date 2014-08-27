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

import com.stormpath.sdk.application.Application;
import com.stormpath.sdk.servlet.application.ApplicationResolver;
import com.stormpath.sdk.servlet.application.DefaultApplicationResolver;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class WelcomeController extends HttpServlet {

    private ApplicationResolver applicationResolver;

    public WelcomeController() {
        //We specify a concrete implementation of an ApplicationResolver here in the constructor, but ideally this
        //would be abstracted away and provided via a setter or annotation from a dependency injection framework
        this.applicationResolver = new DefaultApplicationResolver();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        Application app = applicationResolver.getApplication(req.getServletContext());

        req.setAttribute("app", app);

        req.getRequestDispatcher("/welcome.jsp").forward(req, resp);
    }
}
