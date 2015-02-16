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
import com.stormpath.sdk.directory.Strength;
import com.stormpath.sdk.impl.ds.InternalDataStore;
import com.stormpath.sdk.impl.resource.*;
import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.mail.*;

import java.util.Map;

/**
 * @since 1.0.0
 */
public class DefaultPasswordPolicy extends AbstractInstanceResource implements PasswordPolicy {

    // SIMPLE PROPERTIES
    static final IntegerProperty RESET_TOKEN_TTL = new IntegerProperty("resetTokenTtl");
    static final StatusProperty<EmailStatus> RESET_EMAIL_STATUS = new StatusProperty<EmailStatus>("resetEmailStatus", EmailStatus.class);
    static final StatusProperty<EmailStatus> RESET_SUCCESS_EMAIL_STATUS = new StatusProperty<EmailStatus>("resetSuccessEmailStatus", EmailStatus.class);

    // INSTANCE RESOURCE REFERENCES:
    static final ResourceReference<Strength> STRENGTH = new ResourceReference<Strength>("strength", Strength.class);

    //COLLECTION RESOURCE REFERENCES:
    static final CollectionReference<ResetEmailTemplateList, ResetEmailTemplate> RESET_EMAIL_TEMPLATES =
            new CollectionReference<ResetEmailTemplateList, ResetEmailTemplate>("resetEmailTemplates", ResetEmailTemplateList.class, ResetEmailTemplate.class);
    static final CollectionReference<ResetSuccessEmailTemplateList, ResetSuccessEmailTemplate> RESET_SUCCESS_EMAIL_TEMPLATES =
            new CollectionReference<ResetSuccessEmailTemplateList, ResetSuccessEmailTemplate>("resetSuccessEmailTemplates", ResetSuccessEmailTemplateList.class, ResetSuccessEmailTemplate.class);

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
    public int getResetTokenTtl() {
        return getInt(RESET_TOKEN_TTL);
    }

    @Override
    public PasswordPolicy setResetTokenTtl(int resetTokenTtl) {
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
    public Strength getStrength() {
        return getResourceProperty(STRENGTH);
    }

    @Override
    public ResetEmailTemplateList getResetEmailTemplates() {
        return getResourceProperty(RESET_EMAIL_TEMPLATES);
    }

    @Override
    public ResetSuccessEmailTemplateList getResetSuccessEmailTemplates() {
        return getResourceProperty(RESET_SUCCESS_EMAIL_TEMPLATES);
    }

}
