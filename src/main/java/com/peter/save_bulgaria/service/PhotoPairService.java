package com.peter.save_bulgaria.service;

import com.peter.save_bulgaria.dtos.PhotoPairDTO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface PhotoPairService {
    List<PhotoPairDTO> getAllPhotoPairs();
    List<PhotoPairDTO> getPhotoPairsByUserEmail(String email);
    PhotoPairDTO createPhotoPair(String email, String title, String description);
    PhotoPairDTO uploadBeforePhoto(Long photoPairId, MultipartFile file) throws IOException;
    PhotoPairDTO uploadAfterPhoto(Long photoPairId, MultipartFile file) throws IOException;
    PhotoPairDTO updatePhotoPair(Long photoPairId, String title, String description);
    String deletePhotoPair(Long photoPairId);
    Optional<PhotoPairDTO> getPhotoPairById(Long id);
    byte[] getPhotoData(Long photoId);
}