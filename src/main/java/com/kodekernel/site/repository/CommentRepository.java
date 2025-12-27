package com.kodekernel.site.repository;

import com.kodekernel.site.entity.Comment;
import com.kodekernel.site.entity.BlogPost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.EntityGraph;

@Repository
public interface CommentRepository extends JpaRepository<Comment, UUID> {
    @EntityGraph(attributePaths = {"author"})
    Page<Comment> findByBlogPostOrderByCreatedAtDesc(BlogPost blogPost, Pageable pageable);
}
