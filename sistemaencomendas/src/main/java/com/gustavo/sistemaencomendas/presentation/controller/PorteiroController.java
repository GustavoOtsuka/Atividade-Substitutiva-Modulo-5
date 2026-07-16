package com.gustavo.sistemaencomendas.presentation.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PorteiroController {

    @GetMapping("/porteiro/dashboard")
    public String dashboard() {
        return "porteiro/dashboard";
    }
}