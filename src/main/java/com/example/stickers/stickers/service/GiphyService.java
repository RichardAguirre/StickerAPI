package com.example.stickers.stickers.service;

import com.example.stickers.stickers.model.SearchHistory;
import com.example.stickers.stickers.repository.SearchHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Locale;
import java.util.Map;

@Service
public class GiphyService {

    @Value("${giphy.api.url}")
    private String giphyUrl;

    @Value("${giphy.api.key}")
    private String apiKey;

    @Autowired
    private WebClient webClient;

    @Autowired
    private SearchHistoryRepository searchHistoryRepository;

    @Autowired
    private MessageSource messageSource;

    public Mono<Map<String, Object>> searchStickers(String query, Locale locale) {
        String translatedQuery = translateQuery(query, locale);
        String url = String.format("%s?api_key=%s&q=%s&limit=20", giphyUrl, apiKey, translatedQuery);

        SearchHistory search = new SearchHistory();
        search.setSearchTerm(translatedQuery);
        
        return searchHistoryRepository.save(search)
                .then(webClient.get()
                    .uri(url)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {}));
    }

    public Flux<Object> getStickersData(String query, Locale locale) {
        return searchStickers(query, locale)
                .flatMapMany(response -> {
                    if (response != null && response.containsKey("data")) {
                        return Flux.fromIterable((Iterable<Object>) response.get("data"));
                    }
                    return Flux.empty();
                });
    }

    private String translateQuery(String query, Locale locale) {
        return messageSource.getMessage(query, null, query, locale);
    }
}