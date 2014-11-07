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
package com.stormpath.sdk.servlet.filter.oauth;

import com.stormpath.sdk.lang.Assert;

public class AccessTokenErrorCode {

    public static final AccessTokenErrorCode INVALID_REQUEST = new AccessTokenErrorCode("invalid_request");
    public static final AccessTokenErrorCode INVALID_CLIENT = new AccessTokenErrorCode("invalid_client");
    public static final AccessTokenErrorCode INVALID_GRANT = new AccessTokenErrorCode("invalid_grant");
    public static final AccessTokenErrorCode UNAUTHORIZED_CLIENT = new AccessTokenErrorCode("unauthorized_client");
    public static final AccessTokenErrorCode UNSUPPORTED_GRANT_TYPE = new AccessTokenErrorCode("unsupported_grant_type");
    public static final AccessTokenErrorCode INVALID_SCOPE = new AccessTokenErrorCode("invalid_scope");

    public final String value;

    public AccessTokenErrorCode(String value) {
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
