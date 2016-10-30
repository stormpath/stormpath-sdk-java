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
package com.stormpath.sdk.impl.application.webconfig;

import com.stormpath.sdk.api.ApiKey;
import com.stormpath.sdk.application.Application;
import com.stormpath.sdk.application.webconfig.ApplicationWebConfig;
import com.stormpath.sdk.application.webconfig.ApplicationWebConfigStatus;
import com.stormpath.sdk.application.webconfig.CallbackConfig;
import com.stormpath.sdk.application.webconfig.ChangePasswordConfig;
import com.stormpath.sdk.application.webconfig.CookieConfig;
import com.stormpath.sdk.application.webconfig.ForgotPasswordConfig;
import com.stormpath.sdk.application.webconfig.IdSiteConfig;
import com.stormpath.sdk.application.webconfig.LoginConfig;
import com.stormpath.sdk.application.webconfig.LogoutConfig;
import com.stormpath.sdk.application.webconfig.MeConfig;
import com.stormpath.sdk.application.webconfig.Oauth2Config;
import com.stormpath.sdk.application.webconfig.RegisterConfig;
import com.stormpath.sdk.application.webconfig.VerifyEmailConfig;
import com.stormpath.sdk.impl.ds.InternalDataStore;
import com.stormpath.sdk.impl.resource.AbstractInstanceResource;
import com.stormpath.sdk.impl.resource.AbstractPropertyRetriever;
import com.stormpath.sdk.impl.resource.DateProperty;
import com.stormpath.sdk.impl.resource.EnumProperty;
import com.stormpath.sdk.impl.resource.ParentAwareObjectProperty;
import com.stormpath.sdk.impl.resource.Property;
import com.stormpath.sdk.impl.resource.ResourceReference;
import com.stormpath.sdk.impl.resource.StringProperty;
import com.stormpath.sdk.tenant.Tenant;

import java.util.Date;
import java.util.Map;

/**
 * @since 1.2.0
 */
public class DefaultApplicationWebConfig extends AbstractInstanceResource implements ApplicationWebConfig {

    private static final DateProperty CREATED_AT = new DateProperty("createdAt");
    private static final DateProperty MODIFIED_AT = new DateProperty("modifiedAt");

    // SIMPLE PROPERTIES:
    private static final StringProperty DOMAIN_NAME = new StringProperty("domainName");
    private static final StringProperty DNS_LABEL = new StringProperty("dnsLabel");
    private static final EnumProperty<ApplicationWebConfigStatus> STATUS = new EnumProperty<>(ApplicationWebConfigStatus.class);
    private static final ParentAwareObjectProperty<DefaultOauth2Config, AbstractPropertyRetriever> OAUTH2;
    private static final ParentAwareObjectProperty<DefaultCookieConfig, AbstractPropertyRetriever> ACCESS_TOKEN_COOKIE;
    private static final ParentAwareObjectProperty<DefaultCookieConfig, AbstractPropertyRetriever> REFRESH_TOKEN_COOKIE;
    private static final ParentAwareObjectProperty<DefaultWebFeatureConfig, AbstractPropertyRetriever> REGISTER;
    private static final ParentAwareObjectProperty<DefaultNullableWebFeatureConfig, AbstractPropertyRetriever> VERIFY_EMAIL;
    private static final ParentAwareObjectProperty<DefaultWebFeatureConfig, AbstractPropertyRetriever> LOGIN;
    private static final ParentAwareObjectProperty<DefaultWebFeatureConfig, AbstractPropertyRetriever> LOGOUT;
    private static final ParentAwareObjectProperty<DefaultNullableWebFeatureConfig, AbstractPropertyRetriever> FORGOT_PASSWORD;
    private static final ParentAwareObjectProperty<DefaultNullableWebFeatureConfig, AbstractPropertyRetriever> CHANGE_PASSWORD;
    private static final ParentAwareObjectProperty<DefaultWebFeatureConfig, AbstractPropertyRetriever> CALLBACK;
    private static final ParentAwareObjectProperty<DefaultWebFeatureConfig, AbstractPropertyRetriever> ID_SITE;
    private static final ParentAwareObjectProperty<DefaultMeConfig, AbstractPropertyRetriever> ME;
    // INSTANCE RESOURCE REFERENCES:
    private static final ResourceReference<ApiKey> SIGNING_API_KEY = new ResourceReference<>("signingApiKey", ApiKey.class);
    private static final ResourceReference<Application> APPLICATION = new ResourceReference<>("application", Application.class);
    private static final ResourceReference<Tenant> TENANT = new ResourceReference<>("tenant", Tenant.class);

    static {
        OAUTH2 = new ParentAwareObjectProperty<>("oauth2", DefaultOauth2Config.class, AbstractPropertyRetriever.class);
        ACCESS_TOKEN_COOKIE = new ParentAwareObjectProperty<>("accessTokenCookie", DefaultCookieConfig.class, AbstractPropertyRetriever.class);
        REFRESH_TOKEN_COOKIE = new ParentAwareObjectProperty<>("refreshTokenCookie", DefaultCookieConfig.class, AbstractPropertyRetriever.class);
        REGISTER = new ParentAwareObjectProperty<>("register", DefaultWebFeatureConfig.class, AbstractPropertyRetriever.class);
        VERIFY_EMAIL = new ParentAwareObjectProperty<>("verifyEmail", DefaultNullableWebFeatureConfig.class, AbstractPropertyRetriever.class);
        LOGIN = new ParentAwareObjectProperty<>("login", DefaultWebFeatureConfig.class, AbstractPropertyRetriever.class);
        LOGOUT = new ParentAwareObjectProperty<>("logout", DefaultWebFeatureConfig.class, AbstractPropertyRetriever.class);
        FORGOT_PASSWORD = new ParentAwareObjectProperty<>("forgotPassword", DefaultNullableWebFeatureConfig.class, AbstractPropertyRetriever.class);
        CHANGE_PASSWORD = new ParentAwareObjectProperty<>("changePassword", DefaultNullableWebFeatureConfig.class, AbstractPropertyRetriever.class);
        CALLBACK = new ParentAwareObjectProperty<>("callback", DefaultWebFeatureConfig.class, AbstractPropertyRetriever.class);
        ID_SITE = new ParentAwareObjectProperty<>("idSite", DefaultWebFeatureConfig.class, AbstractPropertyRetriever.class);
        ME = new ParentAwareObjectProperty<>("me", DefaultMeConfig.class, AbstractPropertyRetriever.class);
    }

    private static final Map<String, Property> PROPERTY_DESCRIPTORS = createPropertyDescriptorMap(CREATED_AT, MODIFIED_AT, DOMAIN_NAME,
            DNS_LABEL, STATUS, OAUTH2, REGISTER, VERIFY_EMAIL, LOGIN, LOGOUT, FORGOT_PASSWORD, CHANGE_PASSWORD, CALLBACK, ID_SITE,
            SIGNING_API_KEY, APPLICATION, TENANT);


    public DefaultApplicationWebConfig(InternalDataStore dataStore, Map<String, Object> properties) {
        super(dataStore, properties);
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
    public String getDnsLabel() {
        return getString(DNS_LABEL);
    }

    @Override
    public void setDnsLabel(String dnsLabel) {
        setProperty(DNS_LABEL, dnsLabel);
    }

    @Override
    public ApplicationWebConfigStatus getStatus() {
        return getEnumProperty(STATUS);
    }

    @Override
    public void setStatus(ApplicationWebConfigStatus status) {
        setProperty(STATUS, status);
    }

    @Override
    public ApiKey getSigningApiKey() {
        return getResourceProperty(SIGNING_API_KEY);
    }

    @Override
    public void setSigningApiKey(ApiKey apiKey) {
        if (apiKey == null) {
            setProperty(SIGNING_API_KEY, null);
        } else {
            setResourceProperty(SIGNING_API_KEY, apiKey);
        }
    }

    @Override
    public Application getApplication() {
        return getResourceProperty(APPLICATION);
    }

    @Override
    public Tenant getTenant() {
        return getResourceProperty(TENANT);
    }

    @Override
    public Oauth2Config getOAuth2Config() {
        return getParentAwareObjectProperty(OAUTH2);
    }

    @Override
    public CookieConfig getAccessTokenCookieConfig() {
        return getParentAwareObjectProperty(ACCESS_TOKEN_COOKIE);
    }

    @Override
    public CookieConfig getRefreshTokenCookieConfig() {
        return getParentAwareObjectProperty(REFRESH_TOKEN_COOKIE);
    }

    @Override
    public RegisterConfig getRegisterConfig() {
        return getParentAwareObjectProperty(REGISTER);
    }

    @Override
    public VerifyEmailConfig getVerifyEmailConfig() {
        return getParentAwareObjectProperty(VERIFY_EMAIL);
    }

    @Override
    public LoginConfig getLoginConfig() {
        return getParentAwareObjectProperty(LOGIN);
    }

    @Override
    public LogoutConfig getLogoutConfig() {
        return getParentAwareObjectProperty(LOGOUT);
    }

    @Override
    public ForgotPasswordConfig getForgotPasswordConfig() {
        return getParentAwareObjectProperty(FORGOT_PASSWORD);
    }

    @Override
    public ChangePasswordConfig getChangePasswordConfig() {
        return getParentAwareObjectProperty(CHANGE_PASSWORD);
    }

    @Override
    public IdSiteConfig getIdSiteConfig() {
        return getParentAwareObjectProperty(ID_SITE);
    }

    @Override
    public CallbackConfig getCallbackConfig() {
        return getParentAwareObjectProperty(CALLBACK);
    }

    @Override
    public MeConfig getMeConfig() {
        return getParentAwareObjectProperty(ME);
    }

    @Override
    public Date getCreatedAt() {
        return getDateProperty(CREATED_AT);
    }

    @Override
    public Date getModifiedAt() {
        return getDateProperty(MODIFIED_AT);
    }

}
