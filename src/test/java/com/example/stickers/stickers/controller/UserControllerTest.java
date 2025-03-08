package com.example.stickers.stickers.controller;

import com.example.stickers.stickers.model.User;
import com.example.stickers.stickers.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @Test
    void getAllUsers_ShouldReturnAllUsers() {
        // Arrange
        User user1 = new User();
        user1.setId(1L);
        user1.setUsername("admin");
        user1.setRole("ADMIN");
        
        User user2 = new User();
        user2.setId(2L);
        user2.setUsername("user");
        user2.setRole("USER");
        
        Mockito.when(userService.findAllUsers())
               .thenReturn(Flux.fromIterable(Arrays.asList(user1, user2)));
               
        // Act & Assert
        Flux<User> result = userController.getAllUsers();
        
        StepVerifier.create(result)
                .expectNext(user1)
                .expectNext(user2)
                .expectComplete()
                .verify();
    }

    @Test
    void createUser_ShouldCreateNewUser() {
        // Arrange
        Map<String, String> payload = new HashMap<>();
        payload.put("username", "newuser");
        payload.put("password", "password123");
        payload.put("role", "USER");
        
        User newUser = new User();
        newUser.setId(3L);
        newUser.setUsername("newuser");
        newUser.setRole("USER");
        
        Mockito.when(userService.createUser("newuser", "password123", "USER"))
               .thenReturn(Mono.just(newUser));
               
        // Act & Assert
        Mono<ResponseEntity<User>> result = userController.createUser(payload);
        
        StepVerifier.create(result)
                .expectNextMatches(response -> 
                    response.getStatusCode().is2xxSuccessful() && 
                    response.getBody() != null &&
                    response.getBody().getUsername().equals("newuser"))
                .expectComplete()
                .verify();
    }
}