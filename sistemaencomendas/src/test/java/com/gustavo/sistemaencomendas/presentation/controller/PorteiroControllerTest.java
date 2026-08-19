package com.gustavo.sistemaencomendas.presentation.controller;

import com.gustavo.sistemaencomendas.application.service.PorteiroService;
import com.gustavo.sistemaencomendas.presentation.dto.CadastroFuncionarioRequest;
import org.junit.jupiter.api.Test;
import org.springframework.ui.Model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class PorteiroControllerTest {

    @Test
    void deveAbrirDashboard() {
        PorteiroService porteiroService = mock(PorteiroService.class);

        PorteiroController controller =
                new PorteiroController(porteiroService);

        assertEquals(
                "porteiro/dashboard",
                controller.dashboard()
        );
    }

    @Test
    void deveAbrirCadastroPublico() {
        PorteiroService porteiroService = mock(PorteiroService.class);
        Model model = mock(Model.class);

        PorteiroController controller =
                new PorteiroController(porteiroService);

        String view =
                controller.cadastroPublicoFuncionario(model);

        assertEquals(
                "porteiro/cadastro-funcionario",
                view
        );

        verify(model).addAttribute(
                eq("cadastro"),
                any(CadastroFuncionarioRequest.class)
        );
    }

    @Test
    void deveCadastrarFuncionarioPublico() {
        PorteiroService porteiroService = mock(PorteiroService.class);
        Model model = mock(Model.class);

        PorteiroController controller =
                new PorteiroController(porteiroService);

        CadastroFuncionarioRequest dados =
                new CadastroFuncionarioRequest(
                        "Carlos",
                        "carlos@email.com",
                        "carlos",
                        "senha123"
                );

        String resultado =
                controller.cadastrarPublicoFuncionario(
                        dados,
                        model
                );

        assertEquals(
                "redirect:/login?cadastroFuncionarioSucesso",
                resultado
        );

        verify(porteiroService).cadastrarPublico(dados);
    }
}