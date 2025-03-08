package com.example.stickers.stickers.controller;

import com.example.stickers.stickers.config.CookieLocaleResolver;
import com.example.stickers.stickers.service.GiphyService;
import com.example.stickers.stickers.service.SearchHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContext;
import org.springframework.context.i18n.SimpleLocaleContext;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.reactive.result.view.Rendering;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import java.util.Locale;

@Controller
public class HomeController {

    @Autowired
    private GiphyService giphyService;

    @Autowired
    private SearchHistoryService searchHistoryService;

    @Autowired
    private CookieLocaleResolver localeResolver;

    @SuppressWarnings("null")
    @GetMapping("/")
    public Mono<Rendering> index(ServerWebExchange exchange) {
        LocaleContext localeContext = localeResolver.resolveLocaleContext(exchange);
        Locale locale = localeContext.getLocale();

        System.out.println("Index page, using locale: " + locale);

        return Mono.just(Rendering.view("index")
                .modelAttribute("currentLang", locale.getLanguage())
                .build());
    }

    @SuppressWarnings("null")
    @GetMapping("/search")
    public Mono<Rendering> search(@RequestParam String query, ServerWebExchange exchange) {
        LocaleContext localeContext = localeResolver.resolveLocaleContext(exchange);
        Locale locale = localeContext.getLocale();

        System.out.println("Search with query: " + query + ", using locale: " + locale);

        return giphyService.getStickersData(query, locale)
                .collectList()
                .map(stickers -> Rendering.view("index")
                        .modelAttribute("stickers", stickers)
                        .modelAttribute("currentLang", locale.getLanguage())
                        .build());
    }

    @SuppressWarnings("null")
    @GetMapping("/history")
    public Mono<Rendering> history(ServerWebExchange exchange) {
        LocaleContext localeContext = localeResolver.resolveLocaleContext(exchange);
        Locale locale = localeContext.getLocale();

        System.out.println("History page, using locale: " + locale);

        return searchHistoryService.findAll()
                .collectList()
                .map(searches -> Rendering.view("history")
                        .modelAttribute("searches", searches)
                        .modelAttribute("currentLang", locale.getLanguage())
                        .build());
    }

    @SuppressWarnings("null")
    @GetMapping("/change-language")
    public Mono<Rendering> changeLanguage(
            @RequestParam String lang,
            ServerWebExchange exchange) {
        return Mono.fromSupplier(() -> {
            // Valida parámetro 'lang'
            Locale locale;
            try {
                locale = Locale.forLanguageTag(lang);
            } catch (Exception e) {
                locale = Locale.ENGLISH;
            }

            // Establece cookie de idioma
            localeResolver.setLocaleContext(exchange, new SimpleLocaleContext(locale));

            // Obtener URL redirección
            String redirectUrl = exchange.getRequest().getHeaders().getFirst("Referer");
            if (!StringUtils.hasText(redirectUrl) || redirectUrl.contains("/change-language")) {
                redirectUrl = "/";
            }

            // TODO: Verificar valores
            System.out.println("[DEBUG] Locale: " + locale + " | Redirect: " + redirectUrl);

            return Rendering.redirectTo(redirectUrl).build();
        });
    }

    @GetMapping(value = "/debug", produces = MediaType.TEXT_PLAIN_VALUE)
    @ResponseBody
    public Mono<String> debug(ServerWebExchange exchange) {
        Locale locale = localeResolver.resolveLocaleContext(exchange).getLocale();
        String cookies = exchange.getRequest().getCookies().toString();
        return Mono.just("Locale: " + locale + " | Cookies: " + cookies);
    }
}