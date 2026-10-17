package com.br.deliveryApi.controller;

import com.br.deliveryApi.dto.ClienteResponse;
import com.br.deliveryApi.dto.LoginRequest;
import com.br.deliveryApi.dto.RegisterRequest;
import com.br.deliveryApi.dto.TokenResponse;
import com.br.deliveryApi.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService service;

    public AuthController(AuthService service) {
        this.service = service;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public ClienteResponse registrar(@Valid @RequestBody RegisterRequest request) {
        return service.registrar(request);
    }

    @PostMapping("/login")
    public TokenResponse login(@Valid @RequestBody LoginRequest request) {
        return service.login(request);
    }
}
