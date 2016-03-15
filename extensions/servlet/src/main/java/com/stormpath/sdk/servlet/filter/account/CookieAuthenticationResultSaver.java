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
package com.stormpath.sdk.servlet.filter.account;

import com.stormpath.sdk.authc.AuthenticationResult;
import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.lang.Strings;
import com.stormpath.sdk.oauth.AccessTokenResult;
import com.stormpath.sdk.servlet.config.CookieConfig;
import com.stormpath.sdk.servlet.http.CookieSaver;
import com.stormpath.sdk.servlet.http.Resolver;
import com.stormpath.sdk.servlet.http.Saver;
import com.stormpath.sdk.servlet.util.SecureRequiredExceptForLocalhostResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @since 1.0.RC3
 */
public class CookieAuthenticationResultSaver extends AccountCookieHandler implements Saver<AuthenticationResult> {

    private static final Logger log = LoggerFactory.getLogger(CookieAuthenticationResultSaver.class);

    private AuthenticationJwtFactory authenticationJwtFactory;
    private Resolver<Boolean> secureCookieRequired;

    private boolean secureWarned = false;

    public CookieAuthenticationResultSaver(CookieConfig accountCookieConfig,
                                           Resolver<Boolean> secureCookieRequired,
                                           AuthenticationJwtFactory authenticationJwtFactory) {
        super(accountCookieConfig);
        Assert.notNull(secureCookieRequired, "secureCookieRequired Resolver cannot be null.");
        Assert.notNull(authenticationJwtFactory, "AuthenticationJwtFactory cannot be null.");
        this.secureCookieRequired = secureCookieRequired;
        this.authenticationJwtFactory = authenticationJwtFactory;
    }

    public Resolver<Boolean> getSecureCookieRequired() {
        return secureCookieRequired;
    }

    public AuthenticationJwtFactory getAuthenticationJwtFactory() {
        return authenticationJwtFactory;
    }

    @Override
    public void set(HttpServletRequest request, HttpServletResponse response, AuthenticationResult value) {

        if (value == null) {
            remove(request, response);
            return;
        }

        String jwt;

        if (value instanceof AccessTokenResult) {
            jwt = ((AccessTokenResult) value).getTokenResponse().getAccessToken();
        } else {
            jwt = getAuthenticationJwtFactory().createAccountJwt(request, response, value);
        }

        Saver<String> saver = getCookieSaver(request);

        saver.set(request, response, jwt);
    }

    protected void remove(HttpServletRequest request, HttpServletResponse response) {
        Saver<String> saver = getCookieSaver(request);
        saver.set(request, response, null);
    }

    protected Saver<String> getCookieSaver(HttpServletRequest request) {
        CookieConfig cfg = getAccountCookieConfig(request);
        return new CookieSaver(cfg);
    }

    protected boolean isCookieSecure(final HttpServletRequest request, CookieConfig config) {

        boolean configSecure = config.isSecure();

        Resolver<Boolean> resolver = getSecureCookieRequired();

        boolean resolverSecure = resolver.get(request, null);

        boolean likelyLocalhost = resolver instanceof SecureRequiredExceptForLocalhostResolver;

        boolean warnable = !configSecure || (!resolverSecure && !likelyLocalhost);

        if (!secureWarned && warnable) {
            secureWarned = true;
            String msg = "INSECURE IDENTITY COOKIE CONFIGURATION: Your current Stormpath SDK account cookie " +
                    "configuration allows insecure identity cookies (transmission over non-HTTPS connections)!  " +
                    "This should never occur on staging/production machines otherwise your users will be " +
                    "susceptible to man-in-the-middle attacks.  If you see this warning on a development-only " +
                    "machine, you can likely safely ignore this message.  For more information, please " +
                    "see the Security Notice here: " +
                    "https://docs.stormpath.com/java/servlet-plugin/login.html#security-notice";
            log.warn(msg);
        }

        return configSecure && resolverSecure;
    }

    @Override
    protected CookieConfig getAccountCookieConfig(final HttpServletRequest request) {

        final CookieConfig config = super.getAccountCookieConfig(request);

        final boolean secure = isCookieSecure(request, config);

        String path = Strings.clean(config.getPath());
        if (!Strings.hasText(path)) {
            path = Strings.clean(request.getContextPath());
        }
        if (!Strings.hasText(path)) {
            path = "/";
        }

        final String PATH = path;

        //wrap it to allow for access during development:
        return new CookieConfig() {
            @Override
            public String getName() {
                return config.getName();
            }

            @Override
            public String getComment() {
                return config.getComment();
            }

            @Override
            public String getDomain() {
                return config.getDomain();
            }

            @Override
            public int getMaxAge() {
                return config.getMaxAge();
            }

            @Override
            public String getPath() {
                return PATH;
            }

            @Override
            public boolean isSecure() {
                return secure;
            }

            @Override
            public boolean isHttpOnly() {
                return config.isHttpOnly();
            }
        };
    }
}
