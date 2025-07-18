package com.peter.save_bulgaria.service.impl;

import com.peter.save_bulgaria.dtos.PhotoPairDTO;
import com.peter.save_bulgaria.exception.PhotoException;
import com.peter.save_bulgaria.exception.UserNotFoundException;
import com.peter.save_bulgaria.model.Photo;
import com.peter.save_bulgaria.model.PhotoPair;
import com.peter.save_bulgaria.model.User;
import com.peter.save_bulgaria.repository.PhotoPairRepository;
import com.peter.save_bulgaria.repository.PhotosRepository;
import com.peter.save_bulgaria.repository.UsersRepository;
import com.peter.save_bulgaria.service.PhotoPairService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PhotoPairServiceImpl implements PhotoPairService {

    private final PhotoPairRepository photoPairRepository;
    private final PhotosRepository photosRepository;
    private final UsersRepository usersRepository;

    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB
    private static final List<String> ALLOWED_CONTENT_TYPES = List.of(
            "image/jpeg", "image/jpg", "image/png", "image/gif", "image/webp"
    );

    @Override
    @Transactional(readOnly = true)
    public List<PhotoPairDTO> getAllPhotoPairs() {
        log.info("Fetching all photo pairs");
        return photoPairRepository.findAll().stream()
                .map(PhotoPairDTO::new)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PhotoPairDTO> getPhotoPairsByUserEmail(String email) {
        log.info("Fetching photo pairs for user: {}", email);
        return photoPairRepository.findByUserEmailWithPhotosOrderByCreatedAtDesc(email).stream()
                .map(PhotoPairDTO::new)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<PhotoPairDTO> getPhotoPairById(Long id) {
        log.info("Fetching photo pair by id: {}", id);
        return photoPairRepository.findByIdWithPhotos(id)
                .map(PhotoPairDTO::new);
    }

    @Override
    @Transactional
    public PhotoPairDTO createPhotoPair(String email, String title, String description) {
        log.info("Creating photo pair for user: {}", email);

        User user = usersRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("email", email));

        PhotoPair photoPair = new PhotoPair();
        photoPair.setUser(user);
        photoPair.setTitle(title);
        photoPair.setDescription(description);

        PhotoPair savedPhotoPair = photoPairRepository.save(photoPair);
        log.info("Photo pair created successfully with id: {}", savedPhotoPair.getId());

        return new PhotoPairDTO(savedPhotoPair);
    }

    @Override
    @Transactional
    public PhotoPairDTO uploadBeforePhoto(Long photoPairId, MultipartFile file) throws IOException {
        log.info("Uploading before photo for photo pair: {}", photoPairId);

        PhotoPair photoPair = photoPairRepository.findById(photoPairId)
                .orElseThrow(() -> new PhotoException("Photo pair not found with id: " + photoPairId));

        validateFile(file);

        // Create and save photo
        Photo photo = createPhotoFromFile(file);
        Photo savedPhoto = photosRepository.save(photo);

        // Update photo pair
        photoPair.setBeforePhoto(savedPhoto);
        PhotoPair updatedPhotoPair = photoPairRepository.save(photoPair);

        log.info("Before photo uploaded successfully for photo pair: {}", photoPairId);
        return new PhotoPairDTO(updatedPhotoPair);
    }

    @Override
    @Transactional
    public PhotoPairDTO uploadAfterPhoto(Long photoPairId, MultipartFile file) throws IOException {
        log.info("Uploading after photo for photo pair: {}", photoPairId);

        PhotoPair photoPair = photoPairRepository.findById(photoPairId)
                .orElseThrow(() -> new PhotoException("Photo pair not found with id: " + photoPairId));

        validateFile(file);

        // Create and save photo
        Photo photo = createPhotoFromFile(file);
        Photo savedPhoto = photosRepository.save(photo);

        // Update photo pair
        photoPair.setAfterPhoto(savedPhoto);
        PhotoPair updatedPhotoPair = photoPairRepository.save(photoPair);

        log.info("After photo uploaded successfully for photo pair: {}", photoPairId);
        return new PhotoPairDTO(updatedPhotoPair);
    }

    @Override
    @Transactional
    public PhotoPairDTO updatePhotoPair(Long photoPairId, String title, String description) {
        log.info("Updating photo pair: {}", photoPairId);

        PhotoPair photoPair = photoPairRepository.findById(photoPairId)
                .orElseThrow(() -> new PhotoException("Photo pair not found with id: " + photoPairId));

        if (title != null) {
            photoPair.setTitle(title);
        }
        if (description != null) {
            photoPair.setDescription(description);
        }

        PhotoPair updatedPhotoPair = photoPairRepository.save(photoPair);
        log.info("Photo pair updated successfully: {}", photoPairId);

        return new PhotoPairDTO(updatedPhotoPair);
    }

    @Override
    @Transactional
    public String deletePhotoPair(Long photoPairId) {
        log.info("Deleting photo pair: {}", photoPairId);

        PhotoPair photoPair = photoPairRepository.findById(photoPairId)
                .orElseThrow(() -> new PhotoException("Photo pair not found with id: " + photoPairId));

        // Delete associated photos first
        if (photoPair.getBeforePhoto() != null) {
            photosRepository.delete(photoPair.getBeforePhoto());
        }
        if (photoPair.getAfterPhoto() != null) {
            photosRepository.delete(photoPair.getAfterPhoto());
        }

        // Delete photo pair
        photoPairRepository.delete(photoPair);

        log.info("Photo pair deleted successfully: {}", photoPairId);
        return "Photo pair with id: " + photoPairId + " deleted successfully!";
    }

    @Override
    @Transactional(readOnly = true)
    public byte[] getPhotoData(Long photoId) {
        log.info("Fetching photo data for photo id: {}", photoId);

        Photo photo = photosRepository.findById(photoId)
                .orElseThrow(() -> new PhotoException("Photo not found with id: " + photoId));

        return photo.getData();
    }

    private Photo createPhotoFromFile(MultipartFile file) throws IOException {
        Photo photo = new Photo();
        photo.setFilename(file.getOriginalFilename());
        photo.setContentType(file.getContentType());
        photo.setData(file.getBytes());
        return photo;
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new PhotoException("Cannot upload empty file");
        }

        // Check file size
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new PhotoException("File size too large. Maximum " + (MAX_FILE_SIZE / (1024 * 1024)) + "MB allowed.");
        }

        // Check file type
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType.toLowerCase())) {
            throw new PhotoException("Only image files are allowed. Supported types: " + ALLOWED_CONTENT_TYPES);
        }

        // Check filename
        String filename = file.getOriginalFilename();
        if (filename == null || filename.trim().isEmpty()) {
            throw new PhotoException("Filename cannot be empty");
        }

        log.info("File validation passed for: {} (size: {} bytes, type: {})", filename, file.getSize(), contentType);
    }
}