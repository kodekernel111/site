package com.kodekernel.site.service;

import com.kodekernel.site.dto.PricingPlanDTO;
import com.kodekernel.site.entity.PricingPlan;
import com.kodekernel.site.repository.PricingPlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PricingPlanService {

    private final PricingPlanRepository pricingPlanRepository;

    public List<PricingPlanDTO> getAllPlans() {
        return pricingPlanRepository.findAllByOrderByOrderAsc().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public PricingPlanDTO getPlanById(UUID id) {
        return pricingPlanRepository.findById(id)
                .map(this::mapToDTO)
                .orElseThrow(() -> new RuntimeException("Price plan not found"));
    }

    public PricingPlanDTO createPlan(PricingPlanDTO dto) {
        PricingPlan plan = PricingPlan.builder()
                .name(dto.getName())
                .price(dto.getPrice())
                .period(dto.getPeriod())
                .description(dto.getDescription())
                .features(dto.getFeatures())
                .popular(dto.isPopular())
                .order(dto.getOrder())
                .build();
        
        PricingPlan saved = pricingPlanRepository.save(plan);
        return mapToDTO(saved);
    }

    public PricingPlanDTO updatePlan(UUID id, PricingPlanDTO dto) {
        PricingPlan plan = pricingPlanRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Price plan not found"));
        
        plan.setName(dto.getName());
        plan.setPrice(dto.getPrice());
        plan.setPeriod(dto.getPeriod());
        plan.setDescription(dto.getDescription());
        plan.setFeatures(dto.getFeatures());
        plan.setPopular(dto.isPopular());
        plan.setOrder(dto.getOrder());
        
        PricingPlan saved = pricingPlanRepository.save(plan);
        return mapToDTO(saved);
    }
    
    public void deletePlan(UUID id) {
        pricingPlanRepository.deleteById(id);
    }

    private PricingPlanDTO mapToDTO(PricingPlan plan) {
        return PricingPlanDTO.builder()
                .id(plan.getId())
                .name(plan.getName())
                .price(plan.getPrice())
                .period(plan.getPeriod())
                .description(plan.getDescription())
                .features(plan.getFeatures())
                .popular(plan.isPopular())
                .order(plan.getOrder())
                .build();
    }
}
