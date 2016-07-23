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
package com.stormpath.sdk.servlet.mvc;

import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.servlet.account.AccountResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @since 1.0.0
 */
public class MeController extends AbstractController {

    private List<String> expands;
    private AccountModelFactory accountModelFactory;

    public MeController(List<String> expands) {
        this.expands = expands;
        this.accountModelFactory = new DefaultAccountModelFactory();
    }

    @Override
    public boolean isNotAllowedIfAuthenticated() {
        return false;
    }

    /**
     * Successful JSON login will forward here as a POST so that the account model is returned.
     *
     * See: https://github.com/stormpath/stormpath-sdk-java/issues/682
     *
     * @since 1.0.0
     */
    @Override
    protected ViewModel doPost(HttpServletRequest request, HttpServletResponse response) throws Exception {
        return doGet(request, response);
    }

    @Override
    protected ViewModel doGet(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Account account = AccountResolver.INSTANCE.getAccount(request);

        response.setHeader("Cache-Control", "no-store, no-cache");
        response.setHeader("Pragma", "no-cache");

        if (account != null) {
            return new DefaultViewModel(STORMPATH_JSON_VIEW_NAME, java.util.Collections.singletonMap("account", accountModelFactory.toMap(account, expands)));
        }

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        return new DefaultViewModel(STORMPATH_JSON_VIEW_NAME, null);
    }
}
