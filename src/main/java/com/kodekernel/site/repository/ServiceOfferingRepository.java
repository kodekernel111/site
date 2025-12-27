package com.kodekernel.site.repository;

import com.kodekernel.site.entity.ServiceOffering;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ServiceOfferingRepository extends JpaRepository<ServiceOffering, UUID> {
    List<ServiceOffering> findAllByOrderByDisplayOrderAsc();
}
