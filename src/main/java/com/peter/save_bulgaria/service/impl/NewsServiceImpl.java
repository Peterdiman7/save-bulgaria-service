package com.peter.save_bulgaria.service.impl;

import com.peter.save_bulgaria.model.News;
import com.peter.save_bulgaria.repository.NewsRepository;
import com.peter.save_bulgaria.service.NewsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class NewsServiceImpl implements NewsService {

    private final NewsRepository newsRepository;

    @Override
    public List<News> getAllNews() {
        return newsRepository.findAll();
    }

    @Override
    public Optional<News> findById(UUID id) {
        return newsRepository.findById(id);
    }

    @Override
    public News createNews(News news) {
        return newsRepository.save(news);
    }
}
