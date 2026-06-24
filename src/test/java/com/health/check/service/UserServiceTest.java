package com.health.check.service;

import com.health.check.exceptions.NotFoundException;
import com.health.check.models.User;
import com.health.check.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    private UserService userService;

    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userService = new UserService();
        userRepository = mock(UserRepository.class);

        // Covers setter injection
        userService.setUserRepository(userRepository);
    }

    @Test
    void setUserRepository_shouldInjectRepository() throws NotFoundException {
        User user = new User();
        user.setEmail("test@gmail.com");

        when(userRepository.findByEmail("test@gmail.com"))
                .thenReturn(user);

        User result = userService.getUserByEmail("test@gmail.com");

        assertNotNull(result);
        assertEquals("test@gmail.com", result.getEmail());
    }

    @Test
    void getUserByEmail_shouldReturnUser() throws NotFoundException {
        User user = new User();
        user.setId(1L);
        user.setEmail("test@gmail.com");

        when(userRepository.findByEmail("test@gmail.com"))
                .thenReturn(user);

        User result = userService.getUserByEmail("test@gmail.com");

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("test@gmail.com", result.getEmail());

        verify(userRepository).findByEmail("test@gmail.com");
    }

    @Test
    void getUserByEmail_shouldThrowNotFoundException() {
        when(userRepository.findByEmail("unknown@gmail.com"))
                .thenReturn(null);

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> userService.getUserByEmail("unknown@gmail.com")
        );

        assertEquals("User not found", exception.getMessage());

        verify(userRepository).findByEmail("unknown@gmail.com");
    }

    @Test
    void getUserById_shouldReturnUser() throws NotFoundException {
        User user = new User();
        user.setId(1L);

        when(userRepository.getUserById(1L))
                .thenReturn(user);

        User result = userService.getUserById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());

        verify(userRepository).getUserById(1L);
    }

    @Test
    void getUserById_shouldThrowNotFoundException() {
        when(userRepository.getUserById(1L))
                .thenReturn(null);

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> userService.getUserById(1L)
        );

        assertEquals("User not found", exception.getMessage());

        verify(userRepository).getUserById(1L);
    }
}