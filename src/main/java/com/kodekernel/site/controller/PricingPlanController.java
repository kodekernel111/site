package com.kodekernel.site.controller;

import com.kodekernel.site.dto.PricingPlanDTO;
import com.kodekernel.site.service.PricingPlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/pricing-plans")
@RequiredArgsConstructor
public class PricingPlanController {

    private final PricingPlanService pricingPlanService;

    @GetMapping
    public ResponseEntity<List<PricingPlanDTO>> getAllPlans() {
        return ResponseEntity.ok(pricingPlanService.getAllPlans());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PricingPlanDTO> getPlanById(@PathVariable UUID id) {
        return ResponseEntity.ok(pricingPlanService.getPlanById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PricingPlanDTO> createPlan(@RequestBody PricingPlanDTO dto) {
        return ResponseEntity.ok(pricingPlanService.createPlan(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PricingPlanDTO> updatePlan(@PathVariable UUID id, @RequestBody PricingPlanDTO dto) {
        return ResponseEntity.ok(pricingPlanService.updatePlan(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletePlan(@PathVariable UUID id) {
        pricingPlanService.deletePlan(id);
        return ResponseEntity.ok().build();
    }
}
