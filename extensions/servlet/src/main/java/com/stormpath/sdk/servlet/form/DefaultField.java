/*
 * Copyright 2014 Stormpath, Inc.
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

public class DefaultField implements Field {

    private String  name;
    private String  value;
    private String  label;
    private String  placeholder;
    private boolean required;
    private boolean autofocus;
    private String  type;

    @Override
    public String getName() {
        return name;
    }

    public DefaultField setName(String name) {
        this.name = name;
        return this;
    }

    @Override
    public String getValue() {
        return value;
    }

    public DefaultField setValue(String value) {
        this.value = value;
        return this;
    }

    @Override
    public String getLabel() {
        return label;
    }

    public DefaultField setLabel(String label) {
        this.label = label;
        return this;
    }

    @Override
    public String getPlaceholder() {
        return placeholder;
    }

    public DefaultField setPlaceholder(String placeholder) {
        this.placeholder = placeholder;
        return this;
    }

    @Override
    public boolean isRequired() {
        return required;
    }

    public DefaultField setRequired(boolean required) {
        this.required = required;
        return this;
    }

    @Override
    public boolean isAutofocus() {
        return autofocus;
    }

    public DefaultField setAutofocus(boolean autofocus) {
        this.autofocus = autofocus;
        return this;
    }

    @Override
    public String getType() {
        return type;
    }

    public DefaultField setType(String type) {
        this.type = type;
        return this;
    }
}
