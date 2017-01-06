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
package com.stormpath.spring.examples;

import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.servlet.account.AccountResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

/**
 * @since 1.0.RC5
 */
@RestController
public class HelloController {

    @Autowired
    private HelloService helloService;

    @Autowired
    AccountResolver accountResolver;

    @RequestMapping("/")
    ModelAndView home(HttpServletRequest request) {

        String name = "World";

        Account account = accountResolver.getAccount(request);
        if (account != null) {
            name = account.getGivenName();
        }

        ModelAndView mav = new ModelAndView("home");
        mav.addObject("message", "Hello " + name + "!");
        return mav;
    }

    @RequestMapping("/restricted")
    ModelAndView restricted(HttpServletRequest request) {
        String helloMessage = helloService.sayHello(accountResolver.getAccount(request));
        ModelAndView mav = new ModelAndView("restricted");
        mav.addObject("message", helloMessage);
        return mav;
    }
}
