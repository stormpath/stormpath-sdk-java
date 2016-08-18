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
package com.stormpath.spring.boot.examples;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletResponse;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@Controller
public class AngularController {

    @RequestMapping(
        path = { "/profile", "/login", "/register", "/forgot", "/verify", "/change" },
        method = GET,
        headers = "Accept=text/html"
    )
    public String login(HttpServletResponse res) {
        res.setHeader("Cache-Control", "no-store, no-cache");
        res.setHeader("Pragma", "no-cache");

        return "forward:/";
    }
}
