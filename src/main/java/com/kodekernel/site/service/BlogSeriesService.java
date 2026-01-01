package com.kodekernel.site.service;

import com.kodekernel.site.entity.BlogSeries;
import com.kodekernel.site.entity.User;
import com.kodekernel.site.repository.BlogSeriesRepository;
import com.kodekernel.site.repository.UserRepository;
import com.kodekernel.site.dto.BlogSeriesDTO;
import com.kodekernel.site.dto.BlogPostDTO;
import com.kodekernel.site.entity.BlogPost;
import lombok.RequiredArgsConstructor;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BlogSeriesService {

    private final BlogSeriesRepository blogSeriesRepository;
    private final UserRepository userRepository;

    public List<BlogSeries> getAllSeries() {
        return blogSeriesRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<BlogSeriesDTO> getAllSeriesWithPosts() {
        return blogSeriesRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private BlogSeriesDTO convertToDTO(BlogSeries series) {
        return BlogSeriesDTO.builder()
                .id(series.getId())
                .name(series.getName())
                .description(series.getDescription())
                .coverImage(series.getCoverImage())
                .posts(series.getPosts().stream().map(this::convertPostToDTO).collect(Collectors.toList()))
                .build();
    }

    private BlogPostDTO convertPostToDTO(BlogPost blogPost) {
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
                .authorProfilePic(blogPost.getAuthor().getProfilePic())
                .seriesId(blogPost.getSeries() != null ? blogPost.getSeries().getId() : null)
                .seriesName(blogPost.getSeries() != null ? blogPost.getSeries().getName() : null)
                .build();
    }

    public BlogSeries getSeriesById(UUID id) {
        return blogSeriesRepository.findById(id).orElseThrow(() -> new RuntimeException("Series not found"));
    }

    public BlogSeries getOrCreateSeries(String name, User creator) {
        Optional<BlogSeries> existing = blogSeriesRepository.findByName(name);
        if (existing.isPresent()) {
            return existing.get();
        }

        BlogSeries newSeries = BlogSeries.builder()
                .name(name)
                .creator(creator)
                .build();
        return blogSeriesRepository.save(newSeries);
    }
    
    @Transactional
    public BlogSeries createSeries(String name, String description, String coverImage, UUID creatorId) {
        if (blogSeriesRepository.findByName(name).isPresent()) {
             throw new RuntimeException("Series with this name already exists");
        }
        
        User creator = userRepository.findById(creatorId)
            .orElseThrow(() -> new RuntimeException("User not found"));
            
        BlogSeries series = BlogSeries.builder()
            .name(name)
            .description(description)
            .coverImage(coverImage)
            .creator(creator)
            .build();
            
        return blogSeriesRepository.save(series);
    }
}
