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

import com.stormpath.sdk.application.Application;
import com.stormpath.sdk.authc.AuthenticationResult;
import com.stormpath.sdk.servlet.authz.RequestAuthorizer;
import com.stormpath.sdk.servlet.event.RequestEvent;
import com.stormpath.sdk.servlet.event.impl.Publisher;
import com.stormpath.sdk.servlet.filter.ControllerConfigResolver;
import com.stormpath.sdk.servlet.filter.ServerUriResolver;
import com.stormpath.sdk.servlet.filter.UnauthenticatedHandler;
import com.stormpath.sdk.servlet.filter.UnauthorizedHandler;
import com.stormpath.sdk.servlet.filter.oauth.AccessTokenAuthenticationRequestFactory;
import com.stormpath.sdk.servlet.filter.oauth.AccessTokenResultFactory;
import com.stormpath.sdk.servlet.filter.oauth.RefreshTokenAuthenticationRequestFactory;
import com.stormpath.sdk.servlet.filter.oauth.RefreshTokenResultFactory;
import com.stormpath.sdk.lang.BiPredicate;
import com.stormpath.sdk.servlet.application.ApplicationResolver;
import com.stormpath.sdk.servlet.filter.ChangePasswordConfigResolver;
import com.stormpath.sdk.servlet.http.Resolver;
import com.stormpath.sdk.servlet.http.Saver;
import com.stormpath.sdk.servlet.http.authc.AccountStoreResolver;
import com.stormpath.sdk.servlet.http.authc.BasicAuthenticationScheme;
import com.stormpath.sdk.servlet.idsite.IdSiteOrganizationContext;
import com.stormpath.sdk.servlet.mvc.WebHandler;

import javax.servlet.ServletException;
import java.util.List;
import java.util.Map;

/**
 * @since 1.0.RC3
 */
public interface Config extends Map<String, String> {

    ApplicationResolver getApplicationResolver();

    ControllerConfigResolver getLoginControllerConfig();

    ControllerConfigResolver getLogoutControllerConfig();

    ControllerConfigResolver getRegisterControllerConfig();

    ControllerConfigResolver getForgotPasswordControllerConfig();

    ControllerConfigResolver getVerifyControllerConfig();

    ChangePasswordConfigResolver getChangePasswordControllerConfig();

    Saver<AuthenticationResult> getAuthenticationResultSaver();

    AccountStoreResolver getAccountStoreResolver();

    Publisher<RequestEvent> getRequestEventPublisher();

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

    <T> T getInstance(String classPropertyName) throws ServletException;

    <T> Map<String, T> getInstances(String propertyNamePrefix, Class<T> expectedType) throws ServletException;

    /**
     * @since 1.0.0
     */
    String getProducesMediaTypes();

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

    ControllerConfigResolver getGoogleControllerConfig();

    ControllerConfigResolver getFacebookControllerConfig();

    ControllerConfigResolver getGithubControllerConfig();

    ControllerConfigResolver getLinkedinControllerConfig();

    AccessTokenAuthenticationRequestFactory getAccessTokenAuthenticationRequestFactory();

    RefreshTokenAuthenticationRequestFactory getRefreshTokenAuthenticationRequestFactory();

    RequestAuthorizer getRequestAuthorizer();

    AccessTokenResultFactory getAccessTokenResultFactory();

    RefreshTokenResultFactory getRefreshTokenResultFactory();

    BasicAuthenticationScheme getBasicAuthenticationScheme();

    String getWebApplicationDomain();

    UnauthenticatedHandler getUnauthenticatedHandler();

    UnauthorizedHandler getUnauthorizedHandler();

    String getCallbackUri();

    ServerUriResolver getServerUriResolver();

    String getIDSiteResultUri();

    String getIDSiteLoginUri();

    Resolver<IdSiteOrganizationContext> getIdSiteOrganizationResolver();

    String getIDSiteRegisterUri();

    String getIDSiteForgotUri();

}
