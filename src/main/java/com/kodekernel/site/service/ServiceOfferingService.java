package com.kodekernel.site.service;

import com.kodekernel.site.entity.ServiceOffering;
import com.kodekernel.site.repository.ServiceOfferingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ServiceOfferingService {
    private final ServiceOfferingRepository repository;

    public List<ServiceOffering> getAll() {
        return repository.findAllByOrderByDisplayOrderAsc();
    }
    
    public ServiceOffering save(ServiceOffering s) {
        return repository.save(s);
    }
    
    public void delete(UUID id) {
        repository.deleteById(id);
    }
}
