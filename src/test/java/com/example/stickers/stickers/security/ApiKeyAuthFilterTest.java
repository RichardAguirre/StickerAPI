package com.example.stickers.stickers.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Test class for the API key authentication filter that protects admin routes
 */
@ExtendWith(MockitoExtension.class)
public class ApiKeyAuthFilterTest {

    @InjectMocks
    private ApiKeyAuthFilter apiKeyAuthFilter;

    private WebFilterChain filterChain;

    /**
     * Sets up the test environment with mock objects and test values
     */
    @BeforeEach
    void setUp() {
        // Use ReflectionTestUtils to set the private field
        ReflectionTestUtils.setField(apiKeyAuthFilter, "adminApiToken", "test-api-key-123");

        // Mock the filter chain with lenient setting to avoid unnecessary stubbing errors
        filterChain = mock(WebFilterChain.class);
        lenient().when(filterChain.filter(any())).thenReturn(Mono.empty());
    }

    /**
     * Test that admin routes with a valid API key pass through the filter
     */
    @Test
    void filter_AdminRouteWithValidApiKey_Success() {
        // Arrange
        MockServerHttpRequest request = MockServerHttpRequest
                .get("/api/admin/users")
                .header("X-API-KEY", "test-api-key-123")
                .build();

        MockServerWebExchange exchange = MockServerWebExchange.from(request);

        // Act
        Mono<Void> result = apiKeyAuthFilter.filter(exchange, filterChain);

        // Assert
        StepVerifier.create(result)
                .verifyComplete();

        verify(filterChain).filter(exchange);
        assertEquals(null, exchange.getResponse().getStatusCode());
    }

    /**
     * Test that admin routes with an invalid API key are rejected with 401 Unauthorized
     */
    @Test
    void filter_AdminRouteWithInvalidApiKey_ReturnsUnauthorized() {
        // Arrange
        MockServerHttpRequest request = MockServerHttpRequest
                .get("/api/admin/users")
                .header("X-API-KEY", "wrong-api-key")
                .build();

        MockServerWebExchange exchange = MockServerWebExchange.from(request);

        // Act
        Mono<Void> result = apiKeyAuthFilter.filter(exchange, filterChain);

        // Assert
        StepVerifier.create(result)
                .verifyComplete();

        verify(filterChain, never()).filter(exchange);
        assertEquals(HttpStatus.UNAUTHORIZED, exchange.getResponse().getStatusCode());
    }

    /**
     * Test that non-admin routes bypass the API key validation check
     */
    @Test
    void filter_NonAdminRoute_SkipsApiKeyValidation() {
        // Arrange
        MockServerHttpRequest request = MockServerHttpRequest
                .get("/api/stickers/search")
                .build();

        MockServerWebExchange exchange = MockServerWebExchange.from(request);

        // Act
        Mono<Void> result = apiKeyAuthFilter.filter(exchange, filterChain);

        // Assert
        StepVerifier.create(result)
                .verifyComplete();

        verify(filterChain).filter(exchange);
        assertEquals(null, exchange.getResponse().getStatusCode());
    }
}