/*
 * Copyright 2016 Stormpath, Inc.
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
import com.stormpath.sdk.impl.provider.DefaultGithubProvider;
import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.provider.ProviderAccountRequest;
import com.stormpath.sdk.provider.Providers;
import com.stormpath.sdk.servlet.application.ApplicationResolver;
import com.stormpath.sdk.servlet.http.MediaType;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.stormpath.sdk.servlet.mvc.JacksonFieldValueResolver.MARSHALLED_OBJECT;

// Refactor of Provider requests for
// https://github.com/stormpath/stormpath-sdk-java/issues/915
// and to provide uniform responses across all integrations for
// conformance to stormpath-framework-spec as enforced by
// stormpath-framework-tck
/**
 * @since 1.0.3
 */
public class DefaultAccountProviderRequestHandler implements AccountProviderRequestHandler {

    private static final Logger log = LoggerFactory.getLogger(DefaultAccountProviderRequestHandler.class);
    private static final String GITHUB_ACCESS_TOKEN_URL = "https://github.com/login/oauth/access_token";
    private static final String GITHUB_ACCESS_TOKEN_FIELD = "access_token";

    @Override
    @SuppressWarnings("unchecked")
    public ProviderAccountRequest getAccountProviderRequest(HttpServletRequest request) {
        if (request.getParameterMap().size() == 0 && request.getContentLength() > 0) {
            Map<String, Object> map = (Map<String, Object>) request.getAttribute(MARSHALLED_OBJECT);

            return getAccountProviderRequest(request, map);
        }

        log.debug("Provider data not found in request.");
        return null;
    }

    @Override
    @SuppressWarnings("unchecked")
    public ProviderAccountRequest getAccountProviderRequest(HttpServletRequest request, Map<String, Object> props) {
        ProviderAccountRequest accountRequest = null;

        Map<String, String> providerData = (props != null) ? (Map<String, String>) props.get("providerData") : null;
        if (providerData != null) {
            String providerId = providerData.get("providerId");
            switch (providerId) {
                case "facebook": {
                    String accessToken = providerData.get("accessToken");
                    accountRequest = Providers.FACEBOOK.account().setAccessToken(accessToken).build();
                    break;
                }
                case "github": {
                    String code = providerData.get("code");
                    accountRequest = Providers.GITHUB.account().setAccessToken(exchangeGithubCodeForAccessToken(code, request)).build();
                    break;
                }
                case "google": {
                    String code = providerData.get("code");
                    accountRequest = Providers.GOOGLE.account().setCode(code).build();
                    break;
                }
                case "linkedin": {
                    String code = providerData.get("code");
                    accountRequest = Providers.LINKEDIN.account().setCode(code).build();
                    break;
                }
                default: {
                    log.error("No provider configured for " + providerId);
                }
            }
        }
        return accountRequest;
    }

    /**
     * This method is for exchanging a code for an access token with GitHub.
     * Needed by LoginController and GithubCallbackController.
     * @param code The code from GitHub
     * @param request The current request
     * @return an access token
     */
    @Override
    public String exchangeGithubCodeForAccessToken(String code, HttpServletRequest request) {
        final DefaultGithubProvider[] githubProvider = new DefaultGithubProvider[1];

        Application application = ApplicationResolver.INSTANCE.getApplication(request);
        for (ApplicationAccountStoreMapping mapping : application.getAccountStoreMappings()) {
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
