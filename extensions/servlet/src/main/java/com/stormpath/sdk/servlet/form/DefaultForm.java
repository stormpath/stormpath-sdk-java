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
package com.stormpath.sdk.servlet.form;

import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.lang.Strings;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @since 1.0.RC3
 */
public class DefaultForm implements Form {

    public static final String HIDDEN_FIELD_TYPE = "hidden";

    private static final String DEFAULT_FIELD_IMPL_REQD_MESSAGE = "The ensureCsrfTokenField method requires that the " +
            "csrf token field be a " + DefaultField.class.getName() + " instance.";

    private String action;

    private String csrfTokenName;

    private String next;

    private Map<String, Field> fields;

    public DefaultForm() {
        this.fields = new LinkedHashMap<String, Field>();
    }

    @Override
    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    @Override
    public String getCsrfTokenName() {
        return csrfTokenName;
    }

    /**
     * The name of CSRF token. This name is used to customize the token field name in the form.
     *
     * @return the name of the CSRF field.
     * @since 1.0.RC5.2
     */
    public DefaultForm setCsrfTokenName(String csrfTokenName) {
        this.csrfTokenName = csrfTokenName;
        return this;
    }

    @Override
    public String getCsrfToken() {
        Field field = getField(csrfTokenName);
        if (field == null) {
            return null;
        }
        return ensureCsrfTokenField().getValue();
    }

    public DefaultForm setCsrfToken(String csrfToken) {
        DefaultField field = ensureCsrfTokenField();
        field.setValue(csrfToken);
        return this;
    }

    protected DefaultField ensureCsrfTokenField() {
        Field field = getField(csrfTokenName);
        if (field == null) {
            field = new DefaultField().setName(csrfTokenName).setType(HIDDEN_FIELD_TYPE);
            fields.put(csrfTokenName, field);
        }
        Assert.isInstanceOf(DefaultField.class, field, DEFAULT_FIELD_IMPL_REQD_MESSAGE);
        return (DefaultField) field;
    }

    @Override
    public void autofocus() {

        List<Field> fields = getVisibleFields();

        Field first = null;
        boolean autofocusSet = false;

        for (Field field : fields) {
            if (first == null) {
                first = field;
            }
            if (field.isAutofocus()) {
                autofocusSet = true;
            }
        }

        if (!autofocusSet && first instanceof DefaultField) {
            ((DefaultField) first).setAutofocus(true);
        }
    }

    @Override
    public String getNext() {
        return next;
    }

    public DefaultForm setNext(String next) {
        this.next = next;
        return this;
    }

    @Override
    public List<Field> getFields() {
        return new ArrayList<Field>(fields.values());
    }

    @Override
    public List<Field> getHiddenFields() {
        List<Field> hiddenFields = new ArrayList<Field>(5);

        for (Field field : fields.values()) {
            if (HIDDEN_FIELD_TYPE.equalsIgnoreCase(field.getType())) {
                hiddenFields.add(field);
            }
        }

        return hiddenFields;
    }

    @Override
    public List<Field> getVisibleFields() {
        List<Field> visibleFields = new ArrayList<Field>(fields.size());

        for (Field field : fields.values()) {
            if (!HIDDEN_FIELD_TYPE.equalsIgnoreCase(field.getType())) {
                visibleFields.add(field);
            }
        }

        return visibleFields;
    }

    @Override
    public Field getField(String name) {
        return fields.get(name);
    }

    @Override
    public String getFieldValue(String fieldName) {
        Field field = fields.get(fieldName);
        if (field != null) {
            return Strings.clean(field.getValue());
        }
        return null;
    }

    public DefaultForm addField(Field field) {
        this.fields.put(field.getName(), field);
        return this;
    }
}
