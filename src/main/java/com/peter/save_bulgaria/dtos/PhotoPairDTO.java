package com.peter.save_bulgaria.dtos;

import com.peter.save_bulgaria.model.PhotoPair;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class PhotoPairDTO {
    private Long id;
    private PhotoDTO beforePhoto;
    private PhotoDTO afterPhoto;
    private String title;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean isComplete; // true if both before and after photos exist

    public PhotoPairDTO(PhotoPair photoPair) {
        this.id = photoPair.getId();
        this.beforePhoto = photoPair.getBeforePhoto() != null ? new PhotoDTO(photoPair.getBeforePhoto()) : null;
        this.afterPhoto = photoPair.getAfterPhoto() != null ? new PhotoDTO(photoPair.getAfterPhoto()) : null;
        this.title = photoPair.getTitle();
        this.description = photoPair.getDescription();
        this.createdAt = photoPair.getCreatedAt();
        this.updatedAt = photoPair.getUpdatedAt();
        this.isComplete = this.beforePhoto != null && this.afterPhoto != null;
    }
}