package com.gustavo.sistemaencomendas.infrastructure.security;

import com.gustavo.sistemaencomendas.domain.model.Porteiro;
import com.gustavo.sistemaencomendas.infrastructure.persistence.repository.PorteiroRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PorteiroUserDetailsService implements UserDetailsService {

    private final PorteiroRepository porteiroRepository;

    @Override
    public UserDetails loadUserByUsername(String login)
            throws UsernameNotFoundException {

        Porteiro porteiro = porteiroRepository.findByLogin(login)
                .orElseThrow(() ->
                        new UsernameNotFoundException("Usuário não encontrado"));

        return User.builder()
                .username(porteiro.getLogin())
                .password(porteiro.getSenha())
                .roles("PORTEIRO")
                .disabled(!porteiro.isAtivo())
                .build();
    }
}