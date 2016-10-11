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
import com.stormpath.sdk.impl.resource.DateProperty;
import com.stormpath.sdk.impl.resource.EnumProperty;
import com.stormpath.sdk.impl.resource.ObjectProperty;
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
    private static final StringProperty BASE_PATH = new StringProperty("basePath");
    private static final EnumProperty<WebConfigurationStatus> STATUS = new EnumProperty<>(WebConfigurationStatus.class);
    private static final ObjectProperty<DefaultOAuth2Property> OAUTH2 = new ObjectProperty<>("oauth2", DefaultOAuth2Property.class);
    private static final ObjectProperty<DefaultEnabledProperty> REGISTER = new ObjectProperty<>("register", DefaultEnabledProperty.class);
    private static final ObjectProperty<DefaultEnabledProperty> VERIFY_EMAIL = new ObjectProperty<>("verifyEmail", DefaultEnabledProperty.class);
    private static final ObjectProperty<DefaultEnabledProperty> LOGIN = new ObjectProperty<>("login", DefaultEnabledProperty.class);
    private static final ObjectProperty<DefaultEnabledProperty> LOGOUT = new ObjectProperty<>("logout", DefaultEnabledProperty.class);
    private static final ObjectProperty<DefaultEnabledProperty> FORGOT_PASSWORD = new ObjectProperty<>("forgotPassword", DefaultEnabledProperty.class);
    private static final ObjectProperty<DefaultEnabledProperty> CHANGE_PASSWORD = new ObjectProperty<>("changePassword", DefaultEnabledProperty.class);
    private static final ObjectProperty<DefaultEnabledProperty> CALLBACK = new ObjectProperty<>("callback", DefaultEnabledProperty.class);
    private static final ObjectProperty<DefaultEnabledProperty> ID_SITE = new ObjectProperty<>("idSite", DefaultEnabledProperty.class);
    private static final ObjectProperty<DefaultMeProperty> ME = new ObjectProperty<>("me", DefaultMeProperty.class);

    // INSTANCE RESOURCE REFERENCES:
    private static final ResourceReference<ApiKey> SIGNING_KEY = new ResourceReference<>("signingKey", ApiKey.class);
    private static final ResourceReference<Application> APPLICATION = new ResourceReference<>("application", Application.class);
    private static final ResourceReference<Tenant> TENANT = new ResourceReference<>("tenant", Tenant.class);

    private static final Map<String, Property> PROPERTY_DESCRIPTORS = createPropertyDescriptorMap(CREATED_AT, MODIFIED_AT, DOMAIN_NAME,
            BASE_PATH, STATUS, OAUTH2, REGISTER, VERIFY_EMAIL, LOGIN, LOGOUT, FORGOT_PASSWORD, CHANGE_PASSWORD, CALLBACK, ID_SITE,
            SIGNING_KEY, APPLICATION, TENANT);

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
    public ApiKey getSigningApiKey() {
        return getResourceProperty(SIGNING_KEY);
    }

    public void setSigningApiKey(ApiKey apiKey) {
        setProperty(SIGNING_KEY, apiKey);
    }

    @Override
    public Application getApplication() {
        return getResourceProperty(APPLICATION);
    }

    @Override
    public OAuth2Property getOAuth2() {
        return getObjectProperty(OAUTH2);
    }

    @Override
    public EnabledProperty getRegister() {
        return getObjectProperty(REGISTER);
    }

    @Override
    public EnabledProperty getVerifyEmail() {
        return getObjectProperty(VERIFY_EMAIL);
    }

    @Override
    public EnabledProperty getLogin() {
        return getObjectProperty(LOGIN);
    }


    @Override
    public EnabledProperty getLogout() {
        return getObjectProperty(LOGOUT);
    }

    @Override
    public EnabledProperty getForgotPassword() {
        return getObjectProperty(FORGOT_PASSWORD);
    }

    @Override
    public EnabledProperty getChangePassword() {
        return getObjectProperty(CHANGE_PASSWORD);
    }

    @Override
    public EnabledProperty getIdSite() {
        return getObjectProperty(ID_SITE);
    }

    @Override
    public EnabledProperty getCallback() {
        return getObjectProperty(CALLBACK);
    }

    @Override
    public MeProperty getMe() {
        return getObjectProperty(ME);
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
