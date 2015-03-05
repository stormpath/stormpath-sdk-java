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
import com.stormpath.sdk.application.Application;
import com.stormpath.sdk.authc.AuthenticationResult;
import com.stormpath.sdk.cache.Cache;
import com.stormpath.sdk.cache.CacheManager;
import com.stormpath.sdk.client.Client;
import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.servlet.application.ApplicationLoader;
import com.stormpath.sdk.servlet.authz.RequestAuthorizer;
import com.stormpath.sdk.servlet.client.ClientLoader;
import com.stormpath.sdk.servlet.config.CookieConfig;
import com.stormpath.sdk.servlet.csrf.CsrfTokenManager;
import com.stormpath.sdk.servlet.csrf.DefaultCsrfTokenManager;
import com.stormpath.sdk.servlet.event.RequestEvent;
import com.stormpath.sdk.servlet.event.RequestEventListener;
import com.stormpath.sdk.servlet.event.RequestEventListenerAdapter;
import com.stormpath.sdk.servlet.event.impl.Publisher;
import com.stormpath.sdk.servlet.event.impl.RequestEventPublisher;
import com.stormpath.sdk.servlet.filter.DefaultServerUriResolver;
import com.stormpath.sdk.servlet.filter.DefaultUsernamePasswordRequestFactory;
import com.stormpath.sdk.servlet.filter.DefaultWrappedServletRequestFactory;
import com.stormpath.sdk.servlet.filter.FilterChainResolver;
import com.stormpath.sdk.servlet.filter.ServerUriResolver;
import com.stormpath.sdk.servlet.filter.StormpathFilter;
import com.stormpath.sdk.servlet.filter.UsernamePasswordRequestFactory;
import com.stormpath.sdk.servlet.filter.WrappedServletRequestFactory;
import com.stormpath.sdk.servlet.filter.account.AccountResolverFilter;
import com.stormpath.sdk.servlet.filter.account.AuthenticationJwtFactory;
import com.stormpath.sdk.servlet.filter.account.AuthenticationResultSaver;
import com.stormpath.sdk.servlet.filter.account.AuthorizationHeaderAccountResolver;
import com.stormpath.sdk.servlet.filter.account.CookieAccountResolver;
import com.stormpath.sdk.servlet.filter.account.CookieAuthenticationResultSaver;
import com.stormpath.sdk.servlet.filter.account.DefaultAuthenticationJwtFactory;
import com.stormpath.sdk.servlet.filter.account.DefaultJwtAccountResolver;
import com.stormpath.sdk.servlet.filter.account.DefaultJwtSigningKeyResolver;
import com.stormpath.sdk.servlet.filter.account.JwtAccountResolver;
import com.stormpath.sdk.servlet.filter.account.JwtSigningKeyResolver;
import com.stormpath.sdk.servlet.filter.account.SessionAccountResolver;
import com.stormpath.sdk.servlet.filter.account.SessionAuthenticationResultSaver;
import com.stormpath.sdk.servlet.filter.oauth.AccessTokenAuthenticationRequestFactory;
import com.stormpath.sdk.servlet.filter.oauth.AccessTokenResultFactory;
import com.stormpath.sdk.servlet.filter.oauth.DefaultAccessTokenAuthenticationRequestFactory;
import com.stormpath.sdk.servlet.filter.oauth.DefaultAccessTokenRequestAuthorizer;
import com.stormpath.sdk.servlet.filter.oauth.DefaultAccessTokenResultFactory;
import com.stormpath.sdk.servlet.filter.oauth.OriginAccessTokenRequestAuthorizer;
import com.stormpath.sdk.servlet.form.DefaultField;
import com.stormpath.sdk.servlet.form.Field;
import com.stormpath.sdk.servlet.http.CookieSaver;
import com.stormpath.sdk.servlet.http.Resolver;
import com.stormpath.sdk.servlet.http.Saver;
import com.stormpath.sdk.servlet.http.authc.AccountStoreResolver;
import com.stormpath.sdk.servlet.http.authc.AuthorizationHeaderAuthenticator;
import com.stormpath.sdk.servlet.http.authc.BasicAuthenticationScheme;
import com.stormpath.sdk.servlet.http.authc.BearerAuthenticationScheme;
import com.stormpath.sdk.servlet.http.authc.DisabledAccountStoreResolver;
import com.stormpath.sdk.servlet.http.authc.HeaderAuthenticator;
import com.stormpath.sdk.servlet.http.authc.HttpAuthenticationScheme;
import com.stormpath.sdk.servlet.http.authc.HttpAuthenticator;
import com.stormpath.sdk.servlet.mvc.AccessTokenController;
import com.stormpath.sdk.servlet.mvc.ChangePasswordController;
import com.stormpath.sdk.servlet.mvc.DefaultFormFieldsParser;
import com.stormpath.sdk.servlet.mvc.ForgotPasswordController;
import com.stormpath.sdk.servlet.mvc.FormFieldParser;
import com.stormpath.sdk.servlet.mvc.LoginController;
import com.stormpath.sdk.servlet.mvc.LogoutController;
import com.stormpath.sdk.servlet.mvc.RegisterController;
import com.stormpath.sdk.servlet.mvc.VerifyController;
import com.stormpath.sdk.servlet.util.IsLocalhostResolver;
import com.stormpath.sdk.servlet.util.SecureRequiredExceptForLocalhostResolver;
import com.stormpath.spring.boot.mvc.SpringController;
import com.stormpath.spring.boot.mvc.TemplateLayoutInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.web.WebMvcAutoConfiguration;
import org.springframework.boot.context.embedded.FilterRegistrationBean;
import org.springframework.boot.context.embedded.ServletListenerRegistrationBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.Resource;
import org.springframework.util.PathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.handler.SimpleUrlHandlerMapping;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.mvc.Controller;
import org.springframework.web.util.UrlPathHelper;

import javax.servlet.FilterChain;
import javax.servlet.Servlet;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

@SuppressWarnings("SpringFacetCodeInspection")
@Configuration
@ConditionalOnProperty(name = { "stormpath.enabled", "stormpath.web.enabled" }, matchIfMissing = true)
@ConditionalOnClass({ Servlet.class, DispatcherServlet.class })
@ConditionalOnWebApplication
@AutoConfigureAfter({ StormpathAutoConfiguration.class, WebMvcAutoConfiguration.class })
@EnableConfigurationProperties({ StormpathAccountCookieProperties.class, StormpathAccountJwtProperties.class,
                                   StormpathRequestRemoteUserProperties.class,
                                   StormpathRequestUserPrincipalProperties.class,
                                   StormpathHandlerMappingProperties.class, StormpathCsrfTokenProperties.class,
                                   StormpathNonceCacheProperties.class, StormpathHttpAuthenticationProperties.class,
                                   StormpathRequestClientProperties.class, StormpathRequestApplicationProperties.class,
                                   StormpathFilterProperties.class, StormpathLoginProperties.class,
                                   StormpathForgotPasswordProperties.class, StormpathRegisterProperties.class,
                                   StormpathRegisterFormProperties.class, StormpathVerifyProperties.class,
                                   StormpathLogoutProperties.class, StormpathChangePasswordProperties.class,
                                   StormpathHeadTemplateProperties.class, StormpathAccessTokenProperties.class,
                                   StormpathAccessTokenOriginAuthorizerProperties.class})
public class StormpathWebAutoConfiguration {

    private static final String I18N_PROPERTIES_BASENAME = "com.stormpath.sdk.servlet.i18n";

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

    @Autowired
    private StormpathHandlerMappingProperties handlerMappingProperties;

    @Autowired
    private StormpathCsrfTokenProperties csrfTokenProperties;

    @Autowired
    private StormpathNonceCacheProperties nonceCacheProperties;

    @Autowired
    private StormpathHttpAuthenticationProperties httpAuthenticationProperties;

    @Autowired
    private StormpathRequestClientProperties requestClientProperties;

    @Autowired
    private StormpathRequestApplicationProperties requestApplicationProperties;

    @Autowired
    private StormpathFilterProperties stormpathFilterProperties;

    @Autowired
    private StormpathLoginProperties loginProperties;

    @Autowired
    private StormpathForgotPasswordProperties forgotPasswordProperties;

    @Autowired
    private StormpathRegisterProperties registerProperties;

    @Autowired
    private StormpathRegisterFormProperties registerFormProperties;

    @Autowired
    private StormpathVerifyProperties verifyProperties;

    @Autowired
    private StormpathLogoutProperties logoutProperties;

    @Autowired
    private StormpathChangePasswordProperties changePasswordProperties;

    @Autowired
    private StormpathHeadTemplateProperties headTemplateProperties;

    @Autowired
    private StormpathAccessTokenProperties accessTokenProperties;

    @Autowired
    private StormpathAccessTokenOriginAuthorizerProperties accessTokenOriginAuthorizerProperties;

    @Bean
    @ConditionalOnMissingBean(name = "stormpathHandlerMapping")
    public HandlerMapping stormpathHandlerMapping(LocaleChangeInterceptor localeChangeInterceptor,
                                                  @Qualifier("stormpathLayoutInterceptor")
                                                  HandlerInterceptor stormpathLayoutInterceptor,
                                                  @Qualifier("stormpathLoginController") Controller loginController,
                                                  @Qualifier("stormpathLogoutController") Controller logoutController,
                                                  @Qualifier("stormpathVerifyController") Controller verifyController,
                                                  @Qualifier("stormpathRegisterController") Controller register,
                                                  @Qualifier("stormpathChangePasswordController") Controller change,
                                                  @Qualifier("stormpathForgotPasswordController") Controller forgot,
                                                  @Qualifier("stormpathAccessTokenController") Controller accessToken) {

        Map<String, Controller> mappings = new LinkedHashMap<String, Controller>();

        //TODO: work in progress, just testing for now:

        if (loginProperties.isEnabled()) {
            mappings.put(loginProperties.getUri(), loginController);
        }
        if (logoutProperties.isEnabled()) {
            mappings.put(logoutProperties.getUri(), logoutController);
        }
        if (registerProperties.isEnabled()) {
            mappings.put(registerProperties.getUri(), register);
        }
        if (verifyProperties.isEnabled()) {
            mappings.put(verifyProperties.getUri(), verifyController);
        }
        if (forgotPasswordProperties.isEnabled()) {
            mappings.put(forgotPasswordProperties.getUri(), forgot);
        }
        if (changePasswordProperties.isEnabled()) {
            mappings.put(changePasswordProperties.getUri(), change);
        }
        if (accessTokenProperties.isEnabled()) {
            mappings.put(accessTokenProperties.getUri(), accessToken);
        }

        SimpleUrlHandlerMapping mapping = new SimpleUrlHandlerMapping();
        mapping.setOrder(handlerMappingProperties.getOrder());
        mapping.setUrlMap(mappings);

        mapping.setInterceptors(new Object[]{ localeChangeInterceptor, stormpathLayoutInterceptor });

        if (pathMatcher != null) {
            mapping.setPathMatcher(pathMatcher);
        }

        if (urlPathHelper != null) {
            mapping.setUrlPathHelper(urlPathHelper);
        }

        return mapping;
    }

    @Bean
    @ConditionalOnMissingBean(name="stormpathLayoutInterceptor")
    public HandlerInterceptor stormpathLayoutInterceptor() throws Exception {
        TemplateLayoutInterceptor interceptor = new TemplateLayoutInterceptor();
        interceptor.setHeadFragmentSelector(headTemplateProperties.getFragmentSelector());
        interceptor.setHeadViewName(headTemplateProperties.getView());
        interceptor.afterPropertiesSet();
        return interceptor;
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
    public String stormpathCsrfTokenSigningKey(Client client) {
        return client.getApiKey().getSecret();
    }

    @Bean
    @ConditionalOnMissingBean
    public JwtAccountResolver stormpathJwtAccountResolver(JwtSigningKeyResolver jwtSigningKeyResolver) {
        return new DefaultJwtAccountResolver(jwtSigningKeyResolver);
    }

    @Bean
    @ConditionalOnMissingBean
    public Cache<String, String> stormpathNonceCache(CacheManager cacheManager) {
        return cacheManager.getCache(nonceCacheProperties.getName());
    }

    @Bean
    @ConditionalOnMissingBean
    public CsrfTokenManager stormpathCsrfTokenManager(
        @Qualifier("stormpathNonceCache") Cache<String, String> nonceCache,
        @Qualifier("stormpathCsrfTokenSigningKey") String signingKey) {

        return new DefaultCsrfTokenManager(nonceCache, signingKey, csrfTokenProperties.getTtl());
    }

    @Bean
    @ConditionalOnMissingBean
    public AccessTokenResultFactory stormpathAccessTokenResultFactory(@Qualifier("stormpathApplication") Application app,
                                                                      AuthenticationJwtFactory authenticationJwtFactory) {
        return new DefaultAccessTokenResultFactory(app, authenticationJwtFactory, accountJwtProperties.getTtl());
    }

    @Bean
    @ConditionalOnMissingBean
    public WrappedServletRequestFactory stormpathWrappedServletRequestFactory(
        UsernamePasswordRequestFactory usernamePasswordRequestFactory,
        @Qualifier("stormpathAuthenticationResultSaver") Saver<AuthenticationResult> authenticationResultSaver,
        Publisher<RequestEvent> requestEventPublisher) {

        return new DefaultWrappedServletRequestFactory(usernamePasswordRequestFactory, authenticationResultSaver,
                                                       requestEventPublisher,
                                                       requestUserPrincipalProperties.getStrategy(),
                                                       requestRemoteUserProperties.getStrategy());
    }

    /*
     * The HTTP spec says that more well-supported authentication schemes should be listed first when the challenge is
     * sent.  The default Stormpath header authenticator implementation will send challenge entries in the specified
     * order.  Since 'basic' is a more well-supported scheme than 'bearer' we order basic (0) with higher priority than
     * bearer (10).
     */
    @Bean
    @ConditionalOnMissingBean(name = "stormpathBasicAuthenticationScheme")
    @Order(0)
    public HttpAuthenticationScheme stormpathBasicAuthenticationScheme(UsernamePasswordRequestFactory factory) {
        return new BasicAuthenticationScheme(factory);
    }

    @Bean
    @ConditionalOnMissingBean(name = "stormpathBearerAuthenticationScheme")
    @Order(10)
    public HttpAuthenticationScheme stormpathBearerAuthenticationScheme(JwtSigningKeyResolver resolver) {
        return new BearerAuthenticationScheme(resolver);
    }

    //This bean is defined to allow the app developer to override this list if they want to construct it themselves,
    //for example, with an entirely different order or excluding some schemes
    @Bean
    @ConditionalOnMissingBean(name = "stormpathHttpAuthenticationSchemes")
    public List<HttpAuthenticationScheme> stormpathHttpAuthenticationSchemes(List<HttpAuthenticationScheme> schemes) {
        return schemes;
    }

    @Bean
    @ConditionalOnMissingBean
    public HeaderAuthenticator stormpathAuthorizationHeaderAuthenticator(
        @Value("#{stormpathHttpAuthenticationSchemes}") List<HttpAuthenticationScheme> schemes,
        Publisher<RequestEvent> requestEventPublisher) {

        return new AuthorizationHeaderAuthenticator(schemes, httpAuthenticationProperties.isChallenge(),
                                                    requestEventPublisher);
    }

    @Bean
    @ConditionalOnMissingBean(name = "stormpathAuthorizationHeaderAccountResolver")
    public Resolver<Account> stormpathAuthorizationHeaderAccountResolver(HttpAuthenticator httpAuthenticator) {
        return new AuthorizationHeaderAccountResolver(httpAuthenticator);
    }

    @Bean
    @ConditionalOnMissingBean(name = "stormpathCookieAccountResolver")
    public Resolver<Account> stormpathCookieAccountResolver(
        @Qualifier("stormpathAccountCookieConfig") CookieConfig cookieConfig, JwtAccountResolver resolver) {

        return new CookieAccountResolver(cookieConfig, resolver);
    }

    @Bean
    @ConditionalOnMissingBean(name = "stormpathSessionAccountResolver")
    public Resolver<Account> stormpathSessionAccountResolver() {
        return new SessionAccountResolver();
    }

    @Bean
    @ConditionalOnMissingBean(name = "stormpathAccountResolvers")
    public List<Resolver<Account>> stormpathAccountResolvers(
        @Qualifier("stormpathAuthorizationHeaderAccountResolver") Resolver<Account> headerResolver,
        @Qualifier("stormpathCookieAccountResolver") Resolver<Account> cookieResolver,
        @Qualifier("stormpathSessionAccountResolver") Resolver<Account> sessionResolver) {

        //the order determines which locations are checked.  One an account is found, the remaining locations are
        //skipped, so we must order them based on preference:
        List<Resolver<Account>> resolvers = new ArrayList<>(3);
        resolvers.add(headerResolver);
        resolvers.add(cookieResolver);
        resolvers.add(sessionResolver);

        return resolvers;
    }

    @Bean
    @ConditionalOnMissingBean
    public FilterChainResolver stormpathFilterChainResolver() throws ServletException {
        return new FilterChainResolver() {
            @Override
            public FilterChain getChain(HttpServletRequest request, HttpServletResponse response, FilterChain chain) {
                return chain; //currently testing
            }
        };
    }

    @Bean
    public ServletListenerRegistrationBean<ServletContextListener> stormpathServletContextListener(
        final @Qualifier("stormpathApplication") Application application, final Client client) {

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
    @ConditionalOnMissingBean(name = "stormpathFilter")
    @DependsOn("stormpathServletContextListener")
    public FilterRegistrationBean stormpathFilter(FilterChainResolver filterChainResolver,
                                                  WrappedServletRequestFactory wrappedServletRequestFactory) {

        StormpathFilter filter = new StormpathFilter() {
            @Override
            protected void onInit() throws ServletException {
                //no op - we apply dependencies via setters below
            }
        };

        filter.setEnabled(this.stormpathFilterProperties.isEnabled());
        filter.setClientRequestAttributeNames(this.requestClientProperties.getAttributeNames());
        filter.setApplicationRequestAttributeNames(this.requestApplicationProperties.getAttributeNames());
        filter.setFilterChainResolver(filterChainResolver);
        filter.setWrappedServletRequestFactory(wrappedServletRequestFactory);

        FilterRegistrationBean bean = new FilterRegistrationBean();
        bean.setFilter(filter);
        bean.setEnabled(stormpathFilterProperties.isEnabled());
        bean.setOrder(stormpathFilterProperties.getOrder());
        bean.setUrlPatterns(stormpathFilterProperties.getUrlPatterns());
        bean.setServletNames(stormpathFilterProperties.getServletNames());
        bean.setDispatcherTypes(stormpathFilterProperties.getDispatcherTypes());
        bean.setMatchAfter(stormpathFilterProperties.isMatchAfter());
        return bean;
    }

    @Bean
    @ConditionalOnMissingBean(name = "stormpathAccountResolverFilter")
    @DependsOn("stormpathServletContextListener")
    public FilterRegistrationBean stormpathAccountResolverFilter(
        @Value("#{stormpathAccountResolvers}") List<Resolver<Account>> resolvers) {

        Assert.notEmpty(resolvers, "Account resolver collection cannot be null or empty.");

        AccountResolverFilter filter = new AccountResolverFilter();
        filter.setEnabled(this.stormpathFilterProperties.isEnabled());
        filter.setResolvers(resolvers);

        FilterRegistrationBean bean = new FilterRegistrationBean();
        bean.setFilter(filter);
        bean.setEnabled(stormpathFilterProperties.isEnabled());
        //we always want this filter to be immediately after the StormpathFilter:
        bean.setOrder(stormpathFilterProperties.getOrder() + 1);
        bean.setUrlPatterns(stormpathFilterProperties.getUrlPatterns());
        bean.setServletNames(stormpathFilterProperties.getServletNames());
        bean.setDispatcherTypes(stormpathFilterProperties.getDispatcherTypes());
        bean.setMatchAfter(stormpathFilterProperties.isMatchAfter());
        return bean;
    }

    @Bean
    @ConditionalOnMissingBean(name = "stormpathLoginController")
    public Controller stormpathLoginController(CsrfTokenManager csrfTokenManager,
                                               @Qualifier("stormpathAuthenticationResultSaver")
                                               Saver<AuthenticationResult> authenticationResultSaver) {

        LoginController controller = new LoginController();
        controller.setView(loginProperties.getView());
        controller.setNextUri(loginProperties.getNextUri());
        controller.setForgotLoginUri(forgotPasswordProperties.getUri());
        controller.setRegisterUri(registerProperties.getUri());
        controller.setLogoutUri(logoutProperties.getUri());
        controller.setAuthenticationResultSaver(authenticationResultSaver);
        controller.setCsrfTokenManager(csrfTokenManager);
        controller.init();

        return createSpringController(controller);
    }

    @Bean
    @ConditionalOnMissingBean(name = "stormpathForgotPasswordController")
    public Controller stormpathForgotPasswordController(CsrfTokenManager csrfTokenManager,
                                                        AccountStoreResolver accountStoreResolver) {

        ForgotPasswordController controller = new ForgotPasswordController();
        controller.setView(forgotPasswordProperties.getView());
        controller.setCsrfTokenManager(csrfTokenManager);
        controller.setAccountStoreResolver(accountStoreResolver);
        controller.setNextView(forgotPasswordProperties.getNextUri());
        controller.setLoginUri(loginProperties.getUri());
        controller.init();

        return createSpringController(controller);
    }

    private Controller createSpringController(com.stormpath.sdk.servlet.mvc.Controller controller) {
        SpringController springController = new SpringController(controller);
        if (this.urlPathHelper != null) {
            springController.setUrlPathHelper(urlPathHelper);
        }
        return springController;
    }

    @Bean
    @ConditionalOnMissingBean(name = "stormpathRegisterFormFields")
    public List<Field> stormpathRegisterFormFields(
        @Qualifier("stormpathRegisterFormFieldParser") FormFieldParser parser) {
        return parser.parse(registerFormProperties.getFields());
    }

    @Bean
    @ConditionalOnMissingBean(name = "stormpathRegisterFormFieldParser")
    public FormFieldParser stormpathRegisterFormFieldParser() {
        return new DefaultFormFieldsParser("stormpath.web.register.form.fields");
    }

    @Bean
    @ConditionalOnMissingBean
    public LocaleResolver localeResolver() {
        return new CookieLocaleResolver();
    }

    @Bean
    @ConditionalOnMissingBean
    public LocaleChangeInterceptor localeChangeInterceptor() {
        LocaleChangeInterceptor lci = new LocaleChangeInterceptor();
        lci.setParamName("lang");
        return lci;
    }

    @Bean
    @ConditionalOnMissingBean(name = "stormpathLocaleResolver")
    public Resolver<Locale> stormpathLocaleResolver(final LocaleResolver localeResolver) {
        return new Resolver<Locale>() {
            @Override
            public Locale get(HttpServletRequest request, HttpServletResponse response) {
                return localeResolver.resolveLocale(request);
            }
        };
    }

    @SuppressWarnings("UnusedDeclaration")
    @Configuration
    @ConditionalOnMissingBean(MessageSource.class)
    @ConditionalOnProperty(prefix = "spring.messages", name = "basename")
    public static class MessageSourceConfiguration extends WebMvcConfigurerAdapter {

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
    public static class MissingBasenameMessageSourceConfiguration extends WebMvcConfigurerAdapter {

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
    @ConditionalOnMissingBean
    public com.stormpath.sdk.servlet.i18n.MessageSource stormpathMessageSource(
        final /*@Qualifier("stormpathSpringMessageSource")*/ MessageSource springMessageSource) {

        return new com.stormpath.sdk.servlet.i18n.MessageSource() {

            @Override
            public String getMessage(String key, Locale locale) {
                return springMessageSource.getMessage(key, null, locale);
            }

            @Override
            public String getMessage(String key, Locale locale, Object... args) {
                return springMessageSource.getMessage(key, args, locale);
            }
        };
    }

    private List<DefaultField> toDefaultFields(List<Field> fields) {
        List<DefaultField> defaultFields = new ArrayList<DefaultField>(fields.size());
        for (Field field : fields) {
            Assert.isInstanceOf(DefaultField.class, field);
            defaultFields.add((DefaultField) field);
        }

        return defaultFields;
    }

    @Bean
    @ConditionalOnMissingBean(name = "stormpathRegisterController")
    public Controller stormpathRegisterController(CsrfTokenManager csrfTokenManager, Client client,
                                                  Publisher<RequestEvent> eventPublisher,
                                                  @Value("#{stormpathRegisterFormFields}") List<Field> fields,
                                                  @Qualifier("stormpathLocaleResolver")
                                                  Resolver<Locale> stormpathLocaleResolver,
                                                  com.stormpath.sdk.servlet.i18n.MessageSource messageSource,
                                                  @Qualifier("stormpathAuthenticationResultSaver")
                                                  Saver<AuthenticationResult> authenticationResultSaver) {

        RegisterController controller = new RegisterController();
        controller.setCsrfTokenManager(csrfTokenManager);
        controller.setClient(client);
        controller.setEventPublisher(eventPublisher);
        controller.setFormFields(toDefaultFields(fields));
        controller.setLocaleResolver(stormpathLocaleResolver);
        controller.setMessageSource(messageSource);
        controller.setAuthenticationResultSaver(authenticationResultSaver);
        controller.setView(registerProperties.getView());
        controller.setNextUri(registerProperties.getNextUri());
        controller.setLoginUri(loginProperties.getUri());
        controller.setVerifyViewName(verifyProperties.getView());
        controller.init();

        return createSpringController(controller);
    }

    @Bean
    @ConditionalOnMissingBean(name = "stormpathVerifyController")
    public Controller stormpathVerifyController(Client client, Publisher<RequestEvent> eventPublisher) {

        VerifyController controller = new VerifyController();
        controller.setNextUri(verifyProperties.getNextUri());
        controller.setLogoutUri(logoutProperties.getUri());
        controller.setClient(client);
        controller.setEventPublisher(eventPublisher);
        controller.init();

        return createSpringController(controller);
    }

    @Bean
    @ConditionalOnMissingBean(name = "stormpathChangePasswordController")
    public Controller stormpathChangePasswordController(CsrfTokenManager csrfTokenManager,
                                                        @Qualifier("stormpathLocaleResolver")
                                                        Resolver<Locale> stormpathLocaleResolver,
                                                        com.stormpath.sdk.servlet.i18n.MessageSource messageSource) {

        ChangePasswordController controller = new ChangePasswordController();
        controller.setView(changePasswordProperties.getView());
        controller.setCsrfTokenManager(csrfTokenManager);
        controller.setNextUri(changePasswordProperties.getNextUri());
        controller.setLoginUri(loginProperties.getUri());
        controller.setForgotPasswordUri(forgotPasswordProperties.getUri());
        controller.setLocaleResolver(stormpathLocaleResolver);
        controller.setMessageSource(messageSource);
        controller.init();

        return createSpringController(controller);
    }

    @Bean
    @ConditionalOnMissingBean(name="stormpathAccessTokenController")
    public Controller stormpathAccessTokenController(Publisher<RequestEvent> eventPublisher,
                                                     AccessTokenAuthenticationRequestFactory requestFactory,
                                                     AccessTokenResultFactory resultFactory,
                                                     @Qualifier("stormpathAccessTokenRequestAuthorizer")
                                                     RequestAuthorizer requestAuthorizer,
                                                     @Qualifier("stormpathAuthenticationResultSaver")
                                                     Saver<AuthenticationResult> authenticationResultSaver) {

        AccessTokenController c = new AccessTokenController();
        c.setEventPublisher(eventPublisher);
        c.setAccessTokenAuthenticationRequestFactory(requestFactory);
        c.setAccessTokenResultFactory(resultFactory);
        c.setAccountSaver(authenticationResultSaver);
        c.setRequestAuthorizer(requestAuthorizer);
        c.init();

        return createSpringController(c);
    }

    @Bean
    @ConditionalOnMissingBean
    public AccessTokenAuthenticationRequestFactory stormpathAccessTokenAuthenticationRequestFactory(UsernamePasswordRequestFactory factory) {
        return new DefaultAccessTokenAuthenticationRequestFactory(factory);
    }

    @Bean
    @ConditionalOnMissingBean(name = "stormpathAccessTokenRequestAuthorizer")
    public RequestAuthorizer stormpathAccessTokenRequestAuthorizer(
        @Qualifier("stormpathSecureResolver") Resolver<Boolean> secureRequired,
        @Qualifier("stormpathOriginAccessTokenRequestAuthorizer") RequestAuthorizer originAuthorizer) {

        return new DefaultAccessTokenRequestAuthorizer(secureRequired, originAuthorizer);
    }

    @Bean
    @ConditionalOnMissingBean(name = "stormpathOriginAccessTokenRequestAuthorizer")
    public RequestAuthorizer stormpathOriginAccessTokenRequestAuthorizer(ServerUriResolver serverUriResolver,
                                                                         @Qualifier("stormpathLocalhostResolver")
                                                                         Resolver<Boolean> localhostResolver) {
        Set<String> authorizedOriginUris = accessTokenOriginAuthorizerProperties.getOriginUrisSet();
        return new OriginAccessTokenRequestAuthorizer(serverUriResolver, localhostResolver, authorizedOriginUris);
    }

    @Bean
    @ConditionalOnMissingBean
    public ServerUriResolver stormpathServerUriResolver() {
        return new DefaultServerUriResolver();
    }

    @Bean
    @ConditionalOnMissingBean(name = "stormpathLogoutController")
    public Controller stormpathLogoutController() {
        LogoutController controller = new LogoutController();
        controller.setNextUri(logoutProperties.getNextUri());
        controller.init();
        return createSpringController(controller);
    }
}
