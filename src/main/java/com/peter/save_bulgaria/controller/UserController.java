package com.peter.save_bulgaria.controller;

import com.peter.save_bulgaria.dtos.UserDTO;
import com.peter.save_bulgaria.model.User;
import com.peter.save_bulgaria.repository.UsersRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UsersRepository usersRepository;

    @GetMapping
    @Transactional
    public List<UserDTO> getAllUsers() {
        // This already fetches users with their photos using the @Query annotation
        return usersRepository.findAllWithPhotos().stream()
                .map(UserDTO::new)
                .toList();
    }
}