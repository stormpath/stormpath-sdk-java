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
import com.stormpath.sdk.application.Application;
import com.stormpath.sdk.application.EnabledProperty;
import com.stormpath.sdk.application.MeProperty;
import com.stormpath.sdk.application.OAuth2Property;
import com.stormpath.sdk.application.WebConfiguration;
import com.stormpath.sdk.application.WebConfigurationStatus;
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
public class DefaultWebConfiguration extends AbstractInstanceResource implements WebConfiguration {

    private static final DateProperty CREATED_AT = new DateProperty("createdAt");
    private static final DateProperty MODIFIED_AT = new DateProperty("modifiedAt");

    // SIMPLE PROPERTIES:
    private static final StringProperty DOMAIN_NAME = new StringProperty("domainName");
    private static final StringProperty DNS_LABEL = new StringProperty("dnsLabel");
    private static final EnumProperty<WebConfigurationStatus> STATUS = new EnumProperty<>(WebConfigurationStatus.class);
    private static final ParentAwareObjectProperty<DefaultOAuth2Property, AbstractPropertyRetriever> OAUTH2;
    private static final ParentAwareObjectProperty<DefaultEnabledProperty, AbstractPropertyRetriever> REGISTER;
    private static final ParentAwareObjectProperty<DefaultEnabledProperty, AbstractPropertyRetriever> VERIFY_EMAIL;
    private static final ParentAwareObjectProperty<DefaultEnabledProperty, AbstractPropertyRetriever> LOGIN;
    private static final ParentAwareObjectProperty<DefaultEnabledProperty, AbstractPropertyRetriever> LOGOUT;
    private static final ParentAwareObjectProperty<DefaultEnabledProperty, AbstractPropertyRetriever> FORGOT_PASSWORD;
    private static final ParentAwareObjectProperty<DefaultEnabledProperty, AbstractPropertyRetriever> CHANGE_PASSWORD;
    private static final ParentAwareObjectProperty<DefaultEnabledProperty, AbstractPropertyRetriever> CALLBACK;
    private static final ParentAwareObjectProperty<DefaultEnabledProperty, AbstractPropertyRetriever> ID_SITE;
    private static final ParentAwareObjectProperty<DefaultMeProperty, AbstractPropertyRetriever> ME;

    static {
        OAUTH2 = new ParentAwareObjectProperty<>("oauth2", DefaultOAuth2Property.class, AbstractPropertyRetriever.class);
        REGISTER = new ParentAwareObjectProperty<>("register", DefaultEnabledProperty.class, AbstractPropertyRetriever.class);
        VERIFY_EMAIL = new ParentAwareObjectProperty<>("verifyEmail", DefaultEnabledProperty.class, AbstractPropertyRetriever.class);
        LOGIN = new ParentAwareObjectProperty<>("login", DefaultEnabledProperty.class, AbstractPropertyRetriever.class);
        LOGOUT = new ParentAwareObjectProperty<>("logout", DefaultEnabledProperty.class, AbstractPropertyRetriever.class);
        FORGOT_PASSWORD = new ParentAwareObjectProperty<>("forgotPassword", DefaultEnabledProperty.class, AbstractPropertyRetriever.class);
        CHANGE_PASSWORD = new ParentAwareObjectProperty<>("changePassword", DefaultEnabledProperty.class, AbstractPropertyRetriever.class);
        CALLBACK = new ParentAwareObjectProperty<>("callback", DefaultEnabledProperty.class, AbstractPropertyRetriever.class);
        ID_SITE = new ParentAwareObjectProperty<>("idSite", DefaultEnabledProperty.class, AbstractPropertyRetriever.class);
        ME = new ParentAwareObjectProperty<>("me", DefaultMeProperty.class, AbstractPropertyRetriever.class);
    }

    // INSTANCE RESOURCE REFERENCES:
    private static final ResourceReference<ApiKey> SIGNING_API_KEY = new ResourceReference<>("signingApiKey", ApiKey.class);
    private static final ResourceReference<Application> APPLICATION = new ResourceReference<>("application", Application.class);
    private static final ResourceReference<Tenant> TENANT = new ResourceReference<>("tenant", Tenant.class);

    private static final Map<String, Property> PROPERTY_DESCRIPTORS = createPropertyDescriptorMap(CREATED_AT, MODIFIED_AT, DOMAIN_NAME,
            DNS_LABEL, STATUS, OAUTH2, REGISTER, VERIFY_EMAIL, LOGIN, LOGOUT, FORGOT_PASSWORD, CHANGE_PASSWORD, CALLBACK, ID_SITE,
            SIGNING_API_KEY, APPLICATION, TENANT);

    public DefaultWebConfiguration(InternalDataStore dataStore, Map<String, Object> properties) {
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
    public void setDnsLabel(String basePath) {
        setProperty(DNS_LABEL, basePath);
    }

    @Override
    public WebConfigurationStatus getStatus() {
        return getEnumProperty(STATUS);
    }

    @Override
    public void setStatus(WebConfigurationStatus status) {
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
    public OAuth2Property getOAuth2() {
        return getParentAwareObjectProperty(OAUTH2);
    }

    @Override
    public EnabledProperty getRegister() {
        return getParentAwareObjectProperty(REGISTER);
    }

    @Override
    public EnabledProperty getVerifyEmail() {
        return getParentAwareObjectProperty(VERIFY_EMAIL);
    }

    @Override
    public EnabledProperty getLogin() {
        return getParentAwareObjectProperty(LOGIN);
    }


    @Override
    public EnabledProperty getLogout() {
        return getParentAwareObjectProperty(LOGOUT);
    }

    @Override
    public EnabledProperty getForgotPassword() {
        return getParentAwareObjectProperty(FORGOT_PASSWORD);
    }

    @Override
    public EnabledProperty getChangePassword() {
        return getParentAwareObjectProperty(CHANGE_PASSWORD);
    }

    @Override
    public EnabledProperty getIdSite() {
        return getParentAwareObjectProperty(ID_SITE);
    }

    @Override
    public EnabledProperty getCallback() {
        return getParentAwareObjectProperty(CALLBACK);
    }

    @Override
    public MeProperty getMe() {
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
