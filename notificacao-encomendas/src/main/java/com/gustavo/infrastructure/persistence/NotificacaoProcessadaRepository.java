package com.gustavo.infrastructure.persistence;

import com.gustavo.domain.model.NotificacaoProcessada;
import com.gustavo.domain.model.StatusProcessamento;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class NotificacaoProcessadaRepository
        implements PanacheRepositoryBase<NotificacaoProcessada, UUID> {

    public List<NotificacaoProcessada> buscarProntasParaRetry(
            LocalDateTime agora,
            int limiteTentativas
    ) {
        return find(
                """
                status = ?1
                and proximaTentativaEm is not null
                and proximaTentativaEm <= ?2
                and tentativas < ?3
                order by proximaTentativaEm
                """,
                StatusProcessamento.FALHA_TEMPORARIA,
                agora,
                limiteTentativas
        ).list();
    }
}
