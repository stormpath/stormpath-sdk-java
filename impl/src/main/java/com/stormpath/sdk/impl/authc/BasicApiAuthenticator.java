package com.stormpath.sdk.impl.authc;

import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.account.AccountStatus;
import com.stormpath.sdk.api.ApiKey;
import com.stormpath.sdk.api.ApiKeyStatus;
import com.stormpath.sdk.error.authc.DisabledAccountException;
import com.stormpath.sdk.error.authc.DisabledApiKeyException;
import com.stormpath.sdk.error.authc.IncorrectCredentialsException;
import com.stormpath.sdk.application.Application;
import com.stormpath.sdk.authc.ApiAuthenticationResult;
import com.stormpath.sdk.impl.api.DefaultApiKeyOptions;
import com.stormpath.sdk.impl.ds.InternalDataStore;

import java.util.HashMap;
import java.util.Map;

/**
 * @since 1.0.RC
 */
public class BasicApiAuthenticator {

    private final InternalDataStore dataStore;

    public BasicApiAuthenticator(InternalDataStore dataStore) {
        this.dataStore = dataStore;
    }

    public ApiAuthenticationResult authenticate(Application application, BasicApiAuthenticationRequest request) {

        String id = request.getPrincipals();

        ApiKey apiKey = application.getApiKey(id, new DefaultApiKeyOptions().withAccount());

        if (apiKey.getStatus() == ApiKeyStatus.DISABLED) {
            throw new DisabledApiKeyException(null);
        }

        Account account = apiKey.getAccount();

        if (apiKey.getAccount().getStatus() != AccountStatus.ENABLED) {
            throw new DisabledAccountException(null, account.getStatus());
        }

        if (!apiKey.getSecret().equals(request.getCredentials())) {
            throw new IncorrectCredentialsException(null);
        }

        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(DefaultApiAuthenticationResult.API_KEY.getName(), apiKey);

        return new DefaultApiAuthenticationResult(dataStore, properties);
    }

}
