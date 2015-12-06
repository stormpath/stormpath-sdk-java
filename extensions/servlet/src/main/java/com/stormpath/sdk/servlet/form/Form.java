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

import java.util.List;

/**
 * Very simple form model object to be used in a view's data model for MVC-based controllers.
 *
 * @since 1.0.RC3
 */
public interface Form {

    /**
     * Returns the html action attribute value - the URI to where the form will be submitted.
     *
     * @return the html action attribute value - the URI to where the form will be submitted.
     */
    String getAction();

    /**
     * The name of CSRF token. Since the CSRF name is customizable, this method is used to obtain the actual token name being used in the form.
     *
     * @return the name of the CSRF field.
     * @since 1.0.RC5.2
     */
    String getCsrfTokenName();

    /**
     * Returns the CSRF token that must be included with the form during submission.  If the CSRF token submitted with
     * the form is missing, expired or has already been used, form submission will fail.
     *
     * @return the the CSRF token that must be included with the form during submission.
     */
    String getCsrfToken();

    /**
     * Returns the next URI that the user should visit after successful form submission or an empty or null value, which
     * indicates the controller processing form submission should execute suitable default behavior.
     * <p>If non-null/empty, the value is usually represented as a hidden form field since it is not relevant for users
     * filling out forms.  The hidden field just allows a stateless (sessionless) way of transferring state during form
     * submissions.</p>
     *
     * @return the next URI that the user should visit after successful form submission or an empty or null value, which
     * indicates the controller processing form submission should execute suitable default behavior.
     */
    String getNext();

    /**
     * Ensures that the first field is {@link com.stormpath.sdk.servlet.form.Field#isAutofocus() autofocus}ed if no
     * other field is already autofocused.
     */
    void autofocus();

    /**
     * Returns all fields in the form, both {@link #getVisibleFields() visible} and {@link #getHiddenFields() hidden}
     * fields.
     *
     * @return all fields in the form, both {@link #getVisibleFields() visible} and {@link #getHiddenFields() hidden}
     * fields.
     */
    List<Field> getFields();

    /**
     * Returns a list of all hidden fields in the form, or an empty list if there are no hidden fields.
     *
     * @return a list of all hidden fields in the form, or an empty list if there are no hidden fields.
     */
    List<Field> getHiddenFields();

    /**
     * Returns a list of all visible (non-hidden) fields in the form.
     *
     * @return a list of all visible (non-hidden) fields in the form.
     */
    List<Field> getVisibleFields();

    /**
     * Returns the form field with the specified name or {@code null} if there is no field with that name.
     *
     * @param name the form field name
     * @return the form field with the specified name or {@code null} if there is no field with that name.
     */
    Field getField(String name);

    /**
     * Returns the value of the named form field, or {@code null} if there is no field with that name, or if the field
     * value is null.
     *
     * @param fieldName form field name
     * @return the value of the named form field, or {@code null} if there is no field with that name, or if the field
     * value is null.
     */
    String getFieldValue(String fieldName);
}
