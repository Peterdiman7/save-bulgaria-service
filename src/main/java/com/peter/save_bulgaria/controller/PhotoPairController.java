package com.peter.save_bulgaria.controller;

import com.peter.save_bulgaria.dtos.PhotoPairDTO;
import com.peter.save_bulgaria.service.PhotoPairService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/photo-pairs")
@RequiredArgsConstructor
@Slf4j
public class PhotoPairController {

    private final PhotoPairService photoPairService;

    @GetMapping
    public ResponseEntity<List<PhotoPairDTO>> getAllPhotoPairs() {
        log.info("GET /api/photo-pairs - Fetching all photo pairs");
        List<PhotoPairDTO> photoPairs = photoPairService.getAllPhotoPairs();
        return ResponseEntity.ok(photoPairs);
    }

    @GetMapping("/user/{email}")
    public ResponseEntity<List<PhotoPairDTO>> getPhotoPairsByUserEmail(@PathVariable String email) {
        log.info("GET /api/photo-pairs/user/{} - Fetching photo pairs for user", email);
        List<PhotoPairDTO> photoPairs = photoPairService.getPhotoPairsByUserEmail(email);
        return ResponseEntity.ok(photoPairs);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PhotoPairDTO> getPhotoPairById(@PathVariable Long id) {
        log.info("GET /api/photo-pairs/{} - Fetching photo pair by id", id);
        return photoPairService.getPhotoPairById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createPhotoPair(
            @RequestPart("before") MultipartFile before,
            @RequestPart("after") MultipartFile after,
            @RequestPart("email") String email,
            @RequestPart(value = "title", required = false) String title,
            @RequestPart(value = "description", required = false) String description) {

        try {
            if (email == null || email.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Email is required"));
            }

            PhotoPairDTO photoPair = photoPairService.createPhotoPair(email, title, description);

            // Assuming your service can handle the file uploads too, or add file processing here:
            photoPairService.uploadBeforePhoto(photoPair.getId(), before);
            photoPairService.uploadAfterPhoto(photoPair.getId(), after);

            return ResponseEntity.status(HttpStatus.CREATED).body(photoPair);
        } catch (Exception e) {
            log.error("Error creating photo pair", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to create photo pair: " + e.getMessage()));
        }
    }


    @PostMapping("/{id}/before-photo")
    public ResponseEntity<?> uploadBeforePhoto(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
        try {
            if (file == null || file.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "File is required"));
            }

            PhotoPairDTO photoPair = photoPairService.uploadBeforePhoto(id, file);
            return ResponseEntity.ok(photoPair);
        } catch (IOException e) {
            log.error("Error uploading before photo", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to process file: " + e.getMessage()));
        } catch (RuntimeException e) {
            log.error("Error uploading before photo", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/{id}/after-photo")
    public ResponseEntity<?> uploadAfterPhoto(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
        try {
            if (file == null || file.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "File is required"));
            }

            PhotoPairDTO photoPair = photoPairService.uploadAfterPhoto(id, file);
            return ResponseEntity.ok(photoPair);
        } catch (IOException e) {
            log.error("Error uploading after photo", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to process file: " + e.getMessage()));
        } catch (RuntimeException e) {
            log.error("Error uploading after photo", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updatePhotoPair(@PathVariable Long id, @RequestBody Map<String, String> request) {
        try {
            String title = request.get("title");
            String description = request.get("description");

            PhotoPairDTO photoPair = photoPairService.updatePhotoPair(id, title, description);
            return ResponseEntity.ok(photoPair);
        } catch (RuntimeException e) {
            log.error("Error updating photo pair", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePhotoPair(@PathVariable Long id) {
        try {
            String message = photoPairService.deletePhotoPair(id);
            return ResponseEntity.ok(Map.of("message", message));
        } catch (Exception e) {
            log.error("Error deleting photo pair", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to delete photo pair: " + e.getMessage()));
        }
    }

    @GetMapping("/photos/{photoId}/data")
    public ResponseEntity<byte[]> getPhotoData(@PathVariable Long photoId) {
        try {
            byte[] photoData = photoPairService.getPhotoData(photoId);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_JPEG); // You might want to store and return the actual content type
            headers.setContentLength(photoData.length);

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(photoData);
        } catch (RuntimeException e) {
            log.error("Error fetching photo data", e);
            return ResponseEntity.notFound().build();
        }
    }
}