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
package com.stormpath.spring.boot.autoconfigure;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.application.Application;
import com.stormpath.sdk.authc.AuthenticationResult;
import com.stormpath.sdk.cache.Cache;
import com.stormpath.sdk.lang.BiPredicate;
import com.stormpath.sdk.lang.Collections;
import com.stormpath.sdk.lang.Strings;
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
import com.stormpath.sdk.servlet.http.MediaType;
import com.stormpath.sdk.servlet.http.Resolver;
import com.stormpath.sdk.servlet.http.Saver;
import com.stormpath.sdk.servlet.http.authc.AccountStoreResolver;
import com.stormpath.sdk.servlet.http.authc.HeaderAuthenticator;
import com.stormpath.sdk.servlet.http.authc.HttpAuthenticationScheme;
import com.stormpath.sdk.servlet.idsite.IdSiteOrganizationContext;
import com.stormpath.sdk.servlet.mvc.Controller;
import com.stormpath.sdk.servlet.mvc.ExpandsResolver;
import com.stormpath.sdk.servlet.mvc.RequestFieldValueResolver;
import com.stormpath.sdk.servlet.mvc.View;
import com.stormpath.sdk.servlet.mvc.provider.AccountStoreModelFactory;
import com.stormpath.spring.config.AbstractStormpathWebMvcConfiguration;
import com.stormpath.spring.config.AccessTokenCookieProperties;
import com.stormpath.spring.config.RefreshTokenCookieProperties;
import com.stormpath.spring.config.StormpathMessageSourceConfiguration;
import com.stormpath.spring.mvc.ChangePasswordControllerConfig;
import com.stormpath.spring.mvc.MessageContextRegistrar;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.web.WebMvcAutoConfiguration;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Import;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import javax.servlet.DispatcherType;
import javax.servlet.Servlet;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.util.Collection;
import java.util.EnumSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * @since 1.0.RC4
 */
@SuppressWarnings("SpringFacetCodeInspection")
@Configuration
@ConditionalOnProperty(name = {"stormpath.enabled", "stormpath.web.enabled"}, matchIfMissing = true)
@ConditionalOnClass({Servlet.class, DispatcherServlet.class})
@ConditionalOnWebApplication
@Import(StormpathMessageSourceConfiguration.class)
@AutoConfigureAfter({WebMvcAutoConfiguration.class, StormpathAutoConfiguration.class})
public class StormpathWebMvcAutoConfiguration extends AbstractStormpathWebMvcConfiguration {

    @Bean
    @ConditionalOnMissingBean
    @Override
    public ApplicationResolver stormpathApplicationResolver() {
        return super.stormpathApplicationResolver();
    }

    @Override
    @Bean
    @ConditionalOnProperty(value = "stormpath.web.assets.enabled", matchIfMissing = true)
    public HandlerMapping stormpathStaticResourceHandlerMapping() {
        return super.stormpathStaticResourceHandlerMapping();
    }

    @Bean
    @ConditionalOnMissingBean(name = "stormpathControllerView")
    @Override
    public View stormpathControllerView() {
        return super.stormpathControllerView();
    }

    @Bean
    @ConditionalOnMissingBean(name = "stormpathLayoutInterceptor")
    public HandlerInterceptor stormpathLayoutInterceptor() {
        return super.stormpathLayoutInterceptor();
    }

    /**
     * @since 1.0.0
     */
    @Bean
    @ConditionalOnMissingBean(name = "stormpathProducedMediaTypes")
    @Override
    public List<MediaType> stormpathProducedMediaTypes() {
        return super.stormpathProducedMediaTypes();
    }

    @Bean
    @ConditionalOnMissingBean(name = "stormpathJsonView")
    @Override
    public org.springframework.web.servlet.View stormpathJsonView() {
        return super.stormpathJsonView();
    }

    @Bean
    @ConditionalOnMissingBean(name = "stormpathJsonViewResolver")
    @Override
    public org.springframework.web.servlet.ViewResolver stormpathJsonViewResolver() {
        return super.stormpathJsonViewResolver();
    }

    @Bean
    @ConditionalOnMissingBean(name = "stormpathJspViewResolver")
    @Override
    public InternalResourceViewResolver stormpathJspViewResolver() {
        return super.stormpathJspViewResolver();
    }

    @Bean
    @ConditionalOnMissingBean
    public AccountStoreResolver stormpathAccountStoreResolver() {
        return super.stormpathAccountStoreResolver();
    }

    @Bean
    @ConditionalOnMissingBean
    public UsernamePasswordRequestFactory stormpathUsernamePasswordRequestFactory() {
        return super.stormpathUsernamePasswordRequestFactory();
    }

    @Bean
    @ConditionalOnMissingBean
    public AccessTokenCookieProperties accessTokenCookieProperties() {
        return super.accessTokenCookieProperties();
    }

    @Bean
    @ConditionalOnMissingBean
    public RefreshTokenCookieProperties refreshTokenCookieProperties() {
        return super.refreshTokenCookieProperties();
    }

    @Bean
    @ConditionalOnMissingBean(name = "stormpathAccessTokenCookieConfig")
    public CookieConfig stormpathAccessTokenCookieConfig() {
        return super.stormpathAccessTokenCookieConfig();
    }

    @Bean
    @ConditionalOnMissingBean(name = "stormpathRefreshTokenCookieConfig")
    public CookieConfig stormpathRefreshTokenCookieConfig() {
        return super.stormpathRefreshTokenCookieConfig();
    }

    @Bean
    @ConditionalOnMissingBean(name = "stormpathRemoteAddrResolver")
    public Resolver<String> stormpathRemoteAddrResolver() {
        return super.stormpathRemoteAddrResolver();
    }

    @Bean
    @ConditionalOnMissingBean(name = "stormpathLocalhostResolver")
    public Resolver<Boolean> stormpathLocalhostResolver() {
        return super.stormpathLocalhostResolver();
    }

    @Bean
    @ConditionalOnMissingBean(name = "stormpathSecureResolver")
    public Resolver<Boolean> stormpathSecureResolver() {
        return super.stormpathSecureResolver();
    }

    @Bean
    @ConditionalOnMissingBean(name = "stormpathCookieAuthenticationResultSaver")
    public Saver<AuthenticationResult> stormpathCookieAuthenticationResultSaver() {
        return super.stormpathCookieAuthenticationResultSaver();
    }

    @Bean
    @ConditionalOnMissingBean(name = "stormpathSessionAuthenticationResultSaver")
    public Saver<AuthenticationResult> stormpathSessionAuthenticationResultSaver() {
        return super.stormpathSessionAuthenticationResultSaver();
    }

    @Bean
    @ConditionalOnMissingBean(name = "stormpathAuthenticationResultSavers")
    public List<Saver<AuthenticationResult>> stormpathAuthenticationResultSavers() {
        return super.stormpathAuthenticationResultSavers();
    }

    @Bean
    @ConditionalOnMissingBean(name = "stormpathAuthenticationResultSaver")
    public Saver<AuthenticationResult> stormpathAuthenticationResultSaver() {
        return super.stormpathAuthenticationResultSaver();
    }

    @Bean
    @ConditionalOnMissingBean
    public JwtSigningKeyResolver stormpathJwtSigningKeyResolver() {
        return super.stormpathJwtSigningKeyResolver();
    }

    @Bean
    @ConditionalOnMissingBean(name = "stormpathRequestEventListener")
    public RequestEventListener stormpathRequestEventListener() {
        return super.stormpathRequestEventListener();
    }

    @Bean
    @ConditionalOnMissingBean
    public Publisher<RequestEvent> stormpathRequestEventPublisher() {
        return super.stormpathRequestEventPublisher();
    }

    @Bean
    @ConditionalOnMissingBean(name = "stormpathCsrfTokenSigningKey")
    public String stormpathCsrfTokenSigningKey() {
        return super.stormpathCsrfTokenSigningKey();
    }

    @Bean
    @ConditionalOnMissingBean
    public JwtAccountResolver stormpathJwtAccountResolver() {
        return super.stormpathJwtAccountResolver();
    }

    @Bean
    @ConditionalOnMissingBean(name = "stormpathNonceCache")
    public Cache<String, String> stormpathNonceCache() {
        return super.stormpathNonceCache();
    }

    @Bean
    @ConditionalOnMissingBean
    public CsrfTokenManager stormpathCsrfTokenManager() {
        return super.stormpathCsrfTokenManager();
    }

    @Bean
    @ConditionalOnMissingBean
    @Override
    public RequestFieldValueResolver stormpathFieldValueResolver() {
        return super.stormpathFieldValueResolver();
    }

    @Bean
    @ConditionalOnMissingBean
    public AccessTokenResultFactory stormpathAccessTokenResultFactory() {
        return super.stormpathAccessTokenResultFactory();
    }

    @Bean
    @ConditionalOnMissingBean
    public WrappedServletRequestFactory stormpathWrappedServletRequestFactory() {
        return super.stormpathWrappedServletRequestFactory();
    }

    @Bean
    @ConditionalOnMissingBean(name = "stormpathBasicAuthenticationScheme")
    public HttpAuthenticationScheme stormpathBasicAuthenticationScheme() {
        return super.stormpathBasicAuthenticationScheme();
    }

    @Bean
    @ConditionalOnMissingBean(name = "stormpathBearerAuthenticationScheme")
    public HttpAuthenticationScheme stormpathBearerAuthenticationScheme() {
        return super.stormpathBearerAuthenticationScheme();
    }

    @Bean
    @ConditionalOnMissingBean(name = "stormpathHttpAuthenticationSchemes")
    public List<HttpAuthenticationScheme> stormpathHttpAuthenticationSchemes() {
        return super.stormpathHttpAuthenticationSchemes();
    }

    @Bean
    @ConditionalOnMissingBean
    public HeaderAuthenticator stormpathAuthorizationHeaderAuthenticator() {
        return super.stormpathAuthorizationHeaderAuthenticator();
    }

    @Bean
    @ConditionalOnMissingBean(name = "stormpathAuthorizationHeaderAccountResolver")
    public Resolver<Account> stormpathAuthorizationHeaderAccountResolver() {
        return super.stormpathAuthorizationHeaderAccountResolver();
    }

    @Bean
    @ConditionalOnMissingBean(name = "stormpathCookieAccountResolver")
    public Resolver<Account> stormpathCookieAccountResolver() {
        return super.stormpathCookieAccountResolver();
    }

    @Bean
    @ConditionalOnMissingBean(name = "stormpathSessionAccountResolver")
    public Resolver<Account> stormpathSessionAccountResolver() {
        return super.stormpathSessionAccountResolver();
    }

    @Bean
    @ConditionalOnMissingBean(name = "stormpathSubdomainResolver")
    public Resolver<List<String>> stormpathSubdomainResolver() {
        return super.stormpathSubdomainResolver();
    }

    @Bean
    @ConditionalOnMissingBean(name = "stormpathOrganizationNameKeyResolver")
    public Resolver<String> stormpathOrganizationNameKeyResolver() {
        return super.stormpathOrganizationNameKeyResolver();
    }

    @Bean
    @ConditionalOnMissingBean(name = "stormpathIdSiteOrganizationResolver")
    public Resolver<IdSiteOrganizationContext> stormpathIdSiteOrganizationResolver() {
        return super.stormpathIdSiteOrganizationResolver();
    }

    @Bean
    @ConditionalOnMissingBean(name = "stormpathAccountResolvers")
    public List<Resolver<Account>> stormpathAccountResolvers() {
        return super.stormpathAccountResolvers();
    }

    @Bean
    @ConditionalOnMissingBean(name = "stormpathGoogleCallbackController")
    public Controller stormpathGoogleCallbackController() {
        return super.stormpathGoogleCallbackController();
    }

    @Bean
    @ConditionalOnMissingBean(name = "stormpathGithubCallbackController")
    public Controller stormpathGithubCallbackController() {
        return super.stormpathGithubCallbackController();
    }

    @Bean
    @ConditionalOnMissingBean(name = "stormpathFacebookCallbackController")
    public Controller stormpathFacebookCallbackController() {
        return super.stormpathFacebookCallbackController();
    }

    @Bean
    @ConditionalOnMissingBean(name = "stormpathLinkedinCallbackController")
    public Controller stormpathLinkedinCallbackController() {
        return super.stormpathLinkedinCallbackController();
    }

    @Bean
    @ConditionalOnMissingBean(name = "stormpathLoginController")
    public Controller stormpathLoginController() {
        return super.stormpathLoginController();
    }

    @Bean
    @ConditionalOnMissingBean
    @Override
    public AccountStoreModelFactory stormpathAccountStoreModelFactory() {
        return super.stormpathAccountStoreModelFactory();
    }

    @Bean
    @ConditionalOnMissingBean(name = "stormpathForgotPasswordController")
    public Controller stormpathForgotPasswordController() {
        return super.stormpathForgotPasswordController();
    }

    @Bean
    @ConditionalOnMissingBean(name = "stormpathSpringLocaleResolver")
    public LocaleResolver stormpathSpringLocaleResolver() {
        return super.stormpathSpringLocaleResolver();
    }

    @Bean
    @ConditionalOnMissingBean(name = "stormpathLocaleChangeInterceptor")
    public LocaleChangeInterceptor stormpathLocaleChangeInterceptor() {
        return super.stormpathLocaleChangeInterceptor();
    }

    @Bean
    @ConditionalOnMissingBean(name = "stormpathRequestClientAttributeNames")
    public Set<String> stormpathRequestClientAttributeNames() {
        return super.stormpathRequestClientAttributeNames();
    }

    @Bean
    @ConditionalOnMissingBean(name = "stormpathRequestApplicationAttributeNames")
    public Set<String> stormpathRequestApplicationAttributeNames() {
        return super.stormpathRequestApplicationAttributeNames();
    }

    @Bean
    @ConditionalOnMissingBean(name = "stormpathLocaleResolver")
    public Resolver<Locale> stormpathLocaleResolver() {
        return super.stormpathLocaleResolver();
    }

    @Bean
    @ConditionalOnMissingBean
    @Override
    public MessageContextRegistrar stormpathMessageContextRegistrar() {
        return super.stormpathMessageContextRegistrar();
    }

    @Bean
    @ConditionalOnMissingBean
    public com.stormpath.sdk.servlet.i18n.MessageSource stormpathMessageSource() {
        return super.stormpathMessageSource();
    }

    @Bean
    @ConditionalOnMissingBean(name = "stormpathRegisterController")
    public Controller stormpathRegisterController() {
        return super.stormpathRegisterController();
    }

    @Bean
    @ConditionalOnMissingBean(name = "stormpathRegisterEnabledResolver")
    @Override
    public Resolver<Boolean> stormpathRegisterEnabledResolver() {
        return super.stormpathRegisterEnabledResolver();
    }

    @Bean
    @ConditionalOnMissingBean(name = "stormpathRegisterEnabledPredicate")
    @Override
    public BiPredicate<Boolean, Application> stormpathRegisterEnabledPredicate() {
        return super.stormpathRegisterEnabledPredicate();
    }

    @Bean
    @ConditionalOnMissingBean(name = "stormpathVerifyController")
    public Controller stormpathVerifyController() {
        return super.stormpathVerifyController();
    }

    @Bean
    @ConditionalOnMissingBean(name = "stormpathChangePasswordController")
    public Controller stormpathChangePasswordController() {
        return super.stormpathChangePasswordController();
    }

    @Bean
    @ConditionalOnMissingBean(name = "stormpathAccessTokenController")
    public Controller stormpathAccessTokenController() {
        return super.stormpathAccessTokenController();
    }

    @Bean
    @ConditionalOnMissingBean
    public AccessTokenAuthenticationRequestFactory stormpathAccessTokenAuthenticationRequestFactory() {
        return super.stormpathAccessTokenAuthenticationRequestFactory();
    }

    @Bean
    @ConditionalOnMissingBean(name = "stormpathAccessTokenRequestAuthorizer")
    public RequestAuthorizer stormpathAccessTokenRequestAuthorizer() {
        return super.stormpathAccessTokenRequestAuthorizer();
    }

    @Bean
    @ConditionalOnMissingBean(name = "stormpathOriginAccessTokenRequestAuthorizer")
    public RequestAuthorizer stormpathOriginAccessTokenRequestAuthorizer() {
        return super.stormpathOriginAccessTokenRequestAuthorizer();
    }

    @Bean
    @ConditionalOnMissingBean
    public ServerUriResolver stormpathServerUriResolver() {
        return super.stormpathServerUriResolver();
    }

    @Bean
    @ConditionalOnMissingBean(name = "stormpathLogoutController")
    public Controller stormpathLogoutController() {
        return super.stormpathLogoutController();
    }

    @Bean
    @ConditionalOnMissingBean(name = "stormpathIdSiteResultController")
    public Controller stormpathIdSiteResultController() {
        return super.stormpathIdSiteResultController();
    }

    @Override
    @Bean
    @ConditionalOnMissingBean(name = "stormpathMeController")
    public Controller stormpathMeController() {
        return super.stormpathMeController();
    }

    @Bean
    @ConditionalOnMissingBean
    public ExpandsResolver stormpathMeExpandsResolver(){
        return super.stormpathMeExpandsResolver();
    }

    @Bean
    public ServletListenerRegistrationBean<ServletContextListener> stormpathServletContextListener() {

        ServletContextListener listener = new ServletContextListener() {
            @Override
            public void contextInitialized(ServletContextEvent sce) {
                sce.getServletContext().setAttribute(ClientLoader.CLIENT_ATTRIBUTE_KEY, client);
                sce.getServletContext().setAttribute(ApplicationLoader.APP_ATTRIBUTE_NAME, application);
            }

            @Override
            public void contextDestroyed(ServletContextEvent sce) {
                //no op
            }
        };

        return new ServletListenerRegistrationBean<>(listener);
    }

    @Bean
    @ConditionalOnMissingBean
    @Override
    public FilterChainResolver stormpathFilterChainResolver() {
        return super.stormpathFilterChainResolver();
    }

    @Bean
    @ConditionalOnMissingBean
    @Override
    public FilterChainManager stormpathFilterChainManager() {
        return super.stormpathFilterChainManager();
    }

    public Collection<String> stormpathFilterUrlPatterns() {
        return Strings.commaDelimitedListToSet(stormpathFilterUrlPatterns);
    }

    public Collection<String> stormpathFilterServletNames() {
        return Strings.commaDelimitedListToSet(stormpathFilterServletNames);
    }

    public Set<DispatcherType> stormpathFilterDispatcherTypes() {
        Set<String> names = Strings.commaDelimitedListToSet(stormpathFilterDispatcherTypes);
        if (Collections.isEmpty(names)) {
            return java.util.Collections.emptySet();
        }
        Set<DispatcherType> types = new LinkedHashSet<DispatcherType>(names.size());
        for (String name : names) {
            types.add(DispatcherType.valueOf(name));
        }
        return types;
    }

    @Bean
    @ConditionalOnMissingBean(name = "stormpathFilter")
    @DependsOn("stormpathServletContextListener")
    public FilterRegistrationBean stormpathFilter() {
        StormpathFilter filter = newStormpathFilter();
        FilterRegistrationBean bean = new FilterRegistrationBean();
        bean.setFilter(filter);
        bean.setEnabled(stormpathFilterEnabled);
        bean.setOrder(stormpathFilterOrder);
        bean.setUrlPatterns(stormpathFilterUrlPatterns());
        bean.setServletNames(stormpathFilterServletNames());
        bean.setDispatcherTypes(EnumSet.copyOf(stormpathFilterDispatcherTypes()));
        bean.setMatchAfter(stormpathFilterMatchAfter);
        return bean;
    }

    @Bean
    @ConditionalOnMissingBean
    @Override
    public AccountResolver stormpathAccountResolver() {
        return super.stormpathAccountResolver();
    }

    @Bean
    @ConditionalOnMissingBean
    @Override
    public ContentNegotiationResolver stormpathContentNegotiationResolver() {
        return super.stormpathContentNegotiationResolver();
    }

    @Bean
    @ConditionalOnMissingBean(name = "stormpathLoginConfig")
    public ControllerConfig stormpathLoginConfig() {
        return super.stormpathLoginConfig();
    }

    @Bean
    @ConditionalOnMissingBean(name = "stormpathForgotPasswordConfig")
    public ControllerConfig stormpathForgotPasswordConfig() {
        return super.stormpathForgotPasswordConfig();
    }

    @Bean
    @ConditionalOnMissingBean(name = "stormpathRegisterConfig")
    public ControllerConfig stormpathRegisterConfig() {
        return super.stormpathRegisterConfig();
    }

    @Bean
    @ConditionalOnMissingBean(name = "stormpathChangePasswordConfig")
    public ChangePasswordControllerConfig stormpathChangePasswordConfig() {
        return super.stormpathChangePasswordConfig();
    }

    @Bean
    @ConditionalOnMissingBean(name = "stormpathLogoutConfig")
    public ControllerConfig stormpathLogoutConfig() {
        return super.stormpathLogoutConfig();
    }

    @Bean
    @ConditionalOnMissingBean(name = "stormpathVerifyConfig")
    public ControllerConfig stormpathVerifyConfig() {
        return super.stormpathVerifyConfig();
    }
}
