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
package com.stormpath.sdk.servlet.i18n;

import com.stormpath.sdk.servlet.http.Resolver;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.jstl.core.Config;
import java.util.Locale;

public class JstlLocaleResolver implements Resolver<Locale> {

    @Override
    public Locale get(HttpServletRequest request, HttpServletResponse response) {

        Object localeObject = Config.get(request, Config.FMT_LOCALE);

        if (localeObject == null) {

            HttpSession session = request.getSession(false);
            if (session != null) {
                localeObject = Config.get(session, Config.FMT_LOCALE);
            }

            ServletContext servletContext = request.getServletContext();

            if (localeObject == null && servletContext != null) {
                localeObject = Config.get(servletContext, Config.FMT_LOCALE);
            }
        }

        return localeObject instanceof Locale ? (Locale) localeObject : null;
    }
}
