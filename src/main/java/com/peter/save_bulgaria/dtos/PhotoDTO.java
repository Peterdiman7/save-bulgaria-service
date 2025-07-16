package com.peter.save_bulgaria.dtos;

import com.peter.save_bulgaria.model.Photo;
import lombok.Data;

@Data
public class PhotoDTO {
    private Long id;
    private String filename;
    private String contentType;
    private long fileSize;

    public PhotoDTO(Photo photo) {
        this.id = photo.getId();
        this.filename = photo.getFilename();
        this.contentType = photo.getContentType();
        this.fileSize = photo.getData() != null ? photo.getData().length : 0;
    }
}