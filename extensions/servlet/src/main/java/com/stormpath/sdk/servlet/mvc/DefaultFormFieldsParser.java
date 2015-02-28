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
package com.stormpath.sdk.servlet.mvc;

import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.lang.Strings;
import com.stormpath.sdk.servlet.form.DefaultField;
import com.stormpath.sdk.servlet.form.Field;

import java.util.ArrayList;
import java.util.List;

public class DefaultFormFieldsParser implements FormFieldParser {

    private final String CONFIG_PROPERTY_NAME;
    private final String CONFIG_PROPERTY_NAME_PREFIX;

    public DefaultFormFieldsParser(String configPropertyName) {
        Assert.hasText(configPropertyName, "configPropertyName argument cannot be null or empty.");
        this.CONFIG_PROPERTY_NAME = configPropertyName;
        this.CONFIG_PROPERTY_NAME_PREFIX = configPropertyName + ".";
    }

    @Override
    public List<Field> parse(String fieldsDefinition) throws IllegalArgumentException {

        Assert.hasText(fieldsDefinition, "fieldsDefinition argument cannot be null or empty.");

        try {
            List<Field> fields = doParse(fieldsDefinition);
            Assert.notEmpty(fields, CONFIG_PROPERTY_NAME + " value [" + fieldsDefinition + "] did not result in any fields.");
            return fields;
        } catch (Exception e) {
            String msg = "Unable to parse " + CONFIG_PROPERTY_NAME + " property value: " + e.getMessage();
            throw new IllegalArgumentException(msg, e);
        }
    }

    private List<Field> doParse(String val) {

        String[] vals = Strings.split(val, ',', '(', ')', true, true);

        if (vals == null || vals.length == 0) {
            throw new IllegalArgumentException("Invalid " + CONFIG_PROPERTY_NAME + " definition value: " + val);
        }

        List<Field> fields = new ArrayList<Field>(vals.length);

        for(String token : vals) {

            String fieldName = token;
            boolean required = false;
            String type = "text";

            int i = token.indexOf('(');

            if (i != -1) {

                fieldName = token.substring(0, i);

                String inner = token.substring(i + 1, token.length() - 1);

                if (inner.contains("required")) {
                    required = true;
                }
                if (inner.contains("password")) {
                    type = "password";
                }
            }

            String label = CONFIG_PROPERTY_NAME_PREFIX + fieldName + ".label";
            String placeholder = CONFIG_PROPERTY_NAME_PREFIX + fieldName + ".placeholder";

            DefaultField field = new DefaultField();
            field.setName(fieldName);
            field.setLabel(label);
            field.setPlaceholder(placeholder);
            field.setRequired(required);
            field.setType(type);
            fields.add(field);
        }

        return fields;
    }
}
