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
package com.stormpath.sdk.servlet.filter.account;

import com.stormpath.sdk.servlet.config.CookieConfig;

import javax.servlet.http.HttpServletRequest;

public class LocalhostAccountCookieSecureEvaluator implements AccountCookieSecureEvaluator {

    @Override
    public boolean isAccountCookieSecure(HttpServletRequest request, CookieConfig accountCookieConfig) {

        String serverName = request.getServerName();

        boolean localhost = serverName.equalsIgnoreCase("localhost") ||
                            serverName.equals("127.0.0.1") ||
                            serverName.equals("::1") ||
                            serverName.equals("0:0:0:0:0:0:0:1");

        return !localhost;
    }
}
