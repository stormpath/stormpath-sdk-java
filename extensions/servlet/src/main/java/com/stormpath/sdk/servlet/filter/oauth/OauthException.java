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
public class OauthException extends RuntimeException {

    private final OauthErrorCode errorCode;
    private final String description;
    private final String uri;

    public OauthException(OauthErrorCode code) {
        this(code, null, null);
    }

    public OauthException(OauthErrorCode code, String description, String uri) {
        super(description != null ? description : (code != null ? code.getValue() : ""));
        Assert.notNull(code, "OauthErrorCode cannot be null.");
        this.errorCode = code;
        this.description = description;
        this.uri = uri;
    }

    public OauthException(OauthErrorCode code, String description, String uri, Exception cause) {
        super(description != null ? description : (code != null ? code.getValue() : ""), cause);
        Assert.notNull(code, "OauthErrorCode cannot be null.");
        this.errorCode = code;
        this.description = description;
        this.uri = uri;
    }

    public OauthErrorCode getErrorCode() {
        return errorCode;
    }

    public String getDescription() {
        return description;
    }

    public String getUri() {
        return uri;
    }

    public String toJson() {

        String json = "{" + toJson("error", getErrorCode());

        String val = getDescription();
        if (Strings.hasText(val)) {
            json += "," + toJson("error_description", val);
        }

        val = getUri();
        if (Strings.hasText(val)) {
            json += "," + toJson("error_uri", val);
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
