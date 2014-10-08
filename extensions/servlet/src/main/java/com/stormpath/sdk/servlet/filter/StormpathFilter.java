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
package com.stormpath.sdk.servlet.filter;

import com.stormpath.sdk.servlet.http.impl.StormpathHttpServletRequest;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

public class StormpathFilter extends HttpFilter {

    private final List<Filter> filters;

    public StormpathFilter() {
        this.filters = new CopyOnWriteArrayList<Filter>();
    }

    @Override
    protected void onInit() throws ServletException {
        Map<String,Filter> defaults = DefaultFilter.createInstanceMap(getServletContext());
        this.filters.addAll(defaults.values());
    }

    @Override
    public void filter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws Exception {
        //wrap:
        request = new StormpathHttpServletRequest(request);
        chain = new ProxiedFilterChain(chain, this.filters);

        //continue:
        chain.doFilter(request, response);
    }
}
