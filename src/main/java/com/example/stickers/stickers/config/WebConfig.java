package com.example.stickers.stickers.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.config.EnableWebFlux;
import org.springframework.web.reactive.config.ViewResolverRegistry;
import org.springframework.web.reactive.config.WebFluxConfigurer;
import org.springframework.web.server.i18n.LocaleContextResolver;
import org.thymeleaf.spring6.view.reactive.ThymeleafReactiveViewResolver;

@Configuration
@EnableWebFlux
public class WebConfig implements WebFluxConfigurer {

    private final ThymeleafReactiveViewResolver thymeleafReactiveViewResolver;
    
    @Autowired
    public WebConfig(ThymeleafReactiveViewResolver thymeleafReactiveViewResolver) {
        this.thymeleafReactiveViewResolver = thymeleafReactiveViewResolver;
        System.out.println("WebConfig initialized with ThymeleafReactiveViewResolver");
    }
    
    @Override
    public void configureViewResolvers(ViewResolverRegistry registry) {
        registry.viewResolver(thymeleafReactiveViewResolver);
    }
    
}