package com.stormpath.sdk.impl.authc;

import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.account.AccountStatus;
import com.stormpath.sdk.api.ApiKey;
import com.stormpath.sdk.api.ApiKeyStatus;
import com.stormpath.sdk.application.Application;
import com.stormpath.sdk.authc.ApiAuthenticationResult;
import com.stormpath.sdk.error.authc.DisabledAccountException;
import com.stormpath.sdk.error.authc.IncorrectCredentialsException;
import com.stormpath.sdk.impl.api.DefaultApiKeyOptions;
import com.stormpath.sdk.impl.ds.InternalDataStore;
import com.stormpath.sdk.impl.error.ApiAuthenticationExceptionFactory;
import com.stormpath.sdk.lang.Assert;

import java.util.HashMap;
import java.util.Map;

/**
 * @since 1.0.RC
 */
public class BasicApiAuthenticator {

    private final InternalDataStore dataStore;

    public BasicApiAuthenticator(InternalDataStore dataStore) {
        Assert.notNull(dataStore);
        this.dataStore = dataStore;
    }

    public ApiAuthenticationResult authenticate(Application application, DefaultBasicApiAuthenticationRequest request) {
        Assert.notNull(request, "request cannot be null.");

        String id = request.getPrincipals();
        String secret = request.getCredentials();

        return this.authenticate(application, id, secret);
    }

    public ApiAuthenticationResult authenticate(Application application, String id, String secret) {
        Assert.notNull(application, "application  cannot be null.");

        ApiKey apiKey = application.getApiKey(id, new DefaultApiKeyOptions().withAccount());

        if (apiKey.getStatus() == ApiKeyStatus.DISABLED) {
            throw ApiAuthenticationExceptionFactory.newApiAuthenticationException(DisabledAccountException.class);
        }

        Account account = apiKey.getAccount();

        if (apiKey.getAccount().getStatus() != AccountStatus.ENABLED) {
            throw new DisabledAccountException(null, account.getStatus());
        }

        if (!apiKey.getSecret().equals(secret)) {
            throw ApiAuthenticationExceptionFactory.newApiAuthenticationException(IncorrectCredentialsException.class);
        }
        return new DefaultApiAuthenticationResult(dataStore, apiKey);
    }
}
