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
package com.stormpath.sdk.servlet.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stormpath.sdk.application.Application;
import com.stormpath.sdk.authc.AuthenticationResult;
import com.stormpath.sdk.client.Client;
import com.stormpath.sdk.lang.BiPredicate;
import com.stormpath.sdk.servlet.account.AccountResolver;
import com.stormpath.sdk.servlet.application.ApplicationResolver;
import com.stormpath.sdk.servlet.csrf.CsrfTokenManager;
import com.stormpath.sdk.servlet.event.RequestEvent;
import com.stormpath.sdk.servlet.event.impl.Publisher;
import com.stormpath.sdk.servlet.filter.ChangePasswordConfig;
import com.stormpath.sdk.servlet.filter.ContentNegotiationResolver;
import com.stormpath.sdk.servlet.filter.ControllerConfig;
import com.stormpath.sdk.servlet.filter.FilterChainManager;
import com.stormpath.sdk.servlet.filter.FilterChainResolver;
import com.stormpath.sdk.servlet.filter.ServerUriResolver;
import com.stormpath.sdk.servlet.http.MediaType;
import com.stormpath.sdk.servlet.http.Resolver;
import com.stormpath.sdk.servlet.http.Saver;
import com.stormpath.sdk.servlet.http.authc.AccountStoreResolver;
import com.stormpath.sdk.servlet.i18n.MessageContext;
import com.stormpath.sdk.servlet.i18n.MessageSource;
import com.stormpath.sdk.servlet.idsite.IdSiteOrganizationContext;
import com.stormpath.sdk.servlet.mvc.RequestFieldValueResolver;
import com.stormpath.sdk.servlet.mvc.WebHandler;

import javax.servlet.ServletException;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * @since 1.0.RC3
 */
public interface Config extends Map<String, String> {

    ObjectMapper getObjectMapper();

    Client getClient();

    ApplicationResolver getApplicationResolver();

    MessageContext getMessageContext();

    Resolver<Locale> getLocaleResolver();

    CsrfTokenManager getCsrfTokenManager();

    RequestFieldValueResolver getFieldValueResolver();

    MessageSource getMessageSource();

    ControllerConfig getLoginConfig();

    ControllerConfig getLogoutConfig();

    ControllerConfig getRegisterConfig();

    ControllerConfig getForgotPasswordConfig();

    ControllerConfig getVerifyConfig();

    ChangePasswordConfig getChangePasswordConfig();

    Saver<AuthenticationResult> getAuthenticationResultSaver();

    AccountResolver getAccountResolver();

    AccountStoreResolver getAccountStoreResolver();

    ContentNegotiationResolver getContentNegotiationResolver();

    Publisher<RequestEvent> getRequestEventPublisher();

    /**
     * @since 1.0.4
     */
    boolean isStormpathEnabled();

    /**
     * @since 1.0.4
     */
    boolean isStormpathWebEnabled();

    boolean isRegisterAutoLoginEnabled();

    /**
     * @since 1.0.RC6
     */
    boolean isLogoutInvalidateHttpSession();

    String getAccessTokenUrl();

    String getUnauthorizedUrl();

    boolean isMeEnabled();

    String getMeUrl();

    List<String> getMeExpandedProperties();

    CookieConfig getRefreshTokenCookieConfig();

    CookieConfig getAccessTokenCookieConfig();

    String getAccessTokenValidationStrategy();

    WebHandler getLoginPreHandler();

    WebHandler getLoginPostHandler();

    WebHandler getRegisterPreHandler();

    WebHandler getRegisterPostHandler();

    BiPredicate<Boolean,Application> getRegisterEnabledPredicate();

    Resolver<Boolean> getRegisterEnabledResolver();

    FilterChainManager getFilterChainManager();

    FilterChainResolver getFilterChainResolver();

    <T> T getInstance(String classPropertyName) throws ServletException;

    <T> Map<String, T> getInstances(String propertyNamePrefix, Class<T> expectedType) throws ServletException;

    /**
     * @since 1.0.0
     */
    String getProducesMediaTypes();

    /**
     * @since 1.0.0
     */
    List<MediaType> getProducedMediaTypes();

    /**
     * @since 1.0.0
     */
    boolean isOAuthEnabled();

    /**
     * @since 1.0.0
     */
    boolean isIdSiteEnabled();

    /**
     * @since 1.0.0
     */
    boolean isCallbackEnabled();

    /**
     * @since 1.0.0
     */
    String getCallbackUri();

    /**
     * @since 1.0.0
     */
    String getWebApplicationDomain();

    ServerUriResolver getServerUriResolver();

    Resolver<IdSiteOrganizationContext> getIdSiteOrganizationResolver();

}
