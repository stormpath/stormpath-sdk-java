package com.stormpath.sdk.impl.provider;

import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.api.ApiKey;
import com.stormpath.sdk.authc.AuthenticationResultVisitor;
import com.stormpath.sdk.lang.Collections;
import com.stormpath.sdk.lang.Strings;
import com.stormpath.sdk.oauth.TokenResponse;
import com.stormpath.sdk.provider.OktaProviderAccountResult;
import com.stormpath.sdk.provider.ProviderAccountResult;

import java.util.Set;

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

    @Override
    public void accept(AuthenticationResultVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public Set<String> getScope() {
        return Strings.delimitedListToSet(tokenResponse.getScope(), " ");
    }

    @Override
    public ApiKey getApiKey() {
        return null;
    }
}
