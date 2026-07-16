package com.gustavo.sistemaencomendas.presentation.controller;

import com.gustavo.sistemaencomendas.application.service.EncomendaService;
import com.gustavo.sistemaencomendas.application.service.MoradorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/porteiro/encomendas")
public class EncomendaController {

    private final EncomendaService encomendaService;
    private final MoradorService moradorService;

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("encomendas", encomendaService.listarTodas());
        return "porteiro/encomendas/lista";
    }

    @GetMapping("/nova")
    public String formulario(Model model) {
        model.addAttribute("moradores", moradorService.listarTodos());
        return "porteiro/encomendas/formulario";
    }

    @PostMapping
    public String cadastrar(
            @RequestParam Long moradorId,
            @RequestParam String descricao,
            Model model
    ) {
        try {
            encomendaService.cadastrar(moradorId, descricao);
            return "redirect:/porteiro/encomendas?sucesso";
        } catch (IllegalArgumentException exception) {
            model.addAttribute("erro", exception.getMessage());
            model.addAttribute("moradores", moradorService.listarTodos());
            model.addAttribute("moradorId", moradorId);
            model.addAttribute("descricao", descricao);
            return "porteiro/encomendas/formulario";
        }
    }

    @PostMapping("/{id}/retirada")
    public String registrarRetirada(@PathVariable Long id) {
        try {
            encomendaService.registrarRetirada(id);
            return "redirect:/porteiro/encomendas?retirada";
        } catch (IllegalArgumentException exception) {
            return "redirect:/porteiro/encomendas?erro="
                    + exception.getMessage();
        }
    }
}