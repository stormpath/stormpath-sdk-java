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

import com.stormpath.sdk.authc.AuthenticationResult;
import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.lang.Classes;
import com.stormpath.sdk.lang.Strings;
import com.stormpath.sdk.servlet.authz.RequestAuthorizer;
import com.stormpath.sdk.servlet.config.Config;
import com.stormpath.sdk.servlet.config.ConfigSingletonFactory;
import com.stormpath.sdk.servlet.config.CookieConfig;
import com.stormpath.sdk.servlet.config.Factory;
import com.stormpath.sdk.servlet.config.ImplementationClassResolver;
import com.stormpath.sdk.servlet.event.RequestEvent;
import com.stormpath.sdk.servlet.event.impl.Publisher;
import com.stormpath.sdk.servlet.filter.ControllerConfigResolver;
import com.stormpath.sdk.servlet.filter.ServerUriResolver;
import com.stormpath.sdk.servlet.filter.ServletControllerConfigResolver;
import com.stormpath.sdk.servlet.filter.UnauthenticatedHandler;
import com.stormpath.sdk.servlet.filter.UnauthorizedHandler;
import com.stormpath.sdk.servlet.filter.config.UnauthorizedHandlerFactory;
import com.stormpath.sdk.servlet.filter.oauth.AccessTokenAuthenticationRequestFactory;
import com.stormpath.sdk.servlet.filter.oauth.AccessTokenResultFactory;
import com.stormpath.sdk.servlet.filter.oauth.RefreshTokenAuthenticationRequestFactory;
import com.stormpath.sdk.servlet.filter.oauth.RefreshTokenResultFactory;
import com.stormpath.sdk.servlet.http.Resolver;
import com.stormpath.sdk.servlet.http.Saver;
import com.stormpath.sdk.servlet.http.authc.AccountStoreResolver;
import com.stormpath.sdk.servlet.http.authc.BasicAuthenticationScheme;
import com.stormpath.sdk.servlet.idsite.IdSiteOrganizationContext;
import com.stormpath.sdk.servlet.mvc.WebHandler;
import com.stormpath.sdk.servlet.util.ServletContextInitializable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @since 1.0.RC3
 */
public class DefaultConfig implements Config {

    private static final Logger log = LoggerFactory.getLogger(DefaultConfig.class);

    public static final String UNAUTHORIZED_URL = "stormpath.web.unauthorized.uri";
    public static final String LOGOUT_INVALIDATE_HTTP_SESSION = "stormpath.web.logout.invalidateHttpSession";
    public static final String ACCESS_TOKEN_URL = "stormpath.web.oauth2.uri";
    public static final String ACCESS_TOKEN_VALIDATION_STRATEGY = "stormpath.web.oauth2.password.validationStrategy";
    public static final String ACCESS_TOKEN_AUTHENTICATION_REQUEST_FACTORY =
            "stormpath.web.oauth2.authenticationRequestFactory";
    public static final String REFRESH_TOKEN_AUTHENTICATION_REQUEST_FACTORY =
            "stormpath.web.refreshToken.authenticationRequestFactory";

    public static final String ACCESS_TOKEN_RESULT_FACTORY = "stormpath.web.oauth2.resultFactory";
    public static final String REFRESH_TOKEN_RESULT_FACTORY = "stormpath.web.refreshToken.resultFactory";
    public static final String REQUEST_AUTHORIZER = "stormpath.web.oauth2.authorizer";
    public static final String ACCOUNT_SAVER = "stormpath.web.authc.saver";
    public static final String EVENT_PUBLISHER = "stormpath.web.request.event.publisher";
    public static final String BASIC_AUTHENTICATION_REQUEST_FACTORY = "stormpath.web.http.authc.schemes.basic";
    protected static final String UNAUTHENTICATED_HANDLER = "stormpath.web.authc.unauthenticatedHandler";
    protected static final String UNAUTHORIZED_HANDLER = "stormpath.web.authz.unauthorizedHandler";
    protected static final String SERVER_URI_RESOLVER = "stormpath.web.oauth2.origin.authorizer.serverUriResolver";
    protected static final String IDSITE_RESULT_URI = "stormpath.web.idSite.resultUri";
    protected static final String IDSITE_ORGANIZATION_RESOLVER_FACTORY = "stormpath.web.idSite.OrganizationResolverFactory";
    protected static final String IDSITE_LOGIN_URI = "stormpath.web.idSite.loginUri";
    protected static final String IDSITE_REGISTER_URI = "stormpath.web.idSite.registerUri";
    protected static final String IDSITE_FORGOT_URI = "stormpath.web.idSite.forgotUri";
    protected static final String IDSITE_CHANGE_URI = "stormpath.web.idSite.changeUri";

    public static final String WEB_APPLICATION_DOMAIN = "stormpath.web.application.domain";


    public static final String PRODUCES_MEDIA_TYPES = "stormpath.web.produces";

    public static final String ME_ENABLED = "stormpath.web.me.enabled";
    public static final String ME_URL = "stormpath.web.me.uri";

    public static final String OAUTH_ENABLED = "stormpath.web.oauth2.enabled";
    public static final String ID_SITE_ENABLED = "stormpath.web.idSite.enabled";
    public static final String CALLBACK_ENABLED = "stormpath.web.callback.enabled";
    public static final String CALLBACK_URI = "stormpath.web.callback.uri";

    private final ServletContext servletContext;
    private final ConfigReader CFG;
    private final Map<String, String> props;

    private final CookieConfig ACCESS_TOKEN_COOKIE_CONFIG;
    private final CookieConfig REFRESH_TOKEN_COOKIE_CONFIG;

    private final Map<String, Object> SINGLETONS;

    public DefaultConfig(final ServletContext servletContext, Map<String, String> configProps) {

        Assert.notNull(servletContext, "servletContext argument cannot be null.");
        Assert.notNull(configProps, "Properties argument cannot be null.");
        this.servletContext = servletContext;
        this.props = Collections.unmodifiableMap(configProps);
        this.CFG = new ExpressionConfigReader(servletContext, this.props);
        this.SINGLETONS = new LinkedHashMap<>();

        this.ACCESS_TOKEN_COOKIE_CONFIG = new AccessTokenCookieConfig(CFG);
        this.REFRESH_TOKEN_COOKIE_CONFIG = new RefreshTokenCookieConfig(CFG);

        // 748: If stormpath.web.idSite.enabled property is true and the stormpath.web.callback.enabled is false,
        // this is a config error that should be caught on startup.
        if (isIdSiteEnabled() && !isCallbackEnabled()) {
            throw new IllegalArgumentException("Cannot enable ID Site without having callback enabled. Please change 'stormpath.web.callback.enabled' to true " +
                    "or disable ID Site by setting 'stormpath.web.idSite.enabled` to false.");
        }
    }

    @Override
    public ControllerConfigResolver getLoginControllerConfig() {
        return new ServletControllerConfigResolver(this, CFG, "login");
    }

    @Override
    public ControllerConfigResolver getLogoutControllerConfig() {
        return new ServletControllerConfigResolver(this, CFG, "logout");
    }

    @Override
    public ControllerConfigResolver getRegisterControllerConfig() {
        return new ServletControllerConfigResolver(this, CFG, "register");
    }

    @Override
    public ControllerConfigResolver getForgotPasswordControllerConfig() {
        return new ServletControllerConfigResolver(this, CFG, "forgotPassword");
    }

    @Override
    public ControllerConfigResolver getVerifyControllerConfig() {
        return new ServletControllerConfigResolver(this, CFG, "verifyEmail");
    }

    @Override
    public ControllerConfigResolver getSendVerificationEmailControllerConfig() {
        //TODO hack since verify and send verify should be single config and controller but now is not
        return new ServletControllerConfigResolver(this, CFG, "sendVerificationEmail") {
            @Override
            public String getNextUri() {
                return getVerifyControllerConfig().getNextUri();
            }

            @Override
            public boolean isEnabled() {
                return getVerifyControllerConfig().isEnabled();
            }
        };
    }

    @Override
    public ControllerConfigResolver getChangePasswordControllerConfig() {
        return new ServletControllerConfigResolver(this, CFG, "changePassword");
    }

    @Override
    public ControllerConfigResolver getGoogleControllerConfig() {
        return new ServletControllerConfigResolver(this, CFG, "social.google");
    }

    @Override
    public ControllerConfigResolver getFacebookControllerConfig() {
        return new ServletControllerConfigResolver(this, CFG, "social.facebook");
    }

    @Override
    public ControllerConfigResolver getGithubControllerConfig() {
        return new ServletControllerConfigResolver(this, CFG, "social.github");
    }

    @Override
    public ControllerConfigResolver getLinkedinControllerConfig() {
        return new ServletControllerConfigResolver(this, CFG, "social.linkedin");
    }

    @Override
    public Saver<AuthenticationResult> getAuthenticationResultSaver() {
        try {
            return getInstance("stormpath.web.authc.saver");
        } catch (ServletException e) {
            throw new RuntimeException("Couldn't instantiate the default authentication result saver", e);
        }
    }

    @Override
    public AccountStoreResolver getAccountStoreResolver() {
        try {
            return getInstance("stormpath.web.accountStoreResolver");
        } catch (ServletException e) {
            throw new RuntimeException("Couldn't instantiate " + AccountStoreResolver.class.getName(), e);
        }
    }

    @Override
    public Publisher<RequestEvent> getRequestEventPublisher() {
        try {
            return getInstance("stormpath.web.request.event.publisher");
        } catch (ServletException e) {
            throw new RuntimeException("Couldn't instantiate the default RequestEventPublisher instance", e);
        }
    }

    @Override
    public boolean isRegisterAutoLoginEnabled() {
        return CFG.getBoolean("stormpath.web.register.autoLogin");
    }

    @Override
    public boolean isLogoutInvalidateHttpSession() {
        return CFG.getBoolean(LOGOUT_INVALIDATE_HTTP_SESSION);
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
    public boolean isMeEnabled() {
        return CFG.getBoolean(ME_ENABLED);
    }

    @Override
    public String getMeUrl() {
        return CFG.getString(ME_URL);
    }

    @Override
    public List<String> getMeExpandedProperties() {
        List<String> results = new ArrayList<String>();

        Pattern pattern = Pattern.compile("^stormpath\\.web\\.me\\.expand\\.(\\w+)$");

        for (String key : keySet()) {
            Matcher matcher = pattern.matcher(key);
            if (matcher.find()) {
                if (CFG.getBoolean(key)) {
                    results.add(matcher.group(1));
                }
            }
        }
        return results;
    }

    @Override
    public CookieConfig getRefreshTokenCookieConfig() {
        return this.REFRESH_TOKEN_COOKIE_CONFIG;
    }

    @Override
    public CookieConfig getAccessTokenCookieConfig() {
        return this.ACCESS_TOKEN_COOKIE_CONFIG;
    }

    @Override
    public String getAccessTokenValidationStrategy() {
        return CFG.getString(ACCESS_TOKEN_VALIDATION_STRATEGY);
    }

    @Override
    public WebHandler getLoginPreHandler() {
        try {
            return getInstance("stormpath.web.login.preHandler");
        } catch (ServletException e) {
            throw new RuntimeException("Couldn't instantiate login pre handler", e);
        }
    }

    @Override
    public WebHandler getLoginPostHandler() {
        try {
            return getInstance("stormpath.web.login.postHandler");
        } catch (ServletException e) {
            throw new RuntimeException("Couldn't instantiate login post handler", e);
        }
    }

    @Override
    public WebHandler getRegisterPreHandler() {
        try {
            return getInstance("stormpath.web.register.preHandler");
        } catch (ServletException e) {
            throw new RuntimeException("Couldn't instantiate login pre handler", e);
        }
    }

    @Override
    public WebHandler getRegisterPostHandler() {
        try {
            return getInstance("stormpath.web.register.postHandler");
        } catch (ServletException e) {
            throw new RuntimeException("Couldn't instantiate register post handler", e);
        }
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
        Map<String, Class<T>> classes =
                new ImplementationClassResolver<T>(this, propertyNamePrefix, expectedType).findImplementationClasses();

        Map<String, T> instances = new LinkedHashMap<String, T>(classes.size());

        for (Map.Entry<String, Class<T>> entry : classes.entrySet()) {

            String name = entry.getKey();

            T instance = getInstance(propertyNamePrefix + name);
            Assert.isInstanceOf(expectedType, instance);

            instances.put(name, instance);
        }

        return instances;
    }

    /**
     * @since 1.0.0
     */
    @Override
    @SuppressWarnings("unchecked")
    public String getProducesMediaTypes() {
        List<String> mediaTypes = CFG.getList(PRODUCES_MEDIA_TYPES);
        return Strings.collectionToCommaDelimitedString(mediaTypes);
    }

    @Override
    public boolean isOAuthEnabled() {
        return CFG.getBoolean(OAUTH_ENABLED);
    }

    @Override
    public boolean isIdSiteEnabled() {
        return CFG.getBoolean(ID_SITE_ENABLED);
    }

    @Override
    public boolean isCallbackEnabled() {
        return CFG.getBoolean(CALLBACK_ENABLED);
    }

    @Override
    public String getCallbackUri() {
        return CFG.getString(CALLBACK_URI);
    }

    public ServerUriResolver getServerUriResolver() {
        try {
            return (ServerUriResolver) this.getInstance(SERVER_URI_RESOLVER);
        } catch (ServletException e) {
            log.error("Exception occurred when instantiating " + SERVER_URI_RESOLVER, e);
        }
        return null;
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
                instance = ((Factory<T>) instance).getInstance();
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

    @Override
    public AccessTokenAuthenticationRequestFactory getAccessTokenAuthenticationRequestFactory() {
        try {
            return (AccessTokenAuthenticationRequestFactory) this.getInstance(ACCESS_TOKEN_AUTHENTICATION_REQUEST_FACTORY);
        } catch (ServletException e) {
            log.error("Exception occurred when instantiating " + ACCESS_TOKEN_AUTHENTICATION_REQUEST_FACTORY, e);
        }
        return null;
    }

    @Override
    public RefreshTokenAuthenticationRequestFactory getRefreshTokenAuthenticationRequestFactory() {
        try {
            return (RefreshTokenAuthenticationRequestFactory) this.getInstance(REFRESH_TOKEN_AUTHENTICATION_REQUEST_FACTORY);
        } catch (ServletException e) {
            log.error("Exception occurred when instantiating " + REFRESH_TOKEN_AUTHENTICATION_REQUEST_FACTORY, e);
        }
        return null;
    }

    @Override
    public RequestAuthorizer getRequestAuthorizer() {
        try {
            return (RequestAuthorizer) this.getInstance(REFRESH_TOKEN_AUTHENTICATION_REQUEST_FACTORY);
        } catch (ServletException e) {
            log.error("Exception occurred when instantiating " + REFRESH_TOKEN_AUTHENTICATION_REQUEST_FACTORY, e);
        }
        return null;
    }

    @Override
    public AccessTokenResultFactory getAccessTokenResultFactory() {
        try {
            return (AccessTokenResultFactory) this.getInstance(ACCESS_TOKEN_RESULT_FACTORY);
        } catch (ServletException e) {
            log.error("Exception occurred when instantiating " + ACCESS_TOKEN_RESULT_FACTORY, e);
        }
        return null;
    }

    @Override
    public RefreshTokenResultFactory getRefreshTokenResultFactory() {
        try {
            return (RefreshTokenResultFactory) this.getInstance(REFRESH_TOKEN_RESULT_FACTORY);
        } catch (ServletException e) {
            log.error("Exception occurred when instantiating " + REFRESH_TOKEN_RESULT_FACTORY, e);
        }
        return null;
    }

//    @Override
//    public Saver<AuthenticationResult> getAuthenticationResultSaver() {
//        try {
//            return (Saver<AuthenticationResult>) this.getInstance(ACCOUNT_SAVER);
//        } catch (ServletException e) {
//            log.error("Exception occurred when instantiating " + ACCOUNT_SAVER, e);
//        }
//        return null;
//    }

//    @Override
//    public Publisher<RequestEvent> getRequestEventPublisher() {
//        try {
//            return (Publisher<RequestEvent>) this.getInstance(EVENT_PUBLISHER);
//        } catch (ServletException e) {
//            log.error("Exception occurred when instantiating " + EVENT_PUBLISHER, e);
//        }
//        return null;
//    }

    @Override
    public BasicAuthenticationScheme getBasicAuthenticationScheme() {
        try {
            return (BasicAuthenticationScheme) this.getInstance(BASIC_AUTHENTICATION_REQUEST_FACTORY);
        } catch (ServletException e) {
            log.error("Exception occurred when instantiating " + BASIC_AUTHENTICATION_REQUEST_FACTORY, e);
        }
        return null;
    }

    @Override
    public String getWebApplicationDomain() {
        return CFG.getString(WEB_APPLICATION_DOMAIN);
    }

    public UnauthenticatedHandler getUnauthenticatedHandler() {
        try {
            return (UnauthenticatedHandler) this.getInstance(UNAUTHENTICATED_HANDLER);
        } catch (ServletException e) {
            log.error("Exception occurred when instantiating " + UNAUTHENTICATED_HANDLER, e);
        }
        return null;
    }

    public UnauthorizedHandler getUnauthorizedHandler() {
        try {
            return (UnauthorizedHandler) this.getInstance(UNAUTHORIZED_HANDLER);
        } catch (ServletException e) {
            log.error("Exception occurred when instantiating " + UNAUTHORIZED_HANDLER, e);
        }
        return null;
    }

    @Override
    public String getIDSiteResultUri() {
        return CFG.getString(IDSITE_RESULT_URI);
    }

    @Override
    public Resolver<IdSiteOrganizationContext> getIdSiteOrganizationResolver() {
        try {
            ConfigSingletonFactory<Resolver<IdSiteOrganizationContext>> factory = this.getInstance(IDSITE_ORGANIZATION_RESOLVER_FACTORY);
            return factory.getInstance();
        } catch (ServletException e) {
            log.error("Exception occurred when instantiating " + IDSITE_ORGANIZATION_RESOLVER_FACTORY, e);
        }
        return null;
    }

    @Override
    public String getIDSiteLoginUri() {
        return CFG.getString(IDSITE_LOGIN_URI);
    }

    @Override
    public String getIDSiteRegisterUri() {
        return CFG.getString(IDSITE_REGISTER_URI);
    }

    @Override
    public String getIDSiteForgotUri() {
        return CFG.getString(IDSITE_FORGOT_URI);
    }

}
