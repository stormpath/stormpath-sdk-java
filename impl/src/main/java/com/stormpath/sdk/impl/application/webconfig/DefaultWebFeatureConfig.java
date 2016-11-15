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
package com.stormpath.sdk.impl.application.webconfig;

import com.stormpath.sdk.application.webconfig.ChangePasswordConfig;
import com.stormpath.sdk.application.webconfig.ForgotPasswordConfig;
import com.stormpath.sdk.application.webconfig.LoginConfig;
import com.stormpath.sdk.application.webconfig.RegisterConfig;
import com.stormpath.sdk.application.webconfig.VerifyEmailConfig;
import com.stormpath.sdk.application.webconfig.WebFeatureConfig;
import com.stormpath.sdk.impl.application.ConfigurableProperty;
import com.stormpath.sdk.impl.resource.AbstractPropertyRetriever;
import com.stormpath.sdk.impl.resource.BooleanProperty;

import java.util.Map;

/**
 * @since 1.2.0
 */
public class DefaultWebFeatureConfig<T extends WebFeatureConfig<T>> extends ConfigurableProperty implements WebFeatureConfig<T> {

    private static BooleanProperty ENABLED = new BooleanProperty("enabled");

    public DefaultWebFeatureConfig(String name, Map<String, Object> properties, AbstractPropertyRetriever parent) {
        super(name, properties, parent);
    }

    @Override
    public Boolean isEnabled() {
        return getNullableBoolean(ENABLED);
    }

    public T setEnabled(Boolean enabled) {
        setProperty(ENABLED, enabled);
        return (T) this;
    }

    public static class Register extends DefaultWebFeatureConfig<RegisterConfig> implements RegisterConfig {
        public Register(String name, Map<String, Object> properties, AbstractPropertyRetriever parent) {
            super(name, properties, parent);
        }
    }

    public static class Login extends DefaultWebFeatureConfig<LoginConfig> implements LoginConfig {
        public Login(String name, Map<String, Object> properties, AbstractPropertyRetriever parent) {
            super(name, properties, parent);
        }
    }

    public static class ChangePassword extends DefaultWebFeatureConfig<ChangePasswordConfig> implements ChangePasswordConfig {
        public ChangePassword(String name, Map<String, Object> properties, AbstractPropertyRetriever parent) {
            super(name, properties, parent);
        }
    }

    public static class ForgotPassword extends DefaultWebFeatureConfig<ForgotPasswordConfig> implements ForgotPasswordConfig {
        public ForgotPassword(String name, Map<String, Object> properties, AbstractPropertyRetriever parent) {
            super(name, properties, parent);
        }
    }

    public static class VerifyEmail extends DefaultWebFeatureConfig<VerifyEmailConfig> implements VerifyEmailConfig {
        public VerifyEmail(String name, Map<String, Object> properties, AbstractPropertyRetriever parent) {
            super(name, properties, parent);
        }
    }

}
