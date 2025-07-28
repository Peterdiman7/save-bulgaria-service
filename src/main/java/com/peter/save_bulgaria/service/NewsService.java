package com.peter.save_bulgaria.service;

import com.peter.save_bulgaria.model.News;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface NewsService {
    List<News> getAllNews();
    Optional<News> findById(UUID id);
    News createNews(News news);
}
