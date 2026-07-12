package com.gustavo.sistemaencomendas.presentation.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MoradorController {

    @GetMapping("/morador/dashboard")
    public String dashboard() {
        return "morador/dashboard";
    }
}