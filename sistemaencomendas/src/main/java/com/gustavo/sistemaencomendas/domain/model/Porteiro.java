package com.gustavo.sistemaencomendas.domain.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "porteiros")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Porteiro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String nome;

    @Column(nullable = false, unique = true, length = 150)
    private String email;

    @Column(nullable = false, unique = true, length = 60)
    private String login;

    @Column(nullable = false)
    private String senha;

    @Column(nullable = false)
    private boolean ativo;
}