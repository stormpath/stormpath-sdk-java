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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.application.Application;
import com.stormpath.sdk.authc.AuthenticationResult;
import com.stormpath.sdk.cache.Cache;
import com.stormpath.sdk.client.Client;
import com.stormpath.sdk.idsite.IdSiteResultListener;
import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.lang.Collections;
import com.stormpath.sdk.lang.Strings;
import com.stormpath.sdk.saml.SamlResultListener;
import com.stormpath.sdk.servlet.authz.RequestAuthorizer;
import com.stormpath.sdk.servlet.config.Config;
import com.stormpath.sdk.servlet.config.CookieConfig;
import com.stormpath.sdk.servlet.config.impl.AccessTokenCookieConfig;
import com.stormpath.sdk.servlet.config.impl.RefreshTokenCookieConfig;
import com.stormpath.sdk.servlet.csrf.CsrfTokenManager;
import com.stormpath.sdk.servlet.csrf.DefaultCsrfTokenManager;
import com.stormpath.sdk.servlet.csrf.DisabledCsrfTokenManager;
import com.stormpath.sdk.servlet.event.RequestEvent;
import com.stormpath.sdk.servlet.event.RequestEventListener;
import com.stormpath.sdk.servlet.event.RequestEventListenerAdapter;
import com.stormpath.sdk.servlet.event.TokenRevocationRequestEventListener;
import com.stormpath.sdk.servlet.event.impl.Publisher;
import com.stormpath.sdk.servlet.event.impl.RequestEventPublisher;
import com.stormpath.sdk.servlet.filter.ControllerConfigResolver;
import com.stormpath.sdk.servlet.filter.DefaultServerUriResolver;
import com.stormpath.sdk.servlet.filter.DefaultUsernamePasswordRequestFactory;
import com.stormpath.sdk.servlet.filter.DefaultWrappedServletRequestFactory;
import com.stormpath.sdk.servlet.filter.FilterChainResolver;
import com.stormpath.sdk.servlet.filter.ProxiedFilterChain;
import com.stormpath.sdk.servlet.filter.ServerUriResolver;
import com.stormpath.sdk.servlet.filter.StormpathFilter;
import com.stormpath.sdk.servlet.filter.UsernamePasswordRequestFactory;
import com.stormpath.sdk.servlet.filter.WrappedServletRequestFactory;
import com.stormpath.sdk.servlet.filter.account.AccountResolverFilter;
import com.stormpath.sdk.servlet.filter.account.AuthenticationResultSaver;
import com.stormpath.sdk.servlet.filter.account.AuthorizationHeaderAccountResolver;
import com.stormpath.sdk.servlet.filter.account.CookieAccountResolver;
import com.stormpath.sdk.servlet.filter.account.CookieAuthenticationResultSaver;
import com.stormpath.sdk.servlet.filter.account.DefaultJwtAccountResolver;
import com.stormpath.sdk.servlet.filter.account.JwtAccountResolver;
import com.stormpath.sdk.servlet.filter.account.JwtSigningKeyResolver;
import com.stormpath.sdk.servlet.filter.account.SessionAccountResolver;
import com.stormpath.sdk.servlet.filter.account.SessionAuthenticationResultSaver;
import com.stormpath.sdk.servlet.filter.oauth.AccessTokenAuthenticationRequestFactory;
import com.stormpath.sdk.servlet.filter.oauth.AccessTokenResultFactory;
import com.stormpath.sdk.servlet.filter.oauth.DefaultAccessTokenAuthenticationRequestFactory;
import com.stormpath.sdk.servlet.filter.oauth.DefaultAccessTokenRequestAuthorizer;
import com.stormpath.sdk.servlet.filter.oauth.DefaultAccessTokenResultFactory;
import com.stormpath.sdk.servlet.filter.oauth.DefaultRefreshTokenAuthenticationRequestFactory;
import com.stormpath.sdk.servlet.filter.oauth.DefaultRefreshTokenResultFactory;
import com.stormpath.sdk.servlet.filter.oauth.OriginAccessTokenRequestAuthorizer;
import com.stormpath.sdk.servlet.filter.oauth.RefreshTokenAuthenticationRequestFactory;
import com.stormpath.sdk.servlet.filter.oauth.RefreshTokenResultFactory;
import com.stormpath.sdk.servlet.form.DefaultField;
import com.stormpath.sdk.servlet.form.Field;
import com.stormpath.sdk.servlet.http.InvalidMediaTypeException;
import com.stormpath.sdk.servlet.http.MediaType;
import com.stormpath.sdk.servlet.http.Resolver;
import com.stormpath.sdk.servlet.http.Saver;
import com.stormpath.sdk.servlet.http.authc.AccountStoreResolver;
import com.stormpath.sdk.servlet.http.authc.AuthorizationHeaderAuthenticator;
import com.stormpath.sdk.servlet.http.authc.BasicAuthenticationScheme;
import com.stormpath.sdk.servlet.http.authc.BearerAuthenticationScheme;
import com.stormpath.sdk.servlet.http.authc.DisabledAccountStoreResolver;
import com.stormpath.sdk.servlet.http.authc.HeaderAuthenticator;
import com.stormpath.sdk.servlet.http.authc.HttpAuthenticationScheme;
import com.stormpath.sdk.servlet.i18n.MessageTag;
import com.stormpath.sdk.servlet.idsite.DefaultIdSiteOrganizationResolver;
import com.stormpath.sdk.servlet.idsite.IdSiteOrganizationContext;
import com.stormpath.sdk.servlet.mvc.AccessTokenController;
import com.stormpath.sdk.servlet.mvc.ChangePasswordController;
import com.stormpath.sdk.servlet.mvc.ErrorModelFactory;
import com.stormpath.sdk.servlet.mvc.ForgotPasswordController;
import com.stormpath.sdk.servlet.mvc.IdSiteController;
import com.stormpath.sdk.servlet.mvc.IdSiteLogoutController;
import com.stormpath.sdk.servlet.mvc.IdSiteResultController;
import com.stormpath.sdk.servlet.mvc.LoginController;
import com.stormpath.sdk.servlet.mvc.LoginErrorModelFactory;
import com.stormpath.sdk.servlet.mvc.LogoutController;
import com.stormpath.sdk.servlet.mvc.MeController;
import com.stormpath.sdk.servlet.mvc.RegisterController;
import com.stormpath.sdk.servlet.mvc.SamlController;
import com.stormpath.sdk.servlet.mvc.SamlLogoutController;
import com.stormpath.sdk.servlet.mvc.SamlResultController;
import com.stormpath.sdk.servlet.mvc.SendVerificationEmailController;
import com.stormpath.sdk.servlet.mvc.VerifyController;
import com.stormpath.sdk.servlet.mvc.WebHandler;
import com.stormpath.sdk.servlet.mvc.provider.AccountStoreModelFactory;
import com.stormpath.sdk.servlet.mvc.provider.DefaultAccountStoreModelFactory;
import com.stormpath.sdk.servlet.mvc.provider.FacebookCallbackController;
import com.stormpath.sdk.servlet.mvc.provider.GithubCallbackController;
import com.stormpath.sdk.servlet.mvc.provider.GoogleCallbackController;
import com.stormpath.sdk.servlet.mvc.provider.LinkedinCallbackController;
import com.stormpath.sdk.servlet.oauth.AccessTokenValidationStrategy;
import com.stormpath.sdk.servlet.oauth.impl.JwtTokenSigningKeyResolver;
import com.stormpath.sdk.servlet.organization.DefaultOrganizationNameKeyResolver;
import com.stormpath.sdk.servlet.saml.DefaultSamlOrganizationResolver;
import com.stormpath.sdk.servlet.saml.SamlOrganizationContext;
import com.stormpath.sdk.servlet.util.IsLocalhostResolver;
import com.stormpath.sdk.servlet.util.RemoteAddrResolver;
import com.stormpath.sdk.servlet.util.SecureRequiredExceptForLocalhostResolver;
import com.stormpath.sdk.servlet.util.SubdomainResolver;
import com.stormpath.spring.context.CompositeMessageSource;
import com.stormpath.spring.mvc.ChangePasswordControllerConfigResolver;
import com.stormpath.spring.mvc.ForgotPasswordControllerConfigResolver;
import com.stormpath.spring.mvc.LoginControllerConfigResolver;
import com.stormpath.spring.mvc.LogoutControllerConfigResolver;
import com.stormpath.spring.mvc.RegisterControllerConfigResolver;
import com.stormpath.spring.mvc.SendVerificationEmailControllerConfigResolver;
import com.stormpath.spring.mvc.SpringController;
import com.stormpath.spring.mvc.SpringSpaController;
import com.stormpath.spring.mvc.TemplateLayoutInterceptor;
import com.stormpath.spring.mvc.VerifyControllerConfigResolver;
import io.jsonwebtoken.SignatureAlgorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.support.DelegatingMessageSource;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.core.Ordered;
import org.springframework.core.env.CompositePropertySource;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.Environment;
import org.springframework.core.env.PropertySource;
import org.springframework.util.PathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.handler.SimpleUrlHandlerMapping;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.mvc.Controller;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;
import org.springframework.web.util.UrlPathHelper;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @since 1.0.RC4
 */
@SuppressWarnings({"SpringFacetCodeInspection", "SpringJavaAutowiredMembersInspection"})
public abstract class AbstractStormpathWebMvcConfiguration {

    private static final Logger log = LoggerFactory.getLogger(AbstractStormpathWebMvcConfiguration.class);

    private static final String PRODUCES_SUPPORTED_TYPES_MSG = "stormpath.web.produces property value must " +
            "specify either " + MediaType.APPLICATION_JSON_VALUE + " or " + MediaType.TEXT_HTML_VALUE + " or both.  " +
            "Other media types for this property are not currently supported.";

    protected static final String I18N_PROPERTIES_BASENAME = "com.stormpath.sdk.servlet.i18n";

    //corresponding value should be present in a message source:
    protected static final String I18N_TEST_KEY = "stormpath.web.login.title";

    // =================== Authentication Components ==========================

    @Value("#{ @environment['stormpath.web.authc.savers.cookie.enabled'] ?: true }")
    protected boolean cookieAuthenticationResultSaverEnabled;

    //session state storage should explicitly be enabled due to the performance impact it might have in
    //larger-scale environments (session state = shared state that must be distributed/clustered):
    @Value("#{ @environment['stormpath.web.authc.savers.session.enabled'] ?: false }")
    protected boolean sessionAuthenticationResultSaverEnabled;

    // ================  Account JWT properties  ===================

    @Value("#{ @environment['stormpath.web.account.jwt.ttl'] ?: 259200 }") //3 days by default
    protected long accountJwtTtl;

    @Value("#{ @environment['stormpath.web.account.jwt.signatureAlgorithm'] ?: 'HS256' }") //3 days by default
    protected SignatureAlgorithm accountJwtSignatureAlgorithm;

    // ================  HTTP Servlet Request behavior  ===================

    @Value("#{ @environment['stormpath.web.request.remoteUser.strategy'] ?: 'username' }")
    protected String requestRemoteUserStrategy;

    @Value("#{ @environment['stormpath.web.request.userPrincipal.strategy'] ?: 'account' }")
    protected String requestUserPrincipalStrategy;

    @Value("#{ @environment['stormpath.web.request.client.attributeNames'] ?: 'client' }")
    protected String requestClientAttributeNames;

    @Value("#{ @environment['stormpath.web.request.application.attributeNames'] ?: 'application' }")
    protected String requestApplicationAttributeNames;

    //By default, we want the the RequestMappingHandlerMapping to take precedence over this HandlerMapping: this
    //allows app developers to override any of the Stormpath default controllers by creating their own
    //@Controller class at the same URI path.
    //Spring Boot sets the default RequestMappingHandlerMapping's order to be zero, so we'll add a little
    //(lower numbers have higher precedence):
    @Value("#{ @environment['stormpath.web.handlerMapping.order'] ?: 10 }")
    protected int handlerMappingOrder;

    @Value("#{ @environment['stormpath.web.csrf.token.enabled'] ?: true }")
    protected boolean csrfTokenEnabled;

    @Value("#{ @environment['stormpath.web.csrf.token.ttl'] ?: 3600000 }") //1 hour (unit is millis)
    protected long csrfTokenTtl;

    @Value("#{ @environment['stormpath.web.csrf.token.name'] ?: 'csrfToken'}")
    protected String csrfTokenName;

    @Value("#{ @environment['stormpath.web.nonce.cache.name'] ?: 'com.stormpath.sdk.servlet.nonces' }")
    protected String nonceCacheName;

    @Value("#{ @environment['stormpath.web.http.authc.challenge'] ?: true }")
    protected boolean httpAuthenticationChallenge;

    // ================  StormpathFilter properties  ===================

    @Value("#{ @environment['stormpath.web.stormpathFilter.enabled'] ?: true }")
    protected boolean stormpathFilterEnabled;

    @Value("#{ @environment['stormpath.web.stormpathFilter.order'] ?: T(org.springframework.core.Ordered).HIGHEST_PRECEDENCE }")
    protected int stormpathFilterOrder;

    @Value("#{ @environment['stormpath.web.stormpathFilter.urlPatterns'] ?: '/*' }")
    protected String stormpathFilterUrlPatterns;

    @Value("#{ @environment['stormpath.web.stormpathFilter.servletNames'] }")
    protected String stormpathFilterServletNames;

    @Value("#{ @environment['stormpath.web.stormpathFilter.dispatcherTypes'] ?: 'REQUEST, INCLUDE, FORWARD, ERROR' }")
    protected String stormpathFilterDispatcherTypes;

    @Value("#{ @environment['stormpath.web.stormpathFilter.matchAfter'] ?: false }")
    protected boolean stormpathFilterMatchAfter;

    // ================  'Head' view template properties  ===================

    @Value("#{ @environment['stormpath.web.head.view'] ?: 'stormpath/head' }")
    protected String headView;

    @Value("#{ @environment['stormpath.web.head.fragmentSelector'] ?: 'head' }")
    protected String headFragmentSelector;

    @Value("#{ @environment['stormpath.web.head.cssUris'] ?: '//fonts.googleapis.com/css?family=Open+Sans:300italic,300,400italic,400,600italic,600,700italic,700,800italic,800 //netdna.bootstrapcdn.com/bootstrap/3.3.6/css/bootstrap.min.css /assets/css/stormpath.css' }")
    protected String headCssUris;

    @Value("#{ @environment['stormpath.web.head.extraCssUris'] }")
    protected String headExtraCssUris;

    // ================  Register Controller properties  ===================

    @Value("#{ @environment['stormpath.web.register.autoLogin'] ?: false }")
    protected boolean registerAutoLogin;

    // ================  Logout Controller properties  ===================

    @Value("#{ @environment['stormpath.web.logout.invalidateHttpSession'] ?: true }")
    protected boolean logoutInvalidateHttpSession;

    // ================  Access Token Controller properties  ===================

    @Value("#{ @environment['stormpath.web.oauth2.enabled'] ?: true }")
    protected boolean accessTokenEnabled;

    @Value("#{ @environment['stormpath.web.oauth2.uri'] ?: '/oauth/token' }")
    protected String accessTokenUri;

    @Value("#{ @environment['stormpath.web.oauth2.origin.authorizer.originUris'] }")
    protected String accessTokenAuthorizedOriginUris;

    @Value("#{ @environment['stormpath.web.oauth2.validationStrategy'] ?: 'stormpath'}")
    protected String accessTokenValidationStrategy;

    // ================  ID Site properties  ===================

    @Value("#{ @environment['stormpath.web.idSite.enabled'] ?: false }")
    protected boolean idSiteEnabled;

    @Value("#{ @environment['stormpath.web.idSite.loginUri'] }")
    protected String idSiteLoginUri; //null by default as it is assumed the id site root is the same as the login page (usually)

    @Value("#{ @environment['stormpath.web.idSite.registerUri'] ?: '/#/register' }")
    protected String idSiteRegisterUri;

    @Value("#{ @environment['stormpath.web.idSite.forgotUri'] ?: '/#/forgot' }")
    protected String idSiteForgotUri;

    @Value("#{ @environment['stormpath.web.idSite.resultUri'] ?: '/idSiteResult' }")
    protected String idSiteResultUri;

    @Value("#{ @environment['stormpath.web.idSite.useSubdomain'] }")
    protected Boolean idSiteUseSubdomain;

    @Value("#{ @environment['stormpath.web.idSite.showOrganizationField'] }")
    protected Boolean idSiteShowOrganizationField;

    @Value("#{ @environment['stormpath.web.callback.enabled'] ?: true }")
    protected boolean callbackEnabled;

    @Value("#{ @environment['stormpath.web.callback.uri'] ?: '/samlResult' }")
    protected String samlResultUri;

    // ================  Me Controller properties ==================

    @Value("#{ @environment['stormpath.web.me.enabled'] ?: true }")
    protected boolean meEnabled;

    @Value("#{ @environment['stormpath.web.me.uri'] ?: '/me' }")
    protected String meUri;

    @Value("#{ @environment['stormpath.web.me.nextUri'] ?: '/' }")
    protected String meNextUri;

    @Value("#{ @environment['stormpath.web.me.expand.groups'] ?: true }")
    protected boolean meExpandGroups;

    // ================  Content negotiation support properties  ===================

    @Value("#{ @environment['stormpath.web.produces'] ?: 'application/json, text/html' }")
    protected String produces;

    @Value("#{ @environment['stormpath.web.social.google.uri'] ?: '/callbacks/google' }")
    protected String googleCallbackUri;

    @Value("#{ @environment['stormpath.web.social.facebook.uri'] ?: '/callbacks/facebook' }")
    protected String facebookCallbackUri;

    @Value("#{ @environment['stormpath.web.social.linkedin.uri'] ?: '/callbacks/linkedin' }")
    protected String linkedinCallbackUri;

    @Value("#{ @environment['stormpath.web.social.github.uri'] ?: '/callbacks/github' }")
    protected String githubCallbackUri;

    @Value("#{ @environment['stormpath.web.application.domain'] }")
    protected String baseDomainName;

    @Value("#{ @environment['stormpath.web.json.view'] ?: 'stormpathJsonView' }")
    protected String jsonView;

    //Spring's ThymeleafViewResolver defaults to an order of Ordered.LOWEST_PRECEDENCE - 5.  We want to ensure that this
    //JSON view resolver has a slightly higher precedence to ensure that JSON is rendered and not a Thymeleaf template.
    @Value("#{ @environment['stormpath.web.json.view.resolver.order'] ?: T(org.springframework.core.Ordered).LOWEST_PRECEDENCE - 10 }")
    protected int jsonViewResolverOrder;

    @Autowired(required = false)
    protected PathMatcher pathMatcher;

    @Autowired(required = false)
    protected UrlPathHelper urlPathHelper;

    @Autowired
    protected Client client;

    @Autowired
    @Qualifier("stormpathApplication")
    protected Application application;

    @Autowired(required = false)
    protected MessageSource messageSource;

    @Autowired(required = false)
    protected LocaleResolver localeResolver;

    @Autowired(required = false)
    protected LocaleChangeInterceptor localeChangeInterceptor;

    @Autowired(required = false)
    @Qualifier("springSecurityIdSiteResultListener")
    IdSiteResultListener springSecurityIdSiteResultListener;

    @Autowired(required = false)
    @Qualifier("springSecuritySamlResultListener")
    SamlResultListener springSecuritySamlResultListener;

    @Autowired(required = false)
    protected ErrorModelFactory loginErrorModelFactory;

    @Autowired(required = false)
    protected ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    protected Environment environment;

    @Autowired(required = false)
    @Qualifier("loginPreHandler")
    protected WebHandler loginPreHandler;

    @Autowired(required = false)
    @Qualifier("loginPostHandler")
    protected WebHandler loginPostHandler;

    @Autowired(required = false)
    @Qualifier("registerPreHandler")
    protected WebHandler registerPreHandler;

    @Autowired(required = false)
    @Qualifier("registerPostHandler")
    protected WebHandler registerPostHandler;

    public HandlerMapping stormpathHandlerMapping() throws Exception {

        Map<String, Controller> mappings = new LinkedHashMap<String, Controller>();

        if (stormpathLoginControllerConfigResolver().isEnabled()) {
            mappings.put(stormpathLoginControllerConfigResolver().getUri(), stormpathLoginController());
            mappings.put(googleCallbackUri, stormpathGoogleCallbackController());
            mappings.put(githubCallbackUri, stormpathGithubCallbackController());
            mappings.put(facebookCallbackUri, stormpathFacebookCallbackController());
            mappings.put(linkedinCallbackUri, stormpathLinkedinCallbackController());
        }
        if (stormpathLogoutControllerConfigResolver().isEnabled()) {
            mappings.put(stormpathLogoutControllerConfigResolver().getUri(), stormpathLogoutController());
        }
        if (stormpathRegisterControllerConfigResolver().isEnabled()) {
            mappings.put(stormpathRegisterControllerConfigResolver().getUri(), stormpathRegisterController());
        }
        if (stormpathVerifyControllerConfigResolver().isEnabled()) {
            mappings.put(stormpathVerifyControllerConfigResolver().getUri(), stormpathVerifyController());
            mappings.put(stormpathSendVerificationEmailControllerConfigResolver().getUri(), stormpathSendVerificationEmailController());
        }
        if (stormpathForgotPasswordControllerConfigResolver().isEnabled()) {
            mappings.put(stormpathForgotPasswordControllerConfigResolver().getUri(), stormpathForgotPasswordController());
        }
        if (stormpathChangePasswordControllerConfigResolver().isEnabled()) {
            mappings.put(stormpathChangePasswordControllerConfigResolver().getUri(), stormpathChangePasswordController());
        }
        if (accessTokenEnabled) {
            mappings.put(accessTokenUri, stormpathAccessTokenController());
        }
        if (idSiteEnabled) {
            mappings.put(idSiteResultUri, stormpathIdSiteResultController());
        }
        if (callbackEnabled) {
            mappings.put("/saml", stormpathSamlController());
            mappings.put(samlResultUri, stormpathSamlResultController());
        }
        if (meEnabled) {
            mappings.put(meUri, stormpathMeController());
        }

        SimpleUrlHandlerMapping mapping = new SimpleUrlHandlerMapping();
        mapping.setOrder(handlerMappingOrder);
        mapping.setUrlMap(mappings);

        mapping.setInterceptors(new Object[]{stormpathLocaleChangeInterceptor(), stormpathLayoutInterceptor()});

        if (pathMatcher != null) {
            mapping.setPathMatcher(pathMatcher);
        }

        if (urlPathHelper != null) {
            mapping.setUrlPathHelper(urlPathHelper);
        }

        return mapping;
    }

    public Controller stormpathGoogleCallbackController() {
        GoogleCallbackController googleCallbackController = new GoogleCallbackController(
                stormpathLoginControllerConfigResolver().getNextUri(),
                stormpathAuthenticationResultSaver()
        );

        return createSpringController(googleCallbackController);
    }

    public Controller stormpathGithubCallbackController() {
        GithubCallbackController githubCallbackController = new GithubCallbackController(
                stormpathLoginControllerConfigResolver().getNextUri(),
                stormpathAuthenticationResultSaver()
        );

        return createSpringController(githubCallbackController);
    }

    public Controller stormpathFacebookCallbackController() {
        FacebookCallbackController facebookCallbackController = new FacebookCallbackController(
                stormpathLoginControllerConfigResolver().getNextUri(),
                stormpathAuthenticationResultSaver()
        );

        return createSpringController(facebookCallbackController);
    }

    public Controller stormpathLinkedinCallbackController() {
        LinkedinCallbackController linkedinCallbackController = new LinkedinCallbackController(
                stormpathLoginControllerConfigResolver().getNextUri(),
                stormpathAuthenticationResultSaver()
        );

        return createSpringController(linkedinCallbackController);
    }

    public HandlerInterceptor stormpathLayoutInterceptor() throws Exception {
        TemplateLayoutInterceptor interceptor = new TemplateLayoutInterceptor();
        interceptor.setHeadViewName(headView);
        interceptor.setHeadFragmentSelector(headFragmentSelector);

        //deal w/ URIs:
        String[] uris = StringUtils.tokenizeToStringArray(headCssUris, " \t");
        Set<String> uriSet = new LinkedHashSet<String>();
        if (uris != null && uris.length > 0) {
            java.util.Collections.addAll(uriSet, uris);
        }

        uris = StringUtils.tokenizeToStringArray(headExtraCssUris, " \t");
        if (uris != null && uris.length > 0) {
            java.util.Collections.addAll(uriSet, uris);
        }

        if (!Collections.isEmpty(uriSet)) {
            List<String> list = new ArrayList<String>();
            list.addAll(uriSet);
            interceptor.setHeadCssUris(list);
        }

        interceptor.afterPropertiesSet();

        return interceptor;
    }

    /**
     * @since 1.0.0
     */
    public List<MediaType> stormpathProducesMediaTypes() {

        String mediaTypes = Strings.clean(produces);
        Assert.notNull(mediaTypes, "stormpath.web.produces property value cannot be null or empty.");

        try {
            return MediaType.parseMediaTypes(mediaTypes);
        } catch (InvalidMediaTypeException e) {
            String msg = "Unable to parse value in stormpath.web.produces property: " + e.getMessage();
            throw new IllegalArgumentException(msg, e);
        }
    }

    public org.springframework.web.servlet.View stormpathJsonView() {
        return new MappingJackson2JsonView(objectMapper);
    }

    interface OrderedViewResolver extends ViewResolver, Ordered {
    }

    public ViewResolver stormpathJsonViewResolver() {

        return new OrderedViewResolver() {

            @Override
            public int getOrder() {
                return jsonViewResolverOrder;
            }

            @Override
            public View resolveViewName(String viewName, Locale locale) throws Exception {
                if (viewName.equals(jsonView)) {
                    return stormpathJsonView();
                }
                return null;
            }
        };
    }

    public AccountStoreResolver stormpathAccountStoreResolver() {
        return new DisabledAccountStoreResolver();
    }

    public UsernamePasswordRequestFactory stormpathUsernamePasswordRequestFactory() {
        return new DefaultUsernamePasswordRequestFactory(stormpathAccountStoreResolver());
    }

    public AccessTokenCookieProperties accessTokenCookieProperties() {
        return new AccessTokenCookieProperties();
    }

    public RefreshTokenCookieProperties refreshTokenCookieProperties() {
        return new RefreshTokenCookieProperties();
    }

    public CookieConfig stormpathRefreshTokenCookieConfig() {
        return new RefreshTokenCookieConfig(refreshTokenCookieProperties());
    }

    public CookieConfig stormpathAccessTokenCookieConfig() {
        return new AccessTokenCookieConfig(accessTokenCookieProperties());
    }

    public Resolver<String> stormpathRemoteAddrResolver() {
        return new RemoteAddrResolver();
    }

    public Resolver<Boolean> stormpathLocalhostResolver() {
        return new IsLocalhostResolver(stormpathRemoteAddrResolver());
    }

    public Resolver<Boolean> stormpathSecureResolver() {
        return new SecureRequiredExceptForLocalhostResolver(stormpathLocalhostResolver());
    }

    public Saver<AuthenticationResult> stormpathCookieAuthenticationResultSaver() {

        if (cookieAuthenticationResultSaverEnabled) {
            return new CookieAuthenticationResultSaver(
                    stormpathAccessTokenCookieConfig(),
                    stormpathRefreshTokenCookieConfig(),
                    stormpathSecureResolver()
            );
        }

        //otherwise, return a dummy saver:
        return DisabledAuthenticationResultSaver.INSTANCE;
    }

    public Saver<AuthenticationResult> stormpathSessionAuthenticationResultSaver() {

        if (sessionAuthenticationResultSaverEnabled) {
            String[] attributeNames = {Account.class.getName(), "account"};
            Set<String> set = new HashSet<String>(Arrays.asList(attributeNames));
            return new SessionAuthenticationResultSaver(set);
        }

        return DisabledAuthenticationResultSaver.INSTANCE;
    }

    public List<Saver<AuthenticationResult>> stormpathAuthenticationResultSavers() {

        List<Saver<AuthenticationResult>> savers = new ArrayList<Saver<AuthenticationResult>>();

        Saver<AuthenticationResult> saver = stormpathCookieAuthenticationResultSaver();
        if (!(saver instanceof DisabledAuthenticationResultSaver)) {
            savers.add(saver);
        }

        saver = stormpathSessionAuthenticationResultSaver();
        if (!(saver instanceof DisabledAuthenticationResultSaver)) {
            savers.add(saver);
        }

        return savers;
    }

    public AuthenticationResultSaver stormpathAuthenticationResultSaver() {

        List<Saver<AuthenticationResult>> savers = stormpathAuthenticationResultSavers();

        if (Collections.isEmpty(savers)) {
            String msg = "No Saver<AuthenticationResult> instances have been enabled or configured.  This is " +
                    "required to save authentication result state.";
            throw new IllegalStateException(msg);
        }

        return new AuthenticationResultSaver(savers);
    }

    public JwtSigningKeyResolver stormpathJwtSigningKeyResolver() {
        return new JwtTokenSigningKeyResolver();
    }

    public RequestEventListener stormpathRequestEventListener() {
        return new RequestEventListenerAdapter();
    }

    public Publisher<RequestEvent> stormpathRequestEventPublisher() {
        List<RequestEventListener> listeners = new ArrayList<RequestEventListener>();
        listeners.add(new TokenRevocationRequestEventListener()); //revoke access and refresh tokens after logout
        listeners.add(stormpathRequestEventListener());
        return new RequestEventPublisher(listeners);
    }

    public String stormpathCsrfTokenSigningKey() {
        return client.getApiKey().getSecret();
    }

    public JwtAccountResolver stormpathJwtAccountResolver() {
        return new DefaultJwtAccountResolver(stormpathJwtSigningKeyResolver());
    }

    public Cache<String, String> stormpathNonceCache() {
        return client.getCacheManager().getCache(nonceCacheName);
    }

    public CsrfTokenManager stormpathCsrfTokenManager() {

        if (csrfTokenEnabled) {
            return new DefaultCsrfTokenManager(csrfTokenName, stormpathNonceCache(), stormpathCsrfTokenSigningKey(), csrfTokenTtl);
        }

        //otherwise disabled, return dummy implementation (NullObject design pattern):
        return new DisabledCsrfTokenManager(csrfTokenName);
    }

    public AccessTokenResultFactory stormpathAccessTokenResultFactory() {
        return new DefaultAccessTokenResultFactory(application);
    }

    /**
     * @since 1.0.RC8.3
     */
    public RefreshTokenResultFactory stormpathRefreshTokenResultFactory() {
        return new DefaultRefreshTokenResultFactory(application);
    }

    public WrappedServletRequestFactory stormpathWrappedServletRequestFactory() {
        return new DefaultWrappedServletRequestFactory(
                stormpathUsernamePasswordRequestFactory(), stormpathAuthenticationResultSaver(),
                stormpathRequestEventPublisher(), requestUserPrincipalStrategy, requestRemoteUserStrategy
        );
    }

    public HttpAuthenticationScheme stormpathBasicAuthenticationScheme() {
        return new BasicAuthenticationScheme(stormpathUsernamePasswordRequestFactory());
    }

    public HttpAuthenticationScheme stormpathBearerAuthenticationScheme() {
        return new BearerAuthenticationScheme(stormpathJwtSigningKeyResolver(), AccessTokenValidationStrategy.fromName(accessTokenValidationStrategy));
    }

    public List<HttpAuthenticationScheme> stormpathHttpAuthenticationSchemes() {
        // The HTTP spec says that more well-supported authentication schemes should be listed first when the challenge
        // is sent.  The default Stormpath header authenticator implementation will send challenge entries in the
        // specified list order.  Since 'basic' is a more well-supported scheme than 'bearer' we order basic with
        // higher priority than bearer.
        return Arrays.asList(stormpathBasicAuthenticationScheme(), stormpathBearerAuthenticationScheme());
    }

    public HeaderAuthenticator stormpathAuthorizationHeaderAuthenticator() {
        return new AuthorizationHeaderAuthenticator(
                stormpathHttpAuthenticationSchemes(), httpAuthenticationChallenge, stormpathRequestEventPublisher()
        );
    }

    public Resolver<Account> stormpathAuthorizationHeaderAccountResolver() {
        return new AuthorizationHeaderAccountResolver(stormpathAuthorizationHeaderAuthenticator(), idSiteResultUri);
    }

    public Resolver<Account> stormpathCookieAccountResolver() {
        return new CookieAccountResolver(
                stormpathAccessTokenCookieConfig(),
                stormpathRefreshTokenCookieConfig(),
                stormpathJwtAccountResolver(),
                stormpathCookieAuthenticationResultSaver(),
                stormpathAccessTokenResultFactory());
    }

    public Resolver<Account> stormpathSessionAccountResolver() {
        return new SessionAccountResolver();
    }

    public List<Resolver<Account>> stormpathAccountResolvers() {

        //the order determines which locations are checked.  One an account is found, the remaining locations are
        //skipped, so we must order them based on preference:
        List<Resolver<Account>> resolvers = new ArrayList<Resolver<Account>>(3);
        resolvers.add(stormpathAuthorizationHeaderAccountResolver());
        resolvers.add(stormpathCookieAccountResolver());
        resolvers.add(stormpathSessionAccountResolver());

        return resolvers;
    }

    public Resolver<List<String>> stormpathSubdomainResolver() {
        SubdomainResolver resolver = new SubdomainResolver();
        resolver.setBaseDomainName(baseDomainName);
        return resolver;
    }

    public Resolver<String> stormpathOrganizationNameKeyResolver() {
        DefaultOrganizationNameKeyResolver resolver = new DefaultOrganizationNameKeyResolver();
        resolver.setSubdomainResolver(stormpathSubdomainResolver());
        return resolver;
    }

    public Resolver<IdSiteOrganizationContext> stormpathIdSiteOrganizationResolver() {
        DefaultIdSiteOrganizationResolver resolver = new DefaultIdSiteOrganizationResolver();
        resolver.setOrganizationNameKeyResolver(stormpathOrganizationNameKeyResolver());
        resolver.setUseSubdomain(idSiteUseSubdomain);
        resolver.setShowOrganizationField(idSiteShowOrganizationField);
        return resolver;
    }

    /**
     * @since 1.0.RC8
     */
    public Resolver<SamlOrganizationContext> stormpathSamlOrganizationResolver() {
        DefaultSamlOrganizationResolver resolver = new DefaultSamlOrganizationResolver();
        resolver.setOrganizationNameKeyResolver(stormpathOrganizationNameKeyResolver());
        return resolver;
    }

    protected Controller createIdSiteController(String idSiteUri) {
        IdSiteController controller = new IdSiteController();
        controller.setServerUriResolver(stormpathServerUriResolver());
        controller.setIdSiteUri(idSiteUri);
        controller.setCallbackUri(idSiteResultUri);
        controller.setAlreadyLoggedInUri(stormpathLoginControllerConfigResolver().getNextUri());
        controller.setIdSiteOrganizationResolver(stormpathIdSiteOrganizationResolver());
        controller.init();
        return createSpringController(controller);
    }

    /**
     * @since 1.0.RC8
     */
    protected Controller stormpathSamlController() {
        SamlController controller = new SamlController();
        controller.setServerUriResolver(stormpathServerUriResolver());
        controller.setCallbackUri(samlResultUri);
        controller.setAlreadyLoggedInUri(stormpathLoginControllerConfigResolver().getNextUri());
        controller.setSamlOrganizationResolver(stormpathSamlOrganizationResolver());
        controller.init();
        return createSpringController(controller);
    }

    public ErrorModelFactory stormpathLoginErrorModelFactory() {
        return new LoginErrorModelFactory(stormpathMessageSource());
    }

    protected String createForwardView(String uri) {
        Assert.hasText("uri cannot be null or empty.");
        assert uri != null;
        if (!uri.startsWith("forward:")) {
            uri = "forward:" + uri;
        }
        return uri;
    }


    public AccountStoreModelFactory stormpathAccountStoreModelFactory() {
        return new DefaultAccountStoreModelFactory();
    }

    public ControllerConfigResolver stormpathLoginControllerConfigResolver() {
        return new LoginControllerConfigResolver();
    }

    public Controller stormpathLoginController() {

        if (idSiteEnabled) {
            return createIdSiteController(idSiteLoginUri);
        }

        //otherwise standard login controller:
        LoginController controller = new LoginController(stormpathInternalConfig(), loginErrorModelFactory);

        return createSpaAwareSpringController(controller);
    }

    public ControllerConfigResolver stormpathForgotPasswordControllerConfigResolver() {
        return new ForgotPasswordControllerConfigResolver();
    }

    public Controller stormpathForgotPasswordController() {

        if (idSiteEnabled) {
            return createIdSiteController(idSiteForgotUri);
        }

        ForgotPasswordController controller = new ForgotPasswordController(stormpathInternalConfig());

        return createSpaAwareSpringController(controller);
    }

    private Controller createSpaAwareSpringController(com.stormpath.sdk.servlet.mvc.Controller controller) {

        Controller c = createSpringController(controller);

        if (produces.contains(MediaType.APPLICATION_JSON.toString())) {
            c = new SpringSpaController(c, jsonView, stormpathProducesMediaTypes());
        }

        return c;
    }

    private Controller createSpringController(com.stormpath.sdk.servlet.mvc.Controller controller) {
        SpringController springController = new SpringController(controller);
        if (urlPathHelper != null) {
            springController.setUrlPathHelper(urlPathHelper);
        }
        return springController;
    }

    public LocaleResolver stormpathSpringLocaleResolver() {
        if (localeResolver != null) {
            return localeResolver;
        }

        //otherwise create a default:
        return new CookieLocaleResolver();
    }

    public LocaleChangeInterceptor stormpathLocaleChangeInterceptor() {

        if (localeChangeInterceptor != null) {
            return localeChangeInterceptor;
        }

        //otherwise create a default:
        LocaleChangeInterceptor lci = new LocaleChangeInterceptor();
        lci.setParamName("lang");
        return lci;
    }

    public Set<String> stormpathRequestClientAttributeNames() {
        Set<String> set = new LinkedHashSet<String>();
        set.addAll(Strings.commaDelimitedListToSet(requestClientAttributeNames));
        //we always want the client to be available as an attribute by it's own class name:
        set.add(Client.class.getName());
        return set;
    }

    public Set<String> stormpathRequestApplicationAttributeNames() {
        Set<String> set = new LinkedHashSet<String>();
        set.addAll(Strings.commaDelimitedListToSet(requestApplicationAttributeNames));
        set.add(Application.class.getName());
        return set;
    }

    public Resolver<Locale> stormpathLocaleResolver() {

        final LocaleResolver localeResolver = stormpathSpringLocaleResolver();

        return new Resolver<Locale>() {
            @Override
            public Locale get(HttpServletRequest request, HttpServletResponse response) {
                return localeResolver.resolveLocale(request);
            }
        };
    }

    public MessageSource stormpathSpringMessageSource() {

        //we need the default i18n keys if the user hasn't configured their own:

        MessageSource messageSource = this.messageSource;

        if (messageSource == null || isPlaceholder(messageSource)) {
            messageSource = createI18nPropertiesMessageSource();
        } else {

            //not null, so the user configured their own.  Need to ensure the stormpath keys are resolvable:

            boolean stormpathI18nAlreadyConfigured = false;

            try {
                String value = messageSource.getMessage(I18N_TEST_KEY, null, Locale.ENGLISH);
                Assert.hasText(value, "i18n message key " + I18N_TEST_KEY + " must resolve to a non-empty value.");
                stormpathI18nAlreadyConfigured = true;
            } catch (NoSuchMessageException e) {
                log.debug("Stormpath i18n properties have not been specified during message source configuration.  " +
                        "Adding these property values as a fallback. Exception for reference (this and the " +
                        "stack trace can safely be ignored): " + e.getMessage(), e);
            }

            if (!stormpathI18nAlreadyConfigured) {

                //we need to 'wrap' the existing message source and ensure it can fall back to our default values.
                //ensure the user-configured message source is first to take precedence:
                messageSource = new CompositeMessageSource(messageSource, createI18nPropertiesMessageSource());
            }
        }

        return messageSource;
    }

    /**
     * At ApplicationContext startup, Spring creates a placeholder in the ApplicationContext to await a 'real' message
     * source.  This method returns {@code true} if the specified message source is just a placeholder (and not relevant
     * for our needs) or {@code false} if the message source is a 'real' message source and usable.
     *
     * @param messageSource the message source to check
     * @return {@code true} if the specified message source is just a placeholder (and not relevant for our needs) or
     * {@code false} if the message source is a 'real' message source and usable.
     */
    //
    protected boolean isPlaceholder(MessageSource messageSource) {
        return messageSource instanceof DelegatingMessageSource &&
                ((DelegatingMessageSource) messageSource).getParentMessageSource() == null;
    }

    protected MessageSource createI18nPropertiesMessageSource() {
        ResourceBundleMessageSource src = new ResourceBundleMessageSource();
        src.setBasename(I18N_PROPERTIES_BASENAME);
        src.setDefaultEncoding("UTF-8");
        return src;
    }

    public com.stormpath.sdk.servlet.i18n.MessageSource stormpathMessageSource() {

        final MessageSource springMessageSource = stormpathSpringMessageSource();

        return new com.stormpath.sdk.servlet.i18n.MessageSource() {

            @Override
            public String getMessage(String key, Locale locale) {
                return getMessage(key, locale, new Object[0]);
            }

            @Override
            public String getMessage(String key, String defaultMessage, Locale locale, Object... args) {
                try {
                    return springMessageSource.getMessage(key, args, locale);
                } catch (NoSuchMessageException e) {
                    //Same behavior as com.stormpath.sdk.servlet.i18n.DelegatingMessageSource
                    log.warn("i18n key not found", e);
                    return defaultMessage;
                }
            }

            @Override
            public String getMessage(String key, String defaultMessage, Locale locale) {
                try {
                    return springMessageSource.getMessage(key, new Object[0], locale);
                } catch (NoSuchMessageException e) {
                    //Same behavior as com.stormpath.sdk.servlet.i18n.DelegatingMessageSource
                    log.warn("i18n key not found", e);
                    return defaultMessage;
                }
            }

            @Override
            public String getMessage(String key, Locale locale, Object... args) {
                try {
                    return springMessageSource.getMessage(key, args, locale);
                } catch (NoSuchMessageException e) {
                    //Same behavior as com.stormpath.sdk.servlet.i18n.DelegatingMessageSource
                    log.warn("i18n key not found", e);
                    return '!' + key + '!';
                }
            }
        };
    }

    private List<Field> toDefaultFields(List<Field> fields) {
        List<Field> defaultFields = new ArrayList<Field>(fields.size());
        for (Field field : fields) {
            Assert.isInstanceOf(DefaultField.class, field);
            defaultFields.add(field);
        }

        return defaultFields;
    }

    public ControllerConfigResolver stormpathRegisterControllerConfigResolver() {
        return new RegisterControllerConfigResolver();
    }

    public Controller stormpathRegisterController() {

        if (idSiteEnabled) {
            return createIdSiteController(idSiteRegisterUri);
        }

        //otherwise standard registration:
        RegisterController controller = new RegisterController(stormpathInternalConfig(), client);

        return createSpaAwareSpringController(controller);
    }

    public ControllerConfigResolver stormpathVerifyControllerConfigResolver() {
        return new VerifyControllerConfigResolver();
    }

    public Controller stormpathVerifyController() {

        if (idSiteEnabled) {
            return createIdSiteController(null);
        }

        VerifyController controller = new VerifyController(
                stormpathVerifyControllerConfigResolver(),
                stormpathLogoutControllerConfigResolver().getUri(),
                stormpathSendVerificationEmailControllerConfigResolver().getUri(),
                client,
                produces
        );

        return createSpringController(controller);
    }

    public ControllerConfigResolver stormpathSendVerificationEmailControllerConfigResolver() {
        return new SendVerificationEmailControllerConfigResolver();
    }

    public Controller stormpathSendVerificationEmailController() {
        if (idSiteEnabled) {
            return createIdSiteController(null);
        }

        SendVerificationEmailController controller = new SendVerificationEmailController(stormpathInternalConfig());

        return createSpringController(controller);
    }

    public ControllerConfigResolver stormpathChangePasswordControllerConfigResolver() {
        return new ChangePasswordControllerConfigResolver();
    }

    public Controller stormpathChangePasswordController() {

        if (idSiteEnabled) {
            return createIdSiteController(null);
        }

        ChangePasswordController controller = new ChangePasswordController(stormpathInternalConfig());

        return createSpaAwareSpringController(controller);
    }

    public Controller stormpathAccessTokenController() {

        AccessTokenController c = new AccessTokenController();
        c.setEventPublisher(stormpathRequestEventPublisher());
        c.setAccessTokenAuthenticationRequestFactory(stormpathAccessTokenAuthenticationRequestFactory());
        c.setAccessTokenResultFactory(stormpathAccessTokenResultFactory());
        c.setRefreshTokenAuthenticationRequestFactory(stormpathRefreshTokenAuthenticationRequestFactory());
        c.setRefreshTokenResultFactory(stormpathRefreshTokenResultFactory());
        c.setAccountSaver(stormpathAuthenticationResultSaver());
        c.setRequestAuthorizer(stormpathAccessTokenRequestAuthorizer());
        c.setBasicAuthenticationScheme(stormpathBasicAuthenticationScheme());
        c.init();

        return createSpringController(c);
    }

    public Controller stormpathIdSiteResultController() {
        IdSiteResultController controller = new IdSiteResultController();
        controller.setLoginNextUri(stormpathLoginControllerConfigResolver().getNextUri());
        controller.setRegisterNextUri(stormpathRegisterControllerConfigResolver().getNextUri());
        controller.setLogoutController(stormpathMvcLogoutController());
        controller.setAuthenticationResultSaver(stormpathAuthenticationResultSaver());
        controller.setEventPublisher(stormpathRequestEventPublisher());
        controller.setAccessTokenResultFactory(stormpathAccessTokenResultFactory());
        if (springSecurityIdSiteResultListener != null) {
            controller.addIdSiteResultListener(springSecurityIdSiteResultListener);
        }
        controller.init();
        return createSpringController(controller);
    }

    public Controller stormpathMeController() {
        List<String> results = new ArrayList<String>();

        getPropertiesStartingWith((ConfigurableEnvironment) environment, "stormpath.web.me.expand");

        Pattern pattern = Pattern.compile("^stormpath\\.web\\.me\\.expand\\.(\\w+)$");

        for (String key : getPropertiesStartingWith((ConfigurableEnvironment) environment, "stormpath.web.me.expand").keySet()) {
            Matcher matcher = pattern.matcher(key);
            if (matcher.find()) {
                if (environment.getProperty(key, Boolean.class, false)) {
                    results.add(matcher.group(1));
                }
            }
        }

        MeController controller = new MeController(results);

        return createSpaAwareSpringController(controller);
    }

    public Controller stormpathSamlResultController() {
        SamlResultController controller = new SamlResultController();
        controller.setLoginNextUri(stormpathLoginControllerConfigResolver().getNextUri());
        controller.setLogoutController(stormpathMvcLogoutController());
        controller.setAuthenticationResultSaver(stormpathAuthenticationResultSaver());
        controller.setEventPublisher(stormpathRequestEventPublisher());
        if (springSecuritySamlResultListener != null) {
            controller.addSamlResultListener(springSecuritySamlResultListener);
        }
        controller.init();
        return createSpringController(controller);
    }

    public AccessTokenAuthenticationRequestFactory stormpathAccessTokenAuthenticationRequestFactory() {
        return new DefaultAccessTokenAuthenticationRequestFactory(stormpathAccountStoreResolver());
    }

    /**
     * @since 1.0.RC8.3
     */
    public RefreshTokenAuthenticationRequestFactory stormpathRefreshTokenAuthenticationRequestFactory() {
        return new DefaultRefreshTokenAuthenticationRequestFactory();
    }

    public RequestAuthorizer stormpathAccessTokenRequestAuthorizer() {
        return new DefaultAccessTokenRequestAuthorizer(
                stormpathSecureResolver(), stormpathOriginAccessTokenRequestAuthorizer()
        );
    }

    public Set<String> stormpathAccessTokenAuthorizedOriginUris() {
        return Strings.delimitedListToSet(accessTokenAuthorizedOriginUris, " \t");
    }

    public RequestAuthorizer stormpathOriginAccessTokenRequestAuthorizer() {
        return new OriginAccessTokenRequestAuthorizer(
                stormpathServerUriResolver(), stormpathLocalhostResolver(), stormpathAccessTokenAuthorizedOriginUris(),
                stormpathProducesMediaTypes()
        );
    }

    public ServerUriResolver stormpathServerUriResolver() {
        return new DefaultServerUriResolver();
    }

    public ControllerConfigResolver stormpathLogoutControllerConfigResolver() {
        return new LogoutControllerConfigResolver();
    }

    public com.stormpath.sdk.servlet.mvc.Controller stormpathMvcLogoutController() {

        LogoutController controller = new LogoutController(
                stormpathInternalConfig().getLogoutControllerConfig(),
                stormpathInternalConfig().getProducesMediaTypes());

        if (idSiteEnabled) {
            IdSiteLogoutController c = new IdSiteLogoutController(stormpathInternalConfig().getLogoutControllerConfig(), stormpathInternalConfig().getProducesMediaTypes());
            c.setServerUriResolver(stormpathServerUriResolver());
            c.setIdSiteResultUri(idSiteResultUri);
            c.setIdSiteOrganizationResolver(stormpathIdSiteOrganizationResolver());
            controller = c;
        }

        if (callbackEnabled) {
            SamlLogoutController c = new SamlLogoutController(stormpathInternalConfig().getLogoutControllerConfig(), stormpathInternalConfig().getProducesMediaTypes());
            c.setServerUriResolver(stormpathServerUriResolver());
            c.setSamlResultUri(samlResultUri);
            controller = c;
        }

        controller.setLogoutNextUri(stormpathLogoutControllerConfigResolver().getNextUri());
        controller.setLogoutInvalidateHttpSession(logoutInvalidateHttpSession);
        controller.setInvalidateHttpSession(logoutInvalidateHttpSession);
        controller.init();

        return controller;
    }

    public Controller stormpathLogoutController() {
        return createSpringController(stormpathMvcLogoutController());
    }

    public Config stormpathInternalConfig() {

        final com.stormpath.sdk.servlet.i18n.MessageSource messageSource = stormpathMessageSource();
        final Resolver<Locale> localeResolver = stormpathLocaleResolver();

        return new Config() {
            @Override
            public WebHandler getLoginPreHandler() {
                return loginPreHandler;
            }

            @Override
            public WebHandler getLoginPostHandler() {
                return loginPostHandler;
            }

            @Override
            public WebHandler getRegisterPreHandler() {
                return registerPreHandler;
            }

            @Override
            public WebHandler getRegisterPostHandler() {
                return registerPostHandler;
            }

            @Override
            public ControllerConfigResolver getLoginControllerConfig() {
                return stormpathLoginControllerConfigResolver();
            }

            @Override
            public ControllerConfigResolver getLogoutControllerConfig() {
                return stormpathLogoutControllerConfigResolver();
            }

            @Override
            public ControllerConfigResolver getRegisterControllerConfig() {
                return stormpathRegisterControllerConfigResolver();
            }

            @Override
            public ControllerConfigResolver getForgotPasswordControllerConfig() {
                return stormpathForgotPasswordControllerConfigResolver();
            }

            @Override
            public ControllerConfigResolver getVerifyControllerConfig() {
                return stormpathVerifyControllerConfigResolver();
            }

            @Override
            public ControllerConfigResolver getSendVerificationEmailControllerConfig() {
                return stormpathSendVerificationEmailControllerConfigResolver();
            }

            @Override
            public ControllerConfigResolver getChangePasswordControllerConfig() {
                return stormpathChangePasswordControllerConfigResolver();
            }

            @Override
            public Saver<AuthenticationResult> getAuthenticationResultSaver() {
                return stormpathAuthenticationResultSaver();
            }

            @Override
            public AccountStoreResolver getAccountStoreResolver() {
                return stormpathAccountStoreResolver();
            }

            @Override
            public boolean isLogoutInvalidateHttpSession() {
                return logoutInvalidateHttpSession;
            }

            @Override
            public CookieConfig getRefreshTokenCookieConfig() {
                return stormpathRefreshTokenCookieConfig();
            }

            @Override
            public CookieConfig getAccessTokenCookieConfig() {
                return stormpathAccessTokenCookieConfig();
            }

            @Override
            public String getAccessTokenUrl() {
                return accessTokenUri;
            }

            @Override
            public String getUnauthorizedUrl() {
                return "/unauthorized";
            }

            @Override
            public boolean isMeEnabled() {
                return meEnabled;
            }

            @Override
            public String getMeUrl() {
                return meUri;
            }

            @Override
            public boolean isRegisterAutoLoginEnabled() {
                return registerAutoLogin;
            }

            @Override
            public boolean isSamlLoginEnabled() {
                return callbackEnabled;
            }

            @Override
            public List<String> getMeExpandedProperties() {
                return java.util.Collections.EMPTY_LIST;
            }

            @Override
            public String getAccessTokenValidationStrategy() {
                return accessTokenValidationStrategy;
            }

            @Override
            public <T> T getInstance(String classPropertyName) throws ServletException {
                if (MessageTag.LOCALE_RESOLVER_CONFIG_KEY.equals(classPropertyName)) {
                    return (T) localeResolver;
                } else if (MessageTag.MESSAGE_SOURCE_CONFIG_KEY.equals(classPropertyName)) {
                    return (T) messageSource;
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
            public String getProducesMediaTypes() {
                return produces;
            }

            @Override
            public boolean isOAuthEnabled() {
                return accessTokenEnabled;
            }

            @Override
            public boolean isIdSiteEnabled() {
                return idSiteEnabled;
            }

            @Override
            public boolean isCallbackEnabled() {
                return callbackEnabled;
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
                return java.util.Collections.EMPTY_SET;
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

    public FilterChainResolver stormpathFilterChainResolver() {

        return new FilterChainResolver() {

            @Override
            public FilterChain getChain(HttpServletRequest request, HttpServletResponse response, FilterChain chain) {

                // The account resolver filter always executes immediately after the StormpathFilter but
                // before any other configured filters in the chain:
                Filter accountResolverFilter = stormpathAccountResolverFilter();
                List<Filter> immediateExecutionFilters = Arrays.asList(accountResolverFilter);
                chain = new ProxiedFilterChain(chain, immediateExecutionFilters);

                return chain;
            }
        };
    }

    public Filter stormpathAccountResolverFilter() {

        List<Resolver<Account>> resolvers = stormpathAccountResolvers();

        org.springframework.util.Assert.notEmpty(resolvers, "Account resolver collection cannot be null or empty.");

        AccountResolverFilter filter = new AccountResolverFilter();
        filter.setEnabled(stormpathFilterEnabled);
        filter.setResolvers(resolvers);
        filter.setOauthEndpointUri(accessTokenUri);

        return filter;
    }

    //Class that suppresses configuration logic in the default StormpathFilter implementation
    //Dependencies must be injected into the instance in @Bean annotated methods explicitly.
    public static class SpringStormpathFilter extends StormpathFilter {

        @Override
        protected void onInit() throws ServletException {
            //no-op - we apply dependencies via setters
        }
    }

    protected static class DisabledAuthenticationResultSaver implements Saver<AuthenticationResult> {

        protected static final DisabledAuthenticationResultSaver INSTANCE = new DisabledAuthenticationResultSaver();

        @Override
        public void set(HttpServletRequest request, HttpServletResponse response, AuthenticationResult value) {
            //no-op
        }
    }

    //The code bellow is taken out of http://stackoverflow.com/questions/23506471/spring-access-all-environment-properties-as-a-map-or-properties-object
    public static Map<String, Object> getPropertiesStartingWith(ConfigurableEnvironment aEnv, String aKeyPrefix) {
        Map<String, Object> result = new HashMap<String, Object>();

        Map<String, Object> map = getAllProperties(aEnv);

        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String key = entry.getKey();

            if (key.startsWith(aKeyPrefix)) {
                result.put(key, entry.getValue());
            }
        }

        return result;
    }

    protected static Map<String, Object> getAllProperties(ConfigurableEnvironment aEnv) {
        Map<String, Object> result = new HashMap<String, Object>();
        for (PropertySource propertySource : aEnv.getPropertySources()) {
            addAll(result, getAllProperties(propertySource));
        }
        return result;
    }

    protected static Map<String, Object> getAllProperties(PropertySource<?> aPropSource) {
        Map<String, Object> result = new HashMap<String, Object>();

        if (aPropSource instanceof CompositePropertySource) {
            CompositePropertySource cps = (CompositePropertySource) aPropSource;
            for (PropertySource<?> propertySource : cps.getPropertySources()) {
                addAll(result, getAllProperties(propertySource));
            }
            return result;
        }

        if (aPropSource instanceof EnumerablePropertySource<?>) {
            EnumerablePropertySource<?> ps = (EnumerablePropertySource<?>) aPropSource;
            for (String propertyName : ps.getPropertyNames()) {
                result.put(propertyName, ps.getProperty(propertyName));
            }

            return result;
        }

        return result;
    }

    private static void addAll(Map<String, Object> aBase, Map<String, Object> aToBeAdded) {
        for (Map.Entry<String, Object> entry : aToBeAdded.entrySet()) {
            if (aBase.containsKey(entry.getKey())) {
                continue;
            }

            aBase.put(entry.getKey(), entry.getValue());
        }
    }
}

