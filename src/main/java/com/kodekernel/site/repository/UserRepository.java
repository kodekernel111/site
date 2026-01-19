package com.kodekernel.site.repository;

import com.kodekernel.site.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);
    java.util.List<User> findAllByShowOnTeamTrue();
    
    @org.springframework.data.jpa.repository.Query("SELECT u FROM User u WHERE LOWER(u.firstName) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(u.lastName) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(u.email) LIKE LOWER(CONCAT('%', :query, '%'))")
    java.util.List<User> searchUsers(@org.springframework.data.repository.query.Param("query") String query);

    Optional<User> findByResetPasswordToken(String token);
}
