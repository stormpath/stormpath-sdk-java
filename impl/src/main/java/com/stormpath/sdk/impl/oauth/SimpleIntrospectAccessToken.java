package com.stormpath.sdk.impl.oauth;

import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.application.Application;
import com.stormpath.sdk.oauth.AccessToken;
import com.stormpath.sdk.okta.TokenIntrospectResponse;
import com.stormpath.sdk.tenant.Tenant;

import java.util.Map;

/**
 */
public class SimpleIntrospectAccessToken implements AccessToken {

    private final Account account;
    private final String accessTokenString;
    private final Application application;

    public SimpleIntrospectAccessToken(String tokenString, Account account, Application application) {
        this.account = account;
        this.accessTokenString = tokenString;
        this.application = application;
    }


    @Override
    public void delete() {
        throw new UnsupportedOperationException("delete() not implemented");
    }

    @Override
    public String getHref() {
        return null;
    }

    @Override
    public String getJwt() {
        return accessTokenString;
    }

    @Override
    public Account getAccount() {
        return account;
    }

    @Override
    public Application getApplication() {
        return application;
    }

    @Override
    public Tenant getTenant() {
        return getApplication().getTenant();
    }

    @Override
    public Map<String, Object> getExpandedJwt() {
        return null;
    }

    @Override
    public void revoke() {
        throw new UnsupportedOperationException("revoke() not implemented");
    }
}
