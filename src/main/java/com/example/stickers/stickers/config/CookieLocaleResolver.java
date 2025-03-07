package com.example.stickers.stickers.config;

import org.springframework.context.annotation.Primary;
import org.springframework.context.i18n.LocaleContext;
import org.springframework.context.i18n.SimpleLocaleContext;
import org.springframework.http.HttpCookie;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.i18n.LocaleContextResolver;

import java.time.Duration;
import java.util.Locale;

@Component
@Primary
public class CookieLocaleResolver implements LocaleContextResolver {
    
    private String cookieName = "language";
    private Locale defaultLocale = new Locale("en");
    
    @Override
    public LocaleContext resolveLocaleContext(ServerWebExchange exchange) {
        // Intenta obtener el idioma de la cookie
        HttpCookie cookie = exchange.getRequest().getCookies().getFirst(cookieName);
        
        if (cookie != null && StringUtils.hasText(cookie.getValue())) {
            String localeValue = cookie.getValue();
            System.out.println("Locale from cookie: " + localeValue); // Debug
            return new SimpleLocaleContext(StringUtils.parseLocaleString(localeValue));
        }
        
        System.out.println("Using default locale: " + defaultLocale); // Debug
        return new SimpleLocaleContext(defaultLocale);
    }

    @Override
    public void setLocaleContext(ServerWebExchange exchange, LocaleContext localeContext) {
        Locale locale = localeContext != null ? localeContext.getLocale() : defaultLocale;
        
        ResponseCookie cookie = ResponseCookie.from(cookieName, locale.toLanguageTag())
            .path("/")
            .maxAge(Duration.ofDays(30))
            .sameSite("Lax")
            .httpOnly(false) // Permitir acceso desde JS si es necesario
            .build();
        
        exchange.getResponse().addCookie(cookie);
        System.out.println("[DEBUG] Cookie configurada: " + cookie.toString());
    }
}