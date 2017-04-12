package com.stormpath.sdk.impl.provider;

import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.oauth.TokenResponse;
import com.stormpath.sdk.provider.OktaProviderAccountResult;
import com.stormpath.sdk.provider.ProviderAccountResult;

/**
 *
 */
public class DefaultOktaProviderAccountResult implements OktaProviderAccountResult {

    private final Account account;
    private final TokenResponse tokenResponse;

    public DefaultOktaProviderAccountResult(Account account, TokenResponse tokenResponse) {
        this.account = account;
        this.tokenResponse = tokenResponse;
    }

    public TokenResponse getTokenResponse() {
        return tokenResponse;
    }

    @Override
    public String getHref() {
        return null;
    }

    @Override
    public Account getAccount() {
        return account;
    }

    @Override
    public boolean isNewAccount() {
        return false;
    }

}
