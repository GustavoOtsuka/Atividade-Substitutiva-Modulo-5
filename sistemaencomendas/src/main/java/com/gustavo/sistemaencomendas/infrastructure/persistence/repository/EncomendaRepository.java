package com.gustavo.sistemaencomendas.infrastructure.persistence.repository;

import com.gustavo.sistemaencomendas.domain.model.Encomenda;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface EncomendaRepository extends JpaRepository<Encomenda, Long> {

    List<Encomenda> findAllByOrderByDataRecebimentoDesc();

    List<Encomenda> findByMoradorIdOrderByDataRecebimentoDesc(Long moradorId);

}
