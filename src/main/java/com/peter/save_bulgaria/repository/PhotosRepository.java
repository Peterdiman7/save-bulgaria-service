package com.peter.save_bulgaria.repository;

import com.peter.save_bulgaria.model.Photo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PhotosRepository extends JpaRepository<Photo, Long> {
    // Basic repository methods are provided by JpaRepository
    // Since Photo entity doesn't have a user relationship,
    // user-specific photo queries should be handled through PhotoPair entity
}