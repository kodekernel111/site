package com.kodekernel.site.controller;

import com.kodekernel.site.dto.AuthorStatsDTO;
import com.kodekernel.site.dto.BlogPostDTO;
import com.kodekernel.site.dto.CreateBlogPostRequest;
import com.kodekernel.site.service.BlogPostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/blogs")
@RequiredArgsConstructor
public class BlogPostController {
    
    private final BlogPostService blogPostService;
    
    @PostMapping
    @PreAuthorize("hasAnyRole('WRITER', 'ADMIN')")
    public ResponseEntity<BlogPostDTO> createBlogPost(
            @Valid @RequestBody CreateBlogPostRequest request,
            Authentication authentication) {
        String userEmail = authentication.getName();
        BlogPostDTO createdPost = blogPostService.createBlogPost(request, userEmail);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdPost);
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('WRITER', 'ADMIN')")
    public ResponseEntity<BlogPostDTO> updateBlogPost(
            @PathVariable UUID id,
            @Valid @RequestBody CreateBlogPostRequest request,
            Authentication authentication) {
        String userEmail = authentication.getName();
        BlogPostDTO updatedPost = blogPostService.updateBlogPost(id, request, userEmail);
        return ResponseEntity.ok(updatedPost);
    }
    
    @GetMapping
    public ResponseEntity<Page<BlogPostDTO>> getAllPublishedPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "9") int size) {
        Page<BlogPostDTO> posts = blogPostService.getAllPublishedPosts(page, size);
        return ResponseEntity.ok(posts);
    }
    
    @GetMapping("/my-posts")
    @PreAuthorize("hasAnyRole('WRITER', 'ADMIN')")
    public ResponseEntity<List<BlogPostDTO>> getMyPosts(Authentication authentication) {
        String userEmail = authentication.getName();
        List<BlogPostDTO> posts = blogPostService.getMyPosts(userEmail);
        return ResponseEntity.ok(posts);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<BlogPostDTO> getPublishedPostById(@PathVariable UUID id) {
        BlogPostDTO post = blogPostService.getPublishedPostById(id);
        return ResponseEntity.ok(post);
    }
    
    @GetMapping("/{id}/edit")
    @PreAuthorize("hasAnyRole('WRITER', 'ADMIN')")
    public ResponseEntity<BlogPostDTO> getPostForEdit(
            @PathVariable UUID id,
            Authentication authentication) {
        String userEmail = authentication.getName();
        BlogPostDTO post = blogPostService.getPostById(id, userEmail);
        return ResponseEntity.ok(post);
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('WRITER', 'ADMIN')")
    public ResponseEntity<Void> deleteBlogPost(
            @PathVariable UUID id,
            Authentication authentication) {
        String userEmail = authentication.getName();
        blogPostService.deleteBlogPost(id, userEmail);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/search")
    public ResponseEntity<Page<BlogPostDTO>> searchPosts(
            @RequestParam String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "9") int size) {
        Page<BlogPostDTO> posts = blogPostService.searchPosts(q, page, size);
        return ResponseEntity.ok(posts);
    }
    
    @GetMapping("/tag/{tag}")
    public ResponseEntity<List<BlogPostDTO>> getPostsByTag(@PathVariable String tag) {
        List<BlogPostDTO> posts = blogPostService.getPostsByTag(tag);
        return ResponseEntity.ok(posts);
    }
    
    @GetMapping("/author/{email}/stats")
    public ResponseEntity<AuthorStatsDTO> getAuthorStats(@PathVariable String email) {
        AuthorStatsDTO stats = blogPostService.getAuthorStatsByEmail(email);
        return ResponseEntity.ok(stats);
    }
    
    @PostMapping("/{id}/view")
    public ResponseEntity<Void> incrementViewCount(@PathVariable UUID id) {
        blogPostService.incrementViewCount(id);
        return ResponseEntity.ok().build();
    }
    
    @PostMapping("/{id}/like")
    public ResponseEntity<Map<String, Object>> toggleLike(@PathVariable UUID id, @RequestParam(required = false, defaultValue = "false") boolean unlike) {
        boolean liked = blogPostService.updateLikeCount(id, !unlike);
        Long likeCount = blogPostService.getLikeCount(id);
        
        Map<String, Object> response = new HashMap<>();
        response.put("liked", liked);
        response.put("likeCount", likeCount);
        
        return ResponseEntity.ok(response);
    }
}
