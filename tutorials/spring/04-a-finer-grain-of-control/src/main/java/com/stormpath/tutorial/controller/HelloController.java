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
package com.stormpath.tutorial.controller;

import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.group.Group;
import com.stormpath.sdk.servlet.account.AccountResolver;
import com.stormpath.spring.security.authz.permission.Permission;
import com.stormpath.spring.security.provider.AccountPermissionResolver;
import com.stormpath.spring.security.provider.GroupPermissionResolver;
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
import java.util.Set;

/**
 * @since 1.3.0
 */
@Controller
public class HelloController {

    private AccountResolver accountResolver;
    private GroupPermissionResolver stormpathGroupPermissionResolver;
    private AccountPermissionResolver stormpathAccountPermissionResolver;
    private HelloService helloService;

    @Autowired
    public HelloController(
        AccountResolver accountResolver, GroupPermissionResolver stormpathGroupPermissionResolver,
        AccountPermissionResolver stormpathAccountPermissionResolver, HelloService helloService
    ) {
        Assert.notNull(accountResolver);
        Assert.notNull(stormpathAccountPermissionResolver);
        Assert.notNull(stormpathGroupPermissionResolver);
        Assert.notNull(helloService);
        this.accountResolver = accountResolver;
        this.stormpathAccountPermissionResolver = stormpathAccountPermissionResolver;
        this.stormpathGroupPermissionResolver = stormpathGroupPermissionResolver;
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
        Map<String, Set<Permission>> springSecurityPermissions = new HashMap<>();

        // groups & group perms
        List<Group> groups = new ArrayList<>();
        for (Group group : account.getGroups()) {
            groups.add(group);
            springSecurityPermissions.put(
                "group:" + group.getName(),
                stormpathGroupPermissionResolver.resolvePermissions(group)
            );
        }
        model.addAttribute("groups", groups);

        // account perms
        springSecurityPermissions.put(
            "account",
            stormpathAccountPermissionResolver.resolvePermissions(account)
        );
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
}