package com.gustavo.sistemaencomendas.domain.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "outbox_eventos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OutboxEvento {

    @Id
    private UUID id;

    @Column(nullable = false, length = 100)
    private String tipoEvento;

    @Column(nullable = false, length = 100)
    private String agregadoTipo;

    @Column(nullable = false, length = 100)
    private String agregadoId;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String payload;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private StatusOutbox status;

    @Column(nullable = false)
    private int tentativas;

    @Column(nullable = false)
    private LocalDateTime criadoEm;

    private LocalDateTime publicadoEm;

    @Column(columnDefinition = "TEXT")
    private String ultimoErro;
}