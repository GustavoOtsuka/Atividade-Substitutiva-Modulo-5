package com.gustavo.infrastructure.persistence;

import com.gustavo.domain.model.NotificacaoProcessada;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.UUID;

@ApplicationScoped
public class NotificacaoProcessadaRepository
        implements PanacheRepositoryBase<NotificacaoProcessada, UUID> {
}
