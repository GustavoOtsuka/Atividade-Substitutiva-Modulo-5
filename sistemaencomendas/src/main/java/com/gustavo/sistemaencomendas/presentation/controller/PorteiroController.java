package com.gustavo.sistemaencomendas.presentation.controller;

import com.gustavo.sistemaencomendas.application.service.PorteiroService;
import com.gustavo.sistemaencomendas.presentation.dto.CadastroFuncionarioRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class PorteiroController {

    private final PorteiroService porteiroService;

    @GetMapping("/porteiro/dashboard")
    public String dashboard() {
        return "porteiro/dashboard";
    }

    @GetMapping("/cadastro/funcionario")
    public String cadastroPublicoFuncionario(Model model) {

        model.addAttribute(
                "cadastro",
                new CadastroFuncionarioRequest(
                        "",
                        "",
                        "",
                        ""
                )
        );

        return "porteiro/cadastro-funcionario";
    }

    @PostMapping("/cadastro/funcionario")
    public String cadastrarPublicoFuncionario(
            @ModelAttribute("cadastro") CadastroFuncionarioRequest dados,
            Model model
    ) {

        try {

            porteiroService.cadastrarPublico(dados);

            return "redirect:/login?cadastroFuncionarioSucesso";

        } catch (IllegalArgumentException e) {

            model.addAttribute("erro", e.getMessage());
            model.addAttribute("cadastro", dados);

            return "porteiro/cadastro-funcionario";
        }
    }
}