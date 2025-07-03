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
    private List<PhotoDTO> photos;

    public UserDTO(User user) {
        this.id = user.getId();
        this.name = user.getName();
        this.email = user.getEmail();
        this.photos = user.getPhotos() == null ? List.of() :
                user.getPhotos().stream().map(PhotoDTO::new).collect(Collectors.toList());
    }
}
