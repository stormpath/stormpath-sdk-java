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

import com.stormpath.sdk.authc.AuthenticationResult;
import com.stormpath.sdk.servlet.config.Config;
import com.stormpath.sdk.servlet.config.ConfigResolver;
import com.stormpath.sdk.servlet.config.impl.DefaultConfig;
import com.stormpath.sdk.servlet.http.Mutator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.List;

public class DefaultAccountStateStore implements Mutator<AuthenticationResult> {

    protected Config getConfig(HttpServletRequest request) {
        return ConfigResolver.INSTANCE.getConfig(request.getServletContext());
    }

    @Override
    public void set(HttpServletRequest request, HttpServletResponse response, AuthenticationResult result) {

        List<String> locations = getConfig(request).getAccountStoreLocations();

        if (locations.contains("disabled")) {
            return;
        }

        for (String location : locations) {
            if ("cookie".equalsIgnoreCase(location)) {
                setAccountCookie(request, response, result);
            } else if ("session".equalsIgnoreCase(location)) {
                HttpSession session = request.getSession();
                session.setAttribute("account", result.getAccount());
            } else {
                String msg = "Unrecognized " + DefaultConfig.ACCOUNT_STATE_STORE_LOCATIONS + " config value: " + location;
                throw new IllegalArgumentException(msg);
            }
        }
    }

    protected void setAccountCookie(HttpServletRequest request, HttpServletResponse response,
                                    AuthenticationResult result) {
        AccountCookieMutator.INSTANCE.set(request, response, result);
    }
}
