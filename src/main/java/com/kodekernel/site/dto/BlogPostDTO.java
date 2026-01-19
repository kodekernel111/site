package com.kodekernel.site.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BlogPostDTO {
    private UUID id;
    private String title;
    private String excerpt;
    private String content;
    private String coverImage;
    private List<String> tags;
    private Boolean published;
    private String editorMode;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime publishedAt;
    
    // Engagement metrics
    private Long viewCount;
    private Long likeCount;
    
    // Author information
    private UUID authorId;
    private String authorName;
    private String authorEmail;
    private String authorBio;
    private java.util.Set<String> authorRoles;
    private String authorProfilePic;
    
    // Series Info
    private UUID seriesId;
    private String seriesName;
}
