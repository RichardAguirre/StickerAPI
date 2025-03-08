package com.example.stickers.stickers.service;

import com.example.stickers.stickers.model.SearchHistory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface SearchHistoryService {

    /**
     * Guarda un nuevo término de búsqueda en el historial
     * @param searchTerm término buscado
     * @return Mono<Void> que se completa cuando la operación termina
     */
    Mono<Void> saveSearch(String searchTerm);
    
    /**
     * Recupera todas las búsquedas del historial
     * @return Flux de búsquedas ordenadas por fecha (más reciente primero)
     */
    Flux<SearchHistory> findAll();
}