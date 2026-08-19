package com.gustavo.sistemaencomendas.presentation.controller;

import com.gustavo.sistemaencomendas.application.service.EncomendaService;
import com.gustavo.sistemaencomendas.application.service.MoradorService;
import com.gustavo.sistemaencomendas.domain.model.Morador;
import org.junit.jupiter.api.Test;
import org.springframework.ui.Model;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class EncomendaControllerTest {

    @Test
    void deveListarEncomendas() {
        EncomendaService encomendaService = mock(EncomendaService.class);
        MoradorService moradorService = mock(MoradorService.class);
        Model model = mock(Model.class);

        EncomendaController controller =
                new EncomendaController(encomendaService, moradorService);

        String view = controller.listar(model);

        assertEquals("porteiro/encomendas/lista", view);

        verify(model).addAttribute(
                eq("encomendas"),
                any()
        );
    }

    @Test
    void deveAbrirFormularioDeNovaEncomenda() {
        EncomendaService encomendaService = mock(EncomendaService.class);
        MoradorService moradorService = mock(MoradorService.class);
        Model model = mock(Model.class);

        when(moradorService.listarTodos())
                .thenReturn(List.of(new Morador()));

        EncomendaController controller =
                new EncomendaController(encomendaService, moradorService);

        String view = controller.formulario(model);

        assertEquals("porteiro/encomendas/formulario", view);

        verify(model).addAttribute(
                "moradores",
                moradorService.listarTodos()
        );
    }

    @Test
    void deveCadastrarEncomenda() {
        EncomendaService encomendaService = mock(EncomendaService.class);
        MoradorService moradorService = mock(MoradorService.class);
        Model model = mock(Model.class);

        EncomendaController controller =
                new EncomendaController(encomendaService, moradorService);

        String resultado = controller.cadastrar(
                1L,
                "Caixa",
                model
        );

        assertEquals(
                "redirect:/porteiro/encomendas?sucesso",
                resultado
        );

        verify(encomendaService).cadastrar(1L, "Caixa");
    }

    @Test
    void deveRegistrarRetirada() {
        EncomendaService encomendaService = mock(EncomendaService.class);
        MoradorService moradorService = mock(MoradorService.class);

        EncomendaController controller =
                new EncomendaController(encomendaService, moradorService);

        String resultado = controller.registrarRetirada(10L);

        assertEquals(
                "redirect:/porteiro/encomendas?retirada",
                resultado
        );

        verify(encomendaService).registrarRetirada(10L);
    }
}