package com.example.stickers.stickers.service;

import com.example.stickers.stickers.model.SearchHistory;
import com.example.stickers.stickers.repository.SearchHistoryRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Service
public class SearchHistoryServiceImpl implements SearchHistoryService {

    private final SearchHistoryRepository searchHistoryRepository;
    
    public SearchHistoryServiceImpl(SearchHistoryRepository searchHistoryRepository) {
        this.searchHistoryRepository = searchHistoryRepository;
    }
    
    @Override
    public Mono<Void> saveSearch(String searchTerm) {
        SearchHistory history = new SearchHistory();
        history.setSearchTerm(searchTerm);
        history.setSearchDate(LocalDateTime.now());
        
        return searchHistoryRepository.save(history).then();
    }
    
    @Override
    public Flux<SearchHistory> findAll() {
        return searchHistoryRepository.findAll();
    }
}