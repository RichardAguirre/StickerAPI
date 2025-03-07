package com.example.stickers.stickers.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
public class ApiKeyAuthFilter implements WebFilter {

    @Value("${admin.api.token}")
    private String adminApiToken;

    private static final String API_KEY_HEADER = "X-API-KEY";
    private static final String ADMIN_API_PATH = "/api/admin/";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String path = exchange.getRequest().getPath().value();
        
        // Solo verificamos el token para endpoints administrativos
        if (path.startsWith(ADMIN_API_PATH)) {
            String apiKey = exchange.getRequest().getHeaders().getFirst(API_KEY_HEADER);
            
            if (apiKey == null || !apiKey.equals(adminApiToken)) {
                exchange.getResponse().setStatusCode(org.springframework.http.HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }
        }
        
        return chain.filter(exchange);
    }
}