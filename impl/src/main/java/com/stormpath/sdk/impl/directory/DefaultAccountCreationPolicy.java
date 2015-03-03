/*
* Copyright 2015 Stormpath, Inc.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package com.stormpath.sdk.impl.directory;

import com.stormpath.sdk.directory.AccountCreationPolicy;
import com.stormpath.sdk.impl.ds.InternalDataStore;
import com.stormpath.sdk.impl.resource.AbstractInstanceResource;
import com.stormpath.sdk.impl.resource.CollectionReference;
import com.stormpath.sdk.impl.resource.Property;
import com.stormpath.sdk.impl.resource.StatusProperty;
import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.mail.*;

import java.util.Map;

/**
 * @since 1.0.RC4.1
 */
public class DefaultAccountCreationPolicy extends AbstractInstanceResource implements AccountCreationPolicy {

    static final StatusProperty<EmailStatus> ACCOUNT_VERIFICATION_EMAIL_STATUS = new StatusProperty<EmailStatus>("verificationEmailStatus", EmailStatus.class);
    static final StatusProperty<EmailStatus> ACCOUNT_VERIFICATION_SUCCESS_EMAIL_STATUS = new StatusProperty<EmailStatus>("verificationSuccessEmailStatus", EmailStatus.class);
    static final StatusProperty<EmailStatus> WELCOME_EMAIL_STATUS = new StatusProperty<EmailStatus>("welcomeEmailStatus", EmailStatus.class);

    static final CollectionReference<ModeledEmailTemplateList, ModeledEmailTemplate> ACCOUNT_VERIFICATION_EMAIL_TEMPLATES =
            new CollectionReference<ModeledEmailTemplateList, ModeledEmailTemplate>("verificationEmailTemplates", ModeledEmailTemplateList.class, ModeledEmailTemplate.class);


    static final CollectionReference<AccountVerificationSuccessEmailTemplateList, AccountVerificationSuccessEmailTemplate> ACCOUNT_VERIFICATION_SUCCESS_EMAIL_TEMPLATES =
            new CollectionReference<AccountVerificationSuccessEmailTemplateList, AccountVerificationSuccessEmailTemplate>("verificationSuccessEmailTemplates", AccountVerificationSuccessEmailTemplateList.class, AccountVerificationSuccessEmailTemplate.class);

    static final CollectionReference<WelcomeEmailTemplateList, WelcomeEmailTemplate> WELCOME_EMAIL_TEMPLATES =
            new CollectionReference<WelcomeEmailTemplateList, WelcomeEmailTemplate>("welcomeEmailTemplates", WelcomeEmailTemplateList.class, WelcomeEmailTemplate.class);

    private static final Map<String, Property> PROPERTY_DESCRIPTORS = createPropertyDescriptorMap(
            ACCOUNT_VERIFICATION_EMAIL_STATUS, ACCOUNT_VERIFICATION_SUCCESS_EMAIL_STATUS, WELCOME_EMAIL_STATUS, ACCOUNT_VERIFICATION_EMAIL_TEMPLATES,
            ACCOUNT_VERIFICATION_SUCCESS_EMAIL_TEMPLATES, WELCOME_EMAIL_TEMPLATES);

    public DefaultAccountCreationPolicy(InternalDataStore dataStore) {
        super(dataStore);
    }

    public DefaultAccountCreationPolicy(InternalDataStore dataStore, Map<String, Object> properties) {
        super(dataStore, properties);
    }

    @Override
    public Map<String, Property> getPropertyDescriptors() {
        return PROPERTY_DESCRIPTORS;
    }

    @Override
    public ModeledEmailTemplateList getAccountVerificationEmailTemplates() {
        return getResourceProperty(ACCOUNT_VERIFICATION_EMAIL_TEMPLATES);
    }

    @Override
    public AccountVerificationSuccessEmailTemplateList getAccountVerificationSuccessEmailTemplates() {
        return getResourceProperty(ACCOUNT_VERIFICATION_SUCCESS_EMAIL_TEMPLATES);
    }

    @Override
    public WelcomeEmailTemplateList getWelcomeEmailTemplates() {
        return getResourceProperty(WELCOME_EMAIL_TEMPLATES);
    }

    @Override
    public EmailStatus getAccountVerificationEmailStatus() {
        String value = getStringProperty(ACCOUNT_VERIFICATION_EMAIL_STATUS.getName());
        if (value == null) {
            return null;
        }
        return EmailStatus.valueOf(value.toUpperCase());
    }

    @Override
    public AccountCreationPolicy setAccountVerificationEmailStatus(EmailStatus accountVerificationEmailStatus) {
        Assert.notNull(accountVerificationEmailStatus, "verificationEmailStatus cannot be null.");
        setProperty(ACCOUNT_VERIFICATION_EMAIL_STATUS, accountVerificationEmailStatus.name());
        return this;
    }

    @Override
    public EmailStatus getAccountVerificationSuccessEmailStatus() {
        String value = getStringProperty(ACCOUNT_VERIFICATION_SUCCESS_EMAIL_STATUS.getName());
        if (value == null) {
            return null;
        }
        return EmailStatus.valueOf(value.toUpperCase());
    }

    @Override
    public AccountCreationPolicy setAccountVerificationSuccessEmailStatus(EmailStatus accountVerificationSuccessEmailStatus) {
        Assert.notNull(accountVerificationSuccessEmailStatus, "verificationSuccessEmailStatus cannot be null.");
        setProperty(ACCOUNT_VERIFICATION_SUCCESS_EMAIL_STATUS, accountVerificationSuccessEmailStatus.name());
        return this;
    }

    @Override
    public EmailStatus getWelcomeEmailStatus() {
        String value = getStringProperty(WELCOME_EMAIL_STATUS.getName());
        if (value == null) {
            return null;
        }
        return EmailStatus.valueOf(value.toUpperCase());
    }

    @Override
    public AccountCreationPolicy setWelcomeEmailStatus(EmailStatus welcomeEmailStatus) {
        Assert.notNull(welcomeEmailStatus, "welcomeEmailStatus cannot be null.");
        setProperty(WELCOME_EMAIL_STATUS, welcomeEmailStatus.name());
        return this;
    }
}
