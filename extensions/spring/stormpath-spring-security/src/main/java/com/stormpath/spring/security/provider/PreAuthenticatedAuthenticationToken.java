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
package com.stormpath.spring.security.provider;

import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.lang.Assert;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

//TODO TM: delete this, not needed
@Deprecated
public class PreAuthenticatedAuthenticationToken extends AbstractAuthenticationToken {

    private Account account;

    public PreAuthenticatedAuthenticationToken(Account account) {
        this(account, null);
    }

    //TODO: TM documentation is failing
    /**
     * Constructor used for an authentication response. The
     * {@link org.springframework.security.core.Authentication#isAuthenticated()} will
     * return <code>true</code>.
     *
     * @param aPrincipal The authenticated principal
     * @param anAuthorities The granted authorities
     */
    public PreAuthenticatedAuthenticationToken(Account account,
                                               Collection<? extends GrantedAuthority> anAuthorities) {
        super(anAuthorities);
        Assert.notNull(account, "account cannot be null.");
        this.account = account;
        setAuthenticated(true);
    }

    /**
     * Get the credentials
     */
    public Object getCredentials() {
        return null;
    }

    /**
     * Get the principal
     */
    public Object getPrincipal() {
        return this.account.getEmail();
    }

    /**
     * Get the account
     */
    public Account getAccount() {
        return account;
    }

}
