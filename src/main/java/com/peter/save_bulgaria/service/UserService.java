package com.peter.save_bulgaria.service;

import com.peter.save_bulgaria.dtos.UserDTO;
import com.peter.save_bulgaria.model.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    List<UserDTO> getAllUsers();
    Optional<User> findByEmail(String email);
    Optional<User> findById(Long id);
    User createUser(String googleId, String email, String name);
    User updateUser(Long id, String name, String email);
    void deleteUser(Long id);
    boolean existsByEmail(String email);
    User findOrCreateUserByGoogleAuth(String googleId, String email, String name);
}