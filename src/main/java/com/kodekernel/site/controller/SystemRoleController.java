package com.kodekernel.site.controller;

import com.kodekernel.site.entity.SystemRole;
import com.kodekernel.site.repository.SystemRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
public class SystemRoleController {

    private final SystemRoleRepository systemRoleRepository;

    @GetMapping
    public ResponseEntity<List<SystemRole>> getAllRoles() {
        return ResponseEntity.ok(systemRoleRepository.findAll());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SystemRole> createRole(@RequestBody SystemRole role) {
        if (systemRoleRepository.findByName(role.getName()).isPresent()) {
            throw new RuntimeException("Role already exists");
        }
        return ResponseEntity.ok(systemRoleRepository.save(role));
    }
}
