package com.example.stickers.stickers.controller;

import com.example.stickers.stickers.config.CookieLocaleResolver;
import com.example.stickers.stickers.service.GiphyService;
import com.example.stickers.stickers.service.SearchHistoryService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.i18n.LocaleContext;
import org.springframework.context.i18n.SimpleLocaleContext;
import org.springframework.web.reactive.result.view.Rendering;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@ExtendWith(MockitoExtension.class)
class HomeControllerTest {

    @Mock
    private GiphyService giphyService;

    @Mock
    private SearchHistoryService searchHistoryService;
    
    @Mock
    private CookieLocaleResolver localeResolver;
    
    @Mock
    private ServerWebExchange exchange;
    
    @InjectMocks
    private HomeController homeController;

    @Test
    void index_ShouldRenderIndexPage() {
        // Arrange
        LocaleContext localeContext = new SimpleLocaleContext(Locale.ENGLISH);
        
        Mockito.when(localeResolver.resolveLocaleContext(exchange))
               .thenReturn(localeContext);
               
        // Act & Assert
        Mono<Rendering> result = homeController.index(exchange);
        
        StepVerifier.create(result)
                .expectNextMatches(rendering -> 
                    rendering.view().equals("index") && 
                    rendering.modelAttributes().containsKey("currentLang"))
                .expectComplete()
                .verify();
    }
    
    @Test
    void search_ShouldReturnResults() { 
        // Arrange
        String query = "test";
        LocaleContext localeContext = new SimpleLocaleContext(Locale.ENGLISH);
        
        Map<String, Object> sticker = new HashMap<>();
        sticker.put("id", "123");
        sticker.put("title", "Test Sticker");
        sticker.put("url", "https://example.com/sticker.gif");
        
        Mockito.when(localeResolver.resolveLocaleContext(exchange))
               .thenReturn(localeContext);
               
        Mockito.when(giphyService.getStickersData(query, Locale.ENGLISH))
               .thenReturn(Flux.just(sticker));
        
        // Act & Assert
        Mono<Rendering> result = homeController.search(query, exchange);
        
        StepVerifier.create(result)
                .expectNextMatches(rendering -> 
                    rendering.view().equals("index") && 
                    rendering.modelAttributes().containsKey("stickers") &&
                    rendering.modelAttributes().containsKey("currentLang"))
                .expectComplete()
                .verify();
    }
}