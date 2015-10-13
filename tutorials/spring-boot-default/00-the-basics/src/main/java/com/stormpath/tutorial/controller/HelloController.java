package com.stormpath.tutorial.controller;

import com.stormpath.sdk.application.Application;
import com.stormpath.sdk.servlet.application.ApplicationResolver;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
public class HelloController {
    @RequestMapping("/")
    public String hello(HttpServletRequest req) {
        String greeting = "World!";

        Application app = ApplicationResolver.INSTANCE.getApplication(req);
        if (app != null) { greeting = app.getName(); }

        return "Hello, " + greeting;
    }
}
