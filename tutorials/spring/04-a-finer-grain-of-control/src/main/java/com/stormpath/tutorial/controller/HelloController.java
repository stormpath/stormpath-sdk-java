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
package com.stormpath.tutorial.controller;

import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.directory.CustomData;
import com.stormpath.sdk.group.Group;
import com.stormpath.sdk.lang.Collections;
import com.stormpath.sdk.servlet.account.AccountResolver;
import com.stormpath.tutorial.service.HelloService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @since 1.3.0
 */
@Controller
public class HelloController {

    private AccountResolver accountResolver;
    private HelloService helloService;

    @Autowired
    public HelloController(AccountResolver accountResolver, HelloService helloService) {
        Assert.notNull(accountResolver);
        Assert.notNull(helloService);
        this.accountResolver = accountResolver;
        this.helloService = helloService;
    }

    @RequestMapping("/")
    String home(HttpServletRequest req, Model model) {
        model.addAttribute("status", req.getParameter("status"));
        return "home";
    }

    @RequestMapping("/userdetails")
    String userDetails(HttpServletRequest req, Model model) {
        Account account = accountResolver.getAccount(req);
        Map<String, List<String>> springSecurityPermissions = new HashMap<>();

        // groups
        List<Group> groups = new ArrayList<>();
        for (Group group : account.getGroups()) {
            groups.add(group);
            updateSpringSecurityPermissionsMap(
                "group:" + group.getName(), springSecurityPermissions, group.getCustomData()
            );
        }
        model.addAttribute("groups", groups);

        // perms
        updateSpringSecurityPermissionsMap("account", springSecurityPermissions, account.getCustomData());

        model.addAttribute("springSecurityPermissions", springSecurityPermissions);

        return "userdetails";
    }

    @RequestMapping("/restricted")
    String restricted(HttpServletRequest req, Model model) {
        String msg = helloService.sayHello(
            accountResolver.getAccount(req)
        );
        model.addAttribute("msg", msg);
        return "restricted";
    }

    @SuppressWarnings("unchecked")
    private void updateSpringSecurityPermissionsMap(
        String key, Map<String, List<String>> springSecurityPermissions, CustomData customData
    ) {
        List<String> springSecurityPermissionsList = (List<String>) customData.get("springSecurityPermissions");
        if (!Collections.isEmpty(springSecurityPermissionsList)) {
            springSecurityPermissions.put(key, springSecurityPermissionsList);
        }
    }
}