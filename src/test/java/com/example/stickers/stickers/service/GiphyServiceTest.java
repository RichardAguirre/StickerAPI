package com.example.stickers.stickers.service;

import com.example.stickers.stickers.model.SearchHistory;
import com.example.stickers.stickers.repository.SearchHistoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Test class for GiphyService using JUnit 5 and Mockito
 */
@ExtendWith(MockitoExtension.class)
public class GiphyServiceTest {

    @Mock
    private WebClient webClient;

    @SuppressWarnings("rawtypes")
    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;

    @SuppressWarnings("rawtypes")
    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    @Mock
    private SearchHistoryRepository searchHistoryRepository;

    @Mock
    private MessageSource messageSource;

    @InjectMocks
    private GiphyService giphyService;

    /**
     * Setup test environment with mocks and reflection utils
     */
    @SuppressWarnings("unchecked")
    @BeforeEach
    void setUp() {
        // Set private fields using ReflectionTestUtils
        ReflectionTestUtils.setField(giphyService, "apiKey", "test_api_key");
        ReflectionTestUtils.setField(giphyService, "giphyUrl", "https://api.giphy.com/v1/stickers/search");
        ReflectionTestUtils.setField(giphyService, "webClient", webClient);
        ReflectionTestUtils.setField(giphyService, "searchHistoryRepository", searchHistoryRepository);

        // Configure mocks with fixed generic types
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    }

    /**
     * Test successful sticker search operation
     */
    @SuppressWarnings("unchecked")
    @Test
    void searchStickers_Success() {
        // Arrange
        String query = "cats";
        Locale locale = Locale.ENGLISH;

        Map<String, Object> mockResponse = new HashMap<>();
        Object[] mockData = {
                createMockStickerData("cat1"),
                createMockStickerData("cat2")
        };
        mockResponse.put("data", mockData);

        when(messageSource.getMessage(eq("cats"), any(), eq("cats"), eq(locale)))
                .thenReturn("cats");
        when(responseSpec.bodyToMono(any(ParameterizedTypeReference.class)))
                .thenReturn(Mono.just(mockResponse));
        when(searchHistoryRepository.save(any(SearchHistory.class)))
                .thenReturn(Mono.just(new SearchHistory()));

        // Act
        Mono<Map<String, Object>> result = giphyService.searchStickers(query, locale);

        // Assert
        StepVerifier.create(result)
                .expectNextMatches(map -> {
                    Object[] data = (Object[]) map.get("data");
                    return data.length == 2;
                })
                .verifyComplete();

        verify(searchHistoryRepository).save(any(SearchHistory.class));
    }

    /**
     * Test data extraction from stickers response
     */
    @SuppressWarnings("unchecked")
    @Test
    void getStickersData_ReturnsFluxOfStickers() {
        // Arrange
        String query = "dogs";
        Locale locale = Locale.ENGLISH;

        Map<String, Object> mockResponse = new HashMap<>();
        // Use List instead of array to be compatible with getStickersData method
        List<Object> mockData = new ArrayList<>();
        mockData.add(createMockStickerData("dog1"));
        mockData.add(createMockStickerData("dog2"));
        mockResponse.put("data", mockData);

        when(messageSource.getMessage(eq("dogs"), any(), eq("dogs"), eq(locale)))
                .thenReturn("dogs");
        when(responseSpec.bodyToMono(any(ParameterizedTypeReference.class)))
                .thenReturn(Mono.just(mockResponse));
        when(searchHistoryRepository.save(any(SearchHistory.class)))
                .thenReturn(Mono.just(new SearchHistory()));

        // Act
        Flux<Object> stickersFlux = giphyService.getStickersData(query, locale);

        // Assert
        StepVerifier.create(stickersFlux)
                .expectNextCount(2)
                .verifyComplete();
    }

    /**
     * Test empty response handling
     */
    @SuppressWarnings("unchecked")
    @Test
    void searchStickers_WithEmptyResponse_ShouldReturnEmptyData() {
        // Arrange
        String query = "nonexistent";
        Locale locale = Locale.ENGLISH;

        Map<String, Object> mockResponse = new HashMap<>();
        mockResponse.put("data", new Object[0]);

        when(messageSource.getMessage(eq("nonexistent"), any(), eq("nonexistent"), eq(locale)))
                .thenReturn("nonexistent");
        when(responseSpec.bodyToMono(any(ParameterizedTypeReference.class)))
                .thenReturn(Mono.just(mockResponse));
        when(searchHistoryRepository.save(any(SearchHistory.class)))
                .thenReturn(Mono.just(new SearchHistory()));

        // Act
        Mono<Map<String, Object>> result = giphyService.searchStickers(query, locale);

        // Assert
        StepVerifier.create(result)
                .expectNextMatches(map -> {
                    Object[] data = (Object[]) map.get("data");
                    return data.length == 0;
                })
                .verifyComplete();
    }

    /**
     * Helper method to create mock sticker data
     */
    private Map<String, Object> createMockStickerData(String id) {
        Map<String, Object> sticker = new HashMap<>();
        sticker.put("id", id);

        Map<String, Object> images = new HashMap<>();
        Map<String, Object> original = new HashMap<>();
        original.put("url", "https://media.giphy.com/media/" + id + "/giphy.gif");
        images.put("original", original);
        sticker.put("images", images);

        return sticker;
    }
}