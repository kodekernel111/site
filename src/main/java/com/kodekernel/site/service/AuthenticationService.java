package com.kodekernel.site.service;

import com.kodekernel.site.dto.*;
import com.kodekernel.site.entity.Role;
import com.kodekernel.site.entity.User;
import com.kodekernel.site.repository.UserRepository;
import com.kodekernel.site.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthenticationResponse register(RegisterRequest request) {
        if (repository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already in use");
        }
        var role = request.getRole() != null ? request.getRole() : "USER";
        var user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .country(request.getCountry())
                .password(passwordEncoder.encode(request.getPassword()))
                .roles(java.util.Set.of(role))
                .build();
        repository.save(user);
        
        var jwtToken = jwtService.generateToken(user);
        
        var userDTO = UserDTO.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .country(user.getCountry())
                .roles(user.getRoles())
                .bio(user.getBio())
                .build();

        return AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .user(userDTO)
                .build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        var user = repository.findByEmail(request.getEmail())
                .orElseThrow();
        
        var jwtToken = jwtService.generateToken(user);
        
        var userDTO = UserDTO.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .country(user.getCountry())
                .roles(user.getRoles())
                .build();

        return AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .user(userDTO)
                .build();
    }
    public void forgotPassword(ForgotPasswordRequest request) {
        var userOptional = repository.findByEmail(request.getEmail());
        if (userOptional.isEmpty()) {
            return; // Silently fail for security
        }
        var user = userOptional.get();
        String token = java.util.UUID.randomUUID().toString();
        user.setResetPasswordToken(token);
        user.setResetPasswordTokenExpiry(java.time.LocalDateTime.now().plusMinutes(15));
        repository.save(user); // Save to DB

        // MOCK EMAIL SENDING
        System.out.println("--------------------------------------------------");
        System.out.println("PASSWORD RESET LINK FOR: " + user.getEmail());
        System.out.println("http://localhost:5173/reset-password?token=" + token); 
        System.out.println("--------------------------------------------------");
    }

    public void resetPassword(ResetPasswordRequest request) {
        var user = repository.findByResetPasswordToken(request.getToken())
                .orElseThrow(() -> new IllegalArgumentException("Invalid or expired reset token"));

        if (user.getResetPasswordTokenExpiry().isBefore(java.time.LocalDateTime.now())) {
            throw new IllegalArgumentException("Token has expired");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setResetPasswordToken(null);
        user.setResetPasswordTokenExpiry(null);
        repository.save(user);
    }
    public void changePassword(ChangePasswordRequest request) {
        var user = repository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Incorrect old password");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        repository.save(user);
    }
}
