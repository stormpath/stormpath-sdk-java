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

import com.stormpath.sdk.servlet.form.Field;

import java.util.List;

/**
 * A form field parser can read a text string definition of what form fields should exist and return a list of fields
 * that reflects that string definition.
 *
 * @since 1.0.RC4
 * @deprecated since 1.0.0
 */
@Deprecated
public interface FormFieldParser {

    /**
     * Returns a list of fields that reflect the specified string definition.
     *
     * @param fieldsDefinition the definition of the fields to create
     * @return a list of fields that reflect the specified string definition.
     */
    List<Field> parse(String fieldsDefinition);
}
