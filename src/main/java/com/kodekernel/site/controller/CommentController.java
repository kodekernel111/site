package com.kodekernel.site.controller;

import com.kodekernel.site.dto.CommentDTO;
import com.kodekernel.site.dto.CommentRequest;
import com.kodekernel.site.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.UUID;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/post/{postId}")
    public ResponseEntity<CommentDTO> addComment(
            @PathVariable UUID postId,
            @RequestBody CommentRequest request,
            Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(401).build();
        }
        return ResponseEntity.ok(commentService.addComment(postId, request.getContent(), principal.getName(), request.getParentId()));
    }

    @GetMapping("/post/{postId}")
    public ResponseEntity<Page<CommentDTO>> getComments(
            @PathVariable UUID postId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Principal principal) {
        String userEmail = (principal != null) ? principal.getName() : null;
        return ResponseEntity.ok(commentService.getComments(postId, PageRequest.of(page, size), userEmail));
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<?> deleteComment(@PathVariable UUID commentId, Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(401).build();
        }
        try {
            commentService.deleteComment(commentId, principal.getName());
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }
}
