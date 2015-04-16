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
package com.stormpath.sdk.impl.mail;

import com.stormpath.sdk.impl.ds.InternalDataStore;
import com.stormpath.sdk.impl.resource.Property;
import com.stormpath.sdk.mail.UnModeledEmailTemplate;

import java.util.Map;

/**
 * @since 1.0.RC4
 */
public class DefaultUnModeledEmailTemplate extends AbstractEmailTemplate<UnModeledEmailTemplate> implements UnModeledEmailTemplate {

    private static final Map<String, Property> PROPERTY_DESCRIPTORS = createPropertyDescriptorMap(
            NAME, DESCRIPTION, FROM_NAME, FROM_EMAIL_ADDRESS, SUBJECT, TEXT_BODY, HTML_BODY, MIME_TYPE);

    public DefaultUnModeledEmailTemplate(InternalDataStore dataStore) {
        super(dataStore);
    }

    public DefaultUnModeledEmailTemplate(InternalDataStore dataStore, Map<String, Object> properties) {
        super(dataStore, properties);
    }

    @Override
    public Map<String, Property> getPropertyDescriptors() {
        return PROPERTY_DESCRIPTORS;
    }

}
