package com.peter.save_bulgaria.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "news")
public class News {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String title;

    private String category;

    @Column(columnDefinition = "TEXT")
    private String content;

    private LocalDateTime createdAt;
}