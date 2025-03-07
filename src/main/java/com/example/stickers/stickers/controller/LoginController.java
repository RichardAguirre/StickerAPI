package com.example.stickers.stickers.controller;

import com.example.stickers.stickers.config.CookieLocaleResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.reactive.result.view.Rendering;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Locale;

@Controller
public class LoginController {

    @Autowired
    private CookieLocaleResolver localeResolver;
    
    @GetMapping("/login")
    public Mono<Rendering> login(ServerWebExchange exchange) {
        Locale locale = localeResolver.resolveLocaleContext(exchange).getLocale();
        System.out.println("Login page requested with locale: " + locale); // Debug logging
        
        return Mono.just(Rendering.view("login")
                .modelAttribute("currentLang", locale.getLanguage())
                .modelAttribute("error", exchange.getRequest().getQueryParams().containsKey("error"))
                .build());
    }
}