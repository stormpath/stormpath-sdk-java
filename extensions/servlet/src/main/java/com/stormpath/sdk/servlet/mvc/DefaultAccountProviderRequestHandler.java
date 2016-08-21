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
import com.stormpath.sdk.lang.Strings;
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
        Map<String, String> providerData = (props != null) ? (Map<String, String>) props.get("providerData") : null;
        if (providerData != null) {
            String providerId = providerData.get("providerId");
            String codeOrToken = (Strings.hasText(providerData.get("accessToken"))) ?
                providerData.get("accessToken") : providerData.get("code");

            return getAccountProviderRequest(request, providerId, codeOrToken);
        }

        log.debug("Provider data not found in request.");
        return null;
    }

    @Override
    public ProviderAccountRequest getAccountProviderRequest(HttpServletRequest request, String providerId, String codeOrToken) {
        if (Strings.hasText(providerId) && Strings.hasText(codeOrToken)) {
            switch (providerId) {
                case "facebook": {
                    return Providers.FACEBOOK
                        .account().setAccessToken(codeOrToken).build();
                }
                case "github": {
                    return Providers.GITHUB
                        .account().setAccessToken(exchangeGithubCodeForAccessToken(codeOrToken, request)).build();
                }
                case "google": {
                    return Providers.GOOGLE
                        .account().setCode(codeOrToken).build();
                }
                case "linkedin": {
                    return Providers.LINKEDIN
                        .account().setCode(codeOrToken).build();
                }
                default: {
                    log.error("No provider configured for " + providerId);
                    return null;
                }
            }
        }
        log.debug("providerId and/or token missing.");
        return null;
    }

    /**
     * This method is for exchanging a code for an access token with GitHub.
     *
     * @since 1.0.3
     */
    private String exchangeGithubCodeForAccessToken(String code, HttpServletRequest request) {
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
