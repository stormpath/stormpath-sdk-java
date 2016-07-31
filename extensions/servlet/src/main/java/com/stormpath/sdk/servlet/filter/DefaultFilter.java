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

import com.stormpath.sdk.servlet.config.ConfigSingletonFactory;
import com.stormpath.sdk.servlet.config.filter.AccessTokenFilterFactory;
import com.stormpath.sdk.servlet.config.filter.AccountAuthorizationFilterFactory;
import com.stormpath.sdk.servlet.config.filter.AuthenticationFilterFactory;
import com.stormpath.sdk.servlet.config.filter.ChangePasswordFilterFactory;
import com.stormpath.sdk.servlet.config.filter.FacebookCallbackFilterFactory;
import com.stormpath.sdk.servlet.config.filter.ForgotPasswordFilterFactory;
import com.stormpath.sdk.servlet.config.filter.GithubCallbackFilterFactory;
import com.stormpath.sdk.servlet.config.filter.GoogleCallbackFilterFactory;
import com.stormpath.sdk.servlet.config.filter.IDSiteForgotFilterFactory;
import com.stormpath.sdk.servlet.config.filter.IDSiteLoginFilterFactory;
import com.stormpath.sdk.servlet.config.filter.IDSiteRegisterFilterFactory;
import com.stormpath.sdk.servlet.config.filter.IdSiteLogoutFilterFactory;
import com.stormpath.sdk.servlet.config.filter.IDSiteResultFilterFactory;
import com.stormpath.sdk.servlet.config.filter.LinkedInCallbackFilterFactory;
import com.stormpath.sdk.servlet.config.filter.LoginFilterFactory;
import com.stormpath.sdk.servlet.config.filter.LogoutFilterFactory;
import com.stormpath.sdk.servlet.config.filter.MeFilterFactory;
import com.stormpath.sdk.servlet.config.filter.RegisterFilterFactory;
import com.stormpath.sdk.servlet.config.filter.SamlFilterFactory;
import com.stormpath.sdk.servlet.config.filter.SamlResultFilterFactory;
import com.stormpath.sdk.servlet.config.filter.VerifyFilterFactory;
import com.stormpath.sdk.servlet.filter.account.AccountAuthorizationFilter;
import com.stormpath.sdk.servlet.filter.mvc.ControllerFilter;

import javax.servlet.Filter;

/**
 * Enum representing all of the default Stormpath Filter instances available to web applications.
 *
 * @since 1.0.RC3
 */
public enum DefaultFilter {

    accessToken(ControllerFilter.class, AccessTokenFilterFactory.class),
    account(AccountAuthorizationFilter.class, AccountAuthorizationFilterFactory.class),
    anon(AnonymousFilter.class, null),
    authc(AuthenticationFilter.class, AuthenticationFilterFactory.class),
    change(ControllerFilter.class, ChangePasswordFilterFactory.class),
    facebookCallback(ControllerFilter.class, FacebookCallbackFilterFactory.class),
    forgot(ControllerFilter.class, ForgotPasswordFilterFactory.class),
    githubCallback(ControllerFilter.class, GithubCallbackFilterFactory.class),
    googleCallback(ControllerFilter.class, GoogleCallbackFilterFactory.class),
    linkedinCallback(ControllerFilter.class, LinkedInCallbackFilterFactory.class),
    login(ControllerFilter.class, LoginFilterFactory.class),
    logout(ControllerFilter.class, LogoutFilterFactory.class),
    me(MeFilter.class, MeFilterFactory.class),
    register(ControllerFilter.class, RegisterFilterFactory.class),
    saml(ControllerFilter.class, SamlFilterFactory.class),
    samlResult(ControllerFilter.class, SamlResultFilterFactory.class),
    unauthorized(UnauthorizedFilter.class, null),
    verify(ControllerFilter.class, VerifyFilterFactory.class),
    idSite(ControllerFilter.class, IDSiteLoginFilterFactory.class),
    idSiteResult(ControllerFilter.class, IDSiteResultFilterFactory.class),
    idSiteLogout(ControllerFilter.class, IdSiteLogoutFilterFactory.class),
    idSiteRegister(ControllerFilter.class, IDSiteRegisterFilterFactory.class),
    idSiteForgot(ControllerFilter.class, IDSiteForgotFilterFactory.class);

    private final Class<? extends Filter> filterClass;

    private final Class<? extends ConfigSingletonFactory<? extends Filter>> factoryClass;

    DefaultFilter(Class<? extends Filter> filterClass, Class<? extends ConfigSingletonFactory<? extends Filter>> factoryClass) {
        this.filterClass = filterClass;
        this.factoryClass = factoryClass;
    }

    public Class<? extends Filter> getFilterClass() {
        return this.filterClass;
    }

    public Class<? extends ConfigSingletonFactory<? extends Filter>> getFactoryClass() {
        return factoryClass;
    }

    public static DefaultFilter forName(String name) {
        for (DefaultFilter filter : values()) {
            if (filter.name().equalsIgnoreCase(name)) {
                return filter;
            }
        }

        String msg = "There is no default filter available with name '" + name + "'.";
        throw new IllegalArgumentException(msg);
    }
}
