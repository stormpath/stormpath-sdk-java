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
package com.stormpath.sdk.servlet.mvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stormpath.sdk.application.Application;
import com.stormpath.sdk.application.ApplicationAccountStoreMapping;
import com.stormpath.sdk.directory.AccountStore;
import com.stormpath.sdk.directory.AccountStoreVisitor;
import com.stormpath.sdk.directory.AccountStoreVisitorAdapter;
import com.stormpath.sdk.directory.Directory;
import com.stormpath.sdk.http.HttpMethod;
import com.stormpath.sdk.impl.provider.DefaultGithubProvider;
import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.lang.Strings;
import com.stormpath.sdk.servlet.account.AccountResolver;
import com.stormpath.sdk.servlet.application.ApplicationResolver;
import com.stormpath.sdk.servlet.event.RequestEvent;
import com.stormpath.sdk.servlet.event.impl.Publisher;
import com.stormpath.sdk.servlet.filter.ContentNegotiationResolver;
import com.stormpath.sdk.servlet.http.MediaType;
import com.stormpath.sdk.servlet.http.Resolver;
import com.stormpath.sdk.servlet.http.UnresolvedMediaTypeException;
import com.stormpath.sdk.servlet.i18n.MessageSource;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * @since 1.0.RC4
 */
public abstract class AbstractController implements Controller {

    private static final String GITHUB_ACCESS_TOKEN_URL = "https://github.com/login/oauth/access_token";
    private static final String GITHUB_ACCESS_TOKEN_FIELD = "access_token";
    private static final Logger log = LoggerFactory.getLogger(AbstractController.class);

    private static final HttpServlet DEFAULT_HANDLER = new HttpServlet() {
    };

    protected String uri;
    protected String nextUri;
    protected String view;
    protected MessageSource messageSource;
    protected Publisher<RequestEvent> eventPublisher;
    protected List<MediaType> produces;
    protected ApplicationResolver applicationResolver;

    private String controllerKey;
    private Resolver<Locale> localeResolver;
    private AccountResolver accountResolver = AccountResolver.INSTANCE;
    private ContentNegotiationResolver contentNegotiationResolver = ContentNegotiationResolver.INSTANCE;

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getNextUri() {
        return nextUri;
    }

    public void setNextUri(String nextUri) {
        this.nextUri = nextUri;
    }

    public String getView() {
        return view;
    }

    public void setView(String view) {
        this.view = view;
    }

    public MessageSource getMessageSource() {
        return messageSource;
    }

    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public Publisher<RequestEvent> getEventPublisher() {
        return eventPublisher;
    }

    public void setEventPublisher(Publisher<RequestEvent> eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    public List<MediaType> getProduces() {
        return produces;
    }

    public void setProduces(List<MediaType> produces) {
        this.produces = produces;
    }

    public void setControllerKey(String controllerKey) {
        this.controllerKey = controllerKey;
    }

    public String getControllerKey() {
        return controllerKey;
    }

    public Resolver<Locale> getLocaleResolver() {
        return localeResolver;
    }

    public void setLocaleResolver(Resolver<Locale> localeResolver) {
        this.localeResolver = localeResolver;
    }

    public AccountResolver getAccountResolver() {
        return accountResolver;
    }

    public void setAccountResolver(AccountResolver accountResolver) {
        this.accountResolver = accountResolver;
    }

    public ContentNegotiationResolver getContentNegotiationResolver() {
        return contentNegotiationResolver;
    }

    public void setContentNegotiationResolver(ContentNegotiationResolver contentNegotiationResolver) {
        this.contentNegotiationResolver = contentNegotiationResolver;
    }

    public void setApplicationResolver(ApplicationResolver applicationResolver) {
        this.applicationResolver = applicationResolver;
    }

    public void init() throws Exception {
        Assert.hasText(this.uri, "uri cannot be null or empty.");
        Assert.hasText(this.nextUri, "nextUri property cannot be null or empty.");
        Assert.hasText(this.view, "view cannot be null or empty.");
        Assert.notNull(this.messageSource, "messageSource cannot be null.");
        Assert.notNull(this.eventPublisher, "eventPublisher cannot be null or empty.");
        Assert.notEmpty(this.produces, "produces MediaType list cannot be null or empty.");
        Assert.hasText(this.controllerKey, "controllerKey cannot be null or empty.");
        Assert.notNull(this.localeResolver, "localeResolver cannot be null.");
        Assert.notNull(this.accountResolver, "accountResolver cannot be null.");
        Assert.notNull(this.contentNegotiationResolver, "contentNegotiationResolver cannot be null or empty.");
    }

    protected Map<String, Object> newModel() {
        return new HashMap<String, Object>();
    }

    /**
     * Method returns true if the controller doesn't allow requests if there is a user already authenticated,
     * this is true for most controllers.
     *
     * @return True if controller doesn't allow request when user is authenticated, false otherwise
     */
    public abstract boolean isNotAllowedIfAuthenticated();

    protected String i18n(HttpServletRequest request, String key) {
        Locale locale = localeResolver.get(request, null);
        return messageSource.getMessage(key, locale);
    }

    protected String i18n(HttpServletRequest request, String key, String defaultMessage) {
        Locale locale = localeResolver.get(request, null);
        return messageSource.getMessage(key, defaultMessage, locale);
    }

    protected String i18n(HttpServletRequest request, String key, Object... args) {
        Locale locale = localeResolver.get(request, null);
        return messageSource.getMessage(key, locale, args);
    }

    protected boolean isJsonPreferred(HttpServletRequest request, HttpServletResponse response) {
        try {
            return MediaType.APPLICATION_JSON.equals(contentNegotiationResolver.getContentType(request, response, produces));
        } catch (UnresolvedMediaTypeException e) {
            log.debug("isJsonPreferred: Couldn't resolve content type: {}", e.getMessage());
            return false;
        }
    }

    protected boolean isHtmlPreferred(HttpServletRequest request, HttpServletResponse response) {
        try {
            return MediaType.TEXT_HTML.equals(contentNegotiationResolver.getContentType(request, response, produces));
        } catch (UnresolvedMediaTypeException e) {
            log.debug("isHtmlPreferred: Couldn't resolve content type: {}", e.getMessage());
            return false;
        }
    }

    @Override
    public ViewModel handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {

        String method = request.getMethod();

        boolean hasAccount = accountResolver.hasAccount(request);

        if (HttpMethod.GET.name().equalsIgnoreCase(method)) {
            if (hasAccount && isNotAllowedIfAuthenticated()) {
                return new DefaultViewModel(nextUri).setRedirect(true);
            }
            return doGet(request, response);
        } else if (HttpMethod.POST.name().equalsIgnoreCase(method)) {
            if (isNotAllowedIfAuthenticated() && hasAccount) {
                response.sendError(403);
                return null;
            }
            return doPost(request, response);
        } else {
            return service(request, response);
        }
    }

    protected ViewModel service(HttpServletRequest request, HttpServletResponse response) throws Exception {
        DEFAULT_HANDLER.service(request, response);
        return null;
    }

    protected ViewModel doGet(HttpServletRequest request, HttpServletResponse response) throws Exception {
        return service(request, response);
    }

    protected ViewModel doPost(HttpServletRequest request, HttpServletResponse response) throws Exception {
        return service(request, response);
    }

    protected String getNextUri(HttpServletRequest request) {
        String nextQueryParam = request.getParameter(NEXT_QUERY_PARAM);

        if (Strings.hasText(nextQueryParam)) {
            return nextQueryParam;
        }

        return nextUri;
    }

    protected void publishRequestEvent(RequestEvent e) throws ServletException {
        if (e != null) {
            try {
                eventPublisher.publish(e);
            } catch (Exception ex) {
                String msg = "Unable to publish registered account request event: " + ex.getMessage();
                throw new ServletException(msg, ex);
            }
        }
    }

    protected Application getApplication(HttpServletRequest request) {
        return applicationResolver.getApplication(request);
    }

    /**
     * This method is for exchanging a code for an access token with GitHub. Needed by LoginController
     * and GithubCallbackController, hence the reason it's in a common parent class.
     * @param code The code from GitHub
     * @param request The current request
     * @return an access token
     */
    protected String exchangeGithubCodeForAccessToken(String code, HttpServletRequest request) {
        final DefaultGithubProvider[] githubProvider = new DefaultGithubProvider[1];

        for (ApplicationAccountStoreMapping mapping : getApplication(request).getAccountStoreMappings()) {
            AccountStore accountStore = mapping.getAccountStore();

            AccountStoreVisitor accountStoreVisitor = new AccountStoreVisitorAdapter() {
                @Override
                public void visit(Directory directory) {
                    if ("github".equals(directory.getProvider().getProviderId())) {
                        githubProvider[0] = (DefaultGithubProvider) directory.getProvider();
                    }
                }
            };
            accountStore.accept(accountStoreVisitor);
        }

        Assert.notNull(githubProvider[0], "githubProvider cannot be null.");

        HttpClient client = HttpClientBuilder.create().build();

        try {
            HttpPost httpPost = new HttpPost(GITHUB_ACCESS_TOKEN_URL);
            List<NameValuePair> nvps = new ArrayList<NameValuePair>();
            nvps.add(new BasicNameValuePair("code", code));
            nvps.add(new BasicNameValuePair("client_id", githubProvider[0].getClientId()));
            nvps.add(new BasicNameValuePair("client_secret", githubProvider[0].getClientSecret()));

            httpPost.setEntity(new UrlEncodedFormEntity(nvps, StandardCharsets.UTF_8.displayName()));
            httpPost.addHeader("Accept", MediaType.APPLICATION_JSON_VALUE);

            HttpResponse response = client.execute(httpPost);
            ObjectMapper objectMapper = new ObjectMapper();

            //noinspection unchecked
            Map<String, String> result = objectMapper.readValue(response.getEntity().getContent(), Map.class);
            return result.get(GITHUB_ACCESS_TOKEN_FIELD);
        } catch (Exception e) {
            log.error("Couldn't exchange GitHub oAuth code for an access token", e);
            throw new RuntimeException(e);
        }
    }
}
