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

public class AccessTokenRequestException extends RuntimeException {

    private final AccessTokenErrorCode errorCode;
    private final String description;
    private final String uri;

    public AccessTokenRequestException(AccessTokenErrorCode code) {
        this(code, null, null);
    }

    public AccessTokenRequestException(AccessTokenErrorCode code, String description, String uri) {
        super(description != null ? description : (code != null ? code.getValue() : ""));
        Assert.notNull(code, "AccessTokenErrorCode cannot be null.");
        this.errorCode = code;
        this.description = description;
        this.uri = uri;
    }

    public AccessTokenErrorCode getErrorCode() {
        return errorCode;
    }

    public String getDescription() {
        return description;
    }

    public String getUri() {
        return uri;
    }
}
