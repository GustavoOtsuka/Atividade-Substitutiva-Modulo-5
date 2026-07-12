package com.gustavo.sistemaencomendas.domain.model;

public enum StatusNotificacao {

    PENDENTE,
    PUBLICADA_NO_KAFKA,
    PROCESSANDO,
    ENVIADA,
    CONFIRMADA_PELO_MORADOR,
    FALHA_TEMPORARIA,
    FALHA_DEFINITIVA
}