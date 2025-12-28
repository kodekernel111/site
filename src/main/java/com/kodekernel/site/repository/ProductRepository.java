package com.kodekernel.site.repository;

import com.kodekernel.site.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
