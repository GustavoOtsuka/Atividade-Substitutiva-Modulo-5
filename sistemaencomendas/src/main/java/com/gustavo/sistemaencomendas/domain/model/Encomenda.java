package com.gustavo.sistemaencomendas.domain.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

import java.util.UUID;


@Entity
@Table(name = "encomendas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Encomenda {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "morador_id", nullable = false)
    private Morador morador;

    @Column(nullable = false, length = 500)
    private String descricao;

    @Column(nullable = false)
    private LocalDateTime dataRecebimento;

    private LocalDateTime dataRetirada;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private StatusEncomenda status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private StatusNotificacao statusNotificacao;

    @Column(nullable = false)
    private boolean moradorCiente;


    @Column(name = "token_confirmacao", unique = true)
    private UUID tokenConfirmacao;

    @Column(name = "data_ciencia")
    private LocalDateTime dataCiencia;

}
