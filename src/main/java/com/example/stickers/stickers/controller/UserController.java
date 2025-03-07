package com.example.stickers.stickers.controller;

import com.example.stickers.stickers.model.User;
import com.example.stickers.stickers.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;

import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class UserController {

    @Autowired
    private UserService userService;

    // Lista todos los usuarios - solo para administradores
    @GetMapping("/users")
    public Flux<User> getAllUsers() {
        return userService.findAllUsers();
    }

    // Crea un nuevo usuario - solo para administradores
    @PostMapping("/users")
    public Mono<ResponseEntity<User>> createUser(@RequestBody Map<String, String> payload) {
        String username = payload.get("username");
        String password = payload.get("password");
        String role = payload.get("role");
        
        if (username == null || password == null || role == null) {
            return Mono.just(ResponseEntity.badRequest().build());
        }
        
        return userService.createUser(username, password, role)
                .map(ResponseEntity::ok)
                .onErrorResume(e -> Mono.just(ResponseEntity.badRequest().build()));
    }
    
    // Cambia la contraseña de cualquier usuario - solo para administradores
    @PostMapping("/users/change-password")
    public Mono<ResponseEntity<Map<String, String>>> changePassword(@RequestBody Map<String, String> payload) {
        String username = payload.get("username");
        String newPassword = payload.get("newPassword");
        
        if (username == null || newPassword == null) {
            return Mono.just(ResponseEntity.badRequest()
                    .body(Map.of("error", "Se requiere nombre de usuario y contraseña")));
        }
        
        return userService.changePassword(username, newPassword)
                .map(user -> ResponseEntity.ok(Map.of(
                    "message", "Contraseña actualizada correctamente para " + username,
                    "timestamp", String.valueOf(System.currentTimeMillis())
                )))
                .onErrorResume(e -> Mono.just(ResponseEntity.badRequest()
                        .body(Map.of("error", e.getMessage()))));
    }
}