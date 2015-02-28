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
package com.stormpath.sdk.servlet.mvc;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

public class AbstractHttpServlet extends HttpServlet {

    @Override
    public void init() throws ServletException {

        try {

            preInit();

            doInit();

            postInit();

        } catch (Exception e) {

            if (e instanceof ServletException) {
                throw (ServletException) e;
            }

            String msg = "Unable to initialize HttpServlet instance of type " + getClass().getName() + ": " + e.getMessage();
            throw new ServletException(msg);
        }
    }

    protected void preInit() throws Exception {
    }

    protected void doInit() throws Exception {
    }

    protected void postInit() throws Exception {
    }


}
