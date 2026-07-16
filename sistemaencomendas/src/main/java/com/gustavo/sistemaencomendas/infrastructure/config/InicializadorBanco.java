package com.gustavo.sistemaencomendas.infrastructure.config;

import com.gustavo.sistemaencomendas.domain.model.Porteiro;
import com.gustavo.sistemaencomendas.infrastructure.persistence.repository.PorteiroRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class InicializadorBanco implements CommandLineRunner {

    private final PorteiroRepository porteiroRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {

        if (porteiroRepository.count() == 0) {

            Porteiro porteiro = Porteiro.builder()
                    .nome("Administrador")
                    .email("admin@sistema.com")
                    .login("porteiro")
                    .senha(passwordEncoder.encode("123456"))
                    .ativo(true)
                    .build();

            porteiroRepository.save(porteiro);
        }
    }
}