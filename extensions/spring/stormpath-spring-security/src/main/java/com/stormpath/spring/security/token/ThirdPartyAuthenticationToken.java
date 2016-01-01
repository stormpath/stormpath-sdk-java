package com.stormpath.spring.security.token;

import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.lang.Assert;
import org.springframework.security.authentication.AbstractAuthenticationToken;

public class ThirdPartyAuthenticationToken extends AbstractAuthenticationToken {
    private final Object principal;
    private final Account account;

    public ThirdPartyAuthenticationToken(Account account) {
        super(null);
        Assert.notNull(account.getEmail(), "email cannot be null");
        Assert.notNull(account, "account cannot be null");
        this.principal = account.getEmail();
        this.account = account;
    }

    /**
     * Get the credentials
     */
    @Override
    public Object getCredentials() {
        return null; // already authenticated via IdSite
    }

    /**
     * Get the principal
     */
    @Override
    public Object getPrincipal() {
        return this.principal;
    }

    /**
     * Get the account
     */
    public Account getAccount() {
        return this.account;
    }
}
