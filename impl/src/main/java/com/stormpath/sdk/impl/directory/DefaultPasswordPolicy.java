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
package com.stormpath.sdk.impl.directory;

import com.stormpath.sdk.directory.PasswordPolicy;
import com.stormpath.sdk.directory.PasswordStrength;
import com.stormpath.sdk.impl.ds.InternalDataStore;
import com.stormpath.sdk.impl.resource.AbstractInstanceResource;
import com.stormpath.sdk.impl.resource.CollectionReference;
import com.stormpath.sdk.impl.resource.IntegerProperty;
import com.stormpath.sdk.impl.resource.Property;
import com.stormpath.sdk.impl.resource.ResourceReference;
import com.stormpath.sdk.impl.resource.EnumProperty;
import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.mail.EmailStatus;
import com.stormpath.sdk.mail.ModeledEmailTemplate;
import com.stormpath.sdk.mail.ModeledEmailTemplateList;
import com.stormpath.sdk.mail.UnmodeledEmailTemplate;
import com.stormpath.sdk.mail.UnmodeledEmailTemplateList;

import java.util.Map;

/**
 * @since 1.0.RC4
 */
public class DefaultPasswordPolicy extends AbstractInstanceResource implements PasswordPolicy {

    // SIMPLE PROPERTIES
    static final IntegerProperty RESET_TOKEN_TTL = new IntegerProperty("resetTokenTtl");
    static final EnumProperty<EmailStatus> RESET_EMAIL_STATUS = new EnumProperty<EmailStatus>("resetEmailStatus", EmailStatus.class);
    static final EnumProperty<EmailStatus> RESET_SUCCESS_EMAIL_STATUS = new EnumProperty<EmailStatus>("resetSuccessEmailStatus", EmailStatus.class);

    // INSTANCE RESOURCE REFERENCES:
    static final ResourceReference<PasswordStrength> STRENGTH = new ResourceReference<PasswordStrength>("strength", PasswordStrength.class);

    //COLLECTION RESOURCE REFERENCES:
    static final CollectionReference<ModeledEmailTemplateList, ModeledEmailTemplate> RESET_EMAIL_TEMPLATES =
            new CollectionReference<ModeledEmailTemplateList, ModeledEmailTemplate>("resetEmailTemplates", ModeledEmailTemplateList.class, ModeledEmailTemplate.class);
    static final CollectionReference<UnmodeledEmailTemplateList, UnmodeledEmailTemplate> RESET_SUCCESS_EMAIL_TEMPLATES =
            new CollectionReference<UnmodeledEmailTemplateList, UnmodeledEmailTemplate>("resetSuccessEmailTemplates", UnmodeledEmailTemplateList.class, UnmodeledEmailTemplate.class);

    private static final Map<String, Property> PROPERTY_DESCRIPTORS = createPropertyDescriptorMap(
            RESET_TOKEN_TTL, RESET_EMAIL_STATUS, RESET_SUCCESS_EMAIL_STATUS, STRENGTH, RESET_EMAIL_TEMPLATES, RESET_SUCCESS_EMAIL_TEMPLATES);

    public DefaultPasswordPolicy(InternalDataStore dataStore) {
        super(dataStore);
    }

    public DefaultPasswordPolicy(InternalDataStore dataStore, Map<String, Object> properties) {
        super(dataStore, properties);
    }

    @Override
    public Map<String, Property> getPropertyDescriptors() {
        return PROPERTY_DESCRIPTORS;
    }

    @Override
    public int getResetTokenTtlHours() {
        return getInt(RESET_TOKEN_TTL);
    }

    @Override
    public PasswordPolicy setResetTokenTtlHours(int resetTokenTtl) {
        Assert.isTrue(resetTokenTtl > 0 && resetTokenTtl < 169, "resetTokenTtl must be a positive integer, less than 169.");
        setProperty(RESET_TOKEN_TTL, resetTokenTtl);
        return this;
    }

    @Override
    public EmailStatus getResetEmailStatus() {
        String value = getStringProperty(RESET_EMAIL_STATUS.getName());
        if (value == null) {
            return null;
        }
        return EmailStatus.valueOf(value.toUpperCase());
    }

    @Override
    public PasswordPolicy setResetEmailStatus(EmailStatus status) {
        Assert.notNull(status, "status cannot be null.");
        setProperty(RESET_EMAIL_STATUS, status.name());
        return this;
    }

    @Override
    public EmailStatus getResetSuccessEmailStatus() {
        String value = getStringProperty(RESET_SUCCESS_EMAIL_STATUS.getName());
        if (value == null) {
            return null;
        }
        return EmailStatus.valueOf(value.toUpperCase());
    }

    @Override
    public PasswordPolicy setResetSuccessEmailStatus(EmailStatus status) {
        Assert.notNull(status, "status cannot be null.");
        setProperty(RESET_SUCCESS_EMAIL_STATUS, status.name());
        return this;
    }

    @Override
    public PasswordStrength getStrength() {
        return getResourceProperty(STRENGTH);
    }

    @Override
    public ModeledEmailTemplateList getResetEmailTemplates() {
        return getResourceProperty(RESET_EMAIL_TEMPLATES);
    }

    @Override
    public UnmodeledEmailTemplateList getResetSuccessEmailTemplates() {
        return getResourceProperty(RESET_SUCCESS_EMAIL_TEMPLATES);
    }

}
