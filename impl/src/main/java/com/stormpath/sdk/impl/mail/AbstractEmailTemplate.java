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
package com.stormpath.sdk.impl.mail;

import com.stormpath.sdk.impl.ds.InternalDataStore;
import com.stormpath.sdk.impl.resource.*;
import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.mail.EmailTemplate;
import com.stormpath.sdk.mail.MimeType;

import java.util.Map;

/**
 * @since 1.0.RC4
 */
public abstract class AbstractEmailTemplate<T extends EmailTemplate> extends AbstractInstanceResource implements EmailTemplate<T> {

    // SIMPLE PROPERTIES
    static final StringProperty NAME = new StringProperty("name");
    static final StringProperty DESCRIPTION = new StringProperty("description");
    static final StringProperty FROM_NAME = new StringProperty("fromName");
    static final StringProperty FROM_EMAIL_ADDRESS = new StringProperty("fromEmailAddress");
    static final StringProperty SUBJECT = new StringProperty("subject");
    static final StringProperty TEXT_BODY = new StringProperty("textBody");
    static final StringProperty HTML_BODY = new StringProperty("htmlBody");
    static final StringProperty MIME_TYPE = new StringProperty("mimeType");

    protected AbstractEmailTemplate(InternalDataStore dataStore) {
        super(dataStore);
    }

    protected AbstractEmailTemplate(InternalDataStore dataStore, Map<String, Object> properties) {
        super(dataStore, properties);
    }

    @Override
    public String getName() {
        return getString(NAME);
    }

    @Override
    public T setName(String name) {
        Assert.hasText(name, "name cannot be null or empty.");
        setProperty(NAME, name);
        return (T) this;
    }

    @Override
    public String getDescription() {
        return getString(DESCRIPTION);
    }

    @Override
    public T setDescription(String description) {
        setProperty(DESCRIPTION, description);
        return (T) this;
    }

    @Override
    public String getFromName() {
        return getString(FROM_NAME);
    }

    @Override
    public T setFromName(String fromName) {
        Assert.hasText(fromName, "fromName cannot be null or empty.");
        setProperty(FROM_NAME, fromName);
        return (T) this;
    }

    @Override
    public String getFromEmailAddress() {
        return getString(FROM_EMAIL_ADDRESS);
    }

    @Override
    public T setFromEmailAddress(String fromEmailAddress) {
        Assert.hasText(fromEmailAddress, "fromEmailAddress cannot be null or empty.");
        setProperty(FROM_EMAIL_ADDRESS, fromEmailAddress);
        return (T) this;
    }

    @Override
    public String getSubject() {
        return getString(SUBJECT);
    }

    @Override
    public T setSubject(String subject) {
        Assert.hasText(subject, "subject cannot be null or empty.");
        setProperty(SUBJECT, subject);
        return (T) this;
    }

    @Override
    public String getTextBody() {
        return getString(TEXT_BODY);
    }

    @Override
    public T setTextBody(String textBody) {
        Assert.notNull(textBody, "textBody cannot be null or empty.");
        setProperty(TEXT_BODY, textBody);
        return (T) this;
    }

    @Override
    public String getHtmlBody() {
        return getString(HTML_BODY);
    }

    @Override
    public T setHtmlBody(String htmlBody) {
        Assert.notNull(htmlBody, "htmlBody cannot be null or empty.");
        setProperty(HTML_BODY, htmlBody);
        return (T) this;
    }

    @Override
    public MimeType getMimeType() {
        String value = getStringProperty(MIME_TYPE.getName());
        if (value == null) {
            return null;
        }
        return MimeType.fromString(value.toUpperCase());
    }

    @Override
    public T setMimeType(MimeType mimeType) {
        Assert.notNull(mimeType, "mimeType cannot be null");
        setProperty(MIME_TYPE, mimeType.value());
        return (T) this;
    }

}
