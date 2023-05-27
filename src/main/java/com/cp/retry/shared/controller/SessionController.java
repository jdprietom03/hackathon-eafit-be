package com.cp.retry.shared.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cp.retry.shared.dto.UserCredentials;
import com.cp.retry.shared.services.AuthenticationService;

@RestController
@RequestMapping("/auth")
public class SessionController {

    @Autowired
    private AuthenticationService authenticationService;

    @GetMapping("/login")
    public Object loginUser(UserCredentials userCredentials) {
        return authenticationService.login(userCredentials);
    }
}
