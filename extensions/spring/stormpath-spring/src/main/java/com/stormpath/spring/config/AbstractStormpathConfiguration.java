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

import com.stormpath.sdk.api.ApiKey;
import com.stormpath.sdk.api.ApiKeyBuilder;
import com.stormpath.sdk.api.ApiKeys;
import com.stormpath.sdk.application.Application;
import com.stormpath.sdk.mail.EmailService;
import com.stormpath.sdk.mail.EmailServiceBuilder;
import com.stormpath.sdk.mail.config.DefaultEmailServiceConfig;
import com.stormpath.sdk.mail.config.EmailServiceConfig;
import com.stormpath.sdk.okta.ApplicationCredentials;
import com.stormpath.sdk.cache.Caches;
import com.stormpath.sdk.client.AuthenticationScheme;
import com.stormpath.sdk.client.Client;
import com.stormpath.sdk.client.ClientBuilder;
import com.stormpath.sdk.client.Clients;
import com.stormpath.sdk.client.DefaultPairedApiKey;
import com.stormpath.sdk.client.PairedApiKey;
import com.stormpath.sdk.client.Proxy;
import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.lang.Strings;
import com.stormpath.sdk.okta.OktaApplicationConfigResource;
import com.stormpath.sdk.okta.OIDCWellKnownResource;
import com.stormpath.spring.cache.SpringCacheManager;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @since 1.0.RC4
 */
public abstract class AbstractStormpathConfiguration {

    private static final String APP_HREF_ERROR =
        "A 'stormpath.application.href' property value must be configured if you have more than one application " +
            "registered in Stormpath.";

    @Autowired(required = false)
    protected CacheManager cacheManager;

    @Value("#{ @environment['stormpath.client.baseUrl'] }")
    protected String baseUrl;

    @Value("#{ @environment['stormpath.client.apiKey.id'] }")
    protected String apiKeyId;

    @Value("#{ @environment['stormpath.client.apiKey.secret'] }")
    protected String apiKeySecret;

    @Value("#{ @environment['stormpath.client.apiKey.file'] }")
    protected Resource apiKeyFile;

    @Value("#{ @environment['stormpath.client.apiKey.fileIdPropertyName'] }")
    protected String apiKeyFileIdPropertyName;

    @Value("#{ @environment['stormpath.client.apiKey.fileSecretPropertyName'] }")
    protected String apiKeyFileSecretPropertyName;

    @Value("#{ @environment['stormpath.application.href'] }")
    protected String applicationHref;

    @Value("#{ @environment['stormpath.application.name'] }")
    protected String applicationName;

    @Value("#{ @environment['stormpath.client.cacheManager.enabled'] ?: true }")
    protected boolean cachingEnabled;

    @Value("#{ @environment['stormpath.client.proxy.host'] }")
    protected String proxyHost;

    @Value("#{ @environment['stormpath.client.proxy.port'] ?: 80 }")
    protected int proxyPort;

    @Value("#{ @environment['stormpath.client.proxy.username'] }")
    protected String proxyUsername;

    @Value("#{ @environment['stormpath.client.proxy.password'] }")
    protected String proxyPassword;

    @Value("#{ @environment['stormpath.client.connectionTimeout'] ?: 0 }")
    protected int connectionTimeout;

    @Value("#{ @environment['stormpath.client.authenticationScheme'] }")
    protected AuthenticationScheme authenticationScheme;

    @Value("#{ @environment['okta.enabled'] ?: true }")
    protected boolean oktaEnabled;

    @Value("#{ @environment['okta.api.token'] }")
    protected String oktaApiToken;

    @Value("#{ @environment['okta.application.id'] }")
    protected String oktaApplicationId;

    @Value("#{ @environment['okta.authorizationServer.id'] }")
    protected String oktaAuthorizationServerIdConfig;

    @Value("#{ @environment['stormpath.registration.workflow.enabled'] ?: false }")
    protected boolean registrationWorkflowEnabled;

    @Value("#{ @environment['stormpath.email.port'] ?: 25 }")
    protected int emailPort;

    @Value("#{ @environment['stormpath.email.fromName'] ?: 'Application' }")
    protected String emailFromName;

    @Value("#{ @environment['stormpath.email.foo'] ?: 'admin@local' }")
    protected String emailFromAddress;

    @Value("#{ @environment['stormpath.email.applicationBaseUrl'] ?: 'http://localhost:8080' }")
    protected String emailApplicationBaseUrl;

    @Value("#{ @environment['stormpath.email.hostname'] ?: 'localhost' }")
    protected String emailHostname;

    @Value("#{ @environment['stormpath.email.sselEnabled'] ?: false }")
    protected boolean emailSSLEnabled;

    @Value("#{ @environment['stormpath.email.sslCheckServerIdentityEnabled'] ?: false }")
    protected boolean emailSSLCheckServerIdentityEnabled;

    @Value("#{ @environment['stormpath.email.tlsEnabled'] ?: false }")
    protected boolean emailTLSEnabled;

    @Value("#{ @environment['stormpath.email.username']}")
    protected String emailUsername;

    @Value("#{ @environment['stormpath.email.password']}")
    protected String emailPassword;

    @Value("#{ @environment['stormpath.email.tokenExpirationHours'] ?: 2 }")
    protected int emailExpirationHours;

    @Value("#{ @environment['stormpath.email.verifyEmailTemplate'] ?: '/com/stormpath/sdk/mail/templates/verifyEmail.json' }")
    protected String verifyEmailTemplate;

    @Value("#{ @environment['stormpath.email.forgotPasswordTemplate'] ?: '/com/stormpath/sdk/mail/templates/forgotPassword.json' }")
    protected String forgotPasswordEmailTemplate;

    @Value("#{ @environment['stormpath.application.allowApiClientCredentials'] ?: false }")
    protected boolean allowApiSecret;

    @Value("#{ @environment['stormpath.application.apiSecretQueryTemplate'] ?: '?search=profile.stormpathApiKey_1 sw \"{0}\" or profile.stormpathApiKey_2 sw \"{0}\"' }")
    protected String apiSecretQueryTemplate;

    @Value("#{ @environment['okta.password.policy.name'] ?: 'Default Policy' }")
    protected String oktaPasswordPolicyName;


    public ApiKey stormpathClientApiKey() {

        if (oktaEnabled) {
            Assert.hasText(oktaApiToken, "When okta.enabled is true, the okta.api.token " +
                "property must be specified with a valid Okta API Token value.");

            ApiKey primary = ApiKeys.builder()
                .setId(baseUrl) //not really necessary, but what the heck, why not
                .setSecret(oktaApiToken)
                .build();

            return new DefaultPairedApiKey(primary);
        }

        ApiKeyBuilder builder = ApiKeys.builder();

        if (Strings.hasText(apiKeyFileIdPropertyName)) {
            builder.setIdPropertyName(apiKeyFileIdPropertyName);
        }
        if (Strings.hasText(apiKeyFileSecretPropertyName)) {
            builder.setSecretPropertyName(apiKeyFileSecretPropertyName);
        }
        if (apiKeyFile != null) {
            try {
                builder.setInputStream(apiKeyFile.getInputStream());
            } catch (IOException e) {
                String msg = "Unable to acquire specified resource [" + apiKeyFile + "]: " + e.getMessage();
                throw new BeanCreationException(msg, e);
            }
        }

        if (Strings.hasText(apiKeyId)) {
            builder.setId(apiKeyId);
        }
        if (Strings.hasText(apiKeySecret)) {
            builder.setSecret(apiKeySecret);
        }

        return builder.build();
    }

    @Autowired
    public void oktaOidcClientApiKey(@Qualifier("stormpathClientApiKey") ApiKey stormpathClientApiKey) {

        if (oktaEnabled) {
            Assert.hasText(oktaApplicationId, "When okta.enabled is true, okta.application.id " +
                    "must be configured with your Okta Application ID. This can be found in the URL when accessing " +
                    "you application in a browser.");

            Client client = stormpathClient();

            String applicationCredentialsHref = "/api/v1/internal/apps/" + oktaApplicationId + "/settings/clientcreds";
            ApplicationCredentials applicationCredentials = client.getResource(applicationCredentialsHref, ApplicationCredentials.class);

            ApiKey secondary = ApiKeys.builder()
                    .setId(applicationCredentials.getClientId())
                    .setSecret(applicationCredentials.getClientSecret())
                    .build();

            ((PairedApiKey)stormpathClientApiKey).setSecondaryApiKey(secondary);
        }
    }

    protected String oktaAuthorizationServerId() {

        if (oktaAuthorizationServerIdConfig == null) {
            Client client = stormpathClient();

            String applicationCredentialsHref = "/api/v1/apps/" + oktaApplicationId;
            OktaApplicationConfigResource oktaApplicationConfigResource = client.getResource(applicationCredentialsHref, OktaApplicationConfigResource.class);
            oktaAuthorizationServerIdConfig = oktaApplicationConfigResource.getAuthorizationServerId();
        }

        return oktaAuthorizationServerIdConfig;
    }

    public EmailServiceConfig emailServiceConfig() {
        return new DefaultEmailServiceConfig()
                .setTokenExpirationHours(emailExpirationHours)
                .setValidationTemplateConfig(verifyEmailTemplate)
                .setResetPasswordTemplateConfig(forgotPasswordEmailTemplate)
                .setHostname(emailHostname)
                .setPort(emailPort)
                .setSsl(emailSSLEnabled)
                .setSslCheckServerIdentity(emailSSLCheckServerIdentityEnabled)
                .setTls(emailTLSEnabled)
                .setUsername(emailUsername)
                .setPassword(emailPassword);
    }

    public EmailService emailService() {
        return EmailServiceBuilder.INSTANCE
                .setConfig(emailServiceConfig())
                .build();
    }

    public OIDCWellKnownResource oidcWellKnownResource() {

        if (oktaEnabled) {
            String authorizationServerId = oktaAuthorizationServerId();
            String wellKnownUrlBaseUrl = authorizationServerId != null ? "/oauth2/"+authorizationServerId : "/";
            String href = wellKnownUrlBaseUrl + "/.well-known/openid-configuration";
            return stormpathClient().getResource(href, OIDCWellKnownResource.class);
        }
        return null;
    }

    public Application stormpathApplication() {

        Client client = stormpathClient();

        if (oktaEnabled) {

            Map<String, Object> oktaAppConfigMap = new LinkedHashMap<>();
            oktaAppConfigMap.put("authorizationServerId", oktaAuthorizationServerId());
            oktaAppConfigMap.put("emailService", emailService());
            oktaAppConfigMap.put("registrationWorkflowEnabled", registrationWorkflowEnabled);
            oktaAppConfigMap.put("client", client);
            oktaAppConfigMap.put("allowApiSecret", allowApiSecret);
            oktaAppConfigMap.put("userApiQueryTemplate", apiSecretQueryTemplate);
            oktaAppConfigMap.put("applicationId", oktaApplicationId);
            oktaAppConfigMap.put("passwordPolicyName", oktaPasswordPolicyName);

            Application application = client.getDataStore().getResource("local", Application.class);
            application.configureWithProperties(oktaAppConfigMap);
            if (Strings.hasText(applicationName)) {
                application.setName(applicationName);
            }
            return application;
        }

        if (Strings.hasText(applicationHref)) {
            return client.getResource(applicationHref, Application.class);
        }

        //otherwise no href configured - try to find an application:

        Application single = null;

        for (Application app : client.getApplications()) {
            if (app.getName().equalsIgnoreCase("Stormpath")) { //ignore the admin app
                continue;
            }
            if (single != null) {
                //there is more than one application in the tenant, and we can't infer which one should be used
                //for this particular application.  Let them know:
                throw new IllegalStateException(APP_HREF_ERROR);
            }
            single = app;
        }

        return single;
    }

    public com.stormpath.sdk.cache.CacheManager stormpathCacheManager() {

        if (!cachingEnabled) {
            return Caches.newDisabledCacheManager();
        }

        if (cacheManager != null) {
            return new SpringCacheManager(cacheManager);
        }

        //otherwise no Spring CacheManager - create a default:
        return Caches.newCacheManager().withDefaultTimeToLive(1, TimeUnit.HOURS)
            .withDefaultTimeToIdle(1, TimeUnit.HOURS).build();
    }

    protected Proxy resolveProxy() {

        if (!Strings.hasText(proxyHost)) {
            return null;
        }

        Proxy proxy;

        if (Strings.hasText(proxyUsername) || Strings.hasText(proxyPassword)) {
            proxy = new Proxy(proxyHost, proxyPort, proxyUsername, proxyPassword);
        } else {
            proxy = new Proxy(proxyHost, proxyPort);
        }

        return proxy;
    }

    public Client stormpathClient() {

        ClientBuilder builder = Clients.builder()
            .setApiKey(stormpathClientApiKey())
            .setCacheManager(stormpathCacheManager());

        if (oktaEnabled) {

            //authc scheme:
            authenticationScheme = AuthenticationScheme.SSWS;

            //base url checks:
            Assert.hasText(baseUrl, "When okta.enabled is true, stormpath.client.baseUrl " +
                "must be configured with your Okta Organization Base URL");

            if (baseUrl.endsWith("/")) {
                baseUrl = baseUrl.substring(0, baseUrl.length() - 1);
            }
        }

        if (authenticationScheme != null) {
            builder.setAuthenticationScheme(authenticationScheme);
        }

        if (Strings.hasText(baseUrl)) {
            builder.setBaseUrl(baseUrl);
        }

        Proxy proxy = resolveProxy();
        if (proxy != null) {
            builder.setProxy(proxy);
        }

        if (connectionTimeout > 0) {
            builder.setConnectionTimeout(connectionTimeout);
        }

        return builder.build();
    }

}
