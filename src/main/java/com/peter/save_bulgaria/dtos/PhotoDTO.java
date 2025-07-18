package com.peter.save_bulgaria.dtos;

import com.peter.save_bulgaria.model.Photo;
import lombok.Data;

import java.util.Base64;

@Data
public class PhotoDTO {
    private Long id;
    private String filename;
    private String contentType;
    private long fileSize;
    private String data; // base64-encoded image

    public PhotoDTO(Photo photo) {
        this.id = photo.getId();
        this.filename = photo.getFilename();
        this.contentType = photo.getContentType();
        this.fileSize = photo.getData() != null ? photo.getData().length : 0;

        this.data = (photo.getData() != null)
                ? Base64.getEncoder().encodeToString(photo.getData())
                : null;
    }
}