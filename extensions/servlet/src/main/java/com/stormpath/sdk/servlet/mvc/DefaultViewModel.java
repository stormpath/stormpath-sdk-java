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

import java.util.Map;

public class DefaultViewModel implements ViewModel {

    private final String viewName;
    private final Map<String,?> model;

    private boolean redirect = false;

    public DefaultViewModel() {
        this.viewName = null;
        this.model = null;
    }

    public DefaultViewModel(String viewName) {
        this.viewName = viewName;
        this.model = null;
    }

    public DefaultViewModel(Map<String,?> model) {
        this.viewName = null;
        this.model = model;
    }

    public DefaultViewModel(String viewName, Map<String, ?> model) {
        this.viewName = viewName;
        this.model = model;
    }

    @Override
    public String getViewName() {
        return this.viewName;
    }

    @Override
    public Map<String, ?> getModel() {
        return this.model;
    }

    public boolean isRedirect() {
        return redirect;
    }

    public DefaultViewModel setRedirect(boolean redirect) {
        this.redirect = redirect;
        return this;
    }
}
