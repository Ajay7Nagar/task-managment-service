package com.talentica.taskmanagement.service;

import com.talentica.taskmanagement.dto.request.UserRegistrationRequest;
import com.talentica.taskmanagement.dto.response.UserResponse;
import com.talentica.taskmanagement.entity.User;
import com.talentica.taskmanagement.enums.UserRole;
import com.talentica.taskmanagement.exception.DuplicateResourceException;
import com.talentica.taskmanagement.exception.ResourceNotFoundException;
import com.talentica.taskmanagement.repository.UserRepository;
import com.talentica.taskmanagement.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private User testUser;
    private UserRegistrationRequest testRequest;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPassword("encodedpassword");
        testUser.setFirstName("Test");
        testUser.setLastName("User");
        testUser.setRole(UserRole.DEVELOPER);
        testUser.setIsActive(true);
        testUser.setCreatedAt(LocalDateTime.now());
        testUser.setUpdatedAt(LocalDateTime.now());

        testRequest = new UserRegistrationRequest();
        testRequest.setUsername("testuser");
        testRequest.setEmail("test@example.com");
        testRequest.setPassword("password123");
        testRequest.setFirstName("Test");
        testRequest.setLastName("User");
        testRequest.setRole(UserRole.DEVELOPER);
    }

    @Test
    void registerUser_Success() {
        // Given
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedpassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        UserResponse response = userService.registerUser(testRequest);

        // Then
        assertNotNull(response);
        assertEquals(testUser.getId(), response.getId());
        assertEquals(testUser.getUsername(), response.getUsername());
        assertEquals(testUser.getEmail(), response.getEmail());
        assertEquals(testUser.getRole(), response.getRole());
        verify(userRepository).save(any(User.class));
        verify(passwordEncoder).encode("password123");
    }

    @Test
    void registerUser_DuplicateUsername_ThrowsException() {
        // Given
        when(userRepository.existsByUsername(anyString())).thenReturn(true);

        // When & Then
        assertThrows(DuplicateResourceException.class, () -> {
            userService.registerUser(testRequest);
        });
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void registerUser_DuplicateEmail_ThrowsException() {
        // Given
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        // When & Then
        assertThrows(DuplicateResourceException.class, () -> {
            userService.registerUser(testRequest);
        });
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void getUserById_Success() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        // When
        UserResponse response = userService.getUserById(1L);

        // Then
        assertNotNull(response);
        assertEquals(testUser.getId(), response.getId());
        assertEquals(testUser.getUsername(), response.getUsername());
    }

    @Test
    void getUserById_NotFound_ThrowsException() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> {
            userService.getUserById(1L);
        });
    }

    @Test
    void getUsersByRole_Success() {
        // Given
        List<User> users = Arrays.asList(testUser);
        when(userRepository.findByRole(UserRole.DEVELOPER)).thenReturn(users);

        // When
        List<UserResponse> responses = userService.getUsersByRole(UserRole.DEVELOPER);

        // Then
        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals(testUser.getUsername(), responses.get(0).getUsername());
    }

    @Test
    void updateUser_Success() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.existsByUsername("newusername")).thenReturn(false);
        when(userRepository.existsByEmail("newemail@example.com")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        testRequest.setUsername("newusername");
        testRequest.setEmail("newemail@example.com");

        // When
        UserResponse response = userService.updateUser(1L, testRequest);

        // Then
        assertNotNull(response);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void deactivateUser_Success() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        userService.deactivateUser(1L);

        // Then
        verify(userRepository).save(any(User.class));
    }

    @Test
    void activateUser_Success() {
        // Given
        testUser.setIsActive(false);
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        userService.activateUser(1L);

        // Then
        verify(userRepository).save(any(User.class));
    }

    @Test
    void existsByUsername_ReturnsTrue() {
        // Given
        when(userRepository.existsByUsername("testuser")).thenReturn(true);

        // When
        boolean exists = userService.existsByUsername("testuser");

        // Then
        assertTrue(exists);
    }

    @Test
    void existsByEmail_ReturnsFalse() {
        // Given
        when(userRepository.existsByEmail("test@example.com")).thenReturn(false);

        // When
        boolean exists = userService.existsByEmail("test@example.com");

        // Then
        assertFalse(exists);
    }
}