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
}
