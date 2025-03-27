package com.example.stickers.stickers.service;

import com.example.stickers.stickers.model.SearchHistory;
import com.example.stickers.stickers.repository.SearchHistoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;

/**
 * Tests for the SearchHistoryService implementation
 */
@ExtendWith(MockitoExtension.class)
class SearchHistoryServiceTest {

    @Mock
    private SearchHistoryRepository searchHistoryRepository;

    // Manually created instead of using @InjectMocks
    private SearchHistoryServiceImpl searchHistoryService;

    /**
     * Set up test environment before each test
     */
    @BeforeEach
    void setUp() {
        // Create an instance of the concrete implementation
        searchHistoryService = new SearchHistoryServiceImpl(searchHistoryRepository);
    }

    /**
     * Test that search terms are saved correctly
     */
    @Test
    void saveSearch_ShouldSaveSearchTerm() {
        // Arrange
        SearchHistory history = new SearchHistory();
        history.setSearchTerm("test");

        Mockito.when(searchHistoryRepository.save(Mockito.any(SearchHistory.class)))
                .thenReturn(Mono.just(history));

        // Act & Assert
        StepVerifier.create(searchHistoryService.saveSearch("test"))
                .expectComplete()
                .verify();

        Mockito.verify(searchHistoryRepository).save(Mockito.any(SearchHistory.class));
    }

    /**
     * Test that all search history can be retrieved
     */
    @Test
    void findAll_ShouldReturnSearchHistory() {
        // Arrange
        SearchHistory history1 = new SearchHistory();
        history1.setId(1L);
        history1.setSearchTerm("test1");

        SearchHistory history2 = new SearchHistory();
        history2.setId(2L);
        history2.setSearchTerm("test2");

        Mockito.when(searchHistoryRepository.findAll())
                .thenReturn(Flux.fromIterable(Arrays.asList(history1, history2)));

        // Act & Assert
        StepVerifier.create(searchHistoryService.findAll())
                .expectNext(history1)
                .expectNext(history2)
                .expectComplete()
                .verify();
    }
}