package com.kodekernel.site.repository;

import com.kodekernel.site.entity.BlogPost;
import com.kodekernel.site.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BlogPostRepository extends JpaRepository<BlogPost, UUID> {
    
    // Find all published blog posts with pagination
    Page<BlogPost> findByPublishedTrueOrderByPublishedAtDesc(Pageable pageable);
    
    // Keep List version for compatibility if needed, or removing it implies updating Service. 
    // I will remove the List version to force usage of pagination.
    
    // Find all blog posts by author
    List<BlogPost> findByAuthorOrderByCreatedAtDesc(User author);
    
    // Find published blog posts by author
    List<BlogPost> findByAuthorAndPublishedTrueOrderByPublishedAtDesc(User author);
    
    // Find blog post by ID and published status
    Optional<BlogPost> findByIdAndPublishedTrue(UUID id);
    
    // Search published blog posts by title, content, excerpt, author, or tags with pagination
    @Query("SELECT DISTINCT b FROM BlogPost b JOIN b.author a LEFT JOIN b.tags t WHERE b.published = true AND (" +
           "LOWER(b.title) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(b.content) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(b.excerpt) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(a.firstName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(a.lastName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(t) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    Page<BlogPost> searchPublishedPosts(@Param("searchTerm") String searchTerm, Pageable pageable);
    
    // Find blog posts by tag
    @Query("SELECT DISTINCT b FROM BlogPost b JOIN b.tags t WHERE t = :tag AND b.published = true ORDER BY b.publishedAt DESC")
    List<BlogPost> findByTagAndPublishedTrue(@Param("tag") String tag);
    
    // Count published articles by author
    @Query("SELECT COUNT(b) FROM BlogPost b WHERE b.author = :author AND b.published = true")
    Long countPublishedByAuthor(@Param("author") User author);
    
    // Sum total views for author's published posts
    @Query("SELECT COALESCE(SUM(b.viewCount), 0) FROM BlogPost b WHERE b.author = :author AND b.published = true")
    Long sumViewsByAuthor(@Param("author") User author);
    
    // Sum total likes for author's published posts
    @Query("SELECT COALESCE(SUM(b.likeCount), 0) FROM BlogPost b WHERE b.author = :author AND b.published = true")
    Long sumLikesByAuthor(@Param("author") User author);
}
