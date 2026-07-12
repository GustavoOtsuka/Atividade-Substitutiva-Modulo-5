package com.gustavo.sistemaencomendas.infrastructure.persistence.repository;

import com.gustavo.sistemaencomendas.domain.model.Morador;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MoradorRepository extends JpaRepository<Morador, Long> {

    Optional<Morador> findByLogin(String login);

    boolean existsByEmail(String email);

    boolean existsByLogin(String login);
}