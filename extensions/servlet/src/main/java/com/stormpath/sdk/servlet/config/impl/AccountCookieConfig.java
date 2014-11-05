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
import com.stormpath.sdk.servlet.config.CookieConfig;

public class AccountCookieConfig implements CookieConfig {

    private static final String NAME = DefaultConfig.ACCOUNT_COOKIE_NAME;
    private static final String COMMENT = DefaultConfig.ACCOUNT_COOKIE_COMMENT;
    private static final String DOMAIN = DefaultConfig.ACCOUNT_COOKIE_DOMAIN;
    private static final String MAX_AGE = DefaultConfig.ACCOUNT_COOKIE_MAX_AGE;
    private static final String PATH = DefaultConfig.ACCOUNT_COOKIE_PATH;
    private static final String SECURE = DefaultConfig.ACCOUNT_COOKIE_SECURE;
    private static final String HTTP_ONLY = DefaultConfig.ACCOUNT_COOKIE_HTTP_ONLY;

    private final String name;
    private final String comment;
    private final String domain;
    private final int maxAge;
    private final String path;
    private final boolean secure;
    private final boolean httpOnly;

    public AccountCookieConfig(ConfigReader configReader) {
        Assert.notNull(configReader);
        this.name = configReader.getString(NAME);
        this.comment = configReader.getString(COMMENT);
        this.domain = configReader.getString(DOMAIN);
        this.path = configReader.getString(PATH);
        this.secure = configReader.getBoolean(SECURE);
        this.httpOnly = configReader.getBoolean(HTTP_ONLY);
        this.maxAge = configReader.getInt(MAX_AGE);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getComment() {
        return comment;
    }

    @Override
    public String getDomain() {
        return domain;
    }

    @Override
    public int getMaxAge() {
        return maxAge;
    }

    @Override
    public String getPath() {
        return path;
    }

    @Override
    public boolean isSecure() {
        return secure;
    }

    @Override
    public boolean isHttpOnly() {
        return httpOnly;
    }
}
