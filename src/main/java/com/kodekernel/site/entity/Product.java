package com.kodekernel.site.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;
import java.util.UUID;

@Entity
@Data
@Table(name = "product_registry")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String title;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id")
    private ProductCategory category;

    private String price;
    
    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(columnDefinition = "TEXT")
    private String longDescription;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "registry_features", joinColumns = @JoinColumn(name = "product_id"))
    @Column(name = "feature")
    private List<String> features;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "registry_specs", joinColumns = @JoinColumn(name = "product_id"))
    @MapKeyColumn(name = "spec_key")
    @Column(name = "spec_value")
    private java.util.Map<String, String> specs;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "registry_tags", joinColumns = @JoinColumn(name = "product_id"))
    @Column(name = "tag")
    private List<String> tags;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "registry_highlights", joinColumns = @JoinColumn(name = "product_id"))
    private List<ProductHighlight> highlights;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "registry_faqs", joinColumns = @JoinColumn(name = "product_id"))
    private List<ProductFaq> faqs;

    private String supportTitle;
    @Column(columnDefinition = "TEXT")
    private String supportDescription;
    private String supportButtonText;
    private String supportButtonLink;

    private String imageUrl;
    private String buttonText;
    private String buttonLink;
    
    @Column(nullable = false)
    private boolean showDeliveryBadge = true;

    private boolean betaBannerEnabled = false;
    @Column(columnDefinition = "TEXT")
    private String betaBannerMessage;
}
