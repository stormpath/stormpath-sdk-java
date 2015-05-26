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

import com.stormpath.sdk.authc.AuthenticationResult;
import com.stormpath.sdk.servlet.filter.account.config.AuthenticationResultSaverFactory;
import com.stormpath.sdk.servlet.http.impl.StormpathHttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;

@Component
public class SpringSecurityWebMvcApplicationListener implements ApplicationListener {

//    @Autowired
//    private AuthenticationResultSaverFactory authenticationResultSaverFactory;
//
//    StormpathWebMvcConfiguration a;

    @Override
    public void onApplicationEvent(ApplicationEvent event) {


//        if (event.getSource() instanceof UsernamePasswordAuthenticationToken) {
//            authenticationResultSaverFactory.getInstance().set(null, null, (AuthenticationResult) event.getSource());
//        }
        System.out.println("aaa");
    }

}
