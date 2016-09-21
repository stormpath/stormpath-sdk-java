/*
 * Copyright 2016 Stormpath, Inc.
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
package com.stormpath.spring.config;

import com.stormpath.sdk.servlet.config.CookieProperties;
import org.springframework.beans.factory.annotation.Value;

/**
 * @since 1.0.RC10
 */
public class AccessTokenCookieProperties implements CookieProperties {
    @Value("#{ @environment['stormpath.web.accessTokenCookie.name'] ?: 'access_token' }")
    protected String cookieName;

    @Value("#{ @environment['stormpath.web.accessTokenCookie.domain'] }")
    protected String cookieDomain;

    @Value("#{ @environment['stormpath.web.accessTokenCookie.path'] }")
    protected String cookiePath;

    @Value("#{ @environment['stormpath.web.accessTokenCookie.httpOnly'] ?: true }")
    protected boolean cookieHttpOnly;

    @Value("#{ @environment['stormpath.web.accessTokenCookie.secure'] ?: true }")
    protected boolean cookieSecure;

    @Override
    public String getCookieName() {
        return cookieName;
    }

    @Override
    public String getCookieComment() {
        return null;
    }

    @Override
    public String getCookieDomain() {
        return cookieDomain;
    }

    @Override
    public String getCookiePath() {
        return cookiePath;
    }

    @Override
    public boolean isCookieHttpOnly() {
        return cookieHttpOnly;
    }

    @Override
    public boolean isCookieSecure() {
        return cookieSecure;
    }

    @Override
    public int getCookieMaxAge() {
        return 0;
    }
}
