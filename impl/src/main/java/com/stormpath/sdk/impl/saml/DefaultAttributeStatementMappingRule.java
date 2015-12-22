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
package com.stormpath.sdk.impl.saml;

import com.stormpath.sdk.impl.ds.InternalDataStore;
import com.stormpath.sdk.impl.resource.AbstractInstanceResource;
import com.stormpath.sdk.impl.resource.Property;
import com.stormpath.sdk.saml.AttributeStatementMappingRule;

import java.util.Map;
import java.util.Set;

/**
 * @since 1.0.RC8
 */
public class DefaultAttributeStatementMappingRule extends AbstractInstanceResource implements AttributeStatementMappingRule {

    static final Map<String,Property> PROPERTY_DESCRIPTORS = createPropertyDescriptorMap();

    public DefaultAttributeStatementMappingRule(InternalDataStore dataStore) {
        super(dataStore);
    }

    public DefaultAttributeStatementMappingRule(InternalDataStore dataStore, Map<String, Object> properties) {
        super(dataStore, properties);
    }

    @Override
    public Map<String, Property> getPropertyDescriptors() {
        return PROPERTY_DESCRIPTORS;
    }

    @Override
    public AttributeStatementMappingRule create(String name, String... accountAttributeNames) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String getName() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Set<String> getAccountAttributeNames() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
