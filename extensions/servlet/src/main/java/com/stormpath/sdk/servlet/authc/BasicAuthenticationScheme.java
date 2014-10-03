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
package com.stormpath.sdk.servlet.authc;

import com.stormpath.sdk.lang.Assert;

import javax.xml.bind.DatatypeConverter;
import java.nio.charset.Charset;

public class BasicAuthenticationScheme extends AbstractAuthenticationScheme {

    private static final Charset UTF8 = Charset.forName("UTF-8");

    private static final String NAME = "Basic";

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public HttpAuthenticationResult authenticate(HttpAuthenticationAttempt attempt) {

        Assert.notNull(attempt, "attempt cannot be null.");
        Assert.notNull(attempt.getCredentials(), "credentials cannot be null.");
        Assert.isTrue(NAME.equalsIgnoreCase(attempt.getCredentials().getSchemeName()), "Unsupported scheme.");
        Assert.hasText(attempt.getCredentials().getSchemeValue(), "Cannot authenticate empty Basic value.");

        String schemeValue = attempt.getCredentials().getSchemeValue();
        byte[] bytes = DatatypeConverter.parseBase64Binary(schemeValue);
        String decoded = new String(bytes, UTF8);

        String usernameOrEmail = null;

        StringBuilder sb = new StringBuilder();

        int len = decoded.length();

        for (int i = 0; i < len; i++) {

            char c = decoded.charAt(i);

            if (usernameOrEmail == null && c == ':') {
                usernameOrEmail = sb.toString();
                sb = new StringBuilder(len - i + 1);
            } else {
                sb.append(c);
            }
        }

        String password = sb.length() > 0 ? sb.toString() : null;

        return authenticate(attempt, usernameOrEmail, password);
    }
}
