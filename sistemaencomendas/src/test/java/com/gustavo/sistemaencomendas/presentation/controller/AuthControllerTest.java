package com.gustavo.sistemaencomendas.presentation.controller;

import com.gustavo.sistemaencomendas.infrastructure.security.JwtService;
import com.gustavo.sistemaencomendas.presentation.dto.LoginRequest;
import com.gustavo.sistemaencomendas.presentation.dto.LoginResponse;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AuthControllerTest {

    @Test
    void deveAutenticarEDevolverJwt() {

        AuthenticationManager authenticationManager =
                mock(AuthenticationManager.class);

        JwtService jwtService =
                mock(JwtService.class);

        Authentication authentication =
                mock(Authentication.class);

        when(authentication.getName())
                .thenReturn("gustavo");

        when(authentication.getAuthorities())
                .thenAnswer(invocation ->
                        List.of(
                                new SimpleGrantedAuthority(
                                        "ROLE_MORADOR"
                                )
                        )
                );

        when(authenticationManager.authenticate(any()))
                .thenReturn(authentication);

        when(jwtService.gerarToken(
                "gustavo",
                "ROLE_MORADOR"
        ))
                .thenReturn("jwt-teste");

        AuthController controller =
                new AuthController(
                        authenticationManager,
                        jwtService
                );

        LoginRequest request =
                new LoginRequest(
                        "gustavo",
                        "123456"
                );

        LoginResponse response =
                controller.login(request);

        assertNotNull(response);
        assertEquals("jwt-teste", response.token());
        assertEquals("Bearer", response.tipo());
        assertEquals("gustavo", response.login());
        assertEquals("ROLE_MORADOR", response.role());

        verify(authenticationManager)
                .authenticate(
                        any(
                                UsernamePasswordAuthenticationToken.class
                        )
                );

        verify(jwtService)
                .gerarToken(
                        "gustavo",
                        "ROLE_MORADOR"
                );
    }
}