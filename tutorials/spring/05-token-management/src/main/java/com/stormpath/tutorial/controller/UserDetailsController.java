/*
 * Copyright 2017 Stormpath, Inc.
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
package com.stormpath.tutorial.controller;

import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.servlet.account.AccountResolver;
import com.stormpath.tutorial.model.AccountInfo;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * @since 1.3.0
 */
@RestController
public class UserDetailsController {

    @RequestMapping(value="/userdetails", produces = MediaType.APPLICATION_JSON_VALUE)
    public AccountInfo info(HttpServletRequest req) {
        // must be logged in to get here per Spring Security config
        Account account = AccountResolver.INSTANCE.getAccount(req);

        return new AccountInfo(account.getEmail(), account.getFullName(), account.getHref());
    }

}
