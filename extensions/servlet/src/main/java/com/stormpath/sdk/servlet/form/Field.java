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

/**
 * A {@link Form form} field, to be used when rendering views and processing form submissions.
 *
 * @since 1.0.RC3
 */
public interface Field {

    /**
     * The form field name.
     *
     * @return form field name.
     */
    String getName();

    /**
     * The form field value.
     *
     * @return form field value.
     */
    String getValue();

    void setValue(String value);

    /**
     * Form field label.  This value is usually an i18n message key instead of a raw language value.  This allows the
     * view rendering to reference a message library using the return value as an i18n key for internationalized
     * rendering.
     *
     * @return field label, usually an i18n message key for i18n-capable view rendering.
     */
    String getLabel();

    /**
     * Form field value placeholder text.  This value is usually an i18n message key instead of a raw language value.
     * This allows the view rendering to reference a message library using the return value as an i18n key for
     * internationalized rendering.
     *
     * @return field value placeholder text, usually an i18n message key for i18n-capable view rendering.
     */
    String getPlaceholder();

    /**
     * Returns {@code true} if the form field is required to be populated by the end user, {@code false} otherwise. When
     * rendered in an html form, this would enable the field element's <code>required=&quot;required&quot;</code>
     * attribute.
     *
     * @return {@code true} if the form field is required to be populated by the end user, {@code false} otherwise.
     */
    boolean isRequired();

    /**
     * Returns the form field type, for example, {@code text}, {@code password}, {@code hidden}, etc.
     *
     * @return the form field type, for example, {@code text}, {@code password}, {@code hidden}, etc.
     */
    String getType();

    boolean isVisible();

    boolean isEnable();

    Field copy();
}
