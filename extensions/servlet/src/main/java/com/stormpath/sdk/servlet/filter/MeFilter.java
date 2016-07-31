/*
 * Copyright 2016 Stormpath, Inc.
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

import com.stormpath.sdk.servlet.filter.mvc.ControllerFilter;
import com.stormpath.sdk.servlet.http.MediaType;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;

/**
 * @since 1.0.0
 */
public class MeFilter extends ControllerFilter {

    @Override
    protected void filter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws Exception {
        // addresses https://github.com/stormpath/stormpath-sdk-java/issues/784
        request = new MeHttpServletRequestWrapper(request);
        super.filter(request, response, chain);
    }

    private class MeHttpServletRequestWrapper extends HttpServletRequestWrapper {

        public MeHttpServletRequestWrapper(HttpServletRequest request) {
            super(request);
        }

        // per spec: https://github.com/stormpath/stormpath-framework-spec/blob/1.0/user-context.md#endpoint-response
        // /me always responds with json
        @Override
        public String getHeader(String headerName) {
            if ("Accept".equals(headerName)) {
                return MediaType.APPLICATION_JSON_VALUE;
            }
            return super.getHeader(headerName);
        }
    }
}
