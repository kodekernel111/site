package com.kodekernel.site.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DatabaseInitializer implements CommandLineRunner {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("Running Database Initializer...");
        
        // Create comments table
        jdbcTemplate.execute("""
            CREATE TABLE IF NOT EXISTS comments (
                id UUID NOT NULL,
                content TEXT NOT NULL,
                created_at TIMESTAMP,
                updated_at TIMESTAMP,
                author_id UUID NOT NULL,
                blog_post_id UUID NOT NULL,
                CONSTRAINT pk_comments PRIMARY KEY (id),
                CONSTRAINT fk_comments_users FOREIGN KEY (author_id) REFERENCES users (id),
                CONSTRAINT fk_comments_blog_posts FOREIGN KEY (blog_post_id) REFERENCES blog_posts (id)
            );
        """);

        // Add columns to blog_posts
        try {
            jdbcTemplate.execute("ALTER TABLE blog_posts ADD COLUMN IF NOT EXISTS view_count BIGINT DEFAULT 0 NOT NULL");
        } catch (Exception e) {
            System.out.println("Error adding view_count (might exist): " + e.getMessage());
        }

        try {
            jdbcTemplate.execute("ALTER TABLE blog_posts ADD COLUMN IF NOT EXISTS like_count BIGINT DEFAULT 0 NOT NULL");
        } catch (Exception e) {
            System.out.println("Error adding like_count (might exist): " + e.getMessage());
        }

        System.out.println("Database Initializer completed.");
    }
}
