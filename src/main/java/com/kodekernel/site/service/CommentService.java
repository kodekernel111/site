package com.kodekernel.site.service;

import com.kodekernel.site.dto.CommentDTO;
import com.kodekernel.site.entity.BlogPost;
import com.kodekernel.site.entity.Comment;
import com.kodekernel.site.entity.User;
import com.kodekernel.site.repository.BlogPostRepository;
import com.kodekernel.site.repository.CommentRepository;
import com.kodekernel.site.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final BlogPostRepository blogPostRepository;
    private final UserRepository userRepository;

    @Transactional
    public CommentDTO addComment(UUID postId, String content, String userEmail, UUID parentId) {
        User author = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        BlogPost blogPost = blogPostRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Blog post not found"));

        Comment parent = null;
        if (parentId != null) {
            parent = commentRepository.findById(parentId)
                    .orElseThrow(() -> new RuntimeException("Parent comment not found"));
        }

        Comment comment = Comment.builder()
                .content(content)
                .author(author)
                .blogPost(blogPost)
                .parent(parent)
                .build();

        Comment savedComment = commentRepository.save(comment);

        return convertToDTO(savedComment, author.getId());
    }

    @Transactional(readOnly = true)
    public Page<CommentDTO> getComments(UUID postId, Pageable pageable, String currentUserEmail) {
        BlogPost blogPost = blogPostRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Blog post not found"));

        User currentUser = null;
        if (currentUserEmail != null) {
            currentUser = userRepository.findByEmail(currentUserEmail).orElse(null);
        }
        final UUID currentUserId = currentUser != null ? currentUser.getId() : null;

        return commentRepository.findByBlogPostAndParentIsNullOrderByCreatedAtDesc(blogPost, pageable)
                .map(comment -> convertToDTO(comment, currentUserId));
    }

    @Transactional
    public void deleteComment(UUID commentId, String userEmail) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Only author or admin (if we had roles) can delete. For now just author.
        if (!comment.getAuthor().getId().equals(user.getId())) {
             throw new RuntimeException("Not authorized to delete this comment");
        }

        commentRepository.delete(comment);
    }

    private CommentDTO convertToDTO(Comment comment, UUID currentUserId) {
        String initials = "";
        if (comment.getAuthor().getFirstName() != null && !comment.getAuthor().getFirstName().isEmpty()) {
            initials += comment.getAuthor().getFirstName().charAt(0);
        }
        if (comment.getAuthor().getLastName() != null && !comment.getAuthor().getLastName().isEmpty()) {
            initials += comment.getAuthor().getLastName().charAt(0);
        }
        
        return CommentDTO.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .authorName(comment.getAuthor().getFirstName() + " " + comment.getAuthor().getLastName())
                .authorInitials(initials)
                .authorId(comment.getAuthor().getId())
                .authorProfilePic(comment.getAuthor().getProfilePic())
                .createdAt(comment.getCreatedAt())
                .isOwner(currentUserId != null && currentUserId.equals(comment.getAuthor().getId()))
                .parentId(comment.getParent() != null ? comment.getParent().getId() : null)
                .replies(comment.getReplies() != null ? 
                        comment.getReplies().stream()
                            .map(reply -> convertToDTO(reply, currentUserId))
                            .sorted((c1, c2) -> c1.getCreatedAt().compareTo(c2.getCreatedAt())) // Oldest replies first
                            .collect(Collectors.toList()) 
                        : null)
                .build();
    }
}
