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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stormpath.sdk.application.Application;
import com.stormpath.sdk.authc.AuthenticationResult;
import com.stormpath.sdk.client.Client;
import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.lang.BiPredicate;
import com.stormpath.sdk.lang.Classes;
import com.stormpath.sdk.lang.Strings;
import com.stormpath.sdk.servlet.authz.RequestAuthorizer;
import com.stormpath.sdk.servlet.account.AccountResolver;
import com.stormpath.sdk.servlet.application.ApplicationResolver;
import com.stormpath.sdk.servlet.client.ClientResolver;
import com.stormpath.sdk.servlet.config.Config;
import com.stormpath.sdk.servlet.config.CookieConfig;
import com.stormpath.sdk.servlet.config.Factory;
import com.stormpath.sdk.servlet.config.ImplementationClassResolver;
import com.stormpath.sdk.servlet.config.RegisterEnabledResolver;
import com.stormpath.sdk.servlet.csrf.CsrfTokenManager;
import com.stormpath.sdk.servlet.event.RequestEvent;
import com.stormpath.sdk.servlet.event.impl.Publisher;
import com.stormpath.sdk.servlet.filter.ServerUriResolver;
import com.stormpath.sdk.servlet.http.Resolver;
import com.stormpath.sdk.servlet.http.Saver;
import com.stormpath.sdk.servlet.http.authc.AccountStoreResolver;
import com.stormpath.sdk.servlet.idsite.IdSiteOrganizationContext;
import com.stormpath.sdk.servlet.filter.ChangePasswordConfig;
import com.stormpath.sdk.servlet.filter.ChangePasswordServletControllerConfig;
import com.stormpath.sdk.servlet.filter.ContentNegotiationResolver;
import com.stormpath.sdk.servlet.filter.ControllerConfig;
import com.stormpath.sdk.servlet.filter.FilterChainManager;
import com.stormpath.sdk.servlet.filter.FilterChainResolver;
import com.stormpath.sdk.servlet.filter.ServletControllerConfig;
import com.stormpath.sdk.servlet.http.InvalidMediaTypeException;
import com.stormpath.sdk.servlet.http.MediaType;
import com.stormpath.sdk.servlet.http.Resolver;
import com.stormpath.sdk.servlet.http.Saver;
import com.stormpath.sdk.servlet.http.authc.AccountStoreResolver;
import com.stormpath.sdk.servlet.i18n.DefaultMessageContext;
import com.stormpath.sdk.servlet.i18n.MessageContext;
import com.stormpath.sdk.servlet.i18n.MessageSource;
import com.stormpath.sdk.servlet.mvc.RequestFieldValueResolver;
import com.stormpath.sdk.servlet.mvc.WebHandler;
import com.stormpath.sdk.servlet.util.ServletContextInitializable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
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
    public static final String BASIC_AUTHENTICATION_REQUEST_FACTORY = "stormpath.web.http.authc.schemes.basic";
    protected static final String UNAUTHENTICATED_HANDLER = "stormpath.web.authc.unauthenticatedHandler";
    protected static final String UNAUTHORIZED_HANDLER = "stormpath.web.authz.unauthorizedHandler";
    protected static final String SERVER_URI_RESOLVER = "stormpath.web.oauth2.origin.authorizer.serverUriResolver";
    protected static final String IDSITE_ORGANIZATION_RESOLVER_FACTORY = "stormpath.web.idSite.OrganizationResolverFactory";

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
        this.props = new HashMap<>(configProps);
        this.CFG = new ExpressionConfigReader(servletContext, Collections.unmodifiableMap(this.props));
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
    public Client getClient() {
        return ClientResolver.INSTANCE.getClient(servletContext);
    }

    @Override
    public ApplicationResolver getApplicationResolver() {
        return ApplicationResolver.INSTANCE; //TODO remove static usage
    }

    @Override
    public ObjectMapper getObjectMapper() {
        try {
            return getInstance("stormpath.web.json.objectMapperFactory", ObjectMapper.class);
        } catch (ServletException e) {
            throw new RuntimeException("Couldn't instantiate the default ObjectMapper", e);
        }
    }

    @Override
    public MessageSource getMessageSource() {
        return getRuntimeInstance("stormpath.web.message.source");
    }

    @Override
    public Resolver<Locale> getLocaleResolver() {
        return getRuntimeInstance("stormpath.web.locale.resolver");
    }

    @Override
    public MessageContext getMessageContext() {
        MessageSource messageSource = getMessageSource();
        Resolver<Locale> localeResolver = getLocaleResolver();
        return new DefaultMessageContext(messageSource, localeResolver);
    }

    @Override
    public CsrfTokenManager getCsrfTokenManager() {
        return getRuntimeInstance("stormpath.web.csrf.token.manager");
    }

    @Override
    public RequestFieldValueResolver getFieldValueResolver() {
        return getRuntimeInstance("stormpath.web.form.fields.valueResolver");
    }

    @Override
    public AccountResolver getAccountResolver() {
        return getRuntimeInstance("stormpath.web.account.resolver");
    }

    @Override
    public ContentNegotiationResolver getContentNegotiationResolver() {
        return getRuntimeInstance("stormpath.web.conneg.resolver");
    }

    @Override
    public ControllerConfig getLoginConfig() {
        return new ServletControllerConfig("login", this);
    }

    @Override
    public ControllerConfig getLogoutConfig() {
        return new ServletControllerConfig("logout", this);
    }

    @Override
    public ControllerConfig getRegisterConfig() {
        return new ServletControllerConfig("register", this);
    }

    @Override
    public ControllerConfig getForgotPasswordConfig() {
        return new ServletControllerConfig("forgotPassword", this);
    }

    @Override
    public ControllerConfig getVerifyConfig() {
        return new ServletControllerConfig("verifyEmail", this);
    }

    @Override
    public ChangePasswordConfig getChangePasswordConfig() {
        return new ChangePasswordServletControllerConfig(this, "changePassword");
    }

    @Override
    public Saver<AuthenticationResult> getAuthenticationResultSaver() {
        return getRuntimeInstance("stormpath.web.authc.saver");
    }

    @Override
    public AccountStoreResolver getAccountStoreResolver() {
        return getRuntimeInstance("stormpath.web.accountStoreResolver");
    }

    @Override
    public Publisher<RequestEvent> getRequestEventPublisher() {
        return getRuntimeInstance("stormpath.web.request.event.publisher");
    }

    @Override
    public FilterChainManager getFilterChainManager() {
        return getRuntimeInstance("stormpath.web.filter.chain.manager");
    }

    @Override
    public FilterChainResolver getFilterChainResolver() {
        return getRuntimeInstance("stormpath.web.filter.chain.resolver");
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
        return getRuntimeInstance("stormpath.web.login.preHandler");
    }

    @Override
    public WebHandler getLoginPostHandler() {
        return getRuntimeInstance("stormpath.web.login.postHandler");
    }

    @Override
    public WebHandler getRegisterPreHandler() {
        return getRuntimeInstance("stormpath.web.register.preHandler");
    }

    @Override
    public WebHandler getRegisterPostHandler() {
        return getRuntimeInstance("stormpath.web.register.postHandler");
    }

    @Override
    public Resolver<Boolean> getRegisterEnabledResolver() {
        String key = "stormpath.web.register.enabled.resolver";
        if (containsKey(key)) {
            try {
                return getInstance(key);
            } catch (ServletException e) {
                throw new RuntimeException("Couldn't instantiate stormpath.web.register.enabled.resolver", e);
            }
        } else {
            boolean enabled = CFG.getBoolean("stormpath.web.register.enabled");
            ApplicationResolver appResolver = getApplicationResolver();
            BiPredicate<Boolean, Application> regEnabledPredicate = getRegisterEnabledPredicate();
            RegisterEnabledResolver resolver = new RegisterEnabledResolver(enabled, appResolver, regEnabledPredicate);
            SINGLETONS.put(key, resolver);
            return resolver;
        }
    }

    @Override
    public BiPredicate<Boolean, Application> getRegisterEnabledPredicate() {
        return getRuntimeInstance("stormpath.web.register.enabled.predicate");
    }

    private <T> T getRuntimeInstance(String classPropertyName) {
        try {
            return getInstance(classPropertyName);
        } catch (ServletException e) {
            throw new RuntimeException("Couldn't acquire instance for '" + classPropertyName + "': " + e.getMessage());
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
    public List<MediaType> getProducedMediaTypes() {
        String mediaTypes = Strings.clean(getProducesMediaTypes());
        Assert.notNull(mediaTypes, "stormpath.web.produces property value cannot be null or empty.");

        try {
            return MediaType.parseMediaTypes(mediaTypes);
        } catch (InvalidMediaTypeException e) {
            String msg = "Unable to parse value in stormpath.web.produces property: " + e.getMessage();
            throw new IllegalArgumentException(msg, e);
        }
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

    @Override
    public ServerUriResolver getServerUriResolver() {
        return this.getRuntimeInstance(SERVER_URI_RESOLVER);
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
    public String getWebApplicationDomain() {
        return CFG.getString(WEB_APPLICATION_DOMAIN);
    }

    @Override
    public Resolver<IdSiteOrganizationContext> getIdSiteOrganizationResolver() {
        return this.getRuntimeInstance(IDSITE_ORGANIZATION_RESOLVER_FACTORY);
    }
}
