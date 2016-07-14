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
package com.stormpath.sdk.servlet.mvc.provider;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stormpath.sdk.application.ApplicationAccountStoreMapping;
import com.stormpath.sdk.authc.AuthenticationResult;
import com.stormpath.sdk.directory.AccountStore;
import com.stormpath.sdk.directory.AccountStoreVisitor;
import com.stormpath.sdk.directory.Directory;
import com.stormpath.sdk.group.Group;
import com.stormpath.sdk.impl.provider.DefaultGithubProvider;
import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.organization.Organization;
import com.stormpath.sdk.provider.ProviderAccountRequest;
import com.stormpath.sdk.provider.Providers;
import com.stormpath.sdk.servlet.event.RequestEvent;
import com.stormpath.sdk.servlet.event.impl.Publisher;
import com.stormpath.sdk.servlet.http.Saver;
import com.stormpath.sdk.servlet.mvc.AbstractSocialCallbackController;
import com.stormpath.sdk.servlet.util.ServletUtils;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @since 1.0.0
 */
public class GithubCallbackController extends AbstractSocialCallbackController {

    private static final Logger log = LoggerFactory.getLogger(GithubCallbackController.class);

    private static final String GITHUB_ACCES_TOKEN_URL = "https://github.com/login/oauth/access_token";
    private static final String ACCESS_TOKEN_FIELD = "access_token";

    private AccountStoreModelFactory accountStoreModelFactory;

    public GithubCallbackController(String loginNextUri, Saver<AuthenticationResult> authenticationResultSaver, Publisher<RequestEvent> eventPublisher) {
        super(loginNextUri, authenticationResultSaver, eventPublisher);

        accountStoreModelFactory = new DefaultAccountStoreModelFactory();

        Assert.notNull(accountStoreModelFactory, "accountStoreModelFactory cannot be null.");
    }

    @Override
    protected ProviderAccountRequest getAccountProviderRequest(HttpServletRequest request) {
        String code = ServletUtils.getCleanParam(request, "code");
        return Providers.GITHUB.account().setAccessToken(exchangeCodeForAccessToken(code, request)).build();
    }

    private String exchangeCodeForAccessToken(String code, HttpServletRequest request) {
        final DefaultGithubProvider[] githubProvider = new DefaultGithubProvider[1];

        for (ApplicationAccountStoreMapping mapping : getApplication(request).getAccountStoreMappings()) {
            AccountStore accountStore = mapping.getAccountStore();

            AccountStoreVisitor accountStoreVisitor = new AccountStoreVisitor() {
                @Override
                public void visit(Group group) {
                }

                @Override
                public void visit(Directory directory) {
                    if ("github".equals(directory.getProvider().getProviderId())) {
                        githubProvider[0] = (DefaultGithubProvider) directory.getProvider();
                    }
                }

                @Override
                public void visit(Organization organization) {

                }
            };
            accountStore.accept(accountStoreVisitor);
        }

        Assert.notNull(githubProvider[0], "githubProvider cannot be null.");

        HttpClient client = HttpClientBuilder.create().build();

        try {
            HttpPost httpPost = new HttpPost(GITHUB_ACCES_TOKEN_URL);
            List<NameValuePair> nvps = new ArrayList<NameValuePair>();
            nvps.add(new BasicNameValuePair("code", code));
            nvps.add(new BasicNameValuePair("client_id", githubProvider[0].getClientId()));
            nvps.add(new BasicNameValuePair("client_secret", githubProvider[0].getClientSecret()));

            httpPost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
            httpPost.addHeader("Accept", "application/json");

            HttpResponse response = client.execute(httpPost);
            ObjectMapper objectMapper = new ObjectMapper();

            //noinspection unchecked
            Map<String, String> result = objectMapper.readValue(response.getEntity().getContent(), Map.class);
            return result.get(ACCESS_TOKEN_FIELD);
        } catch (Exception e) {
            log.error("Couldn't exchange GitHub oAuth code for an access token", e);
            throw new RuntimeException(e);
        }
    }
}
