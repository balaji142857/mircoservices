package com.bk.ms.authn.web;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@Slf4j
public class TestHarness {


    @GetMapping("/test")
    public String test(Principal principal) {
        log.info("Received request for test endpoint, {}", principal);
        return "Hello "+ (null != principal ? principal.getName() : "anonymous");
    }


    @GetMapping("/admin/test")
    public String adminTest(Principal principal) {
        log.info("Received request for admin endpoint, {}", principal);
        return "Hello Admin: "+principal.getName();
    }

    @GetMapping("/user/test")
    public String userTest(Principal principal) {
        log.info("Received request for user endpoint, {}", principal);
        return "Hello User:"+principal.getName();
    }



}

