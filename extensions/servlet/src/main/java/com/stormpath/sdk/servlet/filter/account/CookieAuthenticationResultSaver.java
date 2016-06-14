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

import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.application.Application;
import com.stormpath.sdk.authc.AuthenticationResult;
import com.stormpath.sdk.client.Client;
import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.lang.Strings;
import com.stormpath.sdk.oauth.AccessTokenResult;
import com.stormpath.sdk.oauth.Authenticators;
import com.stormpath.sdk.oauth.OAuthGrantRequestAuthenticationResult;
import com.stormpath.sdk.oauth.OAuthPolicy;
import com.stormpath.sdk.oauth.OAuthRequestAuthentication;
import com.stormpath.sdk.oauth.OAuthRequests;
import com.stormpath.sdk.servlet.application.ApplicationResolver;
import com.stormpath.sdk.servlet.authc.impl.TransientAuthenticationResult;
import com.stormpath.sdk.servlet.client.ClientResolver;
import com.stormpath.sdk.servlet.config.CookieConfig;
import com.stormpath.sdk.servlet.config.impl.AccessTokenCookieConfig;
import com.stormpath.sdk.servlet.config.impl.RefreshTokenCookieConfig;
import com.stormpath.sdk.servlet.http.CookieSaver;
import com.stormpath.sdk.servlet.http.Resolver;
import com.stormpath.sdk.servlet.http.Saver;
import com.stormpath.sdk.servlet.util.SecureRequiredExceptForLocalhostResolver;
import io.jsonwebtoken.JwsHeader;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.joda.time.DateTime;
import org.joda.time.Period;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.util.Date;

/**
 * @since 1.0.RC3
 */
public class CookieAuthenticationResultSaver implements Saver<AuthenticationResult> {

    private static final Logger log = LoggerFactory.getLogger(CookieAuthenticationResultSaver.class);

    private static final int DEFAULT_COOKIE_MAX_AGE = 259200;

    private Resolver<Boolean> secureCookieRequired;

    private boolean secureWarned = false;

    private final CookieConfig accessTokenCookieConfig;
    private final CookieConfig refreshTokenCookieConfig;

    public CookieAuthenticationResultSaver(CookieConfig accessTokenCookieConfig,
                                           CookieConfig refreshTokenCookieConfig,
                                           Resolver<Boolean> secureCookieRequired) {
        Assert.notNull(accessTokenCookieConfig, "accessTokenCookieConfig cannot be null.");
        Assert.notNull(refreshTokenCookieConfig, "refreshTokenCookieConfig cannot be null.");
        Assert.notNull(secureCookieRequired, "secureCookieRequired cannot be null.");
        this.accessTokenCookieConfig = accessTokenCookieConfig;
        this.refreshTokenCookieConfig = refreshTokenCookieConfig;
        this.secureCookieRequired = secureCookieRequired;
    }

    @Override
    public void set(HttpServletRequest request, HttpServletResponse response, AuthenticationResult value) {

        if (value == null) {
            remove(request, response);
            return;
        }

        if (value instanceof AccessTokenResult) {
            AccessTokenResult accessTokenResult = (AccessTokenResult) value;

            getAccessTokenCookieSaver(request).set(request, response, accessTokenResult.getTokenResponse().getAccessToken());
            getRefreshTokenCookieSaver(request).set(request, response, accessTokenResult.getTokenResponse().getRefreshToken());
        }
        if (value instanceof TransientAuthenticationResult) {
            Account account = value.getAccount();

            Client client = ClientResolver.INSTANCE.getClient(request);
            Application application = ApplicationResolver.INSTANCE.getApplication(request);

            //Since we only have the account we need to exchange it for an OAuth2 token
            try {
                //code copied from AccessTokenController#clientCredentialsAuthenticationRequest
                String jwt = Jwts.builder()
                        .setHeaderParam(JwsHeader.KEY_ID, client.getApiKey().getId())
                        .setSubject(account.getHref())
                        .setIssuedAt(new Date())
                        .setIssuer(application.getHref())
                        .setAudience(client.getApiKey().getId())
                        .setExpiration(DateTime.now().plusMinutes(1).toDate())
                        .claim("status", "AUTHENTICATED").signWith(SignatureAlgorithm.HS256, client.getApiKey().getSecret().getBytes("UTF-8")).compact();

                OAuthRequestAuthentication authenticationRequest = OAuthRequests.IDSITE_AUTHENTICATION_REQUEST.builder().setToken(jwt).build();
                OAuthGrantRequestAuthenticationResult authenticationResult = Authenticators.ID_SITE_AUTHENTICATOR.forApplication(application).authenticate(authenticationRequest);

                getAccessTokenCookieSaver(request).set(request, response, authenticationResult.getAccessTokenString());
                getRefreshTokenCookieSaver(request).set(request, response, authenticationResult.getRefreshTokenString());
            } catch (UnsupportedEncodingException e) {
                //Should not happen since UTF-8 should always be a supported encoding, but we logged just in case
                log.error("Error get the client API Secret", e);
            }
        }
    }

    protected void remove(HttpServletRequest request, HttpServletResponse response) {
        getAccessTokenCookieSaver(request).set(request, response, null);
        getRefreshTokenCookieSaver(request).set(request, response, null);
    }

    protected boolean isCookieSecure(final HttpServletRequest request, CookieConfig config) {

        boolean configSecure = config.isSecure();

        Resolver<Boolean> resolver = secureCookieRequired;

        boolean resolverSecure = resolver.get(request, null);

        boolean likelyLocalhost = resolver instanceof SecureRequiredExceptForLocalhostResolver;

        boolean warnable = !configSecure || (!resolverSecure && !likelyLocalhost);

        if (!secureWarned && warnable) {
            secureWarned = true;
            String msg = "INSECURE IDENTITY COOKIE CONFIGURATION: Your current Stormpath SDK account cookie " +
                    "configuration allows insecure identity cookies (transmission over non-HTTPS connections)!  " +
                    "This should typically never occur otherwise your users will be " +
                    "susceptible to man-in-the-middle attacks.  For more information in Servlet-only " +
                    "environments, please see the Security Notice here: " +
                    "https://docs.stormpath.com/java/servlet-plugin/login.html#https-required and the " +
                    "documentation on authentication state here: " +
                    "https://docs.stormpath.com/java/servlet-plugin/login.html#authentication-state and here: " +
                    "https://docs.stormpath.com/java/servlet-plugin/login.html#cookie-config (the " +
                    "callout entitled 'Secure Cookies').  If you are using Spring Boot, Spring Boot-specific " +
                    "documentation for these concepts are here: " +
                    "https://docs.stormpath.com/java/spring-boot-web/login.html#security-notice " +
                    "https://docs.stormpath.com/java/spring-boot-web/login.html#authentication-state and " +
                    "https://docs.stormpath.com/java/spring-boot-web/login.html#cookie-storage";
            log.warn(msg);
        }

        return configSecure && resolverSecure;
    }

    private CookieSaver getRefreshTokenCookieSaver(final HttpServletRequest request) {
        return getCookieSaver(refreshTokenCookieConfig, request);
    }

    private CookieSaver getAccessTokenCookieSaver(final HttpServletRequest request) {
        return getCookieSaver(accessTokenCookieConfig, request);
    }

    private CookieSaver getCookieSaver(final CookieConfig cookieConfig, final HttpServletRequest request) {
        final boolean secure = isCookieSecure(request, cookieConfig);

        String path = Strings.clean(cookieConfig.getPath());
        if (!Strings.hasText(path)) {
            path = Strings.clean(request.getContextPath());
        }
        if (!Strings.hasText(path)) {
            path = "/";
        }

        final String PATH = path;

        //wrap it to allow for access during development:
        return new CookieSaver(new CookieConfig() {
            @Override
            public String getName() {
                return cookieConfig.getName();
            }

            @Override
            public String getComment() {
                return cookieConfig.getComment();
            }

            @Override
            public String getDomain() {
                return cookieConfig.getDomain();
            }

            @Override
            public int getMaxAge() {
                return getCookieMaxAge(cookieConfig, ApplicationResolver.INSTANCE.getApplication(request));
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
                return cookieConfig.isHttpOnly();
            }
        });
    }

    private int getCookieMaxAge(CookieConfig cookieConfig, Application application) {
        OAuthPolicy oauthPolicy = application.getOAuthPolicy();

        if (cookieConfig instanceof AccessTokenCookieConfig) {
            return Period.parse(oauthPolicy.getAccessTokenTtl()).toStandardSeconds().getSeconds();
        } else if (cookieConfig instanceof RefreshTokenCookieConfig) {
            return Period.parse(oauthPolicy.getRefreshTokenTtl()).toStandardSeconds().getSeconds();
        }

        //We currently only have those 2 cookies but just in case we add a new one we default to the max value a cookie max age supports
        return DEFAULT_COOKIE_MAX_AGE;
    }
}
