package com.peter.save_bulgaria.repository;

import com.peter.save_bulgaria.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsersRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Optional<User> findByGoogleId(String googleId);

    boolean existsByEmail(String email);

    boolean existsByGoogleId(String googleId);

    @Query("SELECT DISTINCT u FROM User u LEFT JOIN FETCH u.photos")
    List<User> findAllWithPhotos();

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.photos WHERE u.id = :id")
    Optional<User> findByIdWithPhotos(@Param("id") Long id);

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.photos WHERE u.email = :email")
    Optional<User> findByEmailWithPhotos(@Param("email") String email);

    @Query("SELECT COUNT(p) FROM User u JOIN u.photos p WHERE u.id = :userId")
    Long countPhotosByUserId(@Param("userId") Long userId);
}