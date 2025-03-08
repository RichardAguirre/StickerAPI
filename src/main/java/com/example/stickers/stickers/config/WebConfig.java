package com.example.stickers.stickers.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.config.EnableWebFlux;
import org.springframework.web.reactive.config.ViewResolverRegistry;
import org.springframework.web.reactive.config.WebFluxConfigurer;
import org.thymeleaf.spring6.view.reactive.ThymeleafReactiveViewResolver;

@Configuration
@EnableWebFlux
public class WebConfig implements WebFluxConfigurer {

    private final ThymeleafReactiveViewResolver thymeleafReactiveViewResolver;

    public WebConfig(ThymeleafReactiveViewResolver thymeleafReactiveViewResolver) {
        this.thymeleafReactiveViewResolver = thymeleafReactiveViewResolver;
        System.out.println("WebConfig initialized with ThymeleafReactiveViewResolver");
    }

    @Override
    public void configureViewResolvers(@SuppressWarnings("null") ViewResolverRegistry registry) {
        registry.viewResolver(thymeleafReactiveViewResolver);
    }

}