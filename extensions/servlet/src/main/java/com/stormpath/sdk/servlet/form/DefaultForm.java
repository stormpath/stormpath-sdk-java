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
    private final Map<String, Field> fields = new LinkedHashMap<String, Field>();

    public DefaultForm(Builder builder) {
        for (Field field : builder.fields) {
            fields.put(field.getName(), field);
        }
    }

    @Override
    public List<Field> getFields() {
        return new ArrayList<Field>(fields.values());
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

    @Override
    public void addField(Field field) {
        fields.put(field.getName(), field);
    }

    public static class Builder {
        List<Field> fields;

        public Builder setFields(List<Field> fields) {
            this.fields = fields;
            return this;
        }

        public DefaultForm build() {
            return new DefaultForm(this);
        }
    }
}
