package com.example.stickers.stickers.config;

import com.example.stickers.stickers.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInitializer {

    @Autowired
    private UserService userService;

    @Bean
    public CommandLineRunner init() {
        return args -> {
            userService.createUser("admin1", "password", "ADMIN")
                    .subscribe(
                            user -> System.out.println("Usuario creado: " + user.getUsername()),
                            error -> {
                                if (error.getMessage().contains("already taken")) {
                                    System.out.println("El usuario 'admin' ya existe");
                                } else {
                                    System.err.println("Error al crear usuario: " + error.getMessage());
                                }
                            }
                    );

            userService.createUser("user", "password", "USER")
                    .subscribe(
                            user -> System.out.println("Usuario creado: " + user.getUsername()),
                            error -> {
                                if (error.getMessage().contains("already taken")) {
                                    System.out.println("El usuario 'user' ya existe");
                                } else {
                                    System.err.println("Error al crear usuario: " + error.getMessage());
                                }
                            }
                    );
        };
    }
}