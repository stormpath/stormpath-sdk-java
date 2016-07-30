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
package com.stormpath.spring.mvc;

import com.stormpath.sdk.servlet.mvc.AbstractControllerConfig;
import com.stormpath.spring.config.AbstractStormpathWebMvcConfiguration;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.ConfigurableEnvironment;

import java.util.Set;

/**
 * @since 1.0.0
 */
abstract class AbstractSpringControllerConfig extends AbstractControllerConfig implements InitializingBean {

    @Autowired
    protected ConfigurableEnvironment env;

    public AbstractSpringControllerConfig(String controllerKey) {
        super(controllerKey);
    }

    @Override
    public void afterPropertiesSet() throws Exception {

        setPropertyResolver(new AbstractPropertyResolver() {
            @Override
            public String getValue(String key) {
                return env.getProperty(key);
            }

            @Override
            public Set<String> getKeys(String prefix) {
                return AbstractStormpathWebMvcConfiguration.getPropertiesStartingWith(env, prefix).keySet();
            }
        });

        super.init();
    }
}
