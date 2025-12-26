package com.kodekernel.site.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthorStatsDTO {
    private Long articleCount;
    private Long totalReaders;
    private Long totalLikes;
}
