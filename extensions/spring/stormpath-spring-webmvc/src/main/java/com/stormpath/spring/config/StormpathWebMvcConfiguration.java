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
import com.stormpath.sdk.authc.AuthenticationResult;
import com.stormpath.sdk.cache.Cache;
import com.stormpath.sdk.servlet.application.ApplicationLoader;
import com.stormpath.sdk.servlet.authz.RequestAuthorizer;
import com.stormpath.sdk.servlet.client.ClientLoader;
import com.stormpath.sdk.servlet.config.Config;
import com.stormpath.sdk.servlet.config.ConfigLoader;
import com.stormpath.sdk.servlet.config.CookieConfig;
import com.stormpath.sdk.servlet.csrf.CsrfTokenManager;
import com.stormpath.sdk.servlet.event.RequestEvent;
import com.stormpath.sdk.servlet.event.RequestEventListener;
import com.stormpath.sdk.servlet.event.impl.Publisher;
import com.stormpath.sdk.servlet.filter.DefaultFilterBuilder;
import com.stormpath.sdk.servlet.filter.FilterBuilder;
import com.stormpath.sdk.servlet.filter.FilterChainResolver;
import com.stormpath.sdk.servlet.filter.ServerUriResolver;
import com.stormpath.sdk.servlet.filter.StormpathFilter;
import com.stormpath.sdk.servlet.filter.UsernamePasswordRequestFactory;
import com.stormpath.sdk.servlet.filter.WrappedServletRequestFactory;
import com.stormpath.sdk.servlet.filter.account.AuthenticationJwtFactory;
import com.stormpath.sdk.servlet.filter.account.AuthenticationResultSaver;
import com.stormpath.sdk.servlet.filter.account.JwtAccountResolver;
import com.stormpath.sdk.servlet.filter.account.JwtSigningKeyResolver;
import com.stormpath.sdk.servlet.filter.oauth.AccessTokenAuthenticationRequestFactory;
import com.stormpath.sdk.servlet.filter.oauth.AccessTokenResultFactory;
import com.stormpath.sdk.servlet.form.Field;
import com.stormpath.sdk.servlet.http.Resolver;
import com.stormpath.sdk.servlet.http.Saver;
import com.stormpath.sdk.servlet.http.authc.AccountStoreResolver;
import com.stormpath.sdk.servlet.http.authc.HeaderAuthenticator;
import com.stormpath.sdk.servlet.http.authc.HttpAuthenticationScheme;
import com.stormpath.sdk.servlet.i18n.MessageTag;
import com.stormpath.sdk.servlet.idsite.IdSiteOrganizationContext;
import com.stormpath.sdk.servlet.mvc.FormFieldParser;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.mvc.Controller;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;

import javax.servlet.Filter;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * @since 1.0.RC4
 */
@Configuration
public class StormpathWebMvcConfiguration extends AbstractStormpathWebMvcConfiguration
    implements ServletContextAware, InitializingBean {

    private ServletContext servletContext;

    @Override
    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        servletContext.setAttribute(ClientLoader.CLIENT_ATTRIBUTE_KEY, client);
        servletContext.setAttribute(ApplicationLoader.APP_ATTRIBUTE_NAME, application);
        servletContext.setAttribute(ConfigLoader.CONFIG_ATTRIBUTE_NAME, stormpathInternalConfig());
    }

    @SuppressWarnings("SpringFacetCodeInspection")
    @Configuration
    public static class StormpathWebMvcStaticResourceConfigurer extends WebMvcConfigurerAdapter {

        @Override
        public void addResourceHandlers(ResourceHandlerRegistry registry) {
            registry.addResourceHandler("/assets/css/*stormpath.css")
                    //reference the actual files in the stormpath-sdk-servlet .jar:
                    .addResourceLocations("classpath:/META-INF/resources/assets/css/");
        }
    }

    @Bean
    public InternalResourceViewResolver stormpathJspViewResolver() {
        InternalResourceViewResolver bean = new InternalResourceViewResolver();
        bean.setViewClass(JstlView.class);
        bean.setPrefix("/WEB-INF/jsp/");
        bean.setSuffix(".jsp");
        return bean;
    }

    //this isn't needed in a Spring environment, but we have to have it because the
    //default .jsp views reference it (MessageTag uses a Config instance:
    @Bean
    public Config stormpathInternalConfig() {

        final com.stormpath.sdk.servlet.i18n.MessageSource messageSource = stormpathMessageSource();
        final Resolver<Locale> localeResolver = stormpathLocaleResolver();

        return new Config() {
            @Override
            public String getLoginUrl() {
                return loginUri;
            }

            @Override
            public String getLoginNextUrl() {
                return loginNextUri;
            }

            @Override
            public String getLogoutUrl() {
                return logoutUri;
            }

            @Override
            public String getForgotPasswordUrl() {
                return forgotUri;
            }

            @Override
            public String getForgotPasswordNextUrl() {
                return forgotNextUri;
            }

            @Override
            public String getChangePasswordUrl() {
                return changePasswordUri;
            }

            @Override
            public String getChangePasswordNextUrl() {
                return changePasswordNextUri;
            }

            @Override
            public String getLogoutNextUrl() {
                return logoutNextUri;
            }

            @Override
            public boolean isLogoutInvalidateHttpSession() {
                return logoutInvalidateHttpSession;
            }

            @Override
            public String getAccessTokenUrl() {
                return accessTokenUri;
            }

            @Override
            public String getRegisterUrl() {
                return registerUri;
            }

            @Override
            public String getRegisterNextUrl() {
                return registerNextUri;
            }

            @Override
            public String getVerifyUrl() {
                return verifyUri;
            }

            @Override
            public String getVerifyNextUrl() {
                return verifyNextUri;
            }

            @Override
            public String getSendVerificationEmailUrl() {
                return sendVerificationEmailUri;
            }

            @Override
            public boolean isVerifyEnabled() {
                return verifyEnabled;
            }

            @Override
            public String getUnauthorizedUrl() {
                return "/unauthorized";
            }

            @Override
            public CookieConfig getAccountCookieConfig() {
                return stormpathAccountCookieConfig();
            }

            @Override
            public long getAccountJwtTtl() {
                return accountJwtTtl;
            }

            @Override
            public <T> T getInstance(String classPropertyName) throws ServletException {
                if (MessageTag.LOCALE_RESOLVER_CONFIG_KEY.equals(classPropertyName)) {
                    return (T)localeResolver;
                } else if (MessageTag.MESSAGE_SOURCE_CONFIG_KEY.equals(classPropertyName)) {
                    return (T)messageSource;
                } else {
                    String msg = "The config key '" + classPropertyName + "' is not supported in Spring environments " +
                                 "- inject the required dependency via Spring config (e.g. @Autowired) instead.";
                    throw new UnsupportedOperationException(msg);
                }
            }

            @Override
            public <T> Map<String, T> getInstances(String propertyNamePrefix, Class<T> expectedType)
                throws ServletException {
                throw new UnsupportedOperationException("Not supported for spring environments.");
            }

            @Override
            public int size() {
                throw new UnsupportedOperationException("Not supported for spring environments.");
            }

            @Override
            public boolean isEmpty() {
                throw new UnsupportedOperationException("Not supported for spring environments.");
            }

            @Override
            public boolean containsKey(Object o) {
                throw new UnsupportedOperationException("Not supported for spring environments.");
            }

            @Override
            public boolean containsValue(Object o) {
                throw new UnsupportedOperationException("Not supported for spring environments.");
            }

            @Override
            public String get(Object o) {
                throw new UnsupportedOperationException("Not supported for spring environments.");
            }

            @Override
            public String put(String s, String s2) {
                throw new UnsupportedOperationException("Not supported for spring environments.");
            }

            @Override
            public String remove(Object o) {
                throw new UnsupportedOperationException("Not supported for spring environments.");
            }

            @Override
            public void putAll(Map<? extends String, ? extends String> map) {
                throw new UnsupportedOperationException("Not supported for spring environments.");
            }

            @Override
            public void clear() {
                throw new UnsupportedOperationException("Not supported for spring environments.");
            }

            @Override
            public Set<String> keySet() {
                //The Spring Boot WebMVC + Spring Security Example causes this method to be invoked. Thus, we cannot throw an exception here.
                return Collections.EMPTY_SET;
            }

            @Override
            public Collection<String> values() {
                throw new UnsupportedOperationException("Not supported for spring environments.");
            }

            @Override
            public Set<Entry<String, String>> entrySet() {
                throw new UnsupportedOperationException("Not supported for spring environments.");
            }
        };
    }

    @Bean
    public HandlerMapping stormpathHandlerMapping() throws Exception {
        return super.stormpathHandlerMapping();
    }

    @Bean
    public HandlerInterceptor stormpathLayoutInterceptor() throws Exception {
        return super.stormpathLayoutInterceptor();
    }

    @Bean
    public AccountStoreResolver stormpathAccountStoreResolver() {
        return super.stormpathAccountStoreResolver();
    }

    @Bean
    public UsernamePasswordRequestFactory stormpathUsernamePasswordRequestFactory() {
        return super.stormpathUsernamePasswordRequestFactory();
    }

    @Bean
    public CookieConfig stormpathAccountCookieConfig() {
        return super.stormpathAccountCookieConfig();
    }

    @Bean
    public Resolver<String> stormpathRemoteAddrResolver() {
        return super.stormpathRemoteAddrResolver();
    }

    @Bean
    public Resolver<Boolean> stormpathLocalhostResolver() {
        return super.stormpathLocalhostResolver();
    }

    @Bean
    public Resolver<Boolean> stormpathSecureResolver() {
        return super.stormpathSecureResolver();
    }

    @Bean
    public Saver<AuthenticationResult> stormpathCookieAuthenticationResultSaver() {
        return super.stormpathCookieAuthenticationResultSaver();
    }

    @Bean
    public Saver<AuthenticationResult> stormpathSessionAuthenticationResultSaver() {
        return super.stormpathSessionAuthenticationResultSaver();
    }

    @Bean
    public List<Saver<AuthenticationResult>> stormpathAuthenticationResultSavers() {
        return super.stormpathAuthenticationResultSavers();
    }

    @Bean
    public AuthenticationResultSaver stormpathAuthenticationResultSaver() {
        return super.stormpathAuthenticationResultSaver();
    }

    @Bean
    public AuthenticationJwtFactory stormpathAuthenticationJwtFactory() {
        return super.stormpathAuthenticationJwtFactory();
    }

    @Bean
    public JwtSigningKeyResolver stormpathJwtSigningKeyResolver() {
        return super.stormpathJwtSigningKeyResolver();
    }

    @Bean
    public RequestEventListener stormpathRequestEventListener() {
        return super.stormpathRequestEventListener();
    }

    @Bean
    public Publisher<RequestEvent> stormpathRequestEventPublisher() {
        return super.stormpathRequestEventPublisher();
    }

    @Bean
    public String stormpathCsrfTokenSigningKey() {
        return super.stormpathCsrfTokenSigningKey();
    }

    @Bean
    public JwtAccountResolver stormpathJwtAccountResolver() {
        return super.stormpathJwtAccountResolver();
    }

    @Bean
    public Cache<String, String> stormpathNonceCache() {
        return super.stormpathNonceCache();
    }

    @Bean
    public CsrfTokenManager stormpathCsrfTokenManager() {
        return super.stormpathCsrfTokenManager();
    }

    @Bean
    public AccessTokenResultFactory stormpathAccessTokenResultFactory() {
        return super.stormpathAccessTokenResultFactory();
    }

    @Bean
    public WrappedServletRequestFactory stormpathWrappedServletRequestFactory() {
        return super.stormpathWrappedServletRequestFactory();
    }

    @Bean
    public HttpAuthenticationScheme stormpathBasicAuthenticationScheme() {
        return super.stormpathBasicAuthenticationScheme();
    }

    @Bean
    public HttpAuthenticationScheme stormpathBearerAuthenticationScheme() {
        return super.stormpathBearerAuthenticationScheme();
    }

    @Bean
    public List<HttpAuthenticationScheme> stormpathHttpAuthenticationSchemes() {
        return super.stormpathHttpAuthenticationSchemes();
    }

    @Bean
    public HeaderAuthenticator stormpathAuthorizationHeaderAuthenticator() {
        return super.stormpathAuthorizationHeaderAuthenticator();
    }

    @Bean
    public Resolver<Account> stormpathAuthorizationHeaderAccountResolver() {
        return super.stormpathAuthorizationHeaderAccountResolver();
    }

    @Bean
    public Resolver<Account> stormpathCookieAccountResolver() {
        return super.stormpathCookieAccountResolver();
    }

    @Bean
    public Resolver<Account> stormpathSessionAccountResolver() {
        return super.stormpathSessionAccountResolver();
    }

    @Bean
    public List<Resolver<Account>> stormpathAccountResolvers() {
        return super.stormpathAccountResolvers();
    }

    @Bean
    public Resolver<List<String>> stormpathSubdomainResolver() {
        return super.stormpathSubdomainResolver();
    }

    @Bean
    public Resolver<String> stormpathOrganizationNameKeyResolver() {
        return super.stormpathOrganizationNameKeyResolver();
    }

    @Bean
    public Resolver<IdSiteOrganizationContext> stormpathIdSiteOrganizationResolver() {
        return super.stormpathIdSiteOrganizationResolver();
    }

    @Bean
    public Controller stormpathLoginController() {
        return super.stormpathLoginController();
    }

    @Bean
    public Controller stormpathForgotPasswordController() {
        return super.stormpathForgotPasswordController();
    }

    @Bean
    public List<Field> stormpathRegisterFormFields() {
        return super.stormpathRegisterFormFields();
    }

    @Bean
    public FormFieldParser stormpathRegisterFormFieldParser() {
        return super.stormpathRegisterFormFieldParser();
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
    public MessageSource stormpathSpringMessageSource() {
        return super.stormpathSpringMessageSource();
    }

    @Bean
    public com.stormpath.sdk.servlet.i18n.MessageSource stormpathMessageSource() {
        return super.stormpathMessageSource();
    }

    @Bean
    public Controller stormpathRegisterController() {
        return super.stormpathRegisterController();
    }

    @Bean
    public Controller stormpathVerifyController() {
        return super.stormpathVerifyController();
    }

    @Bean
    public Controller stormpathChangePasswordController() {
        return super.stormpathChangePasswordController();
    }

    @Bean
    public Controller stormpathAccessTokenController() {
        return super.stormpathAccessTokenController();
    }

    @Bean
    public Controller stormpathIdSiteResultController() {
        return super.stormpathIdSiteResultController();
    }

    @Bean
    public AccessTokenAuthenticationRequestFactory stormpathAccessTokenAuthenticationRequestFactory() {
        return super.stormpathAccessTokenAuthenticationRequestFactory();
    }

    @Bean
    public RequestAuthorizer stormpathAccessTokenRequestAuthorizer() {
        return super.stormpathAccessTokenRequestAuthorizer();
    }

    @Bean
    public RequestAuthorizer stormpathOriginAccessTokenRequestAuthorizer() {
        return super.stormpathOriginAccessTokenRequestAuthorizer();
    }

    @Bean
    public ServerUriResolver stormpathServerUriResolver() {
        return super.stormpathServerUriResolver();
    }

    @Bean
    public com.stormpath.sdk.servlet.mvc.Controller stormpathMvcLogoutController() {
        return super.stormpathMvcLogoutController();
    }

    @Bean
    public Controller stormpathLogoutController() {
        return super.stormpathLogoutController();
    }

    @Bean
    public FilterChainResolver stormpathFilterChainResolver() {
        return super.stormpathFilterChainResolver();
    }

    @Bean
    public Filter stormpathFilter() throws ServletException {

        FilterBuilder builder = new DefaultFilterBuilder().setFilterClass(
            SpringStormpathFilter.class) //suppress config logic since Spring is used for config here
            .setServletContext(servletContext).setName("stormpathFilter");

        StormpathFilter filter = (StormpathFilter) builder.build();

        filter.setEnabled(stormpathFilterEnabled);
        filter.setClientRequestAttributeNames(stormpathRequestClientAttributeNames());
        filter.setApplicationRequestAttributeNames(stormpathRequestApplicationAttributeNames());
        filter.setFilterChainResolver(stormpathFilterChainResolver());
        filter.setWrappedServletRequestFactory(stormpathWrappedServletRequestFactory());

        return filter;
    }

    @Bean
    public Filter stormpathAccountResolverFilter() {
        return super.stormpathAccountResolverFilter();
    }

}
