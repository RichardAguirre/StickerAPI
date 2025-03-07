package com.example.stickers.stickers.service;

import com.example.stickers.stickers.model.SearchHistory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface SearchHistoryService {
    Flux<SearchHistory> findAll();
    Mono<SearchHistory> save(SearchHistory searchHistory);
}