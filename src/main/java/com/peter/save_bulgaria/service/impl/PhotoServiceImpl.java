package com.peter.save_bulgaria.service.impl;

import com.peter.save_bulgaria.model.Photo;
import com.peter.save_bulgaria.model.User;
import com.peter.save_bulgaria.repository.PhotosRepository;
import com.peter.save_bulgaria.service.PhotoService;
import com.peter.save_bulgaria.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PhotoServiceImpl implements PhotoService {

    private final PhotosRepository photosRepository;
    private final UserService userService;

    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB
    private static final List<String> ALLOWED_CONTENT_TYPES = List.of(
            "image/jpeg", "image/jpg", "image/png", "image/gif", "image/webp"
    );

    @Override
    @Transactional(readOnly = true)
    public List<Photo> getAllPhotos() {
        log.info("Fetching all photos");
        return photosRepository.findAll();
    }

    @Override
    @Transactional
    public Photo savePhotoForUser(String email, String name, MultipartFile file) throws IOException {
        log.info("Saving photo for user: {}", email);

        // Find user
        Optional<User> optionalUser = userService.findByEmail(email);
        User user = optionalUser.orElseThrow(() -> new RuntimeException(
                "User not found with email: " + email + ". Please login first before uploading photos."
        ));

        // Validate file
        validateFile(file);

        // Create and save photo
        Photo photo = new Photo();
        photo.setFilename(file.getOriginalFilename());
        photo.setContentType(file.getContentType());
        photo.setData(file.getBytes());
        photo.setUser(user);

        Photo savedPhoto = photosRepository.save(photo);
        log.info("Photo saved successfully with id: {} for user: {}", savedPhoto.getId(), email);
        return savedPhoto;
    }

    @Override
    @Transactional
    public String deletePhoto(Long id) {
        log.info("Deleting photo with id: {}", id);

        if (photosRepository.existsById(id)) {
            photosRepository.deleteById(id);
            log.info("Photo deleted successfully with id: {}", id);
            return "Photo with id: " + id + " deleted successfully!";
        } else {
            log.warn("Photo not found with id: {}", id);
            return "Photo with id: " + id + " not found!";
        }
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new RuntimeException("Cannot upload empty file");
        }

        // Check file size
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new RuntimeException("File size too large. Maximum " + (MAX_FILE_SIZE / (1024 * 1024)) + "MB allowed.");
        }

        // Check file type
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType.toLowerCase())) {
            throw new RuntimeException("Only image files are allowed. Supported types: " + ALLOWED_CONTENT_TYPES);
        }

        // Check filename
        String filename = file.getOriginalFilename();
        if (filename == null || filename.trim().isEmpty()) {
            throw new RuntimeException("Filename cannot be empty");
        }

        log.info("File validation passed for: {} (size: {} bytes, type: {})", filename, file.getSize(), contentType);
    }
}