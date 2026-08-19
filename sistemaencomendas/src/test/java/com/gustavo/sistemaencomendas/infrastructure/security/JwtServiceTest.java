package com.gustavo.sistemaencomendas.infrastructure.security;

import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class JwtServiceTest {

    @Test
    void deveGerarTokenJwt() {

        JwtEncoder jwtEncoder = mock(JwtEncoder.class);

        Jwt jwt = mock(Jwt.class);

        when(jwt.getTokenValue())
                .thenReturn("token-gerado");

        when(jwtEncoder.encode(any(JwtEncoderParameters.class)))
                .thenReturn(jwt);

        JwtService jwtService = new JwtService(jwtEncoder);

        String token = jwtService.gerarToken(
                "gustavo",
                "ROLE_MORADOR"
        );

        assertEquals("token-gerado", token);

        verify(jwtEncoder).encode(
                any(JwtEncoderParameters.class)
        );
    }
}