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
import com.stormpath.sdk.servlet.config.CookieConfig;
import com.stormpath.sdk.servlet.event.RequestEvent;
import com.stormpath.sdk.servlet.event.RequestEventListener;
import com.stormpath.sdk.servlet.event.RequestEventListenerAdapter;
import com.stormpath.sdk.servlet.event.impl.Publisher;
import com.stormpath.sdk.servlet.event.impl.RequestEventPublisher;
import com.stormpath.sdk.servlet.filter.DefaultUsernamePasswordRequestFactory;
import com.stormpath.sdk.servlet.filter.DefaultWrappedServletRequestFactory;
import com.stormpath.sdk.servlet.filter.UsernamePasswordRequestFactory;
import com.stormpath.sdk.servlet.filter.WrappedServletRequestFactory;
import com.stormpath.sdk.servlet.filter.account.AuthenticationJwtFactory;
import com.stormpath.sdk.servlet.filter.account.AuthenticationResultSaver;
import com.stormpath.sdk.servlet.filter.account.CookieAuthenticationResultSaver;
import com.stormpath.sdk.servlet.filter.account.DefaultAuthenticationJwtFactory;
import com.stormpath.sdk.servlet.filter.account.DefaultJwtSigningKeyResolver;
import com.stormpath.sdk.servlet.filter.account.JwtSigningKeyResolver;
import com.stormpath.sdk.servlet.filter.account.SessionAuthenticationResultSaver;
import com.stormpath.sdk.servlet.http.CookieSaver;
import com.stormpath.sdk.servlet.http.Resolver;
import com.stormpath.sdk.servlet.http.Saver;
import com.stormpath.sdk.servlet.http.authc.AccountStoreResolver;
import com.stormpath.sdk.servlet.http.authc.DisabledAccountStoreResolver;
import com.stormpath.sdk.servlet.util.IsLocalhostResolver;
import com.stormpath.sdk.servlet.util.SecureRequiredExceptForLocalhostResolver;
import com.stormpath.spring.boot.mvc.LoginController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.web.WebMvcAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.PathMatcher;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.handler.SimpleUrlHandlerMapping;
import org.springframework.web.servlet.mvc.Controller;
import org.springframework.web.util.UrlPathHelper;

import javax.servlet.Servlet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@SuppressWarnings("SpringFacetCodeInspection")
@Configuration
@ConditionalOnClass({ Servlet.class, DispatcherServlet.class })
@ConditionalOnWebApplication
@AutoConfigureAfter({ StormpathAutoConfiguration.class, WebMvcAutoConfiguration.class })
@EnableConfigurationProperties({ StormpathAccountCookieProperties.class, StormpathAccountJwtProperties.class,
                                   StormpathRequestRemoteUserProperties.class,
                                   StormpathRequestUserPrincipalProperties.class })
public class StormpathWebAutoConfiguration {

    @Autowired(required = false)
    private PathMatcher pathMatcher;

    @Autowired(required = false)
    private UrlPathHelper urlPathHelper;

    @Autowired
    private StormpathAccountCookieProperties accountCookieProperties;

    @Autowired
    private StormpathAccountJwtProperties accountJwtProperties;

    @Autowired
    private StormpathRequestRemoteUserProperties requestRemoteUserProperties;

    @Autowired
    private StormpathRequestUserPrincipalProperties requestUserPrincipalProperties;

    @Bean
    @ConditionalOnMissingBean(name = "stormpathHandlerMapping")
    public HandlerMapping stormpathHandlerMapping() {

        Map<String, Controller> urlToControllerMap = new LinkedHashMap<String, Controller>();

        //TODO: work in progress, just testing for now:

        urlToControllerMap.put("/login", new LoginController());

        SimpleUrlHandlerMapping mapping = new SimpleUrlHandlerMapping();
        mapping.setOrder(10); //allow user-defined @Controller classes to take precedence over these default controllers
        mapping.setUrlMap(urlToControllerMap);

        if (pathMatcher != null) {
            mapping.setPathMatcher(pathMatcher);
        }

        if (urlPathHelper != null) {
            mapping.setUrlPathHelper(urlPathHelper);
        }

        return mapping;
    }


    @Bean
    @ConditionalOnMissingBean
    public AccountStoreResolver stormpathAccountStoreResolver() {
        return new DisabledAccountStoreResolver();
    }

    @Bean
    @ConditionalOnMissingBean
    public UsernamePasswordRequestFactory stormpathUsernamePasswordRequestFactory(
        AccountStoreResolver accountStoreResolver) {
        return new DefaultUsernamePasswordRequestFactory(accountStoreResolver);
    }

    @Bean
    @ConditionalOnMissingBean
    public CookieConfig stormpathAccountCookieConfig() {

        final StormpathAccountCookieProperties props = this.accountCookieProperties;

        return new CookieConfig() {
            @Override
            public String getName() {
                return props.getName();
            }

            @Override
            public String getComment() {
                return props.getComment();
            }

            @Override
            public String getDomain() {
                return props.getDomain();
            }

            @Override
            public int getMaxAge() {
                return props.getMaxAge();
            }

            @Override
            public String getPath() {
                return props.getPath();
            }

            @Override
            public boolean isSecure() {
                return props.isSecure();
            }

            @Override
            public boolean isHttpOnly() {
                return props.isHttpOnly();
            }
        };
    }

    @Bean
    @ConditionalOnMissingBean(name = "stormpathLocalhostResolver")
    public Resolver<Boolean> stormpathLocalhostResolver() {
        return new IsLocalhostResolver();
    }

    @Bean
    @ConditionalOnMissingBean(name = "stormpathSecureResolver")
    public Resolver<Boolean> stormpathSecureResolver(
        @Qualifier("stormpathLocalhostResolver") Resolver<Boolean> localhostResolver) {
        return new SecureRequiredExceptForLocalhostResolver(localhostResolver);
    }

    @Bean
    @ConditionalOnProperty(prefix = "stormpath.web.authc.savers.cookie", name = "enabled", havingValue = "true",
                           matchIfMissing = true)
    public Saver<AuthenticationResult> stormpathCookieAuthenticationResultSaver(CookieConfig accountCookieConfig,
                                                                                @Qualifier("stormpathSecureResolver")
                                                                                Resolver<Boolean> secureRequired,
                                                                                AuthenticationJwtFactory authenticationJwtFactory) {

        return new CookieAuthenticationResultSaver(accountCookieConfig, secureRequired, authenticationJwtFactory);
    }

    @Bean
    @ConditionalOnProperty(prefix = "stormpath.web.authc.savers.session", name = "enabled", havingValue = "true")
    public Saver<AuthenticationResult> stormpathSessionAuthenticationResultSaver() {
        String[] attributeNames = { Account.class.getName(), "account" };
        Set<String> set = new HashSet<>(Arrays.asList(attributeNames));
        return new SessionAuthenticationResultSaver(set);
    }

    @Bean
    @ConditionalOnMissingBean
    public AuthenticationJwtFactory stormpathAuthenticationJwtFactory(JwtSigningKeyResolver jwtSigningKeyResolver) {
        return new DefaultAuthenticationJwtFactory(jwtSigningKeyResolver, accountJwtProperties.getSignatureAlgorithm(),
                                                   accountJwtProperties.getTtl());
    }

    @Bean
    @ConditionalOnMissingBean
    public JwtSigningKeyResolver stormpathAccountJwtSigningKeyResolver() {
        return new DefaultJwtSigningKeyResolver();
    }

    @Bean
    @ConditionalOnMissingBean
    public AuthenticationResultSaver stormpathAuthenticationResultSaver(List<Saver<AuthenticationResult>> savers) {
        List<Saver<AuthenticationResult>> target = new ArrayList<>();

        for (Saver<AuthenticationResult> saver : savers) {
            if (!CookieSaver.class.isAssignableFrom(saver.getClass())) {
                target.add(saver);
            }
        }

        return new AuthenticationResultSaver(target);
    }

    @Bean
    @ConditionalOnMissingBean
    public RequestEventListener stormpathRequestEventListener() {
        return new RequestEventListenerAdapter();
    }

    @Bean
    @ConditionalOnMissingBean
    public Publisher<RequestEvent> stormpathRequestEventPublisher(RequestEventListener requestEventListener) {
        return new RequestEventPublisher(requestEventListener);
    }

    @Bean
    @ConditionalOnMissingBean
    public WrappedServletRequestFactory stormpathWrappedServletRequestFactory(
        UsernamePasswordRequestFactory usernamePasswordRequestFactory,
        AuthenticationResultSaver authenticationResultSaver, Publisher<RequestEvent> requestEventPublisher) {

        return new DefaultWrappedServletRequestFactory(usernamePasswordRequestFactory, authenticationResultSaver,
                                                       requestEventPublisher,
                                                       requestUserPrincipalProperties.getStrategy(),
                                                       requestRemoteUserProperties.getStrategy());
    }

}
