package com.gustavo.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;

public record EncomendaRecebidaEvento(
        UUID eventoId,
        Long encomendaId,
        Long moradorId,
        String nomeMorador,
        String emailMorador,
        String apartamento,
        String descricao,
        LocalDateTime dataRecebimento
) {
}
