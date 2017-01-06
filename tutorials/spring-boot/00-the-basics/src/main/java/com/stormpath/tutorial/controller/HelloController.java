package com.stormpath.tutorial.controller;

import com.stormpath.sdk.application.Application;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
public class HelloController {

    @Autowired
    Application app;

    @RequestMapping("/")
    public String hello(HttpServletRequest req) {
        return "Hello, " + app.getName();
    }
}
