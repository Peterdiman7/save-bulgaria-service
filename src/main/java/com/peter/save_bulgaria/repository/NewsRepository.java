package com.peter.save_bulgaria.repository;

import com.peter.save_bulgaria.model.News;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface NewsRepository extends JpaRepository<News, UUID> {
}
