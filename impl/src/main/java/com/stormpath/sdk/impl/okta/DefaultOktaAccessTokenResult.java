package com.stormpath.sdk.impl.okta;

import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.api.ApiKey;
import com.stormpath.sdk.authc.AuthenticationResultVisitor;
import com.stormpath.sdk.lang.Strings;
import com.stormpath.sdk.oauth.OktaAccessTokenResult;
import com.stormpath.sdk.oauth.TokenResponse;

import java.util.Set;

/**
 */
public class DefaultOktaAccessTokenResult implements OktaAccessTokenResult {

    private final TokenResponse tokenResponse;
    private final Account account;

    public DefaultOktaAccessTokenResult(TokenResponse tokenResponse, Account account) {
        this.tokenResponse = tokenResponse;
        this.account = account;
    }

    @Override
    public String getHref() {
        return null;
    }

    @Override
    public TokenResponse getTokenResponse() {
        return tokenResponse;
    }

    @Override
    public Set<String> getScope() {
        return Strings.delimitedListToSet(tokenResponse.getScope(), " ");
    }

    @Override
    public ApiKey getApiKey() {
        return null;
    }

    @Override
    public Account getAccount() {
        return account;
    }

    @Override
    public void accept(AuthenticationResultVisitor visitor) {
        visitor.visit(this);
    }
}
