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

    @Query("SELECT DISTINCT u FROM User u LEFT JOIN FETCH u.photoPairs")
    List<User> findAllWithPhotoPairs();

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.photoPairs WHERE u.id = :id")
    Optional<User> findByIdWithPhotoPairs(@Param("id") Long id);

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.photoPairs WHERE u.email = :email")
    Optional<User> findByEmailWithPhotoPairs(@Param("email") String email);

    @Query("SELECT COUNT(pp) FROM User u JOIN u.photoPairs pp WHERE u.id = :userId")
    Long countPhotoPairsByUserId(@Param("userId") Long userId);

    @Query("SELECT COUNT(pp) FROM User u JOIN u.photoPairs pp WHERE u.id = :userId AND pp.beforePhoto IS NOT NULL AND pp.afterPhoto IS NOT NULL")
    Long countCompletePhotoPairsByUserId(@Param("userId") Long userId);
}