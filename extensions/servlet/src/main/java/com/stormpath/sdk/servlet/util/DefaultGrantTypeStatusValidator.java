/*
 * Copyright 2016 Stormpath, Inc.
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
package com.stormpath.sdk.servlet.util;

import com.stormpath.sdk.servlet.filter.oauth.OAuthErrorCode;
import com.stormpath.sdk.servlet.filter.oauth.OAuthException;

/**
 * @since 1.2.0
 */
public class DefaultGrantTypeStatusValidator implements GrantTypeStatusValidator {

    private boolean clientCredentialsGrantTypeEnabled;
    private boolean passwordGrantTypeEnabled;

    public void setClientCredentialsGrantTypeEnabled(boolean clientCredentialsGrantTypeEnabled) {
        this.clientCredentialsGrantTypeEnabled = clientCredentialsGrantTypeEnabled;
    }

    public void setPasswordGrantTypeEnabled(boolean passwordGrantTypeEnabled) {
        this.passwordGrantTypeEnabled = passwordGrantTypeEnabled;
    }

    @Override
    public void validate(String grantType) throws OAuthException {
        switch (grantType) {
            case PASSWORD_GRANT_TYPE:
                if (!passwordGrantTypeEnabled) {
                    throw new OAuthException(OAuthErrorCode.UNSUPPORTED_GRANT_TYPE);
                }
                break;
            case CLIENT_CREDENTIALS_GRANT_TYPE:
                if (!clientCredentialsGrantTypeEnabled) {
                    throw new OAuthException(OAuthErrorCode.UNSUPPORTED_GRANT_TYPE);
                }
                break;
        }
    }
}
