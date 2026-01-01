package com.kodekernel.site.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateBlogPostRequest {
    
    @NotBlank(message = "Title is required")
    @Size(max = 500, message = "Title must not exceed 500 characters")
    private String title;
    
    @NotBlank(message = "Excerpt is required")
    @Size(max = 1000, message = "Excerpt must not exceed 1000 characters")
    private String excerpt;
    
    @NotBlank(message = "Content is required")
    private String content;
    
    @Size(max = 1000, message = "Cover image URL must not exceed 1000 characters")
    private String coverImage;
    
    private List<String> tags;
    
    private Boolean published;
    
    private String editorMode;
    
    private java.util.UUID seriesId;
}
