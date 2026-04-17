package com.distribuidos.stark.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controlador para servir la aplicación frontend
 * 
 * Rutas:
 * - GET /stark-security/ → index.html
 * - GET /stark-security/index.html → index.html
 * - GET /stark-security/dashboard → index.html
 */
@Controller
@RequestMapping("/stark-security")
public class FrontendController {

    /**
     * Sirve la página principal del dashboard
     */
    @GetMapping({"/", "/index.html", "/dashboard"})
    public String index() {
        return "forward:/index.html";
    }

    /**
     * Redirige la raíz a /stark-security/
     */
    @GetMapping("")
    public String root() {
        return "redirect:/stark-security/";
    }
}

