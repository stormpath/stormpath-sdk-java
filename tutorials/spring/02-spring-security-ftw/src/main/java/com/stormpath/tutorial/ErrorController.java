package com.stormpath.tutorial;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashMap;
import java.util.Map;

@Controller
public class ErrorController {

    @RequestMapping("/403")
    public String forbidden(Model model) {
        Map<String, String> errors = new HashMap<>();
        errors.put("status", "403");
        errors.put("message", "Access is Denied");

        model.addAttribute("errors", errors);

        return "error";
    }
}
