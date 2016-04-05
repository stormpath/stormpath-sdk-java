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
import com.stormpath.sdk.client.Client;
import com.stormpath.sdk.idsite.IdSiteResultListener;
import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.lang.Collections;
import com.stormpath.sdk.lang.Strings;
import com.stormpath.sdk.saml.SamlResultListener;
import com.stormpath.sdk.servlet.authz.RequestAuthorizer;
import com.stormpath.sdk.servlet.config.CookieConfig;
import com.stormpath.sdk.servlet.csrf.CsrfTokenManager;
import com.stormpath.sdk.servlet.csrf.DefaultCsrfTokenManager;
import com.stormpath.sdk.servlet.csrf.DisabledCsrfTokenManager;
import com.stormpath.sdk.servlet.event.RequestEvent;
import com.stormpath.sdk.servlet.event.RequestEventListener;
import com.stormpath.sdk.servlet.event.RequestEventListenerAdapter;
import com.stormpath.sdk.servlet.event.TokenRevocationRequestEventListener;
import com.stormpath.sdk.servlet.event.impl.Publisher;
import com.stormpath.sdk.servlet.event.impl.RequestEventPublisher;
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
import com.stormpath.sdk.servlet.filter.oauth.DefaultRefreshTokenAuthenticationRequestFactory;
import com.stormpath.sdk.servlet.filter.oauth.DefaultRefreshTokenResultFactory;
import com.stormpath.sdk.servlet.filter.oauth.OriginAccessTokenRequestAuthorizer;
import com.stormpath.sdk.servlet.filter.oauth.RefreshTokenAuthenticationRequestFactory;
import com.stormpath.sdk.servlet.filter.oauth.RefreshTokenResultFactory;
import com.stormpath.sdk.servlet.form.DefaultField;
import com.stormpath.sdk.servlet.form.Field;
import com.stormpath.sdk.servlet.http.Resolver;
import com.stormpath.sdk.servlet.http.Saver;
import com.stormpath.sdk.servlet.http.authc.AccountStoreResolver;
import com.stormpath.sdk.servlet.http.authc.AuthorizationHeaderAuthenticator;
import com.stormpath.sdk.servlet.http.authc.BasicAuthenticationScheme;
import com.stormpath.sdk.servlet.http.authc.BearerAuthenticationScheme;
import com.stormpath.sdk.servlet.http.authc.DisabledAccountStoreResolver;
import com.stormpath.sdk.servlet.http.authc.HeaderAuthenticator;
import com.stormpath.sdk.servlet.http.authc.HttpAuthenticationScheme;
import com.stormpath.sdk.servlet.idsite.DefaultIdSiteOrganizationResolver;
import com.stormpath.sdk.servlet.idsite.IdSiteOrganizationContext;
import com.stormpath.sdk.servlet.mvc.AccessTokenController;
import com.stormpath.sdk.servlet.mvc.ChangePasswordController;
import com.stormpath.sdk.servlet.mvc.DefaultFormFieldsParser;
import com.stormpath.sdk.servlet.mvc.ErrorModelFactory;
import com.stormpath.sdk.servlet.mvc.ForgotPasswordController;
import com.stormpath.sdk.servlet.mvc.FormFieldParser;
import com.stormpath.sdk.servlet.mvc.IdSiteController;
import com.stormpath.sdk.servlet.mvc.IdSiteLogoutController;
import com.stormpath.sdk.servlet.mvc.IdSiteResultController;
import com.stormpath.sdk.servlet.mvc.LoginController;
import com.stormpath.sdk.servlet.mvc.LogoutController;
import com.stormpath.sdk.servlet.mvc.RegisterController;
import com.stormpath.sdk.servlet.mvc.SamlController;
import com.stormpath.sdk.servlet.mvc.SamlLogoutController;
import com.stormpath.sdk.servlet.mvc.SamlResultController;
import com.stormpath.sdk.servlet.mvc.SendVerificationEmailController;
import com.stormpath.sdk.servlet.mvc.VerifyController;
import com.stormpath.sdk.servlet.oauth.AccessTokenValidationStrategy;
import com.stormpath.sdk.servlet.organization.DefaultOrganizationNameKeyResolver;
import com.stormpath.sdk.servlet.saml.DefaultSamlOrganizationResolver;
import com.stormpath.sdk.servlet.saml.SamlOrganizationContext;
import com.stormpath.sdk.servlet.util.IsLocalhostResolver;
import com.stormpath.sdk.servlet.util.RemoteAddrResolver;
import com.stormpath.sdk.servlet.util.SecureRequiredExceptForLocalhostResolver;
import com.stormpath.sdk.servlet.util.SubdomainResolver;
import com.stormpath.spring.context.CompositeMessageSource;
import com.stormpath.spring.mvc.SpringController;
import com.stormpath.spring.mvc.TemplateLayoutInterceptor;
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
import org.springframework.util.PathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.handler.SimpleUrlHandlerMapping;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.mvc.Controller;
import org.springframework.web.util.UrlPathHelper;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * @since 1.0.RC4
 */
@SuppressWarnings({ "SpringFacetCodeInspection", "SpringJavaAutowiredMembersInspection" })
public abstract class AbstractStormpathWebMvcConfiguration {

    private static final Logger log = LoggerFactory.getLogger(AbstractStormpathWebMvcConfiguration.class);

    protected static final String I18N_PROPERTIES_BASENAME = "com.stormpath.sdk.servlet.i18n";

    //corresponding value should be present in a message source:
    protected static final String I18N_TEST_KEY = "stormpath.web.login.title";

    // ================  Account cookie properties  ===================

    @Value("#{ @environment['stormpath.web.account.cookie.name'] ?: 'account' }")
    protected String accountCookieName;

    @Value("#{ @environment['stormpath.web.account.cookie.comment'] }")
    protected String accountCookieComment;

    @Value("#{ @environment['stormpath.web.account.cookie.domain'] }")
    protected String accountCookieDomain;

    @Value("#{ @environment['stormpath.web.account.cookie.maxAge'] ?: 86400 }") //1 day by default
    protected int accountCookieMaxAge;

    @Value("#{ @environment['stormpath.web.account.cookie.path'] }")
    protected String accountCookiePath;

    @Value("#{ @environment['stormpath.web.account.cookie.httpOnly'] ?: true }")
    protected boolean accountCookieHttpOnly;

    @Value("#{ @environment['stormpath.web.account.cookie.secure'] ?: true }")
    protected boolean accountCookieSecure;

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
    //lower numbers have higher precedence):
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

    @Value(
        "#{ @environment['stormpath.web.stormpathFilter.order'] ?: T(org.springframework.core.Ordered).HIGHEST_PRECEDENCE }")
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

    @Value("#{ @environment['stormpath.web.head.cssUris'] ?: 'https://fonts.googleapis.com/css?family=Open+Sans:300italic,300,400italic,400,600italic,600,700italic,700,800italic,800 https://netdna.bootstrapcdn.com/bootstrap/3.1.1/css/bootstrap.min.css /assets/css/stormpath.css' }")
    protected String headCssUris;

    @Value("#{ @environment['stormpath.web.head.extraCssUris'] }")
    protected String headExtraCssUris;

    // ================  Login Controller properties  ===================

    @Value("#{ @environment['stormpath.web.login.enabled'] ?: true }")
    protected boolean loginEnabled;

    @Value("#{ @environment['stormpath.web.login.uri'] ?: '/login' }")
    protected String loginUri;

    @Value("#{ @environment['stormpath.web.login.nextUri'] ?: '/' }")
    protected String loginNextUri;

    @Value("#{ @environment['stormpath.web.login.view'] ?: 'stormpath/login' }")
    protected String loginView;

    // ================  Forgot Password Controller properties  ===================

    @Value("#{ @environment['stormpath.web.forgot.enabled'] ?: true }")
    protected boolean forgotEnabled;

    @Value("#{ @environment['stormpath.web.forgot.uri'] ?: '/forgot' }")
    protected String forgotUri;

    @Value("#{ @environment['stormpath.web.forgot.nextUri'] ?: '/login?status=forgot' }")
    protected String forgotNextUri;

    @Value("#{ @environment['stormpath.web.forgot.view'] ?: 'stormpath/forgot' }")
    protected String forgotView;

    // ================  Register Controller properties  ===================

    @Value("#{ @environment['stormpath.web.register.enabled'] ?: true }")
    protected boolean registerEnabled;

    @Value("#{ @environment['stormpath.web.register.uri'] ?: '/register' }")
    protected String registerUri;

    @Value("#{ @environment['stormpath.web.register.nextUri'] ?: '/' }")
    protected String registerNextUri;

    @Value("#{ @environment['stormpath.web.register.view'] ?: 'stormpath/register' }")
    protected String registerView;

    @Value(
        "#{ @environment['stormpath.web.register.form.fields'] ?: 'givenName, surname, email(required), password(required,password), confirmPassword(required,password)' }")
    protected String registerFormFields;

    // ================  Verify Email Controller properties  ===================

    @Value("#{ @environment['stormpath.web.verify.enabled'] ?: true }")
    protected boolean verifyEnabled;

    @Value("#{ @environment['stormpath.web.verify.uri'] ?: '/verify' }")
    protected String verifyUri;

    @Value("#{ @environment['stormpath.web.verify.nextUri'] ?: '/login?status=verified' }")
    protected String verifyNextUri;

    @Value("#{ @environment['stormpath.web.verify.view'] ?: 'stormpath/verify' }")
    protected String verifyView;

    @Value("#{ @environment['stormpath.web.sendVerificationEmail.uri'] ?: '/sendVerificationEmail' }")
    protected String sendVerificationEmailUri;

    @Value("#{ @environment['stormpath.web.sendVerificationEmail.view'] ?: 'stormpath/sendVerificationEmail' }")
    protected String sendVerificationEmailView;

    // ================  Logout Controller properties  ===================

    @Value("#{ @environment['stormpath.web.logout.enabled'] ?: true }")
    protected boolean logoutEnabled;

    @Value("#{ @environment['stormpath.web.logout.uri'] ?: '/logout' }")
    protected String logoutUri;

    @Value("#{ @environment['stormpath.web.logout.nextUri'] ?: '/login?status=logout' }")
    protected String logoutNextUri;

    @Value("#{ @environment['stormpath.web.logout.invalidateHttpSession'] ?: true }")
    protected boolean logoutInvalidateHttpSession;

    // ================  Change Password Controller properties  ===================

    @Value("#{ @environment['stormpath.web.change.enabled'] ?: true }")
    protected boolean changePasswordEnabled;

    @Value("#{ @environment['stormpath.web.change.uri'] ?: '/change' }")
    protected String changePasswordUri;

    @Value("#{ @environment['stormpath.web.change.nextUri'] ?: '/login?status=changed' }")
    protected String changePasswordNextUri;

    @Value("#{ @environment['stormpath.web.change.view'] ?: 'stormpath/change' }")
    protected String changePasswordView;

    // ================  Access Token Controller properties  ===================

    @Value("#{ @environment['stormpath.web.accessToken.enabled'] ?: true }")
    protected boolean accessTokenEnabled;

    @Value("#{ @environment['stormpath.web.accessToken.uri'] ?: '/oauth/token' }")
    protected String accessTokenUri;

    @Value("#{ @environment['stormpath.web.accessToken.origin.authorizer.originUris'] }")
    protected String accessTokenAuthorizedOriginUris;

    @Value("#{ @environment['stormpath.web.accessToken.validationStrategy'] ?: 'stormpath'}")
    protected String accessTokenValidationStrategy;

    // ================  ID Site properties  ===================

    @Value("#{ @environment['stormpath.web.idSite.enabled'] ?: false }")
    protected boolean idSiteEnabled;

    @Value("#{ @environment['stormpath.web.idSite.login.uri'] }")
    protected String idSiteLoginUri; //null by default as it is assumed the id site root is the same as the login page (usually)

    @Value("#{ @environment['stormpath.web.idSite.register.uri'] ?: '/#/register' }")
    protected String idSiteRegisterUri;

    @Value("#{ @environment['stormpath.web.idSite.forgot.uri'] ?: '/#/forgot' }")
    protected String idSiteForgotUri;

    @Value("#{ @environment['stormpath.web.idSite.result.uri'] ?: '/idSiteResult' }")
    protected String idSiteResultUri;

    @Value("#{ @environment['stormpath.web.idSite.useSubdomain'] }")
    protected Boolean idSiteUseSubdomain;

    @Value("#{ @environment['stormpath.web.idSite.showOrganizationField'] }")
    protected Boolean idSiteShowOrganizationField;

    @Value("#{ @environment['stormpath.web.saml.enabled'] ?: false }")
    protected boolean samlEnabled;

    @Value("#{ @environment['stormpath.web.saml.result.uri'] ?: '/samlResult' }")
    protected String samlResultUri;

    @Value("#{ @environment['stormpath.web.application.domain'] }")
    protected String baseDomainName;

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

    public HandlerMapping stormpathHandlerMapping() throws Exception {

        Map<String, Controller> mappings = new LinkedHashMap<String, Controller>();

        if (loginEnabled) {
            mappings.put(loginUri, stormpathLoginController());
        }
        if (logoutEnabled) {
            mappings.put(logoutUri, stormpathLogoutController());
        }
        if (registerEnabled) {
            mappings.put(registerUri, stormpathRegisterController());
        }
        if (verifyEnabled) {
            mappings.put(verifyUri, stormpathVerifyController());
            mappings.put(sendVerificationEmailUri, stormpathSendVerificationEmailController());
        }
        if (forgotEnabled) {
            mappings.put(forgotUri, stormpathForgotPasswordController());
        }
        if (changePasswordEnabled) {
            mappings.put(changePasswordUri, stormpathChangePasswordController());
        }
        if (accessTokenEnabled) {
            mappings.put(accessTokenUri, stormpathAccessTokenController());
        }
        if (idSiteEnabled) {
            mappings.put(idSiteResultUri, stormpathIdSiteResultController());
        }
        if (samlEnabled) {
            mappings.put(samlResultUri, stormpathSamlResultController());
        }

        SimpleUrlHandlerMapping mapping = new SimpleUrlHandlerMapping();
        mapping.setOrder(handlerMappingOrder);
        mapping.setUrlMap(mappings);

        mapping.setInterceptors(new Object[]{ stormpathLocaleChangeInterceptor(), stormpathLayoutInterceptor() });

        if (pathMatcher != null) {
            mapping.setPathMatcher(pathMatcher);
        }

        if (urlPathHelper != null) {
            mapping.setUrlPathHelper(urlPathHelper);
        }

        return mapping;
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

    public AccountStoreResolver stormpathAccountStoreResolver() {
        return new DisabledAccountStoreResolver();
    }

    public UsernamePasswordRequestFactory stormpathUsernamePasswordRequestFactory() {
        return new DefaultUsernamePasswordRequestFactory(stormpathAccountStoreResolver());
    }

    public CookieConfig stormpathAccountCookieConfig() {

        return new CookieConfig() {
            @Override
            public String getName() {
                return accountCookieName;
            }

            @Override
            public String getComment() {
                return accountCookieComment;
            }

            @Override
            public String getDomain() {
                return accountCookieDomain;
            }

            @Override
            public int getMaxAge() {
                return accountCookieMaxAge;
            }

            @Override
            public String getPath() {
                return accountCookiePath;
            }

            @Override
            public boolean isSecure() {
                return accountCookieSecure;
            }

            @Override
            public boolean isHttpOnly() {
                return accountCookieHttpOnly;
            }
        };
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
                stormpathAccountCookieConfig(), stormpathSecureResolver(), stormpathAuthenticationJwtFactory()
            );
        }

        //otherwise, return a dummy saver:
        return DisabledAuthenticationResultSaver.INSTANCE;
    }

    public Saver<AuthenticationResult> stormpathSessionAuthenticationResultSaver() {

        if (sessionAuthenticationResultSaverEnabled) {
            String[] attributeNames = { Account.class.getName(), "account" };
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

    public AuthenticationJwtFactory stormpathAuthenticationJwtFactory() {
        return new DefaultAuthenticationJwtFactory(
            stormpathJwtSigningKeyResolver(), accountJwtSignatureAlgorithm, accountJwtTtl
        );
    }

    public JwtSigningKeyResolver stormpathJwtSigningKeyResolver() {
        return new DefaultJwtSigningKeyResolver();
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

    /** @since 1.0.RC8.3 */
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
        return new CookieAccountResolver(stormpathAccountCookieConfig(), stormpathJwtAccountResolver());
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
        controller.setAlreadyLoggedInUri(loginNextUri);
        controller.setIdSiteOrganizationResolver(stormpathIdSiteOrganizationResolver());
        controller.init();
        return createSpringController(controller);
    }

    /**
     * @since 1.0.RC8
     */
    protected Controller createSamlController(String samlUri) {
        SamlController controller = new SamlController();
        controller.setServerUriResolver(stormpathServerUriResolver());
        controller.setSamlUri(samlUri);
        controller.setCallbackUri(samlResultUri);
        controller.setAlreadyLoggedInUri(loginNextUri);
        controller.setSamlOrganizationResolver(stormpathSamlOrganizationResolver());
        controller.init();
        return createSpringController(controller);
    }

    public Controller stormpathLoginController() {

        if (idSiteEnabled) {
            return createIdSiteController(idSiteLoginUri);
        }

        if (samlEnabled) {
            return createSamlController("/");
        }

        //otherwise standard login controller:
        LoginController controller = new LoginController();
        controller.setUri(loginUri);
        controller.setView(loginView);
        controller.setNextUri(loginNextUri);
        controller.setForgotLoginUri(forgotUri);
        controller.setVerifyEnabled(verifyEnabled);
        controller.setVerifyUri(verifyUri);
        controller.setRegisterUri(registerUri);
        controller.setLogoutUri(logoutUri);
        controller.setAuthenticationResultSaver(stormpathAuthenticationResultSaver());
        controller.setCsrfTokenManager(stormpathCsrfTokenManager());

        if (loginErrorModelFactory != null) {
            controller.setErrorModelFactory(loginErrorModelFactory);
        }

        controller.init();

        return createSpringController(controller);
    }

    public Controller stormpathForgotPasswordController() {

        if (idSiteEnabled) {
            return createIdSiteController(idSiteForgotUri);
        }

        ForgotPasswordController controller = new ForgotPasswordController();
        controller.setUri(forgotUri);
        controller.setView(forgotView);
        controller.setCsrfTokenManager(stormpathCsrfTokenManager());
        controller.setAccountStoreResolver(stormpathAccountStoreResolver());
        controller.setNextUri(forgotNextUri);
        controller.setLoginUri(loginUri);
        controller.init();

        return createSpringController(controller);
    }

    private Controller createSpringController(com.stormpath.sdk.servlet.mvc.Controller controller) {
        SpringController springController = new SpringController(controller);
        if (urlPathHelper != null) {
            springController.setUrlPathHelper(urlPathHelper);
        }
        return springController;
    }

    public List<Field> stormpathRegisterFormFields() {
        return stormpathRegisterFormFieldParser().parse(registerFormFields);
    }

    public FormFieldParser stormpathRegisterFormFieldParser() {
        return new DefaultFormFieldsParser("stormpath.web.register.form.fields");
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
                log.debug(
                    "Stormpath i18n properties have not been specified during message source configuration.  " +
                    "Adding these property values as a fallback. Exception for reference (this and the " +
                    "stack trace can safely be ignored): " + e.getMessage(), e
                );
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

    public Controller stormpathRegisterController() {

        if (idSiteEnabled) {
            return createIdSiteController(idSiteRegisterUri);
        }

        //otherwise standard registration:
        RegisterController controller = new RegisterController();
        controller.setCsrfTokenManager(stormpathCsrfTokenManager());
        controller.setClient(client);
        controller.setEventPublisher(stormpathRequestEventPublisher());
        controller.setFormFields(toDefaultFields(stormpathRegisterFormFields()));
        controller.setLocaleResolver(stormpathLocaleResolver());
        controller.setMessageSource(stormpathMessageSource());
        controller.setAuthenticationResultSaver(stormpathAuthenticationResultSaver());
        controller.setUri(registerUri);
        controller.setView(registerView);
        controller.setNextUri(registerNextUri);
        controller.setLoginUri(loginUri);
        controller.setVerifyViewName(verifyView);
        controller.init();

        return createSpringController(controller);
    }

    public Controller stormpathVerifyController() {

        if (idSiteEnabled) {
            return createIdSiteController(null);
        }

        VerifyController controller = new VerifyController();
        controller.setNextUri(verifyNextUri);
        controller.setLogoutUri(logoutUri);
        controller.setSendVerificationEmailUri(sendVerificationEmailUri);
        controller.setClient(client);
        controller.setEventPublisher(stormpathRequestEventPublisher());
        controller.init();

        return createSpringController(controller);
    }

    public Controller stormpathSendVerificationEmailController() {
        if (idSiteEnabled) {
            return createIdSiteController(null);
        }

        SendVerificationEmailController controller = new SendVerificationEmailController();
        controller.setUri(sendVerificationEmailUri);
        controller.setView(sendVerificationEmailView);
        controller.setCsrfTokenManager(stormpathCsrfTokenManager());
        controller.setAccountStoreResolver(stormpathAccountStoreResolver());
        controller.setNextUri(verifyView);
        controller.setLoginUri(loginUri);
        controller.init();

        return createSpringController(controller);
    }

    public Controller stormpathChangePasswordController() {

        if (idSiteEnabled) {
            return createIdSiteController(null);
        }

        ChangePasswordController controller = new ChangePasswordController();
        controller.setView(changePasswordView);
        controller.setUri(changePasswordUri);
        controller.setCsrfTokenManager(stormpathCsrfTokenManager());
        controller.setNextUri(changePasswordNextUri);
        controller.setLoginUri(loginUri);
        controller.setForgotPasswordUri(forgotUri);
        controller.setLocaleResolver(stormpathLocaleResolver());
        controller.setMessageSource(stormpathMessageSource());
        controller.init();

        return createSpringController(controller);
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
        c.init();

        return createSpringController(c);
    }

    public Controller stormpathIdSiteResultController() {
        IdSiteResultController controller = new IdSiteResultController();
        controller.setLoginNextUri(loginNextUri);
        controller.setRegisterNextUri(registerNextUri);
        controller.setLogoutController(stormpathMvcLogoutController());
        controller.setAuthenticationResultSaver(stormpathAuthenticationResultSaver());
        controller.setEventPublisher(stormpathRequestEventPublisher());
        if (springSecurityIdSiteResultListener != null) {
            controller.addIdSiteResultListener(springSecurityIdSiteResultListener);
        }
        controller.init();
        return createSpringController(controller);
    }

    public Controller stormpathSamlResultController() {
        SamlResultController controller = new SamlResultController();
        controller.setLoginNextUri(loginNextUri);
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

    /** @since 1.0.RC8.3 */
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
            stormpathServerUriResolver(), stormpathLocalhostResolver(), stormpathAccessTokenAuthorizedOriginUris()
        );
    }

    public ServerUriResolver stormpathServerUriResolver() {
        return new DefaultServerUriResolver();
    }

    public com.stormpath.sdk.servlet.mvc.Controller stormpathMvcLogoutController() {

        LogoutController controller = new LogoutController();

        if (idSiteEnabled) {
            IdSiteLogoutController c = new IdSiteLogoutController();
            c.setServerUriResolver(stormpathServerUriResolver());
            c.setIdSiteResultUri(idSiteResultUri);
            c.setIdSiteOrganizationResolver(stormpathIdSiteOrganizationResolver());
            controller = c;
        }

        if (samlEnabled) {
            SamlLogoutController c = new SamlLogoutController();
            c.setServerUriResolver(stormpathServerUriResolver());
            c.setSamlResultUri(samlResultUri);
            controller = c;
        }

        controller.setNextUri(logoutNextUri);
        controller.setInvalidateHttpSession(logoutInvalidateHttpSession);
        controller.init();

        return controller;
    }

    public Controller stormpathLogoutController() {
        return createSpringController(stormpathMvcLogoutController());
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

}

