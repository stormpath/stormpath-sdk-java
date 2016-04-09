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
package com.stormpath.sdk.servlet.http.authc;

import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.account.AccountStatus;
import com.stormpath.sdk.api.ApiKey;
import com.stormpath.sdk.api.ApiKeyStatus;
import com.stormpath.sdk.application.Application;
import com.stormpath.sdk.error.Error;
import com.stormpath.sdk.error.authc.DisabledAccountException;
import com.stormpath.sdk.error.authc.DisabledApiKeyException;
import com.stormpath.sdk.error.authc.InvalidApiKeyException;

import javax.servlet.http.HttpServletRequest;

/**
 * @since 1.0.RC3
 */
public abstract class AbstractAuthenticationScheme implements HttpAuthenticationScheme {

    public AbstractAuthenticationScheme() {
    }

    protected Application getApplication(HttpServletRequest request) {
        return (Application)request.getAttribute(Application.class.getName());
    }

    protected ApiKey getEnabledApiKey(HttpServletRequest request, String apiKeyId)
        throws InvalidApiKeyException, DisabledApiKeyException, DisabledAccountException {

        Application app = getApplication(request);
        ApiKey apiKey = app.getApiKey(apiKeyId);

        if (apiKey == null) {
            throw new InvalidApiKeyException(newError("apiKey is invalid."));
        }

        if (apiKey.getStatus() != ApiKeyStatus.ENABLED) {
            throw new DisabledApiKeyException(newError("apiKey is disabled."));
        }

        Account account = apiKey.getAccount();

        AccountStatus status = account.getStatus();
        if (status != AccountStatus.ENABLED) {
            throw new DisabledAccountException(newError("account is disabled."), status);
        }

        return apiKey;
    }

    protected Error newError(final String message) {
        return new Error() {
            @Override
            public int getStatus() {
                return 0;
            }

            @Override
            public int getCode() {
                return 0;
            }

            @Override
            public String getMessage() {
                return message;
            }

            @Override
            public String getDeveloperMessage() {
                return message;
            }

            @Override
            public String getMoreInfo() {
                return null;
            }

            @Override
            public String getRequestId() {
                return null;
            }
        };
    }
}
