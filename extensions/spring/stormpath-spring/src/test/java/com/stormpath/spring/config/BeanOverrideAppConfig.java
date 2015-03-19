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
package com.stormpath.spring.config;

import com.stormpath.sdk.application.Application;
import com.stormpath.sdk.client.Client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableStormpath
public class BeanOverrideAppConfig {

    @Autowired
    private Client client;

    @Bean
    public Application stormpathApplication() {

        //purposefully return the Stormpath admin console app.
        //Real apps would never do this, but this is an easy way to test that bean overrides work:

        for (Application app : client.getApplications()) {
            if (app.getName().equalsIgnoreCase("Stormpath")) { //return the admin app
                return app;
            }
        }

        throw new IllegalStateException("Stormpath application is always available in Stormpath.");
    }

}
