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

package com.stormpath.sdk.impl.ds;

import com.stormpath.sdk.directory.CustomData;
import com.stormpath.sdk.impl.authc.BasicLoginAttempt;
import com.stormpath.sdk.impl.resource.AbstractResource;
import com.stormpath.sdk.impl.resource.ReferenceFactory;
import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.lang.Collections;
import com.stormpath.sdk.mail.ModeledEmailTemplate;
import com.stormpath.sdk.provider.Provider;
import com.stormpath.sdk.provider.ProviderData;
import com.stormpath.sdk.resource.Resource;
import com.stormpath.sdk.saml.AttributeStatementMappingRules;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @since 1.1.0
 */
public class DefaultResourceConverter implements ResourceConverter {

    private final ReferenceFactory referenceFactory;

    public DefaultResourceConverter(ReferenceFactory referenceFactory) {
        Assert.notNull(referenceFactory, "referenceFactory cannot be null.");
        this.referenceFactory = referenceFactory;
    }

    @Override
    public Map<String, Object> convert(AbstractResource resource) {
        Assert.notNull(resource, "resource cannot be null.");
        return toMap(resource, true);
    }

    private LinkedHashMap<String, Object> toMap(final AbstractResource resource, boolean partialUpdate) {

        Set<String> propNames;

        if (partialUpdate) {
            propNames = resource.getUpdatedPropertyNames();
        } else {
            propNames = resource.getPropertyNames();
        }

        LinkedHashMap<String, Object> props = new LinkedHashMap<>(propNames.size());

        for (String propName : propNames) {
            Object value = resource.getProperty(propName);
            value = toMapValue(resource, propName, value, partialUpdate);
            props.put(propName, value);
        }

        return props;
    }

    //since 0.9.2
    private Object toMapValue(final AbstractResource resource, final String propName, Object value,
                              boolean partialUpdate) {
        if (resource instanceof CustomData) {
            //no sanitization: CustomData resources retain their values as-is:
            return value;
        }

        if (value instanceof CustomData || value instanceof ProviderData || value instanceof Provider) {
            if (partialUpdate) {
                Assert.isInstanceOf(AbstractResource.class, value);

                AbstractResource abstractResource = (AbstractResource) value;
                Set<String> updatedPropertyNames = abstractResource.getUpdatedPropertyNames();

                LinkedHashMap<String, Object> properties =
                    new LinkedHashMap<>(Collections.size(updatedPropertyNames));

                for (String updatedCustomPropertyName : updatedPropertyNames) {
                    Object object = abstractResource.getProperty(updatedCustomPropertyName);
                    if (object instanceof AttributeStatementMappingRules){
                        object = convertAttributeStatementMappingRulesToMap(object);
                    }
                    properties.put(updatedCustomPropertyName, object);
                }

                value = properties;
            }

            return value;
        }

        if (value instanceof Map) {
            //Since defaultModel is a map, the DataStore thinks it is a Resource. This causes the code to crash later one as Resources
            //do need to have an href property
            if ((resource instanceof ModeledEmailTemplate && propName.equals("defaultModel")) ||
                    //Check for accountStore in BasicLoginAttempt cause we change it to map to accommodate the organization name key use case
                    //see https://github.com/stormpath/stormpath-sdk-java/issues/284
                    resource instanceof BasicLoginAttempt && propName.equals("accountStore")) {
                return value;
            } else {
                //if the property is a reference, don't write the entire object - just the href will do:
                //TODO need to change this to write the entire object because this code defeats the purpose of entity expansion
                //     when this code gets called (returning the reference instead of the whole object that is returned from Stormpath)
                if(AbstractResource.hasHref((Map) value)) {
                    return this.referenceFactory.createReference(propName, (Map) value);
                }
                else{
                    return this.referenceFactory.createUnmaterializedReference(propName, (Map) value);
                }
            }
        }

        if (value instanceof Resource) {
            if(((Resource)value).getHref() != null) {
                return this.referenceFactory.createReference(propName, (Resource) value);
            }
            else{
                return this.referenceFactory.createUnmaterializedReference(propName, (Resource) value);
            }
        }

        return value;
    }

    /**
     * @since 1.0.RC8
     */
    private Object convertAttributeStatementMappingRulesToMap(Object object){

        Set<Object> elementsSet = (Set<Object>) object;
        Map<String, List<Object>> map = new HashMap<String, List<Object>>();

        List<Object> list = Collections.toList(elementsSet.toArray());

        map.put("items", list);
        return map;
    }
}
