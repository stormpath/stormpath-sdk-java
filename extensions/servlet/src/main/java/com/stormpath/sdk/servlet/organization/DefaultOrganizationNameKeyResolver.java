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
package com.stormpath.sdk.servlet.organization;

import com.stormpath.sdk.lang.Collections;
import com.stormpath.sdk.servlet.http.Resolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @since 1.0.RC5
 */
public class DefaultOrganizationNameKeyResolver implements Resolver<String> {

    private Resolver<List<String>> subdomainResolver;

    public void setSubdomainResolver(Resolver<List<String>> subdomainResolver) {
        this.subdomainResolver = subdomainResolver;
    }

    @Override
    public String get(HttpServletRequest request, HttpServletResponse response) {

        List<String> subdomains = subdomainResolver.get(request, null);

        String subdomain = null;

        if (!Collections.isEmpty(subdomains)) {
            subdomain = subdomains.get(0);
        }

        return subdomain;
    }
}
