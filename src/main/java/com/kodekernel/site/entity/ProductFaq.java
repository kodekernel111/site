package com.kodekernel.site.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductFaq {
    private String question;
    @Column(columnDefinition = "TEXT")
    private String answer;
}
