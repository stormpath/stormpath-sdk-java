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
package com.stormpath.sdk.servlet.config.impl;

import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.lang.Classes;
import com.stormpath.sdk.servlet.config.Config;
import com.stormpath.sdk.servlet.config.CookieConfig;
import com.stormpath.sdk.servlet.config.Factory;
import com.stormpath.sdk.servlet.config.ImplementationClassResolver;
import com.stormpath.sdk.servlet.util.ServletContextInitializable;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * @since 1.0.RC3
 */
public class DefaultConfig implements Config {

    public static final String LOGIN_URL = "stormpath.web.login.uri";
    public static final String LOGIN_NEXT_URL = "stormpath.web.login.nextUri";
    public static final String LOGOUT_URL = "stormpath.web.logout.uri";
    public static final String LOGOUT_NEXT_URL = "stormpath.web.logout.nextUri";
    public static final String LOGOUT_INVALIDATE_HTTP_SESSION = "stormpath.web.logout.invalidateHttpSession";
    public static final String FORGOT_PASSWORD_URL = "stormpath.web.forgot.uri";
    public static final String FORGOT_PASSWORD_NEXT_URL = "stormpath.web.forgot.nextUri";
    public static final String CHANGE_PASSWORD_URL = "stormpath.web.change.uri";
    public static final String CHANGE_PASSWORD_NEXT_URL = "stormpath.web.change.nextUri";
    public static final String REGISTER_URL = "stormpath.web.register.uri";
    public static final String REGISTER_NEXT_URL = "stormpath.web.register.nextUri";
    public static final String VERIFY_URL = "stormpath.web.verify.uri";
    public static final String VERIFY_NEXT_URL = "stormpath.web.verify.nextUri";
    public static final String SEND_VERIFICATION_EMAIL_URL = "stormpath.web.sendVerificationEmail.uri";
    public static final String VERIFY_ENABLED = "stormpath.web.verify.enabled";

    public static final String UNAUTHORIZED_URL = "stormpath.web.unauthorized.uri";

    public static final String ACCESS_TOKEN_URL = "stormpath.web.accessToken.uri";
    public static final String ACCOUNT_COOKIE_NAME = "stormpath.web.account.cookie.name";
    public static final String ACCOUNT_COOKIE_COMMENT = "stormpath.web.account.cookie.comment";
    public static final String ACCOUNT_COOKIE_DOMAIN = "stormpath.web.account.cookie.domain";
    public static final String ACCOUNT_COOKIE_MAX_AGE = "stormpath.web.account.cookie.maxAge";
    public static final String ACCOUNT_COOKIE_PATH = "stormpath.web.account.cookie.path";
    public static final String ACCOUNT_COOKIE_HTTP_ONLY = "stormpath.web.account.cookie.httpOnly";
    public static final String ACCOUNT_JWT_TTL = "stormpath.web.account.jwt.ttl";

    private final ServletContext servletContext;
    private final ConfigReader CFG;
    private final Map<String, String> props;

    private final CookieConfig ACCOUNT_COOKIE_CONFIG;
    private final long _ACCOUNT_JWT_TTL;

    private final Map<String, Object> SINGLETONS;

    public DefaultConfig(final ServletContext servletContext, Map<String, String> configProps) {
        Assert.notNull(servletContext, "servletContext argument cannot be null.");
        Assert.notNull(configProps, "Properties argument cannot be null.");
        this.servletContext = servletContext;
        this.props = Collections.unmodifiableMap(configProps);
        this.CFG = new ExpressionConfigReader(servletContext, this.props);
        this.SINGLETONS = new LinkedHashMap<String, Object>();

        this.ACCOUNT_COOKIE_CONFIG = new AccountCookieConfig(CFG);

        long accountJwtTtl = CFG.getLong(ACCOUNT_JWT_TTL);
        Assert.isTrue(accountJwtTtl > 0, ACCOUNT_JWT_TTL + " value must be a positive long.");

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
    public boolean isLogoutInvalidateHttpSession() {
        return CFG.getBoolean(LOGOUT_INVALIDATE_HTTP_SESSION);
    }

    @Override
    public String getForgotPasswordUrl() {
        return CFG.getString(FORGOT_PASSWORD_URL);
    }

    @Override
    public String getForgotPasswordNextUrl() {
        return CFG.getString(FORGOT_PASSWORD_NEXT_URL);
    }

    @Override
    public String getChangePasswordUrl() {
        return CFG.getString(CHANGE_PASSWORD_URL);
    }

    @Override
    public String getChangePasswordNextUrl() {
        return CFG.getString(CHANGE_PASSWORD_NEXT_URL);
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
    public String getSendVerificationEmailUrl() {
        return CFG.getString(SEND_VERIFICATION_EMAIL_URL);
    }

    @Override
    public boolean isVerifyEnabled() {
        return CFG.getBoolean(VERIFY_ENABLED);
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
    public CookieConfig getAccountCookieConfig() {
        return this.ACCOUNT_COOKIE_CONFIG;
    }

    @Override
    public long getAccountJwtTtl() {
        return _ACCOUNT_JWT_TTL;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getInstance(String classPropertyName) throws ServletException {
        T instance = (T) SINGLETONS.get(classPropertyName);
        if (instance == null) {
            instance = newInstance(classPropertyName);
            SINGLETONS.put(classPropertyName, instance);
        }

        return instance;
    }

    @SuppressWarnings("unchecked")
    public <T> T getInstance(String classPropertyName, Class<T> expectedType) throws ServletException {
        Assert.notNull(expectedType, "expectedType argument cannot be null.");

        T instance = (T) SINGLETONS.get(classPropertyName);
        if (instance == null) {
            instance = newInstance(classPropertyName);
            SINGLETONS.put(classPropertyName, instance);
        }

        if (!expectedType.isInstance(instance)) {
            String msg = "Configured " + classPropertyName + " class name must be an instance of " +
                         expectedType.getName();
            throw new ServletException(msg);
        }

        return instance;
    }

    @Override
    public <T> Map<String, T> getInstances(String propertyNamePrefix, Class<T> expectedType) throws ServletException {
        Map<String,Class<T>> classes =
            new ImplementationClassResolver<T>(this, propertyNamePrefix, expectedType).findImplementationClasses();

        Map<String,T> instances = new LinkedHashMap<String, T>(classes.size());

        for(Map.Entry<String,Class<T>> entry : classes.entrySet()) {

            String name = entry.getKey();

            T instance = getInstance(propertyNamePrefix + name);
            Assert.isInstanceOf(expectedType, instance);

            instances.put(name, instance);
        }

        return instances;
    }

    @SuppressWarnings("unchecked")
    protected <T> T newInstance(String classPropertyName) throws ServletException {

        if (!containsKey(classPropertyName)) {
            String msg = "Unable to instantiate class: there is no configuration property named " + classPropertyName;
            throw new ServletException(msg);
        }

        String val = get(classPropertyName);

        Assert.hasText(val, classPropertyName + " class name value is required.");

        T instance;
        try {
            instance = Classes.newInstance(val);
        } catch (Exception e) {
            String msg = "Unable to instantiate " + classPropertyName + " class name " +
                         val + ": " + e.getMessage();
            throw new ServletException(msg, e);
        }

        if (instance instanceof ServletContextInitializable) {
            try {
                ((ServletContextInitializable) instance).init(this.servletContext);
            } catch (Exception e) {
                String msg = "Unable to initialize " + classPropertyName + " instance of type " +
                             val + ": " + e.getMessage();
                throw new ServletException(msg, e);
            }
        }

        try {
            if (instance instanceof Factory) {
                instance = ((Factory<T>)instance).getInstance();
            }
        } catch (Exception e) {
            String msg = "Unable to obtain factory instance from factory " + instance + ": " + e.getMessage();
            throw new ServletException(msg);
        }

        return instance;
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
