package com.peter.save_bulgaria.service;

import com.peter.save_bulgaria.model.Photo;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface PhotoService {
    List<Photo> getAllPhotos();
    Photo savePhotoForUser(String email, String name, MultipartFile file) throws IOException;
    String deletePhoto(Long id);
}
