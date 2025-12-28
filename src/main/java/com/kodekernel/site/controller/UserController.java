package com.kodekernel.site.controller;

import com.kodekernel.site.dto.UserDTO;
import com.kodekernel.site.entity.User;
import com.kodekernel.site.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserDTO>> getAllUsers(@RequestParam(required = false) String query) {
        List<User> users;
        if (query != null && !query.trim().isEmpty()) {
            System.out.println("Searching for users with query: " + query);
            users = userRepository.searchUsers(query.trim());
            System.out.println("Found " + users.size() + " users.");
        } else {
            users = userRepository.findAll();
        }
        return ResponseEntity.ok(users.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList()));
    }

    @GetMapping("/team")
    public ResponseEntity<List<UserDTO>> getTeam() {
        return ResponseEntity.ok(userRepository.findAllByShowOnTeamTrue().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList()));
    }

    @PutMapping("/profile")
    public ResponseEntity<UserDTO> updateProfile(@RequestBody UserDTO update, java.security.Principal principal) {
        User user = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        user.setBio(update.getBio());
        user.setProfilePic(update.getProfilePic());
        // User requested that only bio can be updated by the user themselves
        // Other details require admin intervention or different process
        // Do not update roles or showOnTeam here
        
        return ResponseEntity.ok(convertToDTO(userRepository.save(user)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDTO> updateUser(@PathVariable UUID id, @RequestBody UserDTO update) {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        
        if (update.getRoles() != null) user.setRoles(update.getRoles());
        user.setDisplayRole(update.getDisplayRole());
        user.setBio(update.getBio());
        user.setProfilePic(update.getProfilePic());
        user.setShowOnTeam(update.isShowOnTeam());
        
        return ResponseEntity.ok(convertToDTO(userRepository.save(user)));
    }

    private UserDTO convertToDTO(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .country(user.getCountry())
                .roles(user.getRoles())
                .displayRole(user.getDisplayRole())
                .bio(user.getBio())
                .showOnTeam(user.isShowOnTeam())
                .profilePic(user.getProfilePic())
                .build();
    }
}
