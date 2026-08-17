package com.gustavo.sistemaencomendas.presentation.controller;

import com.gustavo.sistemaencomendas.infrastructure.security.JwtService;
import com.gustavo.sistemaencomendas.presentation.dto.LoginRequest;
import com.gustavo.sistemaencomendas.presentation.dto.LoginResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.login(),
                        request.senha()
                )
        );

        String role = authentication.getAuthorities()
                .iterator()
                .next()
                .getAuthority();

        String token = jwtService.gerarToken(
                authentication.getName(),
                role
        );

        return new LoginResponse(
                token,
                "Bearer",
                authentication.getName(),
                role
        );
    }
}