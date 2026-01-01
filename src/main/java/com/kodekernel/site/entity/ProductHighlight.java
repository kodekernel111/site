package com.kodekernel.site.entity;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductHighlight {
    private String icon; // Lucide icon name string
    private String title;
    private String description;
}
