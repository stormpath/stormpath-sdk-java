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
import com.stormpath.sdk.servlet.account.event.RegisteredAccountRequestEvent;
import com.stormpath.sdk.servlet.event.RequestEventListener;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @since 1.0.RC3
 */
public class DefaultRegisteredAccountRequestEvent extends AbstractAccountRequestEvent
    implements RegisteredAccountRequestEvent {

    public DefaultRegisteredAccountRequestEvent(HttpServletRequest request, HttpServletResponse response, Account account) {
        super(request, response, account);
    }

    @Override
    public void accept(RequestEventListener listener) {
        listener.on(this);
    }
}
