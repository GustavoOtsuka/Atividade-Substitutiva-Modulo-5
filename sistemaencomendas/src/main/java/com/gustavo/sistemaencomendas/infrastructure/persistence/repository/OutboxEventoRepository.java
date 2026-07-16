package com.gustavo.sistemaencomendas.infrastructure.persistence.repository;

import com.gustavo.sistemaencomendas.domain.model.OutboxEvento;
import com.gustavo.sistemaencomendas.domain.model.StatusOutbox;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface OutboxEventoRepository
        extends JpaRepository<OutboxEvento, UUID> {

    List<OutboxEvento> findTop20ByStatusOrderByCriadoEmAsc(
            StatusOutbox status
    );
}