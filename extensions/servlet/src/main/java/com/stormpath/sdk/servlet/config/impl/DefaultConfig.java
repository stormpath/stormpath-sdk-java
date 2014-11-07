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
package com.stormpath.sdk.servlet.config.impl;

import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.lang.Strings;
import com.stormpath.sdk.servlet.config.Config;
import com.stormpath.sdk.servlet.config.CookieConfig;

import javax.servlet.ServletContext;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
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
    public static final String ACCESS_TOKEN_URL = "stormpath.web.accessToken.url";

    public static final String ACCOUNT_DISCOVERY = "stormpath.web.account.discovery";
    public static final String ACCOUNT_STATE_STORE_LOCATIONS = "stormpath.web.account.state.store.locations";

    public static final String ACCOUNT_COOKIE_NAME = "stormpath.web.account.cookie.name";
    public static final String ACCOUNT_COOKIE_COMMENT = "stormpath.web.account.cookie.comment";
    public static final String ACCOUNT_COOKIE_DOMAIN = "stormpath.web.account.cookie.domain";
    public static final String ACCOUNT_COOKIE_MAX_AGE = "stormpath.web.account.cookie.maxAge";
    public static final String ACCOUNT_COOKIE_PATH = "stormpath.web.account.cookie.path";
    public static final String ACCOUNT_COOKIE_SECURE = "stormpath.web.account.cookie.secure";
    public static final String ACCOUNT_COOKIE_HTTP_ONLY = "stormpath.web.account.cookie.httpOnly";
    public static final String ACCOUNT_JWT_TTL = "stormpath.web.account.jwt.ttl";

    private final ConfigReader CFG;
    private final Map<String, String> props;

    private final List<String> ACCOUNT_DISCOVERY_LOCATIONS;
    private final List<String> ACCOUNT_STORE_LOCATIONS;
    private final CookieConfig ACCOUNT_COOKIE_CONFIG;
    private final int _ACCOUNT_JWT_TTL;


    public DefaultConfig(final ServletContext servletContext, Map<String, String> configProps) {
        Assert.notNull(servletContext, "servletContext argument cannot be null.");
        Assert.notNull(configProps, "Properties argument cannot be null.");
        this.props = Collections.unmodifiableMap(configProps);
        this.CFG = new ExpressionConfigReader(servletContext, this.props);

        this.ACCOUNT_COOKIE_CONFIG = new AccountCookieConfig(CFG);

        String val = CFG.getString(ACCOUNT_DISCOVERY);
        //String val = props.get(ACCOUNT_DISCOVERY);
        if (Strings.hasText(val)) {
            String[] locs = Strings.split(val);
            ACCOUNT_DISCOVERY_LOCATIONS = Arrays.asList(locs);
        } else {
            ACCOUNT_DISCOVERY_LOCATIONS = Collections.emptyList();
        }

        val = CFG.getString(ACCOUNT_STATE_STORE_LOCATIONS);
        //val = props.get(ACCOUNT_STATE_STORE_LOCATIONS);
        if (Strings.hasText(val)) {
            String[] locs = Strings.split(val);
            ACCOUNT_STORE_LOCATIONS = Arrays.asList(locs);
        } else {
            ACCOUNT_STORE_LOCATIONS = Collections.emptyList();
        }

        int accountJwtTtl = CFG.getInt(ACCOUNT_JWT_TTL);
        Assert.isTrue(accountJwtTtl > 0, ACCOUNT_JWT_TTL + " value must be a positive integer.");

        int accountCookieMaxAge = this.ACCOUNT_COOKIE_CONFIG.getMaxAge();

        if (accountCookieMaxAge > 0 && accountCookieMaxAge > accountJwtTtl) {
            String msg = ACCOUNT_JWT_TTL + " must be greater than or equal to " + ACCOUNT_COOKIE_MAX_AGE;
            throw new IllegalArgumentException(msg);
        }

        this._ACCOUNT_JWT_TTL = accountJwtTtl;
    }

    @Override
    public String getLoginUrl() {
        return CFG.getString(LOGIN_URL);
    }

    @Override
    public String getLoginNextUrl() {
        return CFG.getString(LOGIN_NEXT_URL);
    }

    @Override
    public String getLogoutUrl() {
        return CFG.getString(LOGOUT_URL);
    }

    @Override
    public String getLogoutNextUrl() {
        return CFG.getString(LOGOUT_NEXT_URL);
    }

    @Override
    public String getRegisterUrl() {
        return CFG.getString(REGISTER_URL);
    }

    @Override
    public String getRegisterNextUrl() {
        return CFG.getString(REGISTER_NEXT_URL);
    }

    @Override
    public String getVerifyUrl() {
        return CFG.getString(VERIFY_URL);
    }

    @Override
    public String getVerifyNextUrl() {
        return CFG.getString(VERIFY_NEXT_URL);
    }

    @Override
    public String getAccessTokenUrl() {
        return CFG.getString(ACCESS_TOKEN_URL);
    }

    @Override
    public String getUnauthorizedUrl() {
        return CFG.getString(UNAUTHORIZED_URL);
    }

    @Override
    public List<String> getAccountDiscovery() {
        return ACCOUNT_DISCOVERY_LOCATIONS;
    }

    @Override
    public List<String> getAccountStoreLocations() {
        return ACCOUNT_STORE_LOCATIONS;
    }

    @Override
    public CookieConfig getAccountCookieConfig() {
        return this.ACCOUNT_COOKIE_CONFIG;
    }

    @Override
    public int getAccountJwtTtl() {
        return _ACCOUNT_JWT_TTL;
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
