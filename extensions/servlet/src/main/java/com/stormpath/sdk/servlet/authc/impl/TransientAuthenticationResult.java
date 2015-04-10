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
package com.stormpath.sdk.servlet.authc.impl;

import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.authc.AuthenticationResult;
import com.stormpath.sdk.authc.AuthenticationResultVisitor;
import com.stormpath.sdk.lang.Assert;

/**
 * An authentication result that is not persistent (does not have an {@code href} value).  Useful for framework
 * development scenarios.
 *
 * @since 1.0
 */
public class TransientAuthenticationResult implements AuthenticationResult {

    private final Account account;

    public TransientAuthenticationResult(Account account) {
        Assert.notNull(account, "account argument cannot be null.");
        this.account = account;
    }

    @Override
    public Account getAccount() {
        return this.account;
    }

    @Override
    public void accept(AuthenticationResultVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String getHref() {
        return null;
    }

    @Override
    public String toString() {
        return "TransientAuthenticationResult for account " + account.getHref();
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (o instanceof TransientAuthenticationResult) {
            TransientAuthenticationResult other = (TransientAuthenticationResult)o;
            return account.equals(other.getAccount());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return account.hashCode();
    }
}
