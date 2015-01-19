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

import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.servlet.http.Resolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * @since 1.0.RC3
 */
public class CompositeLocaleResolver implements Resolver<Locale> {

    private List<Resolver<Locale>> resolvers;

    public CompositeLocaleResolver() {
        setResolvers(new ArrayList<Resolver<Locale>>());
    }

    public List<Resolver<Locale>> getResolvers() {
        return resolvers;
    }

    public void setResolvers(List<Resolver<Locale>> resolvers) {
        Assert.notEmpty(resolvers, "Resolvers argument cannot be null or empty.");
        this.resolvers = resolvers;
    }

    @Override
    public Locale get(HttpServletRequest request, HttpServletResponse response) {

        Locale locale;

        List<Resolver<Locale>> resolvers = getResolvers();

        for(Resolver<Locale> localeResolver : resolvers) {
            locale = localeResolver.get(request, response);
            if (locale != null) {
                return locale;
            }
        }

        return request.getLocale();
    }
}
