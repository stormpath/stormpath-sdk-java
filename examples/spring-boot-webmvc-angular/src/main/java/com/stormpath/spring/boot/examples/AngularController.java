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
