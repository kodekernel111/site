package com.kodekernel.site.controller;

import com.kodekernel.site.entity.Product;
import com.kodekernel.site.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/products")
@CrossOrigin
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping
    public List<Product> getAllProducts() {
        return productService.getAllProducts();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable UUID id) {
        Product product = productService.getProduct(id);
        if (product == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(product);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Product createProduct(@RequestBody Product product) {
        return productService.saveProduct(product);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Product> updateProduct(@PathVariable UUID id, @RequestBody Product productDetails) {
        Product product = productService.getProduct(id);
        if (product == null) {
            return ResponseEntity.notFound().build();
        }
        product.setTitle(productDetails.getTitle());
        product.setCategory(productDetails.getCategory());
        product.setPrice(productDetails.getPrice());
        product.setDescription(productDetails.getDescription());
        product.setLongDescription(productDetails.getLongDescription());
        product.setFeatures(productDetails.getFeatures());
        product.setSpecs(productDetails.getSpecs());
        product.setHighlights(productDetails.getHighlights());
        product.setFaqs(productDetails.getFaqs());
        product.setSupportTitle(productDetails.getSupportTitle());
        product.setSupportDescription(productDetails.getSupportDescription());
        product.setSupportButtonText(productDetails.getSupportButtonText());
        product.setSupportButtonLink(productDetails.getSupportButtonLink());
        
        product.setImageUrl(productDetails.getImageUrl());
        product.setButtonText(productDetails.getButtonText());
        product.setButtonLink(productDetails.getButtonLink());
        
        product.setShowDeliveryBadge(productDetails.isShowDeliveryBadge());
        product.setBetaBannerEnabled(productDetails.isBetaBannerEnabled());
        product.setBetaBannerMessage(productDetails.getBetaBannerMessage());
        
        return ResponseEntity.ok(productService.saveProduct(product));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteProduct(@PathVariable UUID id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok().build();
    }
}
