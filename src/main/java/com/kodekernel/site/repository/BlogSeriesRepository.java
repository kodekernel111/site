package com.kodekernel.site.repository;

import com.kodekernel.site.entity.BlogSeries;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface BlogSeriesRepository extends JpaRepository<BlogSeries, UUID> {
    Optional<BlogSeries> findByName(String name);
}
