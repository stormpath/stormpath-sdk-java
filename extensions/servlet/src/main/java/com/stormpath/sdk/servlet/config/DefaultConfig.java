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
package com.stormpath.sdk.servlet.config;

import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.lang.Strings;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

public class DefaultConfig implements Config {

    public static final String LOGIN_URL = "stormpath.web.login.url";
    public static final String LOGIN_NEXT_URL = "stormpath.web.login.nextUrl";
    public static final String LOGOUT_URL = "stormpath.web.logout.url";
    public static final String LOGOUT_NEXT_URL = "stormpath.web.logout.nextUrl";
    public static final String REGISTER_URL = "stormpath.web.register.url";
    public static final String REGISTER_NEXT_URL = "stormpath.web.register.nextUrl";
    public static final String VERIFY_URL = "stormpath.web.verify.url";
    public static final String VERIFY_NEXT_URL = "stormpath.web.verify.nextUrl";
    public static final String UNAUTHORIZED_URL = "stormpath.web.unauthorized.url";

    public static final String ACCOUNT_COOKIE_NAME = "stormpath.web.account.cookie.name";
    public static final String ACCOUNT_COOKIE_COMMENT = "stormpath.web.account.cookie.comment";
    public static final String ACCOUNT_COOKIE_DOMAIN = "stormpath.web.account.cookie.domain";
    public static final String ACCOUNT_COOKIE_MAX_AGE = "stormpath.web.account.cookie.maxAge";
    public static final String ACCOUNT_COOKIE_PATH = "stormpath.web.account.cookie.path";
    public static final String ACCOUNT_COOKIE_SECURE = "stormpath.web.account.cookie.secure";
    public static final String ACCOUNT_COOKIE_HTTP_ONLY = "stormpath.web.account.cookie.httpOnly";

    private final Map<String, String> props;

    private final CookieConfig ACCOUNT_COOKIE_CONFIG;

    public DefaultConfig(Map<String, String> configProps) {
        Assert.notNull(configProps, "Properties argument cannot be null.");
        this.props = Collections.unmodifiableMap(configProps);

        final int maxAge;
        String val = props.get(ACCOUNT_COOKIE_MAX_AGE);
        if (Strings.hasText(val)) {
            try {
                int i = Integer.parseInt(val);
                maxAge = Math.max(-1, i);
            } catch (NumberFormatException e) {
                String msg = "Configured " + ACCOUNT_COOKIE_MAX_AGE + " value must be an integer.";
                throw new IllegalArgumentException(msg, e);
            }
        } else {
            maxAge = Integer.MIN_VALUE;
        }

        final boolean httpOnly = !"false".equalsIgnoreCase(props.get(ACCOUNT_COOKIE_HTTP_ONLY));
        final boolean secure = !"false".equalsIgnoreCase(props.get(ACCOUNT_COOKIE_SECURE));

        this.ACCOUNT_COOKIE_CONFIG = new CookieConfig() {
            @Override
            public String getName() {
                return props.get(ACCOUNT_COOKIE_NAME);
            }

            @Override
            public String getComment() {
                return props.get(ACCOUNT_COOKIE_COMMENT);
            }

            @Override
            public String getDomain() {
                return props.get(ACCOUNT_COOKIE_DOMAIN);
            }

            @Override
            public int getMaxAge() {
                return maxAge;
            }

            @Override
            public String getPath() {
                return props.get(ACCOUNT_COOKIE_PATH);
            }

            @Override
            public boolean isSecure() {
                return secure;
            }

            @Override
            public boolean isHttpOnly() {
                return httpOnly;
            }
        };
    }

    @Override
    public String getLoginUrl() {
        return props.get(LOGIN_URL);
    }

    @Override
    public String getLoginNextUrl() {
        return props.get(LOGIN_NEXT_URL);
    }

    @Override
    public String getLogoutUrl() {
        return props.get(LOGOUT_URL);
    }

    @Override
    public String getLogoutNextUrl() {
        return props.get(LOGOUT_NEXT_URL);
    }

    @Override
    public String getRegisterUrl() {
        return props.get(REGISTER_URL);
    }

    @Override
    public String getRegisterNextUrl() {
        return props.get(REGISTER_NEXT_URL);
    }

    @Override
    public String getVerifyUrl() {
        return props.get(VERIFY_URL);
    }

    @Override
    public String getVerifyNextUrl() {
        return props.get(VERIFY_NEXT_URL);
    }

    @Override
    public String getUnauthorizedUrl() {
        return props.get(UNAUTHORIZED_URL);
    }

    @Override
    public CookieConfig getAccountCookieConfig() {
        return this.ACCOUNT_COOKIE_CONFIG;
    }

    @Override
    public int size() {
        return props.size();
    }

    @Override
    public boolean isEmpty() {
        return props.isEmpty();
    }

    @Override
    public boolean containsKey(Object o) {
        return props.containsKey(o);
    }

    @Override
    public boolean containsValue(Object o) {
        return props.containsValue(o);
    }

    @Override
    public String get(Object o) {
        return props.get(o);
    }

    @Override
    public String put(String s, String s2) {
        return props.put(s, s2);
    }

    @Override
    public String remove(Object o) {
        return props.remove(o);
    }

    @Override
    public void putAll(Map<? extends String, ? extends String> map) {
        props.putAll(map);
    }

    @Override
    public void clear() {
        props.clear();
    }

    @Override
    public Set<String> keySet() {
        return props.keySet();
    }

    @Override
    public Collection<String> values() {
        return props.values();
    }

    @Override
    public Set<Entry<String, String>> entrySet() {
        return props.entrySet();
    }
}
