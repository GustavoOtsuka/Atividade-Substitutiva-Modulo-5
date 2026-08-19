package com.gustavo.sistemaencomendas.infrastructure.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class SecurityJwtIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void moradorNaoDeveAcessarDashboardDoPorteiro() throws Exception {

        mockMvc.perform(
                        get("/porteiro/dashboard")
                                .with(jwt()
                                        .jwt(jwt -> jwt
                                                .subject("morador")
                                                .claim("role", "ROLE_MORADOR")
                                        )
                                        .authorities(
                                                new SimpleGrantedAuthority("ROLE_MORADOR")
                                        )
                                )
                )
                .andExpect(status().isForbidden());
    }

    @Test
    void porteiroDeveAcessarDashboardDoPorteiro() throws Exception {

        mockMvc.perform(
                        get("/porteiro/dashboard")
                                .with(jwt()
                                        .jwt(jwt -> jwt
                                                .subject("porteiro")
                                                .claim("role", "ROLE_PORTEIRO")
                                        )
                                        .authorities(
                                                new SimpleGrantedAuthority("ROLE_PORTEIRO")
                                        )
                                )
                )
                .andExpect(status().isOk());
    }

    @Test
    void moradorDeveAcessarDashboardDoMorador() throws Exception {

        mockMvc.perform(
                        get("/morador/dashboard")
                                .with(jwt()
                                        .jwt(jwt -> jwt
                                                .subject("morador")
                                                .claim("role", "ROLE_MORADOR")
                                        )
                                        .authorities(
                                                new SimpleGrantedAuthority("ROLE_MORADOR")
                                        )
                                )
                )
                .andExpect(status().isOk());
    }
}