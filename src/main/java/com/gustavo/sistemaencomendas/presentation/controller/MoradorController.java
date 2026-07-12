package com.gustavo.sistemaencomendas.presentation.controller;

import com.gustavo.sistemaencomendas.application.service.MoradorService;
import com.gustavo.sistemaencomendas.domain.model.Morador;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class MoradorController {

    private final MoradorService moradorService;

    @GetMapping("/morador/dashboard")
    public String dashboard() {
        return "morador/dashboard";
    }

    @GetMapping("/porteiro/moradores")
    public String listar(Model model) {
        model.addAttribute("moradores", moradorService.listarTodos());
        return "porteiro/moradores/lista";
    }

    @GetMapping("/porteiro/moradores/novo")
    public String formulario(Model model) {
        model.addAttribute("morador", new Morador());
        model.addAttribute("edicao", false);
        return "porteiro/moradores/formulario";
    }

    @PostMapping("/porteiro/moradores")
    public String cadastrar(Morador morador, Model model) {
        try {
            moradorService.cadastrar(morador);
            return "redirect:/porteiro/moradores?sucesso";
        } catch (IllegalArgumentException exception) {
            model.addAttribute("erro", exception.getMessage());
            model.addAttribute("morador", morador);
            model.addAttribute("edicao", false);
            return "porteiro/moradores/formulario";
        }
    }

    @GetMapping("/porteiro/moradores/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        model.addAttribute("morador", moradorService.buscarPorId(id));
        model.addAttribute("edicao", true);
        return "porteiro/moradores/formulario";
    }

    @PostMapping("/porteiro/moradores/{id}")
    public String atualizar(
            @PathVariable Long id,
            Morador morador,
            Model model
    ) {
        try {
            moradorService.atualizar(id, morador);
            return "redirect:/porteiro/moradores?atualizado";
        } catch (IllegalArgumentException exception) {
            morador.setId(id);
            model.addAttribute("erro", exception.getMessage());
            model.addAttribute("morador", morador);
            model.addAttribute("edicao", true);
            return "porteiro/moradores/formulario";
        }
    }
}