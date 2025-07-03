package com.peter.save_bulgaria.service.impl;

import com.peter.save_bulgaria.model.Photo;
import com.peter.save_bulgaria.model.User;
import com.peter.save_bulgaria.repository.PhotosRepository;
import com.peter.save_bulgaria.repository.UsersRepository;
import com.peter.save_bulgaria.service.PhotoService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PhotoServiceImpl implements PhotoService {

    @Autowired
    private PhotosRepository photosRepository;

    @Autowired
    private UsersRepository usersRepository;

    public List<Photo> getAllPhotos() {
        return photosRepository.findAll();
    }

    public Photo savePhotoForUser(String email, String name, MultipartFile file) throws IOException {
        // Find existing user by email
        User user = usersRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email +
                        ". Please login first before uploading photos."));

        // Validate file
        if (file.isEmpty()) {
            throw new RuntimeException("Cannot upload empty file");
        }

        // Check file size (e.g., max 10MB)
        if (file.getSize() > 10 * 1024 * 1024) {
            throw new RuntimeException("File size too large. Maximum 10MB allowed.");
        }

        // Check file type
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new RuntimeException("Only image files are allowed");
        }

        Photo photo = new Photo();
        photo.setFilename(file.getOriginalFilename());
        photo.setContentType(file.getContentType());
        photo.setData(file.getBytes());
        photo.setUser(user);

        return photosRepository.save(photo);
    }

    public String deletePhoto(Long id) {
        if (photosRepository.existsById(id)) {
            photosRepository.deleteById(id);
            return "Photo with id: " + id + " deleted successfully!";
        } else {
            return "Photo with id: " + id + " not found!";
        }
    }
}