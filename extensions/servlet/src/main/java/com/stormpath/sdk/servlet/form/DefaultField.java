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

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @since 1.0.RC3
 */
public class DefaultField implements Field {
    private String name;
    private String value;
    private String label;
    private String placeholder;
    private boolean required;
    private String type;
    private boolean enabled;
    private boolean visible;

    public static Builder builder() {
        return new Builder();
    }

    public DefaultField(Builder builder) {
        this.name = builder.name;
        this.value = builder.value;
        this.label = builder.label;
        this.placeholder = builder.placeholder;
        this.required = builder.required;
        this.type = builder.type;
        this.enabled = builder.enabled;
        this.visible = builder.visible;
    }

    public DefaultField() {
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    @JsonIgnore
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    @Override
    public String getPlaceholder() {
        return placeholder;
    }

    public void setPlaceholder(String placeholder) {
        this.placeholder = placeholder;
    }

    @Override
    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    @Override
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @JsonIgnore
    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @JsonIgnore
    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public Field copy() {
        return builder()
                .setName(this.name)
                .setValue(this.value)
                .setLabel(this.label)
                .setPlaceholder(this.placeholder)
                .setRequired(this.required)
                .setType(this.type)
                .setVisible(this.visible)
                .setEnabled(this.enabled)
                .build();
    }

    public static class Builder {
        private String name;
        private String value;
        private String label;
        private String placeholder;
        private boolean required;
        private String type;
        private boolean enabled;
        private boolean visible;

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setValue(String value) {
            this.value = value;
            return this;
        }

        public Builder setLabel(String label) {
            this.label = label;
            return this;
        }

        public Builder setPlaceholder(String placeholder) {
            this.placeholder = placeholder;
            return this;
        }

        public Builder setRequired(boolean required) {
            this.required = required;
            return this;
        }

        public Builder setType(String type) {
            this.type = type;
            return this;
        }

        public Builder setEnabled(boolean enabled) {
            this.enabled = enabled;
            return this;
        }

        public Builder setVisible(boolean visible) {
            this.visible = visible;
            return this;
        }

        public DefaultField build() {
            return new DefaultField(this);
        }
    }
}
