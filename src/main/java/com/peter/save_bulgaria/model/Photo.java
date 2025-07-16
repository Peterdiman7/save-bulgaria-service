package com.peter.save_bulgaria.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "photos")
public class Photo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String filename;

    private String contentType;

    @Lob
    private byte[] data;

}