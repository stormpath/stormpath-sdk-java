package com.stormpath.tutorial.controller;

import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.servlet.account.AccountResolver;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@RestController
public class HelloController {
    @RequestMapping(value="/info", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, String> info(HttpServletRequest req) {
        Account account = AccountResolver.INSTANCE.getAccount(req);

        Map<String, String> retMap = new HashMap<String, String>();
        retMap.put("href", account.getHref());
        retMap.put("email", account.getEmail());
        retMap.put("fullName", account.getFullName());

        return retMap;
    }
}
