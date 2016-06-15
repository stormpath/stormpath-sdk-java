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
package com.stormpath.sdk.servlet.filter;

import com.stormpath.sdk.servlet.config.Config;
import com.stormpath.sdk.servlet.config.impl.ConfigReader;

/**
 * Created by mzumbado on 6/14/16.
 */
public class ChangePasswordServletControllerConfigResolver extends ServletControllerConfigResolver implements ChangePasswordConfigResolver {

    public ChangePasswordServletControllerConfigResolver(Config config, ConfigReader configReader, String controllerKey) {
        super(config, configReader, controllerKey);
    }

    public String getErrorUri() {
        return configReader.getString("stormpath.web." + getControllerKey() + ".errorUri");
    }

    public boolean isAutoLogin() {
        return configReader.getBoolean("stormpath.web." + getControllerKey() + ".autoLogin");
    }
}
