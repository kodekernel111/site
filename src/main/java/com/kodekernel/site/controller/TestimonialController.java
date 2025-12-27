package com.kodekernel.site.controller;

import com.kodekernel.site.entity.Testimonial;
import com.kodekernel.site.service.TestimonialService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/testimonials")
@RequiredArgsConstructor
public class TestimonialController {

    private final TestimonialService service;

    @GetMapping
    public ResponseEntity<List<Testimonial>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Testimonial> create(@RequestBody Testimonial t) {
        return ResponseEntity.ok(service.save(t));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Testimonial> update(@PathVariable UUID id, @RequestBody Testimonial t) {
        t.setId(id);
        return ResponseEntity.ok(service.save(t));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.ok().build();
    }
}
