package com.kodekernel.site.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BlogSeriesDTO {
    private UUID id;
    private String name;
    private String description;
    private String coverImage;
    private List<BlogPostDTO> posts;
}
