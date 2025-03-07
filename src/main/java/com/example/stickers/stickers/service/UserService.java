package com.example.stickers.stickers.service;

import com.example.stickers.stickers.model.User;
import com.example.stickers.stickers.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public Mono<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public Flux<User> findAllUsers() {
        return userRepository.findAll();
    }

    public Mono<User> createUser(String username, String password, String role) {
        return userRepository.findByUsername(username)
                .flatMap(existingUser -> Mono.<User>error(new RuntimeException("Username already taken")))
                .switchIfEmpty(Mono.<User>defer(() -> {
                    User newUser = new User(username, passwordEncoder.encode(password), role);
                    return userRepository.save(newUser);
                }));
    }
    
    /**
     * Cambia la contraseña de un usuario existente
     * @param username nombre de usuario
     * @param newPassword nueva contraseña (sin codificar)
     * @return el usuario actualizado
     */
    public Mono<User> changePassword(String username, String newPassword) {
        return userRepository.findByUsername(username)
                .switchIfEmpty(Mono.error(new RuntimeException("Usuario no encontrado")))
                .flatMap(user -> {
                    user.setPassword(passwordEncoder.encode(newPassword));
                    return userRepository.save(user);
                });
    }
}