package com.gustavo.domain.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "notificacoes_processadas")
public class NotificacaoProcessada {

    @Id
    @Column(name = "evento_id", nullable = false)
    public UUID eventoId;

    @Column(name = "encomenda_id", nullable = false)
    public Long encomendaId;

    @Column(name = "morador_id", nullable = false)
    public Long moradorId;

    @Column(name = "nome_morador", nullable = false, length = 120)
    public String nomeMorador;

    @Column(nullable = false, length = 150)
    public String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    public StatusProcessamento status;

    @Column(nullable = false)
    public int tentativas;

    @Column(name = "recebida_em", nullable = false)
    public LocalDateTime recebidaEm;

    @Column(name = "processada_em")
    public LocalDateTime processadaEm;

    @Column(name = "ultimo_erro", columnDefinition = "TEXT")
    public String ultimoErro;
}
