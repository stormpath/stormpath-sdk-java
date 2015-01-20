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
package com.stormpath.sdk.servlet.filter.account;

import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.authc.AuthenticationResult;
import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.servlet.account.DefaultAccountResolver;
import com.stormpath.sdk.servlet.http.Saver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collections;
import java.util.List;

/**
 * @since 1.0.RC3
 */
public class AuthenticationResultSaver implements Saver<AuthenticationResult> {

    private List<Saver<AuthenticationResult>> savers;

    public AuthenticationResultSaver(List<Saver<AuthenticationResult>> savers) {
        Assert.notEmpty(savers, "At least one Saver<AuthenticationResult> must be specified.");
        this.savers = Collections.unmodifiableList(savers);
    }

    @Override
    public void set(HttpServletRequest request, HttpServletResponse response, AuthenticationResult result) {

        for (Saver<AuthenticationResult> saver : savers) {
            saver.set(request, response, result);
        }

        if (result == null) {
            request.removeAttribute(DefaultAccountResolver.REQUEST_ATTR_NAME);
            request.removeAttribute("account");
            return;
        }

        Account account = result.getAccount();
        //store under both names - can be convenient depending on how it is accessed:
        request.setAttribute(DefaultAccountResolver.REQUEST_ATTR_NAME, account);
        request.setAttribute("account", account);
    }
}
