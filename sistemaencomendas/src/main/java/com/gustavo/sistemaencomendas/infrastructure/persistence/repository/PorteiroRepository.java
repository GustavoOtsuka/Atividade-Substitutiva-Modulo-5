package com.gustavo.sistemaencomendas.infrastructure.persistence.repository;

import com.gustavo.sistemaencomendas.domain.model.Porteiro;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PorteiroRepository extends JpaRepository<Porteiro, Long> {

    Optional<Porteiro> findByLogin(String login);

    boolean existsByLogin(String login);

    boolean existsByEmail(String email);
}