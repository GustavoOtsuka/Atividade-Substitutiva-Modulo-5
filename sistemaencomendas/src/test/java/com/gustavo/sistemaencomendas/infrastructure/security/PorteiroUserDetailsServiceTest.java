package com.gustavo.sistemaencomendas.infrastructure.security;

import com.gustavo.sistemaencomendas.domain.model.Morador;
import com.gustavo.sistemaencomendas.domain.model.Porteiro;
import com.gustavo.sistemaencomendas.infrastructure.persistence.repository.MoradorRepository;
import com.gustavo.sistemaencomendas.infrastructure.persistence.repository.PorteiroRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PorteiroUserDetailsServiceTest {

    @Mock
    private PorteiroRepository porteiroRepository;

    @Mock
    private MoradorRepository moradorRepository;

    private PorteiroUserDetailsService service;

    @BeforeEach
    void preparar() {
        service = new PorteiroUserDetailsService(
                porteiroRepository,
                moradorRepository
        );
    }

    @Test
    void deveCarregarPorteiro() {

        Porteiro porteiro = Porteiro.builder()
                .login("porteiro")
                .senha("senha-criptografada")
                .ativo(true)
                .build();

        when(porteiroRepository.findByLogin("porteiro"))
                .thenReturn(Optional.of(porteiro));

        UserDetails user =
                service.loadUserByUsername("porteiro");

        assertEquals("porteiro", user.getUsername());
        assertTrue(user.isEnabled());

        assertTrue(
                user.getAuthorities()
                        .stream()
                        .anyMatch(a ->
                                a.getAuthority()
                                        .equals("ROLE_PORTEIRO")
                        )
        );
    }

    @Test
    void deveCarregarMoradorQuandoNaoForPorteiro() {

        Morador morador = Morador.builder()
                .login("morador")
                .senha("senha-criptografada")
                .ativo(true)
                .build();

        when(porteiroRepository.findByLogin("morador"))
                .thenReturn(Optional.empty());

        when(moradorRepository.findByLogin("morador"))
                .thenReturn(Optional.of(morador));

        UserDetails user =
                service.loadUserByUsername("morador");

        assertEquals("morador", user.getUsername());
        assertTrue(user.isEnabled());

        assertTrue(
                user.getAuthorities()
                        .stream()
                        .anyMatch(a ->
                                a.getAuthority()
                                        .equals("ROLE_MORADOR")
                        )
        );
    }

    @Test
    void deveDesabilitarUsuarioInativo() {

        Morador morador = Morador.builder()
                .login("morador")
                .senha("senha")
                .ativo(false)
                .build();

        when(porteiroRepository.findByLogin("morador"))
                .thenReturn(Optional.empty());

        when(moradorRepository.findByLogin("morador"))
                .thenReturn(Optional.of(morador));

        UserDetails user =
                service.loadUserByUsername("morador");

        assertFalse(user.isEnabled());
    }

    @Test
    void deveFalharQuandoUsuarioNaoExistir() {

        when(porteiroRepository.findByLogin("inexistente"))
                .thenReturn(Optional.empty());

        when(moradorRepository.findByLogin("inexistente"))
                .thenReturn(Optional.empty());

        assertThrows(
                UsernameNotFoundException.class,
                () -> service.loadUserByUsername("inexistente")
        );
    }
}