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
package com.stormpath.sdk.servlet.filter;

import com.stormpath.sdk.servlet.filter.account.AccountAuthorizationFilter;
import com.stormpath.sdk.servlet.filter.oauth.AccessTokenFilter;

import javax.servlet.Filter;

/**
 * Enum representing all of the default Stormpath Filter instances available to web applications.
 *
 * @since 1.0.RC3
 */
public enum DefaultFilter {

    accessToken(AccessTokenFilter.class),
    account(AccountAuthorizationFilter.class),
    anon(AnonymousFilter.class),
    authc(AuthenticationFilter.class),
    login(LoginFilter.class),
    logout(LogoutFilter.class),
    forgot(ForgotPasswordFilter.class),
    change(ChangePasswordFilter.class),
    register(RegisterFilter.class),
    unauthorized(UnauthorizedFilter.class),
    verify(VerifyFilter.class),
    sendVerificationEmail(SendVerificationEmailFilter.class),
    me(MeFilter.class);

    private final Class<? extends Filter> filterClass;

    private DefaultFilter(Class<? extends Filter> filterClass) {
        this.filterClass = filterClass;
    }

    public Class<? extends Filter> getFilterClass() {
        return this.filterClass;
    }

    public static DefaultFilter forName(String name) {
        for(DefaultFilter filter : values()) {
            if (filter.name().equalsIgnoreCase(name)) {
                return filter;
            }
        }

        String msg = "There is no default filter available with name '" + name + "'.";
        throw new IllegalArgumentException(msg);
    }
}
