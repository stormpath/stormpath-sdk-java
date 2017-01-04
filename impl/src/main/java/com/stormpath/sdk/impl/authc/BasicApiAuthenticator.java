/*
 * Copyright 2014 Stormpath, Inc.
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
package com.stormpath.sdk.impl.authc;

import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.account.AccountStatus;
import com.stormpath.sdk.api.ApiAuthenticationResult;
import com.stormpath.sdk.api.ApiKey;
import com.stormpath.sdk.api.ApiKeyStatus;
import com.stormpath.sdk.application.Application;
import com.stormpath.sdk.error.authc.DisabledApiKeyException;
import com.stormpath.sdk.error.authc.IncorrectCredentialsException;
import com.stormpath.sdk.impl.api.DefaultApiKeyOptions;
import com.stormpath.sdk.impl.ds.InternalDataStore;
import com.stormpath.sdk.impl.error.ApiAuthenticationExceptionFactory;
import com.stormpath.sdk.lang.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @since 1.0.RC
 */
public class BasicApiAuthenticator {

    private static final Logger log = LoggerFactory.getLogger(BasicApiAuthenticator.class);

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

        //Workaround for https://github.com/stormpath/stormpath-sdk-java/issues/440
        ApiKey apiKey = null;
        for (int i = 0; i < 3; i++) {
            try {
                apiKey = application.getApiKey(id, new DefaultApiKeyOptions().withAccount());
                break;
            } catch (Exception e) {
                log.error("Couldn't get Application ApiKey for " + application.getHref(), e);
                if (i == 2) {
                    throw new RuntimeException(e);
                }
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e1) {
                    throw new RuntimeException(e1);
                }
            }
        }

        if (apiKey == null || !apiKey.getSecret().equals(secret)) {
            throw ApiAuthenticationExceptionFactory.newApiAuthenticationException(IncorrectCredentialsException.class);
        }

        if (apiKey.getStatus() == ApiKeyStatus.DISABLED) {
            throw ApiAuthenticationExceptionFactory.newApiAuthenticationException(DisabledApiKeyException.class);
        }

        Account account = apiKey.getAccount();

        if (account.getStatus() != AccountStatus.ENABLED) {
            throw ApiAuthenticationExceptionFactory.newDisabledAccountException(account.getStatus());
        }

        return new DefaultApiAuthenticationResult(dataStore, apiKey);
    }
}
