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
import com.stormpath.sdk.impl.resource.SetProperty;
import com.stormpath.sdk.lang.Collections;
import com.stormpath.sdk.saml.AttributeStatementMappingRule;
import com.stormpath.sdk.saml.AttributeStatementMappingRules;

import java.util.*;

/**
 * @since 1.0.RC8
 */
public class DefaultAttributeStatementMappingRules extends AbstractInstanceResource implements AttributeStatementMappingRules {

    private static final SetProperty<AttributeStatementMappingRule> ITEMS = new SetProperty<AttributeStatementMappingRule>("items", AttributeStatementMappingRule.class);

    private static final String ITEMS_PROPERTY_NAME = "ITEMS";

    static final Map<String, Property> PROPERTY_DESCRIPTORS = createPropertyDescriptorMap(ITEMS);

    public DefaultAttributeStatementMappingRules(InternalDataStore dataStore) {
        super(dataStore);
    }

    public DefaultAttributeStatementMappingRules(InternalDataStore dataStore, Map<String, Object> properties) {
        super(dataStore, properties);
    }

    @Override
    public Map<String, Property> getPropertyDescriptors() {
        return PROPERTY_DESCRIPTORS;
    }

    @SuppressWarnings("unchecked")
    public Set<AttributeStatementMappingRule> getItems() {
        return getSetProperty(ITEMS_PROPERTY_NAME);
    }

    public void setItems(Set<AttributeStatementMappingRule> attributeStatementMappingRules) {
        setProperty(ITEMS, attributeStatementMappingRules);
    }

    @Override
    public Set<AttributeStatementMappingRule> removeByName(String... ruleNames) {
        if (ruleNames == null && ruleNames.length == 0) {
            return java.util.Collections.emptySet();
        }

        Set<AttributeStatementMappingRule> rules = nullSafeItems();

        Set<AttributeStatementMappingRule> removed = new HashSet<AttributeStatementMappingRule>();

        for (AttributeStatementMappingRule rule : rules) {
            for (String name : ruleNames) {
                if (rule.getName().equals(name)) {
                    removed.add(rule);
                    break;
                }
            }
        }

        nullSafeItems().removeAll(removed);

        return removed;
    }

    @Override
    public int size() {
        return nullSafeItems().size();
    }

    @Override
    public boolean isEmpty() {
        return nullSafeItems().isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return o != null && nullSafeItems().contains(o);
    }

    @Override
    public Iterator<AttributeStatementMappingRule> iterator() {
        return nullSafeItems().iterator();
    }

    @Override
    public Object[] toArray() {
        return nullSafeItems().toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return nullSafeItems().toArray(a);
    }

    @Override
    public boolean add(AttributeStatementMappingRule attributeStatementMappingRule) {
        return attributeStatementMappingRule != null && ensureItems().add(attributeStatementMappingRule);
    }

    @Override
    public boolean remove(Object o) {
        return o != null && nullSafeItems().remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return !Collections.isEmpty(c) && nullSafeItems().containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends AttributeStatementMappingRule> c) {
        return !Collections.isEmpty(c) && ensureItems().addAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return !Collections.isEmpty(c) && ensureItems().retainAll(c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return !Collections.isEmpty(c) && nullSafeItems().removeAll(c);
    }

    @Override
    public void clear() {
        nullSafeItems().clear();
    }

    private Set<AttributeStatementMappingRule> nullSafeItems() {
        Set<AttributeStatementMappingRule> items = getItems();
        if (items == null) {
            items = java.util.Collections.emptySet();
        }
        return items;
    }

    private Set<AttributeStatementMappingRule> ensureItems() {
        Set<AttributeStatementMappingRule> items = getItems();
        if (items == null) {
            items = new LinkedHashSet<AttributeStatementMappingRule>();
            setItems(items);
        }
        return items;
    }
}
