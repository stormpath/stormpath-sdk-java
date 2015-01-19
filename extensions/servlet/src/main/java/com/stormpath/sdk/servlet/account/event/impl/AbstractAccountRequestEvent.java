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
package com.stormpath.sdk.servlet.account.event.impl;

import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.servlet.account.event.AccountRequestEvent;
import com.stormpath.sdk.servlet.event.impl.AbstractRequestEvent;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @since 1.0.RC3
 */
public abstract class AbstractAccountRequestEvent extends AbstractRequestEvent
    implements AccountRequestEvent {

    private final Account account;

    public AbstractAccountRequestEvent(HttpServletRequest request, HttpServletResponse response, Account account) {
        super(request, response);
        Assert.notNull(account, "Account cannot be null.");
        this.account = account;
    }

    @Override
    public Account getAccount() {
        return this.account;
    }
}
