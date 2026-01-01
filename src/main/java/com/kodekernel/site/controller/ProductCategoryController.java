package com.kodekernel.site.controller;

import com.kodekernel.site.entity.ProductCategory;
import com.kodekernel.site.repository.ProductCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/product-categories")
@RequiredArgsConstructor
@CrossOrigin
public class ProductCategoryController {

    private final ProductCategoryRepository categoryRepository;

    @GetMapping
    public List<ProductCategory> getAllCategories() {
        return categoryRepository.findAll();
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductCategory> createCategory(@RequestBody ProductCategory category) {
        if (categoryRepository.findByName(category.getName()).isPresent()) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(categoryRepository.save(category));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductCategory> updateCategory(@PathVariable UUID id, @RequestBody ProductCategory details) {
        return categoryRepository.findById(id)
                .map(category -> {
                    category.setName(details.getName());
                    return ResponseEntity.ok(categoryRepository.save(category));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteCategory(@PathVariable UUID id) {
        categoryRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
