package com.kodekernel.site.controller;

import com.kodekernel.site.entity.BlogSeries;
import com.kodekernel.site.entity.User;
import com.kodekernel.site.service.BlogSeriesService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.Map;

@RestController
@RequestMapping("/api/blog-series")
@RequiredArgsConstructor
@CrossOrigin
public class BlogSeriesController {

    private final BlogSeriesService blogSeriesService;

    @GetMapping
    public ResponseEntity<List<BlogSeries>> getAllSeries() {
        return ResponseEntity.ok(blogSeriesService.getAllSeries());
    }
    
    @GetMapping("/with-posts")
    public ResponseEntity<List<com.kodekernel.site.dto.BlogSeriesDTO>> getAllSeriesWithPosts() {
        return ResponseEntity.ok(blogSeriesService.getAllSeriesWithPosts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<BlogSeries> getSeries(@PathVariable UUID id) {
        return ResponseEntity.ok(blogSeriesService.getSeriesById(id));
    }
    
    @PostMapping
    public ResponseEntity<BlogSeries> createSeries(@RequestBody Map<String, String> payload, @AuthenticationPrincipal User user) {
        String name = payload.get("name");
        String description = payload.get("description");
        String coverImage = payload.get("coverImage");
        
        return ResponseEntity.ok(blogSeriesService.createSeries(name, description, coverImage, user.getId()));
    }
}
