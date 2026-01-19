package com.kodekernel.site.controller;

import com.kodekernel.site.entity.SystemConfig;
import com.kodekernel.site.repository.SystemConfigRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/config")
@RequiredArgsConstructor
@CrossOrigin("*")
public class SystemConfigController {

    private final SystemConfigRepository configRepository;

    @GetMapping
    public List<SystemConfig> getAllConfigs() {
        return configRepository.findAll();
    }

    @GetMapping("/{key}")
    public SystemConfig getConfig(@PathVariable String key) {
        return configRepository.findById(key)
                .orElse(SystemConfig.builder().configKey(key).configValue("").build());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public SystemConfig setConfig(@RequestBody SystemConfig config) {
        return configRepository.save(config);
    }
    
    @PostMapping("/batch")
    @PreAuthorize("hasRole('ADMIN')")
    public List<SystemConfig> setConfigs(@RequestBody List<SystemConfig> configs) {
        return configRepository.saveAll(configs);
    }
}
