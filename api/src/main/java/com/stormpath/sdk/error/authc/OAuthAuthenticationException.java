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
package com.stormpath.sdk.error.authc;

import com.stormpath.sdk.error.Error;
import com.stormpath.sdk.resource.ResourceException;

/**
 * A sub-class of {@link com.stormpath.sdk.resource.ResourceException} representing an attempt fail an oauth authentication.
 *
 * @since 1.0.RC
 */
public class OAuthAuthenticationException extends ResourceException {

    public static final String INVALID_REQUEST = "invalid_request";

    public static final String INVALID_CLIENT = "invalid_client";

    public static final String UNSUPPORTED_GRANT_TYPE = "unsupported_grant_type";

    public static final String INVALID_GRANT = "invalid_grant";

    public static final String INVALID_SCOPE = "invalid_scope";

    private final String oauthError;

    public OAuthAuthenticationException(Error error, String oauthError) {
        super(error);
        this.oauthError = oauthError;
    }

    public String getOAuthError() {
        return oauthError;
    }
}
