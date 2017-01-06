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
package com.stormpath.spring.config;

import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.application.Application;
import com.stormpath.sdk.authc.AuthenticationResult;
import com.stormpath.sdk.cache.Cache;
import com.stormpath.sdk.lang.BiPredicate;
import com.stormpath.sdk.servlet.account.AccountResolver;
import com.stormpath.sdk.servlet.application.ApplicationLoader;
import com.stormpath.sdk.servlet.application.ApplicationResolver;
import com.stormpath.sdk.servlet.authz.RequestAuthorizer;
import com.stormpath.sdk.servlet.client.ClientLoader;
import com.stormpath.sdk.servlet.config.CookieConfig;
import com.stormpath.sdk.servlet.csrf.CsrfTokenManager;
import com.stormpath.sdk.servlet.event.RequestEvent;
import com.stormpath.sdk.servlet.event.RequestEventListener;
import com.stormpath.sdk.servlet.event.impl.Publisher;
import com.stormpath.sdk.servlet.filter.ContentNegotiationResolver;
import com.stormpath.sdk.servlet.filter.ControllerConfig;
import com.stormpath.sdk.servlet.filter.DefaultFilterBuilder;
import com.stormpath.sdk.servlet.filter.FilterChainManager;
import com.stormpath.sdk.servlet.filter.FilterChainResolver;
import com.stormpath.sdk.servlet.filter.ServerUriResolver;
import com.stormpath.sdk.servlet.filter.StormpathFilter;
import com.stormpath.sdk.servlet.filter.UsernamePasswordRequestFactory;
import com.stormpath.sdk.servlet.filter.WrappedServletRequestFactory;
import com.stormpath.sdk.servlet.filter.account.JwtAccountResolver;
import com.stormpath.sdk.servlet.filter.account.JwtSigningKeyResolver;
import com.stormpath.sdk.servlet.filter.oauth.AccessTokenAuthenticationRequestFactory;
import com.stormpath.sdk.servlet.filter.oauth.AccessTokenResultFactory;
import com.stormpath.sdk.servlet.filter.oauth.RefreshTokenAuthenticationRequestFactory;
import com.stormpath.sdk.servlet.filter.oauth.RefreshTokenResultFactory;
import com.stormpath.sdk.servlet.http.MediaType;
import com.stormpath.sdk.servlet.http.Resolver;
import com.stormpath.sdk.servlet.http.Saver;
import com.stormpath.sdk.servlet.http.authc.AccountStoreResolver;
import com.stormpath.sdk.servlet.http.authc.HeaderAuthenticator;
import com.stormpath.sdk.servlet.http.authc.HttpAuthenticationScheme;
import com.stormpath.sdk.servlet.idsite.IdSiteOrganizationContext;
import com.stormpath.sdk.servlet.mvc.ProviderAccountRequestFactory;
import com.stormpath.sdk.servlet.mvc.Controller;
import com.stormpath.sdk.servlet.mvc.ErrorModelFactory;
import com.stormpath.sdk.servlet.mvc.ExpandsResolver;
import com.stormpath.sdk.servlet.mvc.RequestFieldValueResolver;
import com.stormpath.sdk.servlet.mvc.provider.AccountStoreModelFactory;
import com.stormpath.sdk.servlet.util.GrantTypeValidator;
import com.stormpath.spring.mvc.AccessTokenControllerConfig;
import com.stormpath.spring.mvc.ChangePasswordControllerConfig;
import com.stormpath.spring.mvc.MessageContextRegistrar;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import javax.servlet.Filter;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import java.util.List;
import java.util.Locale;

/**
 * @since 1.0.RC4
 */
@Configuration
public class StormpathWebMvcConfiguration extends AbstractStormpathWebMvcConfiguration
    implements ServletContextAware, InitializingBean {

    @Override
    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        servletContext.setAttribute(ClientLoader.CLIENT_ATTRIBUTE_KEY, client);
        servletContext.setAttribute(ApplicationLoader.APP_ATTRIBUTE_NAME, application);
    }

    @Bean
    @Override
    public HandlerMapping stormpathStaticResourceHandlerMapping() {
        return super.stormpathStaticResourceHandlerMapping();
    }

    @Bean
    public InternalResourceViewResolver stormpathJspViewResolver() {
        return super.stormpathJspViewResolver();
    }

    @Bean
    @Override
    public com.stormpath.sdk.servlet.mvc.View stormpathControllerView() {
        return super.stormpathControllerView();
    }

    /**
     * @since 1.0.0
     */
    @Bean
    @Override
    public ApplicationResolver stormpathApplicationResolver() {
        return super.stormpathApplicationResolver();
    }

    /**
     * @since 1.3.0
     */
    @Bean
    @Override
    public ProviderAccountRequestFactory stormpathAccountProviderRequestHandler() {
        return super.stormpathAccountProviderRequestHandler();
    }

    /**
     * @since 1.0.0
     */
    @Bean
    @Override
    public List<MediaType> stormpathProducedMediaTypes() {
        return super.stormpathProducedMediaTypes();
    }

    @Bean
    @Override
    public org.springframework.web.servlet.View stormpathJsonView() {
        return super.stormpathJsonView();
    }

    @Bean
    @Override
    public org.springframework.web.servlet.ViewResolver stormpathJsonViewResolver() {
        return super.stormpathJsonViewResolver();
    }

    @Bean
    @Override
    public HandlerInterceptor stormpathLayoutInterceptor() {
        return super.stormpathLayoutInterceptor();
    }

    @Bean
    @Override
    public AccountStoreResolver stormpathAccountStoreResolver() {
        return super.stormpathAccountStoreResolver();
    }

    @Bean
    @Override
    public UsernamePasswordRequestFactory stormpathUsernamePasswordRequestFactory() {
        return super.stormpathUsernamePasswordRequestFactory();
    }

    @Bean
    @Override
    public AccessTokenCookieProperties accessTokenCookieProperties() {
        return super.accessTokenCookieProperties();
    }

    @Bean
    @Override
    public RefreshTokenCookieProperties refreshTokenCookieProperties() {
        return super.refreshTokenCookieProperties();
    }

    @Bean
    @Override
    public CookieConfig stormpathAccessTokenCookieConfig() {
        return super.stormpathAccessTokenCookieConfig();
    }

    @Bean
    @Override
    public Resolver<String> stormpathRemoteAddrResolver() {
        return super.stormpathRemoteAddrResolver();
    }

    @Bean
    @Override
    public Resolver<Boolean> stormpathLocalhostResolver() {
        return super.stormpathLocalhostResolver();
    }

    @Bean
    @Override
    public Resolver<Boolean> stormpathSecureResolver() {
        return super.stormpathSecureResolver();
    }

    @Bean
    @Override
    public Saver<AuthenticationResult> stormpathCookieAuthenticationResultSaver() {
        return super.stormpathCookieAuthenticationResultSaver();
    }

    @Bean
    @Override
    public List<Saver<AuthenticationResult>> stormpathAuthenticationResultSavers() {
        return super.stormpathAuthenticationResultSavers();
    }

    @Bean
    @Override
    public Saver<AuthenticationResult> stormpathAuthenticationResultSaver() {
        return super.stormpathAuthenticationResultSaver();
    }

    @Bean
    @Override
    public JwtSigningKeyResolver stormpathJwtSigningKeyResolver() {
        return super.stormpathJwtSigningKeyResolver();
    }

    @Bean
    @Override
    public RequestEventListener stormpathRequestEventListener() {
        return super.stormpathRequestEventListener();
    }

    @Bean
    @Override
    public Publisher<RequestEvent> stormpathRequestEventPublisher() {
        return super.stormpathRequestEventPublisher();
    }

    @Bean
    public String stormpathCsrfTokenSigningKey() {
        return super.stormpathCsrfTokenSigningKey();
    }

    @Bean
    @Override
    public JwtAccountResolver stormpathJwtAccountResolver() {
        return super.stormpathJwtAccountResolver();
    }

    @Bean
    @Override
    public Cache<String, String> stormpathNonceCache() {
        return super.stormpathNonceCache();
    }

    @Bean
    @Override
    public CsrfTokenManager stormpathCsrfTokenManager() {
        return super.stormpathCsrfTokenManager();
    }

    @Bean
    @Override
    public RequestFieldValueResolver stormpathFieldValueResolver() {
        return super.stormpathFieldValueResolver();
    }

    @Bean
    @Override
    public AccessTokenResultFactory stormpathAccessTokenResultFactory() {
        return super.stormpathAccessTokenResultFactory();
    }

    @Bean
    @Override
    public RefreshTokenResultFactory stormpathRefreshTokenResultFactory(){
        return super.stormpathRefreshTokenResultFactory();
    }

    @Bean
    @Override
    public WrappedServletRequestFactory stormpathWrappedServletRequestFactory() {
        return super.stormpathWrappedServletRequestFactory();
    }

    @Bean
    @Override
    public HttpAuthenticationScheme stormpathBasicAuthenticationScheme() {
        return super.stormpathBasicAuthenticationScheme();
    }

    @Bean
    @Override
    public HttpAuthenticationScheme stormpathBearerAuthenticationScheme() {
        return super.stormpathBearerAuthenticationScheme();
    }

    @Bean
    @Override
    public List<HttpAuthenticationScheme> stormpathHttpAuthenticationSchemes() {
        return super.stormpathHttpAuthenticationSchemes();
    }

    @Bean
    @Override
    public HeaderAuthenticator stormpathAuthorizationHeaderAuthenticator() {
        return super.stormpathAuthorizationHeaderAuthenticator();
    }

    @Bean
    @Override
    public Resolver<Account> stormpathAuthorizationHeaderAccountResolver() {
        return super.stormpathAuthorizationHeaderAccountResolver();
    }

    @Bean
    @Override
    public Resolver<Account> stormpathCookieAccountResolver() {
        return super.stormpathCookieAccountResolver();
    }

    @Bean
    @Override
    public List<Resolver<Account>> stormpathAccountResolvers() {
        return super.stormpathAccountResolvers();
    }

    @Bean
    @Override
    public Resolver<List<String>> stormpathSubdomainResolver() {
        return super.stormpathSubdomainResolver();
    }

    @Bean
    @Override
    public Resolver<String> stormpathOrganizationNameKeyResolver() {
        return super.stormpathOrganizationNameKeyResolver();
    }

    @Bean
    @Override
    public Resolver<IdSiteOrganizationContext> stormpathIdSiteOrganizationResolver() {
        return super.stormpathIdSiteOrganizationResolver();
    }

    @Bean
    @Override
    public Controller stormpathGoogleCallbackController() {
        return super.stormpathGoogleCallbackController();
    }

    @Bean
    @Override
    public Controller stormpathGithubCallbackController() {
        return super.stormpathGithubCallbackController();
    }

    @Bean
    @Override
    public Controller stormpathFacebookCallbackController() {
        return super.stormpathFacebookCallbackController();
    }

    @Bean
    @Override
    public Controller stormpathLinkedinCallbackController() {
        return super.stormpathLinkedinCallbackController();
    }

    @Bean
    @Override
    public ErrorModelFactory stormpathLoginErrorModelFactory() {
        return super.stormpathLoginErrorModelFactory();
    }

    @Bean
    @Override
    public Controller stormpathLoginController() {
        return super.stormpathLoginController();
    }

    @Bean
    @Override
    public AccountStoreModelFactory stormpathAccountStoreModelFactory() {
        return super.stormpathAccountStoreModelFactory();
    }

    @Bean
    @Override
    public Controller stormpathForgotPasswordController() {
        return super.stormpathForgotPasswordController();
    }

    @Bean
    public LocaleResolver stormpathSpringLocaleResolver() {
        return super.stormpathSpringLocaleResolver();
    }

    @Bean
    public LocaleChangeInterceptor stormpathLocaleChangeInterceptor() {
        return super.stormpathLocaleChangeInterceptor();
    }

    @Bean
    public Resolver<Locale> stormpathLocaleResolver() {
        return super.stormpathLocaleResolver();
    }

    @Bean
    @Override
    public MessageContextRegistrar stormpathMessageContextRegistrar() {
        return super.stormpathMessageContextRegistrar();
    }

    @Bean
    @Override
    public com.stormpath.sdk.servlet.i18n.MessageSource stormpathMessageSource() {
        return super.stormpathMessageSource();
    }

    @Bean
    @Override
    public Controller stormpathRegisterController() {
        return super.stormpathRegisterController();
    }

    @Bean
    @Override
    public Resolver<Boolean> stormpathRegisterEnabledResolver() {
        return super.stormpathRegisterEnabledResolver();
    }

    @Bean
    @Override
    public BiPredicate<Boolean, Application> stormpathRegisterEnabledPredicate() {
        return super.stormpathRegisterEnabledPredicate();
    }

    @Bean
    @Override
    public Controller stormpathVerifyController() {
        return super.stormpathVerifyController();
    }

    @Bean
    @Override
    public Controller stormpathChangePasswordController() {
        return super.stormpathChangePasswordController();
    }

    @Bean
    @Override
    public Controller stormpathAccessTokenController() {
        return super.stormpathAccessTokenController();
    }

    @Bean
    @Override
    public Controller stormpathMeController() {
        return super.stormpathMeController();
    }

    @Bean
    @Override
    public ExpandsResolver stormpathMeExpandsResolver(){
        return super.stormpathMeExpandsResolver();
    }

    @Bean
    @Override
    public Controller stormpathIdSiteResultController() {
        return super.stormpathIdSiteResultController();
    }

    @Bean
    @Override
    public AccessTokenAuthenticationRequestFactory stormpathAccessTokenAuthenticationRequestFactory() {
        return super.stormpathAccessTokenAuthenticationRequestFactory();
    }

    @Bean
    @Override
    public RefreshTokenAuthenticationRequestFactory stormpathRefreshTokenAuthenticationRequestFactory(){
        return super.stormpathRefreshTokenAuthenticationRequestFactory();
    }

    @Bean
    @Override
    public RequestAuthorizer stormpathAccessTokenRequestAuthorizer() {
        return super.stormpathAccessTokenRequestAuthorizer();
    }

    @Bean
    @Override
    public RequestAuthorizer stormpathOriginAccessTokenRequestAuthorizer() {
        return super.stormpathOriginAccessTokenRequestAuthorizer();
    }

    @Bean
    @Override
    public ServerUriResolver stormpathServerUriResolver() {
        return super.stormpathServerUriResolver();
    }

    @Bean
    @Override
    public Controller stormpathLogoutController() {
        return super.stormpathLogoutController();
    }

    @Bean
    @Override
    public FilterChainResolver stormpathFilterChainResolver() {
        return super.stormpathFilterChainResolver();
    }

    @Bean
    @Override
    public FilterChainManager stormpathFilterChainManager() {
        return super.stormpathFilterChainManager();
    }

    @Bean
    public Filter stormpathFilter() throws ServletException {
        StormpathFilter filter = newStormpathFilter();
        return new DefaultFilterBuilder()
            .setFilter(filter)
            .setServletContext(servletContext)
            .setName(StormpathFilter.DEFAULT_FILTER_NAME)
            .build();
    }

    @Bean
    @Override
    public AccountResolver stormpathAccountResolver() {
        return super.stormpathAccountResolver();
    }

    @Bean
    @Override
    public ContentNegotiationResolver stormpathContentNegotiationResolver() {
        return super.stormpathContentNegotiationResolver();
    }

    @Bean
    @Override
    public ControllerConfig stormpathForgotPasswordConfig() {
        return super.stormpathForgotPasswordConfig();
    }

    @Bean
    @Override
    public ControllerConfig stormpathLoginConfig() {
        return super.stormpathLoginConfig();
    }

    @Bean
    @Override
    public ControllerConfig stormpathRegisterConfig() {
        return super.stormpathRegisterConfig();
    }

    @Bean
    @Override
    public ControllerConfig stormpathVerifyConfig() {
        return super.stormpathVerifyConfig();
    }

    @Bean
    @Override
    public ChangePasswordControllerConfig stormpathChangePasswordConfig() {
        return super.stormpathChangePasswordConfig();
    }

    @Bean
    @Override
    public ControllerConfig stormpathLogoutConfig() {
        return super.stormpathLogoutConfig();
    }

    /**
     * @since 1.2.0
     */
    @Bean
    @Override
    public AccessTokenControllerConfig stormpathAccessTokenConfig(){
        return super.stormpathAccessTokenConfig();
    }

    /**
     * @since 1.2.0
     */
    @Bean
    @Override
    public GrantTypeValidator stormpathGrantTypeStatusValidator(){
        return super.stormpathGrantTypeStatusValidator();
    }
}
