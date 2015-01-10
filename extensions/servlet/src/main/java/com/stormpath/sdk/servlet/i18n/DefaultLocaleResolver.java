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
package com.stormpath.sdk.servlet.i18n;

import com.stormpath.sdk.servlet.http.Resolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Locale;

/**
 * Default locale resolver implementation that returns {@link javax.servlet.http.HttpServletRequest#getLocale()}.
 */
public class DefaultLocaleResolver implements Resolver<Locale> {

    /**
     * Returns {@link HttpServletRequest#getLocale()}.
     *
     * @param request  the inbound request
     * @param response the outbound response
     * @return {@link HttpServletRequest#getLocale()}.
     */
    @Override
    public Locale get(HttpServletRequest request, HttpServletResponse response) {
        return request.getLocale();
    }
}
