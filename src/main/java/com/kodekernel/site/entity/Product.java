package com.kodekernel.site.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

@Entity
@Data
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String category;
    private String price;
    
    @Column(columnDefinition = "TEXT")
    private String description;

    @ElementCollection
    private List<String> tags;

    private String imageUrl;
    private String buttonText;
    private String buttonLink;
}
