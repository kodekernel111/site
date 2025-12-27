package com.kodekernel.site.config;

import com.kodekernel.site.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

import static com.kodekernel.site.entity.Role.ADMIN;
import static com.kodekernel.site.entity.Role.WRITER;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfiguration {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;
    private final CorsConfigurationSource corsConfigurationSource;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(req ->
                        req
                                // Public endpoints
                                .requestMatchers("/api/auth/**", "/error", "/actuator/**").permitAll()
                                
                                // Blog reading - public GET access only
                                .requestMatchers(HttpMethod.GET, "/api/blogs/search").permitAll()
                                .requestMatchers(HttpMethod.GET, "/api/blogs/tag/**").permitAll()
                                .requestMatchers(HttpMethod.GET, "/api/blogs/author/{email}/stats").permitAll()
                                .requestMatchers(HttpMethod.GET, "/api/blogs/my-posts").authenticated()
                                .requestMatchers(HttpMethod.GET, "/api/blogs/{id}/edit").authenticated()
                                .requestMatchers(HttpMethod.GET, "/api/blogs/**").permitAll()
                                .requestMatchers(HttpMethod.GET, "/api/blogs").permitAll()
                                
                                // Comments - public GET, others authenticated
                                .requestMatchers(HttpMethod.GET, "/api/comments/**").permitAll()
                                .requestMatchers("/api/comments/**").authenticated()
                                
                                // Pricing and Services - public GET
                                .requestMatchers(HttpMethod.GET, "/api/pricing-plans/**").permitAll()
                                .requestMatchers(HttpMethod.GET, "/api/services/**").permitAll()
                                .requestMatchers(HttpMethod.GET, "/api/testimonials/**").permitAll()
                                .requestMatchers(HttpMethod.GET, "/api/users/team").permitAll()
                                
                                // View tracking - public POST access
                                .requestMatchers(HttpMethod.POST, "/api/blogs/{id}/view").permitAll()
                                .requestMatchers(HttpMethod.POST, "/api/blogs/{id}/like").permitAll()
                                
                                // Blog writing - requires WRITER role (handled by @PreAuthorize)
                                .requestMatchers(HttpMethod.POST, "/api/blogs").authenticated()
                                .requestMatchers(HttpMethod.PUT, "/api/blogs/**").authenticated()
                                .requestMatchers(HttpMethod.DELETE, "/api/blogs/**").authenticated()
                                
                                // Image upload - requires WRITER role (handled by @PreAuthorize)
                                .requestMatchers("/api/upload/**").authenticated()
                                
                                // Admin and management
                                .requestMatchers("/api/management/**").hasAnyRole(ADMIN.name(), WRITER.name())
                                .requestMatchers("/api/admin/**").hasRole(ADMIN.name())
                                
                                // Everything else requires authentication
                                .anyRequest().authenticated()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
