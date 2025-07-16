package com.peter.save_bulgaria.service.impl;

import com.peter.save_bulgaria.dtos.UserDTO;
import com.peter.save_bulgaria.model.User;
import com.peter.save_bulgaria.repository.UsersRepository;
import com.peter.save_bulgaria.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UsersRepository usersRepository;

    @Override
    @Transactional(readOnly = true)
    public List<UserDTO> getAllUsers() {
        log.info("Fetching all users with their photo pairs");
        return usersRepository.findAllWithPhotoPairs().stream()
                .map(UserDTO::new)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findByEmail(String email) {
        log.info("Finding user by email: {}", email);
        return usersRepository.findByEmail(email);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findById(Long id) {
        log.info("Finding user by id: {}", id);
        return usersRepository.findById(id);
    }

    @Override
    @Transactional
    public User createUser(String googleId, String email, String name) {
        log.info("Creating new user with email: {}", email);

        if (existsByEmail(email)) {
            throw new RuntimeException("User with email " + email + " already exists");
        }

        User user = new User();
        user.setGoogleId(googleId);
        user.setEmail(email);
        user.setName(name);

        User savedUser = usersRepository.save(user);
        log.info("User created successfully with id: {}", savedUser.getId());
        return savedUser;
    }

    @Override
    @Transactional
    public User updateUser(Long id, String name, String email) {
        log.info("Updating user with id: {}", id);

        User user = usersRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        // Check if email is being changed and if it already exists
        if (!user.getEmail().equals(email) && existsByEmail(email)) {
            throw new RuntimeException("Email " + email + " is already taken by another user");
        }

        user.setName(name);
        user.setEmail(email);

        User updatedUser = usersRepository.save(user);
        log.info("User updated successfully with id: {}", updatedUser.getId());
        return updatedUser;
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        log.info("Deleting user with id: {}", id);

        if (!usersRepository.existsById(id)) {
            throw new RuntimeException("User not found with id: " + id);
        }

        usersRepository.deleteById(id);
        log.info("User deleted successfully with id: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return usersRepository.findByEmail(email).isPresent();
    }

    @Override
    @Transactional
    public User findOrCreateUserByGoogleAuth(String googleId, String email, String name) {
        log.info("Finding or creating user by Google auth for email: {}", email);

        Optional<User> existingUser = findByEmail(email);

        if (existingUser.isPresent()) {
            User user = existingUser.get();

            // Update Google ID if it's different (user might have reconnected)
            if (!googleId.equals(user.getGoogleId())) {
                user.setGoogleId(googleId);
                user = usersRepository.save(user);
                log.info("Updated Google ID for existing user: {}", email);
            }

            return user;
        } else {
            // Create new user
            return createUser(googleId, email, name);
        }
    }
}