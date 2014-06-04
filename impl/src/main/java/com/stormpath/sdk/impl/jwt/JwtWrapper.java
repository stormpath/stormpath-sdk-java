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
package com.stormpath.sdk.impl.jwt;

import com.stormpath.sdk.impl.ds.JacksonMapMarshaller;
import com.stormpath.sdk.impl.ds.MapMarshaller;
import com.stormpath.sdk.impl.util.Base64;
import com.stormpath.sdk.lang.Assert;

import java.nio.charset.Charset;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * JwtWrapper encapsulates the value of a jwt token.
 *
 * @since 1.0.RC
 */
public class JwtWrapper {

    private final static Charset UTF_8 = Charset.forName("UTF-8");

    private final String base64JwtHeader;

    private final String base64JsonPayload;

    private final String base64JwtSignature;

    private final MapMarshaller mapMarshaller;

    public JwtWrapper(String jwt) {

        Assert.hasText(jwt, "jwt cannot ne null or empty");

        StringTokenizer tokenizer = new StringTokenizer(jwt, JwtConstants.JWT_TOKENS_SEPARATOR);

        Assert.isTrue(tokenizer.countTokens() == 3, "Invalid jwt value.");

        this.base64JwtHeader = tokenizer.nextToken();
        this.base64JsonPayload = tokenizer.nextToken();
        this.base64JwtSignature = tokenizer.nextToken();

        this.mapMarshaller = new JacksonMapMarshaller();
    }

    public String getBase64JwtHeader() {
        return base64JwtHeader;
    }

    public String getBase64JsonPayload() {
        return base64JsonPayload;
    }

    public String getBase64JwtSignature() {
        return base64JwtSignature;
    }

    public Map getJsonPayloadAsMap() {

        byte[] jsonBytes = Base64.decodeBase64(base64JsonPayload);

        Assert.notNull(jsonBytes, "JsonPayload couldn't be decoded.");

        return mapMarshaller.unmarshal(new String(jsonBytes, UTF_8));
    }

}
