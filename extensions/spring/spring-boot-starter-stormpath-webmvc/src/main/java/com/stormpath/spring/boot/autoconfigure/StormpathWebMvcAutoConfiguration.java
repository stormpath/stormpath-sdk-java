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

import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.authc.AuthenticationResult;
import com.stormpath.sdk.cache.Cache;
import com.stormpath.sdk.lang.Collections;
import com.stormpath.sdk.lang.Strings;
import com.stormpath.sdk.servlet.application.ApplicationLoader;
import com.stormpath.sdk.servlet.authz.RequestAuthorizer;
import com.stormpath.sdk.servlet.client.ClientLoader;
import com.stormpath.sdk.servlet.config.CookieConfig;
import com.stormpath.sdk.servlet.csrf.CsrfTokenManager;
import com.stormpath.sdk.servlet.event.RequestEvent;
import com.stormpath.sdk.servlet.event.RequestEventListener;
import com.stormpath.sdk.servlet.event.impl.Publisher;
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
import com.stormpath.sdk.servlet.mvc.FormFieldParser;
import com.stormpath.spring.config.AbstractStormpathWebMvcConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.web.WebMvcAutoConfiguration;
import org.springframework.boot.context.embedded.FilterRegistrationBean;
import org.springframework.boot.context.embedded.ServletListenerRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.core.io.Resource;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.mvc.Controller;

import javax.servlet.DispatcherType;
import javax.servlet.Filter;
import javax.servlet.Servlet;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletException;
import java.util.ArrayList;
import java.util.Arrays;
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
@ConditionalOnProperty(name = { "stormpath.enabled", "stormpath.web.enabled" }, matchIfMissing = true)
@ConditionalOnClass({ Servlet.class, DispatcherServlet.class })
@ConditionalOnWebApplication
@AutoConfigureAfter({ StormpathAutoConfiguration.class, WebMvcAutoConfiguration.class })
public class StormpathWebMvcAutoConfiguration extends AbstractStormpathWebMvcConfiguration {

    @Bean
    @ConditionalOnMissingBean(name = "stormpathHandlerMapping")
    public HandlerMapping stormpathHandlerMapping() throws Exception {
        return super.stormpathHandlerMapping();
    }

    @Bean
    @ConditionalOnMissingBean(name = "stormpathLayoutInterceptor")
    public HandlerInterceptor stormpathLayoutInterceptor() throws Exception {
        return super.stormpathLayoutInterceptor();
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
    public CookieConfig stormpathAccountCookieConfig() {
        return super.stormpathAccountCookieConfig();
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
    @ConditionalOnMissingBean(name="stormpathCookieAuthenticationResultSaver")
    public Saver<AuthenticationResult> stormpathCookieAuthenticationResultSaver() {
        return super.stormpathCookieAuthenticationResultSaver();
    }

    @Bean
    @ConditionalOnMissingBean(name="stormpathSessionAuthenticationResultSaver")
    public Saver<AuthenticationResult> stormpathSessionAuthenticationResultSaver() {
        return super.stormpathSessionAuthenticationResultSaver();
    }

    @Bean
    @ConditionalOnMissingBean(name="stormpathAuthenticationResultSavers")
    public List<Saver<AuthenticationResult>> stormpathAuthenticationResultSavers() {
        return super.stormpathAuthenticationResultSavers();
    }

    @Bean
    @ConditionalOnMissingBean(name="stormpathAuthenticationResultSaver")
    public AuthenticationResultSaver stormpathAuthenticationResultSaver() {
        return super.stormpathAuthenticationResultSaver();
    }

    @Bean
    @ConditionalOnMissingBean
    public AuthenticationJwtFactory stormpathAuthenticationJwtFactory() {
        return super.stormpathAuthenticationJwtFactory();
    }

    @Bean
    @ConditionalOnMissingBean
    public JwtSigningKeyResolver stormpathJwtSigningKeyResolver() {
        return super.stormpathJwtSigningKeyResolver();
    }

    @Bean
    @ConditionalOnMissingBean
    public RequestEventListener stormpathRequestEventListener() {
        return super.stormpathRequestEventListener();
    }

    @Bean
    @ConditionalOnMissingBean
    public Publisher<RequestEvent> stormpathRequestEventPublisher() {
        return super.stormpathRequestEventPublisher();
    }

    @Bean
    @ConditionalOnMissingBean(name="stormpathCsrfTokenSigningKey")
    public String stormpathCsrfTokenSigningKey() {
        return super.stormpathCsrfTokenSigningKey();
    }

    @Bean
    @ConditionalOnMissingBean
    public JwtAccountResolver stormpathJwtAccountResolver() {
        return super.stormpathJwtAccountResolver();
    }

    @Bean
    @ConditionalOnMissingBean(name="stormpathNonceCache")
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
    public AccessTokenResultFactory stormpathAccessTokenResultFactory() {
        return super.stormpathAccessTokenResultFactory();
    }

    @Bean
    @ConditionalOnMissingBean
    public WrappedServletRequestFactory stormpathWrappedServletRequestFactory() {
        return super.stormpathWrappedServletRequestFactory();
    }

    @Bean
    @ConditionalOnMissingBean(name="stormpathBasicAuthenticationScheme")
    public HttpAuthenticationScheme stormpathBasicAuthenticationScheme() {
        return super.stormpathBasicAuthenticationScheme();
    }

    @Bean
    @ConditionalOnMissingBean(name="stormpathBearerAuthenticationScheme")
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
    @ConditionalOnMissingBean(name = "stormpathAccountResolvers")
    public List<Resolver<Account>> stormpathAccountResolvers() {
        return super.stormpathAccountResolvers();
    }

    @Bean
    @ConditionalOnMissingBean(name="stormpathLoginController")
    public Controller stormpathLoginController() {
        return super.stormpathLoginController();
    }

    @Bean
    @ConditionalOnMissingBean(name="stormpathForgotPasswordController")
    public Controller stormpathForgotPasswordController() {
        return super.stormpathForgotPasswordController();
    }

    @Bean
    @ConditionalOnMissingBean(name="stormpathRegisterFormFields")
    public List<Field> stormpathRegisterFormFields() {
        return super.stormpathRegisterFormFields();
    }

    @Bean
    @ConditionalOnMissingBean
    public FormFieldParser stormpathRegisterFormFieldParser() {
        return super.stormpathRegisterFormFieldParser();
    }

    @Bean
    @ConditionalOnMissingBean(name="stormpathSpringLocaleResolver")
    public LocaleResolver stormpathSpringLocaleResolver() {
        return super.stormpathSpringLocaleResolver();
    }

    @Bean
    @ConditionalOnMissingBean(name="stormpathLocaleChangeInterceptor")
    public LocaleChangeInterceptor stormpathLocaleChangeInterceptor() {
        return super.stormpathLocaleChangeInterceptor();
    }

    @Bean
    @ConditionalOnMissingBean(name="stormpathLocaleResolver")
    public Resolver<Locale> stormpathLocaleResolver() {
        return super.stormpathLocaleResolver();
    }

    @Bean
    @ConditionalOnMissingBean(name="stormpathSpringMessageSource")
    public MessageSource stormpathSpringMessageSource() {
        return super.stormpathSpringMessageSource();
    }

    @Bean
    @ConditionalOnMissingBean
    public com.stormpath.sdk.servlet.i18n.MessageSource stormpathMessageSource() {
        return super.stormpathMessageSource();
    }

    @Bean
    @ConditionalOnMissingBean(name="stormpathRegisterController")
    public Controller stormpathRegisterController() {
        return super.stormpathRegisterController();
    }

    @Bean
    @ConditionalOnMissingBean(name="stormpathVerifyController")
    public Controller stormpathVerifyController() {
        return super.stormpathVerifyController();
    }

    @Bean
    @ConditionalOnMissingBean(name="stormpathChangePasswordController")
    public Controller stormpathChangePasswordController() {
        return super.stormpathChangePasswordController();
    }

    @Bean
    @ConditionalOnMissingBean(name="stormpathAccessTokenController")
    public Controller stormpathAccessTokenController() {
        return super.stormpathAccessTokenController();
    }

    @Bean
    @ConditionalOnMissingBean
    public AccessTokenAuthenticationRequestFactory stormpathAccessTokenAuthenticationRequestFactory() {
        return super.stormpathAccessTokenAuthenticationRequestFactory();
    }

    @Bean
    @ConditionalOnMissingBean(name="stormpathAccessTokenRequestAuthorizer")
    public RequestAuthorizer stormpathAccessTokenRequestAuthorizer() {
        return super.stormpathAccessTokenRequestAuthorizer();
    }

    @Bean
    @ConditionalOnMissingBean(name="stormpathOriginAccessTokenRequestAuthorizer")
    public RequestAuthorizer stormpathOriginAccessTokenRequestAuthorizer() {
        return super.stormpathOriginAccessTokenRequestAuthorizer();
    }

    @Bean
    @ConditionalOnMissingBean
    public ServerUriResolver stormpathServerUriResolver() {
        return super.stormpathServerUriResolver();
    }

    @Bean
    @ConditionalOnMissingBean(name="stormpathLogoutController")
    public Controller stormpathLogoutController() {
        return super.stormpathLogoutController();
    }

    @SuppressWarnings("UnusedDeclaration")
    @Configuration
    @ConditionalOnMissingBean(MessageSource.class)
    @ConditionalOnProperty(prefix = "spring.messages", name = "basename")
    public static class MessageSourceConfiguration {

        @Bean
        public MessageSource messageSource(@Value("${spring.messages.basename}") String basename) {
            List<String> list = new ArrayList<String>();

            if (StringUtils.hasText(basename)) {
                String[] basenamesArray = StringUtils.commaDelimitedListToStringArray(basename);
                list.addAll(Arrays.asList(basenamesArray));
            }

            if (!list.contains(I18N_PROPERTIES_BASENAME)) {
                list.add(I18N_PROPERTIES_BASENAME);
            }

            ResourceBundleMessageSource src = new ResourceBundleMessageSource();
            String[] basenames = list.toArray(new String[list.size()]);
            src.setBasenames(basenames);
            src.setDefaultEncoding("UTF-8");
            return src;
        }
    }

    @SuppressWarnings("UnusedDeclaration")
    @Configuration
    @ConditionalOnMissingBean(MessageSource.class)
    @ConditionalOnProperty(prefix = "spring.messages", name = "basename", matchIfMissing = true)
    public static class MissingBasenameMessageSourceConfiguration {

        @Autowired
        private ApplicationContext appCtx;

        @Bean
        public MessageSource messageSource() {

            List<String> list = new ArrayList<String>();

            Resource resource = appCtx.getResource("classpath*:messages*.properties");
            if (resource.exists()) {
                list.add("messages");
            }
            list.add(I18N_PROPERTIES_BASENAME);

            ResourceBundleMessageSource src = new ResourceBundleMessageSource();
            String[] basenames = list.toArray(new String[list.size()]);
            src.setBasenames(basenames);
            src.setDefaultEncoding("UTF-8");
            return src;
        }
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

        return new ServletListenerRegistrationBean<ServletContextListener>(listener);
    }

    @Bean
    @Override
    public FilterChainResolver stormpathFilterChainResolver() {
        return super.stormpathFilterChainResolver();
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
        for(String name : names) {
            types.add(DispatcherType.valueOf(name));
        }
        return types;
    }

    @Bean
    @DependsOn("stormpathServletContextListener")
    public FilterRegistrationBean stormpathFilter() {

        StormpathFilter filter = new StormpathFilter() {
            @Override
            protected void onInit() throws ServletException {
                //no op - we apply dependencies via setters below
            }
        };

        filter.setEnabled(stormpathFilterEnabled);
        filter.setClientRequestAttributeNames(stormpathRequestClientAttributeNames());
        filter.setApplicationRequestAttributeNames(stormpathRequestApplicationAttributeNames());
        filter.setFilterChainResolver(stormpathFilterChainResolver());
        filter.setWrappedServletRequestFactory(stormpathWrappedServletRequestFactory());

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
    @DependsOn("stormpathServletContextListener")
    public Filter stormpathAccountResolverFilter() {
        return super.stormpathAccountResolverFilter();
    }

}
