package com.example.stickers.stickers.service;

import com.example.stickers.stickers.model.User;
import com.example.stickers.stickers.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Test class for UserService using JUnit 5, Mockito and Project Reactor
 */
@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User testUser;

    /**
     * Setup test fixture with a predefined user
     */
    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setPassword("encodedPassword");
        testUser.setRole("USER");
    }

    /**
     * Test successful user creation when username is available
     */
    @Test
    void createUser_Success() {
        // Arrange
        String username = "newuser";
        String password = "password123";
        String role = "USER";
        
        when(userRepository.findByUsername(username)).thenReturn(Mono.empty());
        when(passwordEncoder.encode(password)).thenReturn("encodedPassword");
        
        User newUser = new User();
        newUser.setUsername(username);
        newUser.setPassword("encodedPassword");
        newUser.setRole(role);
        
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User savedUser = invocation.getArgument(0);
            savedUser.setId(2L);
            return Mono.just(savedUser);
        });

        // Act
        Mono<User> result = userService.createUser(username, password, role);

        // Assert
        StepVerifier.create(result)
            .expectNextMatches(user -> 
                user.getId() != null && 
                user.getUsername().equals(username) &&
                user.getPassword().equals("encodedPassword") &&
                user.getRole().equals(role)
            )
            .verifyComplete();
            
        verify(userRepository).findByUsername(username);
        verify(userRepository).save(any(User.class));
    }

    /**
     * Test user creation failure when username is already taken
     */
    @Test
    void createUser_UsernameTaken() {
        // Arrange
        String username = "testuser";
        String password = "password123";
        String role = "USER";
        
        when(userRepository.findByUsername(username)).thenReturn(Mono.just(testUser));

        // Act
        Mono<User> result = userService.createUser(username, password, role);

        // Assert
        StepVerifier.create(result)
            .expectErrorMatches(error -> 
                error instanceof RuntimeException && 
                error.getMessage().contains("already taken")
            )
            .verify();
            
        verify(userRepository).findByUsername(username);
        verify(userRepository, never()).save(any(User.class));
    }

    /**
     * Parameterized test for user creation with different roles
     */
    @ParameterizedTest
    @ValueSource(strings = {"ADMIN", "USER", "EDITOR"})
    void createUser_WithDifferentRoles(String role) {
        // Arrange
        String username = "roleuser";
        String password = "password123";
        
        when(userRepository.findByUsername(username)).thenReturn(Mono.empty());
        when(passwordEncoder.encode(password)).thenReturn("encodedPassword");
        
        User savedUser = new User();
        savedUser.setId(3L);
        savedUser.setUsername(username);
        savedUser.setPassword("encodedPassword");
        savedUser.setRole(role);
        
        when(userRepository.save(any(User.class))).thenReturn(Mono.just(savedUser));

        // Act
        Mono<User> result = userService.createUser(username, password, role);

        // Assert
        StepVerifier.create(result)
            .expectNextMatches(user -> user.getRole().equals(role))
            .verifyComplete();
    }
}