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
package com.stormpath.spring.errors;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * DTO for transfering error status with a list of field errors.
 *
 * Copied with much respect from JHipster: https://github.com/jhipster/generator-jhipster/tree/master/generators/server/templates/src/main/java/package/web/rest/errors
 *
 * @since 1.0.0
 */
public class Error implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String status;
    private final String message;

    private List<FieldError> fieldErrors;

    public Error(String status) {
        this(status, null);
    }

    public Error(String status, String message) {
        this.status = status;
        this.message = message;
    }

    public Error(String status, String message, List<FieldError> fieldErrors) {
        this.status = status;
        this.message = message;
        this.fieldErrors = fieldErrors;
    }

    public void add(String objectName, String field, String message) {
        if (fieldErrors == null) {
            fieldErrors = new ArrayList<>();
        }
        fieldErrors.add(new FieldError(objectName, field, message));
    }

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public List<FieldError> getFieldErrors() {
        return fieldErrors;
    }
}