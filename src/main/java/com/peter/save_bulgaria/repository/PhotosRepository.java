package com.peter.save_bulgaria.repository;

import com.peter.save_bulgaria.model.Photo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PhotosRepository extends JpaRepository<Photo, Long> {}
