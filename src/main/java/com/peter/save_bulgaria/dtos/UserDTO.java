package com.peter.save_bulgaria.dtos;

import com.peter.save_bulgaria.model.User;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

@Data
public class UserDTO {
    private Long id;
    private String name;
    private String email;
    private List<PhotoPairDTO> photoPairs;

    public UserDTO(User user) {
        this.id = user.getId();
        this.name = user.getName();
        this.email = user.getEmail();
        this.photoPairs = user.getPhotoPairs() == null ? List.of() :
                user.getPhotoPairs().stream().map(PhotoPairDTO::new).collect(Collectors.toList());
    }
}