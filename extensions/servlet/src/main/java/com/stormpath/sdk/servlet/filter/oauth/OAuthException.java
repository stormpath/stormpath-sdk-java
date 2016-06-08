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
import com.stormpath.sdk.lang.Strings;

/**
 * @since 1.0.RC3
 */
public class OAuthException extends RuntimeException {

    private final OAuthErrorCode errorCode;
    private final String message;

    public OAuthException(OAuthErrorCode code) {
        this(code, null, null);
    }

    public OAuthException(OAuthErrorCode code, String message) {
        super(message != null ? message : (code != null ? code.getValue() : ""));
        Assert.notNull(code, "OAuthErrorCode cannot be null.");
        this.errorCode = code;
        this.message = message;
    }

    public OAuthException(OAuthErrorCode code, String message, Exception cause) {
        super(message != null ? message : (code != null ? code.getValue() : ""), cause);
        Assert.notNull(code, "OAuthErrorCode cannot be null.");
        this.errorCode = code;
        this.message = message;
    }

    public OAuthErrorCode getErrorCode() {
        return errorCode;
    }

    public String getMessage() {
        return message;
    }

    public String toJson() {

        String json = "{" + toJson("error", getErrorCode());

        String val = getMessage();
        if (Strings.hasText(val)) {
            json += "," + toJson("message", val);
        }

        json += "}";

        return json;
    }

    protected static String toJson(String name, Object value) {
        String stringValue = String.valueOf(value);
        return quote(name) + ":" + quote(stringValue);
    }

    protected static String quote(String val) {
        return "\"" + val + "\"";
    }
}
