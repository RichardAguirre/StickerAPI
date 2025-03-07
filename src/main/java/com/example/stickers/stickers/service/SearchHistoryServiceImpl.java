package com.example.stickers.stickers.service;

import com.example.stickers.stickers.model.SearchHistory;
import com.example.stickers.stickers.repository.SearchHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class SearchHistoryServiceImpl implements SearchHistoryService {

    @Autowired
    private SearchHistoryRepository searchHistoryRepository;

    @Override
    public Flux<SearchHistory> findAll() {
        return searchHistoryRepository.findAllByOrderBySearchDateDesc();
    }

    @Override
    public Mono<SearchHistory> save(SearchHistory searchHistory) {
        return searchHistoryRepository.save(searchHistory);
    }
}