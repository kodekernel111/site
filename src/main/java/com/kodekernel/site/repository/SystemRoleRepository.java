package com.kodekernel.site.repository;

import com.kodekernel.site.entity.SystemRole;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface SystemRoleRepository extends JpaRepository<SystemRole, UUID> {
    Optional<SystemRole> findByName(String name);
}
