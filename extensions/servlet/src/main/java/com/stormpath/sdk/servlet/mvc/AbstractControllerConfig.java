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
package com.stormpath.sdk.servlet.mvc;

import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.lang.Collections;
import com.stormpath.sdk.lang.Strings;
import com.stormpath.sdk.servlet.filter.ControllerConfig;
import com.stormpath.sdk.servlet.form.DefaultField;
import com.stormpath.sdk.servlet.form.Field;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * @since 1.0.0
 */
public abstract class AbstractControllerConfig implements ControllerConfig {

    private final String controllerKey;
    private PropertyResolver propertyResolver;
    private List<Field> formFields;
    private List<String> defaultFieldNames = java.util.Collections.emptyList();
    private List<String> disabledFieldNames = java.util.Collections.emptyList();

    public AbstractControllerConfig(String controllerKey) {
        Assert.hasText(controllerKey, "controllerKey cannot be null or empty.");
        this.controllerKey = controllerKey;
    }

    public void init() {
        Assert.notNull(this.propertyResolver, "propertyResolver cannot be null.");
        this.formFields = createFormFields();
        Assert.notNull(this.formFields, "formFields cannot be null.  Use an empty list instead.");
        Assert.notNull(defaultFieldNames, "defaultFieldNames cannot be null.  Use an empty list instead.");
        Assert.notNull(disabledFieldNames, "disabledFieldNames cannot be null.  Use an empty list instead.");
    }

    @Override
    public String getControllerKey() {
        return this.controllerKey;
    }

    public void setPropertyResolver(PropertyResolver propertyResolver) {
        this.propertyResolver = propertyResolver;
    }

    public PropertyResolver getPropertyResolver() {
        return propertyResolver;
    }

    public void setDefaultFieldNames(String... defaultFieldNames) {
        this.defaultFieldNames = Collections.toList(defaultFieldNames);
    }

    public void setDisabledFieldNames(String... disabledFieldNames) {
        this.disabledFieldNames = Collections.toList(disabledFieldNames);
    }

    @Override
    public List<Field> getFormFields() {
        return this.formFields;
    }

    protected List<String> getDefaultFieldNames() {
        return this.defaultFieldNames;
    }

    protected List getDisabledFieldNames() {
        return this.disabledFieldNames;
    }

    private List<Field> createFormFields() {

        List<Field> fields = new ArrayList<>();

        List<String> fieldNames = new ArrayList<>(getFormFieldNames());

        for (String fieldName : fieldNames) {

            String name = Strings.trimAllWhitespace(fieldName);

            Field field = DefaultField.builder()
                .setName(name)
                .setType(getPropValue(name, "type", getFieldType(name)))
                .setLabel(getPropValue(name, "label", getFieldPropertyKey(name, "label")))
                .setPlaceholder(getPropValue(name, "placeholder", getFieldPropertyKey(name, "placeholder")))
                .setRequired(getPropBooleanValue(name, "required", true))
                .setEnabled(getPropBooleanValue(name, "enabled", !getDisabledFieldNames().contains(name)))
                .setVisible(getPropBooleanValue(name, "visible", !"sptoken".equals(name)))
                .build();

            fields.add(field);
        }

        return fields;
    }

    private String getPropValue(String fieldName, String propName, String defaultValue) {
        String key = getFieldPropertyKey(fieldName, propName);
        return propertyResolver.getValue(key, defaultValue);
    }

    private boolean getPropBooleanValue(String fieldName, String propName, boolean defaultValue) {
        return Boolean.valueOf(getPropValue(fieldName, propName, String.valueOf(defaultValue)));
    }

    private List<String> getFormFieldNames() {

        List<String> fieldNames = propertyResolver.getValues(getFormKey("fieldOrder"), getDefaultFieldNames());
        //returned propertyResolver value could be immutable and we might need to append to the list, so wrap it:
        fieldNames = new ArrayList<>(fieldNames);

        String fieldConfigPrefix = getFieldConfigPrefix();

        //Find any other fields that are not in the fieldOrder prop and add them to the end of the list as define in the spec
        Set<String> keys = propertyResolver.getKeys(getFieldConfigPrefix());

        for (String key : keys) {

            String fieldName = key.substring(fieldConfigPrefix.length() + 1);

            //the token before any first period is the actual field name:
            int i = fieldName.indexOf('.');
            if (i > 0) {
                fieldName = fieldName.substring(0, i);
            }
            if (!"fieldOrder".equals(fieldName) && !fieldNames.contains(fieldName)) {
                fieldNames.add(fieldName);
            }
        }

        return fieldNames;
    }

    protected String getConfigPrefix() {
        return "stormpath.web." + getControllerKey();
    }

    protected String getFormConfigPrefix() {
        return getConfigPrefix() + ".form";
    }

    protected String getFieldConfigPrefix() {
        return getFormConfigPrefix() + ".fields";
    }

    protected String getFormKey(String fieldName) {
        return getFormConfigPrefix() + "." + fieldName;
    }

    protected String getFieldKey(String fieldName) {
        return getFieldConfigPrefix() + "." + fieldName;
    }

    protected String getFieldPropertyKey(String fieldName, String propertyName) {
        return getFieldKey(fieldName) + "." + propertyName;
    }

    private String getFieldType(String fieldName) {
        Assert.hasText(fieldName, "fieldName argument cannot be null or empty.");
        String name = fieldName.toLowerCase(Locale.ENGLISH);

        if (name.equals("sptoken")) {
            return "hidden";
        } else if (name.contains("password")) {
            return "password";
        } else if (name.contains("email")) {
            return "email";
        } else {
            return "text";
        }
    }

    public interface PropertyResolver {

        String getValue(String key);

        String getValue(String key, Object defaultValue);

        List<String> getValues(String key, List<String> defaultValues);

        Set<String> getKeys(String prefix);
    }

    public abstract class AbstractPropertyResolver implements PropertyResolver {

        @Override
        public String getValue(String key, Object defaultValue) {
            String value = getValue(key);
            if (value == null && defaultValue != null) {
                value = String.valueOf(defaultValue);
            }
            return value;
        }

        @Override
        public List<String> getValues(String key, List<String> defaultValues) {
            String value = getValue(key);
            if (Strings.hasText(value)) {
                return new ArrayList<>(Strings.commaDelimitedListToSet(value));
            }
            return defaultValues;
        }
    }

}
