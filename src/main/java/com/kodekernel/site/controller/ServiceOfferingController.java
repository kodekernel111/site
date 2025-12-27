package com.kodekernel.site.controller;

import com.kodekernel.site.entity.ServiceOffering;
import com.kodekernel.site.service.ServiceOfferingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/services")
@RequiredArgsConstructor
public class ServiceOfferingController {

    private final ServiceOfferingService service;

    @GetMapping
    public ResponseEntity<List<ServiceOffering>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ServiceOffering> create(@RequestBody ServiceOffering s) {
        return ResponseEntity.ok(service.save(s));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ServiceOffering> update(@PathVariable UUID id, @RequestBody ServiceOffering s) {
        s.setId(id);
        return ResponseEntity.ok(service.save(s));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.ok().build();
    }
}
