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
package com.stormpath.sdk.servlet.filter.oauth;

import com.stormpath.sdk.lang.Assert;

/**
 * @since 1.0.RC3
 */
public class OAuthErrorCode {

    public static final OAuthErrorCode INVALID_REQUEST = new OAuthErrorCode("invalid_request");
    public static final OAuthErrorCode INVALID_CLIENT = new OAuthErrorCode("invalid_client");
    public static final OAuthErrorCode INVALID_GRANT = new OAuthErrorCode("invalid_grant");
    public static final OAuthErrorCode UNAUTHORIZED_CLIENT = new OAuthErrorCode("unauthorized_client");
    public static final OAuthErrorCode UNSUPPORTED_GRANT_TYPE = new OAuthErrorCode("unsupported_grant_type");
    public static final OAuthErrorCode INVALID_SCOPE = new OAuthErrorCode("invalid_scope");

    public final String value;

    public OAuthErrorCode(String value) {
        Assert.hasText(value, "value cannot be null or empty.");
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return getValue();
    }
}
