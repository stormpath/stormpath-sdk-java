/*
 * Copyright 2017 Stormpath, Inc.
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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.authentication.logout.LogoutHandler;

/**
 * @since 1.3.0
 */
public class DisabledStormpathSecurityConfigurerAdapter extends AbstractStormpathSecurityConfigurerAdapter {

    @Autowired
    @Qualifier("stormpathLogoutHandler")
    protected LogoutHandler logoutHandler;

    @Override
    public void init(HttpSecurity http) throws Exception {
        if (stormpathWebEnabled) {
            if (csrfTokenEnabled) {
                //Since our Spring Securoty integration is disabled and we are using our own CSRF tokens then we want
                //to avoid our own pages to be validated by Spring Security, otherwise they will fail
                disableCsrf(loginUri, loginEnabled, http);
                disableCsrf(logoutUri, logoutEnabled, http);
                disableCsrf(forgotUri, forgotEnabled, http);
                disableCsrf(changeUri, changeEnabled, http);
                disableCsrf(registerUri, registerEnabled, http);
                disableCsrf(verifyUri, verifyEnabled, http);

            }
            http.logout().addLogoutHandler(logoutHandler);
        }
    }

    private void disableCsrf(String endpoint, boolean doDisable, HttpSecurity http) throws Exception {
        if (doDisable) {
            http.csrf().ignoringAntMatchers(endpoint);
        }
    }
}
