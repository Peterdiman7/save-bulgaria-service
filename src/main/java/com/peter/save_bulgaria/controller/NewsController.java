package com.peter.save_bulgaria.controller;

import com.peter.save_bulgaria.model.News;
import com.peter.save_bulgaria.service.NewsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/api/news")
public class NewsController {

    @Autowired
    private NewsService newsService;

    @GetMapping
    public List<News> getAllNews() {
        return newsService.getAllNews();
    }

    @GetMapping("/{id}")
    public ResponseEntity<News> getNewsById(@PathVariable UUID id) {
        return newsService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public News createNews(@RequestBody News news) {
        System.out.println("Received: " + news);
        news.setCreatedAt(LocalDateTime.now());
        return newsService.createNews(news);
    }
}
