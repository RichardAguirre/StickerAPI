package com.example.stickers.stickers.repository;

import com.example.stickers.stickers.model.SearchHistory;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface SearchHistoryRepository extends ReactiveCrudRepository<SearchHistory, Long> {
    Flux<SearchHistory> findAllByOrderBySearchDateDesc();
}