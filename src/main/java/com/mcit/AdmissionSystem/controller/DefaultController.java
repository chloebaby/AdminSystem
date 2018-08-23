package com.mcit.AdmissionSystem.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DefaultController {

    private static final Logger log = LoggerFactory.getLogger(DefaultController.class);

    @GetMapping("/403")
    public String error403() {
        log.info("/403 called");
        return "/error/403";
    }

    @GetMapping("/")
    public String index() {
        log.info("/ called - redirecting to /login");
        return "/login";
    }

}
