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
package com.stormpath.sdk.impl.application;

import com.stormpath.sdk.api.ApiKey;
import com.stormpath.sdk.application.*;
import com.stormpath.sdk.impl.ds.InternalDataStore;
import com.stormpath.sdk.impl.resource.*;
import com.stormpath.sdk.tenant.Tenant;

import java.util.Date;
import java.util.Map;

/**
 * @since 1.1.0
 */
public class DefaultWebConfiguration extends AbstractInstanceResource implements WebConfiguration {

    private static final DateProperty CREATED_AT = new DateProperty("createdAt");
    private static final DateProperty MODIFIED_AT = new DateProperty("modifiedAt");

    // SIMPLE PROPERTIES:
    private static final StringProperty DOMAIN_NAME = new StringProperty("domainName");
    private static final StringProperty BASE_PATH = new StringProperty("basePath");
    private static final EnumProperty<WebConfigurationStatus> STATUS = new EnumProperty<>(WebConfigurationStatus.class);
    private static final MapProperty OAUTH2 = new MapProperty("oauth2");
    private static final MapProperty REGISTER = new MapProperty("register");
    private static final MapProperty VERIFY_EMAIL = new MapProperty("verifyEmail");
    private static final MapProperty LOGIN = new MapProperty("login");
    private static final MapProperty LOGOUT = new MapProperty("logout");
    private static final MapProperty FORGOT_PASSWORD = new MapProperty("forgotPassword");
    private static final MapProperty CHANGE_PASSWORD = new MapProperty("changePassword");
    private static final MapProperty ID_SITE = new MapProperty("idSite");
    private static final MapProperty CALLBACK = new MapProperty("callback");
    private static final MapProperty ME = new MapProperty("me");

    // INSTANCE RESOURCE REFERENCES:
    private static final ResourceReference<ApiKey> SIGNING_KEY = new ResourceReference<>("signingKey", ApiKey.class);
    private static final ResourceReference<Application> APPLICATION = new ResourceReference<>("application", Application.class);
    private static final ResourceReference<Tenant> TENANT = new ResourceReference<>("tenant", Tenant.class);

    private static final Map<String, Property> PROPERTY_DESCRIPTORS = createPropertyDescriptorMap(DOMAIN_NAME, BASE_PATH, STATUS,
            CREATED_AT, MODIFIED_AT, SIGNING_KEY, APPLICATION, TENANT);

    private static final String OAUTH2_CONFIGURATION_PROPERTY = "oauth2Configuration";
    private static final String ME_CONFIGURATION_PROPERTY = "meConfiguration";
    private static final String REGISTER_CONFIGURATION_PROPERTY = "registerConfiguration";
    private static final String VERIFY_EMAIL_CONFIGURATION_PROPERTY = "verifyEmailConfiguration";
    private static final String LOGIN_CONFIGURATION_PROPERTY = "loginConfiguration";
    private static final String LOGOUT_CONFIGURATION_PROPERTY = "logoutConfiguration";
    private static final String FORGOT_PASSWORD_CONFIGURATION_PROPERTY = "forgotPasswordConfiguration";
    private static final String CHANGE_PASSWORD_CONFIGURATION_PROPERTY = "changePasswordConfiguration";
    private static final String ID_SITE_CONFIGURATION_PROPERTY = "idSiteConfiguration";
    private static final String CALLBACK_CONFIGURATION_PROPERTY = "callbackConfiguration";


    public DefaultWebConfiguration(InternalDataStore dataStore) {
        super(dataStore);
    }

    public DefaultWebConfiguration(InternalDataStore dataStore, Map<String, Object> properties) {
        super(dataStore, properties);
        setProperty(OAUTH2_CONFIGURATION_PROPERTY, buildOAuth2Configuration());
        setProperty(ME_CONFIGURATION_PROPERTY, buildMeConfiguration());
        setProperty(REGISTER_CONFIGURATION_PROPERTY, buildRegisterConfiguration());
        setProperty(VERIFY_EMAIL_CONFIGURATION_PROPERTY, buildVerifyEmailConfiguration());
        setProperty(LOGIN_CONFIGURATION_PROPERTY, buildLoginConfiguration());
        setProperty(LOGOUT_CONFIGURATION_PROPERTY, buildLogoutConfiguration());
        setProperty(FORGOT_PASSWORD_CONFIGURATION_PROPERTY, buildForgotPasswordConfiguration());
        setProperty(CHANGE_PASSWORD_CONFIGURATION_PROPERTY, buildChangePasswordConfiguration());
        setProperty(ID_SITE_CONFIGURATION_PROPERTY, buildIdSiteConfiguration());
        setProperty(CALLBACK_CONFIGURATION_PROPERTY, buildCallbackConfiguration());
    }

    @Override
    public Map<String, Property> getPropertyDescriptors() {
        return PROPERTY_DESCRIPTORS;
    }

    @Override
    public String getDomainName() {
        return getString(DOMAIN_NAME);
    }

    @Override
    public String getBasePath() {
        return getString(BASE_PATH);
    }

    public void setBasePath(String basePath) {
        setProperty(BASE_PATH, basePath);
    }

    @Override
    public WebConfigurationStatus getStatus() {
        return getEnumProperty(STATUS);
    }

    public void setStatus(WebConfigurationStatus status) {
        setProperty(STATUS, status);
    }

    @Override
    public ApiKey getSigningKey() {
        return getResourceProperty(SIGNING_KEY);
    }

    public void setSigningKey(ApiKey apiKey) {
        setProperty(SIGNING_KEY, apiKey);
    }

    @Override
    public Application getApplication() {
        return getResourceProperty(APPLICATION);
    }

    @Override
    public OAuth2Configuration getOAuth2Configuration() {
        return (OAuth2Configuration) getProperty(OAUTH2_CONFIGURATION_PROPERTY);
    }

    private OAuth2Configuration buildOAuth2Configuration() {
        Map oauth2 = getMap(OAUTH2);

        DefaultOAuth2Configuration configuration = new DefaultOAuth2Configuration();

        DefaultWebConfigurationProperty clientCredentials = new DefaultWebConfigurationProperty();
        clientCredentials.setEnabled((Boolean) ((Map) oauth2.get("clientCredentials")).get("enabled"));

        DefaultWebConfigurationProperty password = new DefaultWebConfigurationProperty();
        password.setEnabled((Boolean) ((Map) oauth2.get("password")).get("enabled"));

        configuration.setClientCredentials(clientCredentials);
        configuration.setPassword(password);
        configuration.setEnabled((Boolean) oauth2.get("enabled"));

        return configuration;
    }

    @Override
    public WebConfigurationProperty getRegisterConfiguration() {
        return (WebConfigurationProperty) getProperty(REGISTER_CONFIGURATION_PROPERTY);
    }

    private WebConfigurationProperty buildRegisterConfiguration() {
        Map register = getMap(REGISTER);

        DefaultWebConfigurationProperty registerConfig = new DefaultWebConfigurationProperty();
        registerConfig.setEnabled((Boolean) register.get("enabled"));

        return registerConfig;
    }

    @Override
    public WebConfigurationProperty getVerifyEmailConfiguration() {
        return (WebConfigurationProperty) getProperty(VERIFY_EMAIL_CONFIGURATION_PROPERTY);
    }

    private WebConfigurationProperty buildVerifyEmailConfiguration() {
        Map verifyEmail = getMap(VERIFY_EMAIL);

        DefaultWebConfigurationProperty verifyEmailConfig = new DefaultWebConfigurationProperty();
        verifyEmailConfig.setEnabled((Boolean) verifyEmail.get("enabled"));

        return verifyEmailConfig;
    }

    @Override
    public WebConfigurationProperty getLoginConfiguration() {
        return (WebConfigurationProperty) getProperty(LOGIN_CONFIGURATION_PROPERTY);
    }

    private WebConfigurationProperty buildLoginConfiguration() {
        Map login = getMap(LOGIN);

        DefaultWebConfigurationProperty loginConfig = new DefaultWebConfigurationProperty();
        loginConfig.setEnabled((Boolean) login.get("enabled"));

        return loginConfig;
    }

    @Override
    public WebConfigurationProperty getLogoutConfiguration() {
        return (WebConfigurationProperty) getProperty(LOGOUT_CONFIGURATION_PROPERTY);
    }

    private WebConfigurationProperty buildLogoutConfiguration() {
        Map logout = getMap(LOGOUT);

        DefaultWebConfigurationProperty logoutConfig = new DefaultWebConfigurationProperty();
        logoutConfig.setEnabled((Boolean) logout.get("enabled"));

        return logoutConfig;
    }

    @Override
    public WebConfigurationProperty getForgotPasswordConfiguration() {
        return (WebConfigurationProperty) getProperty(FORGOT_PASSWORD_CONFIGURATION_PROPERTY);
    }

    private WebConfigurationProperty buildForgotPasswordConfiguration() {
        Map forgotPassword = getMap(FORGOT_PASSWORD);

        DefaultWebConfigurationProperty forgotPasswordConfig = new DefaultWebConfigurationProperty();
        forgotPasswordConfig.setEnabled((Boolean) forgotPassword.get("enabled"));

        return forgotPasswordConfig;
    }

    @Override
    public WebConfigurationProperty getChangePasswordConfiguration() {
        return (WebConfigurationProperty) getProperty(CHANGE_PASSWORD_CONFIGURATION_PROPERTY);
    }

    private WebConfigurationProperty buildChangePasswordConfiguration() {
        Map changePassword = getMap(CHANGE_PASSWORD);

        DefaultWebConfigurationProperty changePasswordConfig = new DefaultWebConfigurationProperty();
        changePasswordConfig.setEnabled((Boolean) changePassword.get("enabled"));

        return changePasswordConfig;
    }

    @Override
    public WebConfigurationProperty getIdSiteConfiguration() {
        return (WebConfigurationProperty) getProperty(ID_SITE_CONFIGURATION_PROPERTY);
    }

    private WebConfigurationProperty buildIdSiteConfiguration() {
        Map idSite = getMap(ID_SITE);

        DefaultWebConfigurationProperty idSiteConfig = new DefaultWebConfigurationProperty();
        idSiteConfig.setEnabled((Boolean) idSite.get("enabled"));

        return idSiteConfig;
    }

    @Override
    public WebConfigurationProperty getCallbackConfiguration() {
        return (WebConfigurationProperty) getProperty(CALLBACK_CONFIGURATION_PROPERTY);
    }

    private WebConfigurationProperty buildCallbackConfiguration() {
        Map callback = getMap(CALLBACK);

        DefaultWebConfigurationProperty callbackConfig = new DefaultWebConfigurationProperty();
        callbackConfig.setEnabled((Boolean) callback.get("enabled"));

        return callbackConfig;
    }

    @Override
    public MeConfiguration getMeConfiguration() {
        return (MeConfiguration) getProperty(ME_CONFIGURATION_PROPERTY);
    }

    private MeConfiguration buildMeConfiguration() {
        Map me = getMap(ME);

        DefaultMeConfiguration configuration = new DefaultMeConfiguration();

        configuration.setEnabled((Boolean) me.get("enabled"));
        configuration.setUri((String) me.get("uri"));

        Map expansionOptions = (Map) me.get("expand");

        DefaultMeExpansionOptions meExpansionOptions = new DefaultMeExpansionOptions();
        meExpansionOptions.setExpandApiKeys((Boolean) expansionOptions.get("apiKeys"));
        meExpansionOptions.setExpandApplications((Boolean) expansionOptions.get("applications"));
        meExpansionOptions.setExpandCustomData((Boolean) expansionOptions.get("customData"));
        meExpansionOptions.setExpandDirectory((Boolean) expansionOptions.get("directory"));
        meExpansionOptions.setExpandGroupMemberships((Boolean) expansionOptions.get("groupMemberships"));
        meExpansionOptions.setExpandProviderData((Boolean) expansionOptions.get("providerData"));
        meExpansionOptions.setExpandTenant((Boolean) expansionOptions.get("tenant"));

        configuration.setExpansionOptions(meExpansionOptions);

        return configuration;
    }

    @Override
    public Date getCreatedAt() {
        return getDateProperty(CREATED_AT);
    }

    @Override
    public Date getModifiedAt() {
        return getDateProperty(MODIFIED_AT);
    }

    @Override
    public void delete() {
        getDataStore().delete(this);
    }

}
