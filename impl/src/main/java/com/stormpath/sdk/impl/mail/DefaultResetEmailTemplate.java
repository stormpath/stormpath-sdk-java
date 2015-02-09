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
import com.stormpath.sdk.impl.resource.MapProperty;
import com.stormpath.sdk.impl.resource.Property;
import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.lang.Collections;
import com.stormpath.sdk.mail.ResetEmailTemplate;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @since 1.0.0
 */
public class DefaultResetEmailTemplate extends AbstractEmailTemplate<ResetEmailTemplate> implements ResetEmailTemplate {

    // SIMPLE PROPERTIES
    static final MapProperty DEFAULT_MODEL = new MapProperty("defaultModel");

    //Internal Constant
    static final String LINK_BASE_URL = "linkBaseUrl";

    private static final Map<String, Property> PROPERTY_DESCRIPTORS = createPropertyDescriptorMap(
            NAME, DESCRIPTION, FROM_NAME, FROM_EMAIL_ADDRESS, SUBJECT, TEXT_BODY, HTML_BODY, MIME_TYPE, DEFAULT_MODEL);

    public DefaultResetEmailTemplate(InternalDataStore dataStore) {
        super(dataStore);
    }

    public DefaultResetEmailTemplate(InternalDataStore dataStore, Map<String, Object> properties) {
        super(dataStore, properties);
    }

    @Override
    public Map<String, Property> getPropertyDescriptors() {
        return PROPERTY_DESCRIPTORS;
    }

    @Override
    public Map<String, String> getDefaultModel() {
        Map<String, String> defaultModel = getDefaultModel(false);
        //Let's avoid the defaultModel to be modified. If the linkBaseUrl property needs to
        //be modified it can be done via the setLinkBaseUrl(String) method
        return defaultModel != null ? java.util.Collections.unmodifiableMap(defaultModel) : null;
    }

    @Override
    public ResetEmailTemplate setLinkBaseUrl(String linkBaseUrl) {
        Assert.hasText(linkBaseUrl, "linkBaseUrl cannot be null or empty");
        Map<String, String> defaultModel = getDefaultModel(true);
        defaultModel.put(LINK_BASE_URL, linkBaseUrl);
        setProperty(DEFAULT_MODEL, defaultModel);
        return this;
    }

    @Override
    public String getLinkBaseUrl() {
        Map<String, String> defaultModel = getDefaultModel(false);

        if(Collections.isEmpty(defaultModel)){
            return null;
        }
        return defaultModel.get(LINK_BASE_URL);
    }

    private Map<String, String> getDefaultModel(boolean createIfNotExist){
        Map<String, String> defaultModel = getMap(DEFAULT_MODEL);

        if(defaultModel == null && createIfNotExist){
            defaultModel = new LinkedHashMap<String, String>();
        }
        return defaultModel;
    }

}
