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

import com.stormpath.sdk.servlet.form.Form;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * @since 1.0
 */
public class DefaultModelContext implements ModelContext {

    private final HttpServletRequest request;
    private final HttpServletResponse response;
    private final Form form;
    private final List<String> errors;
    private final Map<String,Object> model;

    public DefaultModelContext(HttpServletRequest request, HttpServletResponse response, Form form, List<String> errors, Map<String,Object> model) {
        this.request = request;
        this.response = response;
        this.form = form;
        this.errors = errors;
        this.model = model;
    }

    @Override
    public HttpServletRequest getRequest() {
        return this.request;
    }

    @Override
    public HttpServletResponse getResponse() {
        return this.response;
    }

    @Override
    public Form getForm() {
        return this.form;
    }

    @Override
    public List<String> getErrors() {
        return this.errors;
    }

    @Override
    public Map<String, Object> getModel() {
        return this.model;
    }
}
