package com.talentica.taskmanagement.service;


import org.springframework.stereotype.Service;
import com.talentica.taskmanagement.dto.request.UserRegistrationRequest;
import com.talentica.taskmanagement.dto.response.UserResponse;
import com.talentica.taskmanagement.entity.User;
import com.talentica.taskmanagement.enums.UserRole;

import java.util.List;

public interface UserService {

    UserResponse registerUser(UserRegistrationRequest request);

    UserResponse getUserById(Long id);

    UserResponse getUserByUsername(String username);

    List<UserResponse> getAllUsers();

    List<UserResponse> getUsersByRole(UserRole role);

    UserResponse updateUser(Long id, UserRegistrationRequest request);

    void deactivateUser(Long id);

    void activateUser(Long id);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    User findUserEntityById(Long id);

    User findUserEntityByUsername(String username);

    List<UserResponse> searchUsers(String searchTerm);
}