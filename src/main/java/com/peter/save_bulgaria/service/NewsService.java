package com.peter.save_bulgaria.service;

import com.peter.save_bulgaria.model.News;

import java.util.List;
import java.util.Optional;

public interface NewsService {
    List<News> getAllNews();
    Optional<News> findById(Long id);
    News createNews(News news);
}
