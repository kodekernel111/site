package com.kodekernel.site.repository;

import com.kodekernel.site.entity.PricingPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PricingPlanRepository extends JpaRepository<PricingPlan, UUID> {
    List<PricingPlan> findAllByOrderByOrderAsc();
}
