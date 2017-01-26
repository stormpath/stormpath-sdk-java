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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.lang.Strings;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @since 1.0.RC3
 */
public class OAuthException extends RuntimeException {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private final OAuthErrorCode errorCode;

    private Map<String, Object> errorMap;

    public OAuthException(OAuthErrorCode code) {
        this(code, null, (Exception) null);
    }

    public OAuthException(OAuthErrorCode code, String message) {
        super(message != null ? message : (code != null ? code.getValue() : ""));
        Assert.notNull(code, "OAuthErrorCode cannot be null.");
        this.errorCode = code;

        initializeErrorMap();
    }

    public OAuthException(OAuthErrorCode code, String message, Exception cause) {
        super(message != null ? message : (code != null ? code.getValue() : ""), cause);
        Assert.notNull(code, "OAuthErrorCode cannot be null.");
        this.errorCode = code;

        initializeErrorMap();
    }

    public OAuthException(OAuthErrorCode code, Map<String, Object> error, String message) {
        this(code, message, null);

        errorMap.putAll(error);
    }

    private void initializeErrorMap() {
        errorMap = new LinkedHashMap<>();

        errorMap.put("error", errorCode.getValue());

        String val = getMessage();
        if (Strings.hasText(val)) {
            errorMap.put("message", val);
        }
    }

    public OAuthErrorCode getErrorCode() {
        return errorCode;
    }

    public String toJson() {
        try {
            return objectMapper.writeValueAsString(errorMap);
        } catch (Exception e) {
            throw new IllegalStateException("Unable to serialize OAuthException to json.", e);
        }
    }

}
