package com.gustavo.sistemaencomendas.presentation.controller;

import com.gustavo.sistemaencomendas.application.service.MoradorService;
import com.gustavo.sistemaencomendas.domain.model.Morador;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.gustavo.sistemaencomendas.application.service.EncomendaService;
import com.gustavo.sistemaencomendas.presentation.dto.CadastroMoradorRequest;

import org.springframework.security.core.Authentication;

@Controller
@RequiredArgsConstructor
public class MoradorController {

    private final MoradorService moradorService;

    private final EncomendaService encomendaService;

    @GetMapping("/morador/dashboard")
    public String dashboard() {
        return "morador/dashboard";
    }

 
    @GetMapping("/morador/encomendas")
    public String minhasEncomendas(
            Authentication authentication,
            Model model
    ) {
        Morador morador = moradorService.buscarPorLogin(
                authentication.getName()
        );

        model.addAttribute("morador", morador);
        model.addAttribute(
                "encomendas",
                encomendaService.listarPorMorador(morador.getId())
        );

        return "morador/encomendas";
    }


	@GetMapping("/morador/meus-dados")
	public String meusDados(
	        Authentication authentication,
	        Model model
	) {
	    Morador morador = moradorService.buscarPorLogin(
	            authentication.getName()
	    );

	    model.addAttribute("morador", morador);

	    return "morador/meus-dados";
	}

	@PostMapping("/morador/meus-dados")
	public String atualizarMeusDados(
	        Authentication authentication,
	        @ModelAttribute Morador dados,
	        Model model
	) {

	    Morador morador = moradorService.buscarPorLogin(
	            authentication.getName()
	    );

	    try {

	        moradorService.atualizarDadosMorador(
	                morador.getId(),
	                dados
	        );

	        return "redirect:/morador/meus-dados?sucesso";

	    } catch (IllegalArgumentException e) {

	        model.addAttribute("erro", e.getMessage());
	        model.addAttribute("morador", dados);

	        return "morador/meus-dados";
	    }
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


    @GetMapping("/cadastro/morador")
    public String cadastroPublicoMorador(Model model) {
        model.addAttribute(
                "cadastro",
                new CadastroMoradorRequest(
                        "",
                        "",
                        "",
                        "",
                        "",
                        ""
                )
        );

        return "morador/cadastro";
    }

    @PostMapping("/cadastro/morador")
    public String cadastrarPublicoMorador(
            @ModelAttribute("cadastro") CadastroMoradorRequest dados,
            Model model
    ) {
        try {

            moradorService.cadastrarPublico(dados);

            return "redirect:/login?cadastroSucesso";

        } catch (IllegalArgumentException e) {

            model.addAttribute("erro", e.getMessage());
            model.addAttribute("cadastro", dados);

            return "morador/cadastro";
        }
    }

    @PostMapping("/morador/encomendas/{id}/confirmar")
    public String confirmarCienciaAutenticado(
            @PathVariable Long id,
            Authentication authentication
    ) {

        Morador morador = moradorService.buscarPorLogin(
                authentication.getName()
        );

        encomendaService.confirmarCienciaPorMorador(
                id,
                morador.getId()
        );

        return "redirect:/morador/encomendas?confirmada";
    }

}
