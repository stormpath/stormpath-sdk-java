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
package com.stormpath.sdk.servlet.mvc.provider;

import com.stormpath.sdk.provider.Provider;
import com.stormpath.sdk.servlet.form.Form;
import com.stormpath.sdk.servlet.mvc.ModelContext;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @since 1.0
 */
public class DefaultProviderModelContext implements ProviderModelContext {

    private final ModelContext parentContext;
    private final Provider provider;
    private final Map<String,Object> model;

    public DefaultProviderModelContext(ModelContext parentContext, Provider provider) {
        this.parentContext = parentContext;
        this.provider = provider;
        this.model = new LinkedHashMap<String,Object>();
    }

    @Override
    public Provider getProvider() {
        return this.provider;
    }

    @Override
    public HttpServletRequest getRequest() {
        return parentContext.getRequest();
    }

    @Override
    public HttpServletResponse getResponse() {
        return parentContext.getResponse();
    }

    @Override
    public Form getForm() {
        return parentContext.getForm();
    }

    @Override
    public List<String> getErrors() {
        return parentContext.getErrors();
    }

    @Override
    public Map<String, Object> getParentModel() {
        return parentContext.getModel();
    }

    @Override
    public Map<String, Object> getModel() {
        return this.model;
    }
}
