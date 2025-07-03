package com.peter.save_bulgaria.repository;

import com.peter.save_bulgaria.model.Photo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PhotosRepository extends JpaRepository<Photo, Long> {

    List<Photo> findByUserId(Long userId);

    @Query("SELECT p FROM Photo p WHERE p.user.email = :email")
    List<Photo> findByUserEmail(@Param("email") String email);

    @Query("SELECT COUNT(p) FROM Photo p WHERE p.user.id = :userId")
    Long countByUserId(@Param("userId") Long userId);

    @Query("SELECT p FROM Photo p WHERE p.user.id = :userId ORDER BY p.id DESC")
    List<Photo> findByUserIdOrderByIdDesc(@Param("userId") Long userId);

    void deleteByUserId(Long userId);
}