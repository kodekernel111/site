package com.kodekernel.site.dto;

import lombok.AllArgsConstructor;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentDTO {
    private UUID id;
    private String content;
    private String authorName;
    private String authorInitials;
    private UUID authorId; 
    private String authorProfilePic; 
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;
    private boolean isOwner; // To let frontend know if delete button should be shown
    private UUID parentId;
    private java.util.List<CommentDTO> replies;
}
