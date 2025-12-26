package com.kodekernel.site.service;

import com.kodekernel.site.dto.AuthorStatsDTO;
import com.kodekernel.site.dto.BlogPostDTO;
import com.kodekernel.site.dto.CreateBlogPostRequest;
import com.kodekernel.site.entity.BlogPost;
import com.kodekernel.site.entity.User;
import com.kodekernel.site.repository.BlogPostRepository;
import com.kodekernel.site.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@Service
@RequiredArgsConstructor
public class BlogPostService {
    
    private final BlogPostRepository blogPostRepository;
    private final UserRepository userRepository;
    
    @Transactional
    public BlogPostDTO createBlogPost(CreateBlogPostRequest request, String userEmail) {
        User author = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        
        BlogPost blogPost = BlogPost.builder()
                .title(request.getTitle())
                .excerpt(request.getExcerpt())
                .content(request.getContent())
                .coverImage(request.getCoverImage())
                .tags(request.getTags() != null ? request.getTags() : List.of())
                .published(request.getPublished() != null ? request.getPublished() : false)
                .editorMode(request.getEditorMode())
                .author(author)
                .build();
        
        BlogPost savedPost = blogPostRepository.save(blogPost);
        return convertToDTO(savedPost);
    }
    
    @Transactional
    public BlogPostDTO updateBlogPost(UUID id, CreateBlogPostRequest request, String userEmail) {
        BlogPost blogPost = blogPostRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Blog post not found"));
        
        // Verify the user is the author
        if (!blogPost.getAuthor().getEmail().equals(userEmail)) {
            throw new RuntimeException("You are not authorized to update this blog post");
        }
        
        blogPost.setTitle(request.getTitle());
        blogPost.setExcerpt(request.getExcerpt());
        blogPost.setContent(request.getContent());
        blogPost.setCoverImage(request.getCoverImage());
        blogPost.setTags(request.getTags() != null ? request.getTags() : List.of());
        blogPost.setPublished(request.getPublished() != null ? request.getPublished() : false);
        blogPost.setEditorMode(request.getEditorMode());
        
        BlogPost updatedPost = blogPostRepository.save(blogPost);
        return convertToDTO(updatedPost);
    }
    
    @Transactional(readOnly = true)
    public Page<BlogPostDTO> getAllPublishedPosts(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return blogPostRepository.findByPublishedTrueOrderByPublishedAtDesc(pageable)
                .map(this::convertToDTO);
    }
    
    @Transactional(readOnly = true)
    public Page<BlogPostDTO> searchPosts(String query, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "publishedAt"));
        return blogPostRepository.searchPublishedPosts(query, pageable)
                .map(this::convertToDTO);
    }
    
    @Transactional(readOnly = true)
    public List<BlogPostDTO> getMyPosts(String userEmail) {
        User author = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        
        return blogPostRepository.findByAuthorOrderByCreatedAtDesc(author)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public BlogPostDTO getPublishedPostById(UUID id) {
        BlogPost blogPost = blogPostRepository.findByIdAndPublishedTrue(id)
                .orElseThrow(() -> new RuntimeException("Blog post not found"));
        return convertToDTO(blogPost);
    }
    
    @Transactional(readOnly = true)
    public BlogPostDTO getPostById(UUID id, String userEmail) {
        BlogPost blogPost = blogPostRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Blog post not found"));
        
        // Allow author to view unpublished posts
        if (!blogPost.getPublished() && !blogPost.getAuthor().getEmail().equals(userEmail)) {
            throw new RuntimeException("Blog post not found");
        }
        
        return convertToDTO(blogPost);
    }
    
    @Transactional
    public void deleteBlogPost(UUID id, String userEmail) {
        BlogPost blogPost = blogPostRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Blog post not found"));
        
        if (!blogPost.getAuthor().getEmail().equals(userEmail)) {
            throw new RuntimeException("You are not authorized to delete this blog post");
        }
        
        blogPostRepository.delete(blogPost);
    }
    

    
    @Transactional(readOnly = true)
    public List<BlogPostDTO> getPostsByTag(String tag) {
        return blogPostRepository.findByTagAndPublishedTrue(tag)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public AuthorStatsDTO getAuthorStats(User author) {
        Long articleCount = blogPostRepository.countPublishedByAuthor(author);
        Long totalReaders = blogPostRepository.sumViewsByAuthor(author);
        Long totalLikes = blogPostRepository.sumLikesByAuthor(author);
        
        return AuthorStatsDTO.builder()
                .articleCount(articleCount)
                .totalReaders(totalReaders)
                .totalLikes(totalLikes)
                .build();
    }
    
    @Transactional
    public void incrementViewCount(UUID id) {
        BlogPost blogPost = blogPostRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Blog post not found"));
        
        // Handle null viewCount (for old posts)
        Long currentViews = blogPost.getViewCount();
        if (currentViews == null) {
            currentViews = 0L;
        }
        
        blogPost.setViewCount(currentViews + 1);
        blogPostRepository.save(blogPost);
    }
    
    @Transactional
    public boolean updateLikeCount(UUID id, boolean increase) {
        BlogPost blogPost = blogPostRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Blog post not found"));
        
        // Handle null likeCount (for old posts)
        Long currentLikes = blogPost.getLikeCount();
        if (currentLikes == null) {
            currentLikes = 0L;
        }
        
        if (increase) {
            blogPost.setLikeCount(currentLikes + 1);
        } else {
            if (currentLikes > 0) {
                blogPost.setLikeCount(currentLikes - 1);
            }
        }
        
        blogPostRepository.save(blogPost);
        
        return increase;
    }
    
    @Transactional(readOnly = true)
    public Long getLikeCount(UUID id) {
        BlogPost blogPost = blogPostRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Blog post not found"));
        return blogPost.getLikeCount();
    }
    
    public AuthorStatsDTO getAuthorStatsByEmail(String email) {
        User author = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return getAuthorStats(author);
    }
    
    private BlogPostDTO convertToDTO(BlogPost blogPost) {
        return BlogPostDTO.builder()
                .id(blogPost.getId())
                .title(blogPost.getTitle())
                .excerpt(blogPost.getExcerpt())
                .content(blogPost.getContent())
                .coverImage(blogPost.getCoverImage())
                .tags(blogPost.getTags())
                .published(blogPost.getPublished())
                .editorMode(blogPost.getEditorMode())
                .createdAt(blogPost.getCreatedAt())
                .updatedAt(blogPost.getUpdatedAt())
                .publishedAt(blogPost.getPublishedAt())
                .viewCount(blogPost.getViewCount() != null ? blogPost.getViewCount() : 0L)
                .likeCount(blogPost.getLikeCount() != null ? blogPost.getLikeCount() : 0L)
                .authorId(blogPost.getAuthor().getId())
                .authorName(blogPost.getAuthor().getFirstName() + " " + blogPost.getAuthor().getLastName())
                .authorEmail(blogPost.getAuthor().getEmail())
                .build();
    }
}
