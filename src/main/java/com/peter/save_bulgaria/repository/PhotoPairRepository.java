package com.peter.save_bulgaria.repository;

import com.peter.save_bulgaria.model.PhotoPair;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PhotoPairRepository extends JpaRepository<PhotoPair, Long> {

    List<PhotoPair> findByUserId(Long userId);

    @Query("SELECT pp FROM PhotoPair pp WHERE pp.user.email = :email ORDER BY pp.createdAt DESC")
    List<PhotoPair> findByUserEmailOrderByCreatedAtDesc(@Param("email") String email);

    @Query("SELECT pp FROM PhotoPair pp LEFT JOIN FETCH pp.beforePhoto LEFT JOIN FETCH pp.afterPhoto WHERE pp.user.id = :userId ORDER BY pp.createdAt DESC")
    List<PhotoPair> findByUserIdWithPhotosOrderByCreatedAtDesc(@Param("userId") Long userId);

    @Query("SELECT pp FROM PhotoPair pp LEFT JOIN FETCH pp.beforePhoto LEFT JOIN FETCH pp.afterPhoto WHERE pp.user.email = :email ORDER BY pp.createdAt DESC")
    List<PhotoPair> findByUserEmailWithPhotosOrderByCreatedAtDesc(@Param("email") String email);

    @Query("SELECT COUNT(pp) FROM PhotoPair pp WHERE pp.user.id = :userId")
    Long countByUserId(@Param("userId") Long userId);

    @Query("SELECT COUNT(pp) FROM PhotoPair pp WHERE pp.user.id = :userId AND pp.beforePhoto IS NOT NULL AND pp.afterPhoto IS NOT NULL")
    Long countCompletePhotosByUserId(@Param("userId") Long userId);

    void deleteByUserId(Long userId);

    @Query("SELECT pp FROM PhotoPair pp LEFT JOIN FETCH pp.beforePhoto LEFT JOIN FETCH pp.afterPhoto WHERE pp.id = :id")
    Optional<PhotoPair> findByIdWithPhotos(@Param("id") Long id);
}