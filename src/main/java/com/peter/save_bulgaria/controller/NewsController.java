package com.peter.save_bulgaria.controller;

import com.peter.save_bulgaria.model.News;
import com.peter.save_bulgaria.service.NewsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/news")
@RequiredArgsConstructor
@Slf4j
public class NewsController {

    private final NewsService newsService;

    @GetMapping
    public ResponseEntity<List<News>> getAllNews() {
        try {
            log.info("GET /api/news - Fetching all news");
            List<News> news = newsService.getAllNews();
            return ResponseEntity.ok(news);
        } catch (Exception e) {
            log.error("Error fetching all news", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getNewsById(@PathVariable UUID id) {
        try {
            log.info("GET /api/news/{} - Fetching news by id", id);
            return newsService.findById(id)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            log.error("Error fetching news by id: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to fetch news: " + e.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<?> createNews(@RequestBody News news) {
        try {
            log.info("POST /api/news - Creating news: {}", news.getTitle());

            // Validate required fields
            if (news.getTitle() == null || news.getTitle().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Title is required"));
            }
            if (news.getCategory() == null || news.getCategory().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Category is required"));
            }
            if (news.getContent() == null || news.getContent().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Content is required"));
            }

            // Set creation time if not provided
            if (news.getCreatedAt() == null) {
                news.setCreatedAt(LocalDateTime.now());
            }

            News savedNews = newsService.createNews(news);
            log.info("News created successfully with id: {}", savedNews.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(savedNews);

        } catch (Exception e) {
            log.error("Error creating news", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to create news: " + e.getMessage()));
        }
    }
}