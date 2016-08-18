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

import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.servlet.account.AccountResolver;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
public class ProfileController {

    @RequestMapping(path = "/profile", method = POST)
    public void profile(HttpServletRequest req, HttpServletResponse res, @RequestBody Map<String, String> params) {

        Account account = AccountResolver.INSTANCE.getAccount(req);

        if (account != null) {
            account.setGivenName(params.get("givenName"));
            account.setSurname(params.get("surname"));
            account.getCustomData().put("favoriteColor", params.get("favoriteColor"));
            account.save();
        }

        res.setStatus(200);
    }
}
