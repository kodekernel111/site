package com.kodekernel.site.service;

import com.kodekernel.site.entity.Testimonial;
import com.kodekernel.site.repository.TestimonialRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TestimonialService {
    private final TestimonialRepository repository;

    public List<Testimonial> getAll() {
        return repository.findAllByOrderByDisplayOrderAsc();
    }
    
    public Testimonial save(Testimonial t) {
        return repository.save(t);
    }
    
    public void delete(UUID id) {
        repository.deleteById(id);
    }
}
