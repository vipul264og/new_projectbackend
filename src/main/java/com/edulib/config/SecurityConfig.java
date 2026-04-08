package com.edulib.config;

import com.edulib.security.JwtAuthEntryPoint;
import com.edulib.security.JwtAuthenticationFilter;
import com.edulib.security.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final CustomUserDetailsService userDetailsService;
    private final JwtAuthEntryPoint jwtAuthEntryPoint;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthFilter,
                          CustomUserDetailsService userDetailsService,
                          JwtAuthEntryPoint jwtAuthEntryPoint) {
        this.jwtAuthFilter    = jwtAuthFilter;
        this.userDetailsService = userDetailsService;
        this.jwtAuthEntryPoint  = jwtAuthEntryPoint;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .exceptionHandling(ex -> ex.authenticationEntryPoint(jwtAuthEntryPoint))
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth

                        // Internal Spring error forward — must always be permitted
                        .requestMatchers("/error").permitAll()

                        // ── Public ───────────────────────────────────────────────────────────
                        .requestMatchers("/api/v1/auth/**").permitAll()

                        .requestMatchers(HttpMethod.GET, "/api/v1/books").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/books/search").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/books/{id}").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/books/*/reviews").permitAll()

                        // ── ADMIN only ───────────────────────────────────────────────────────
                        .requestMatchers(HttpMethod.POST,   "/api/v1/books").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT,    "/api/v1/books/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/books/**").hasRole("ADMIN")

                        // ── Authenticated ────────────────────────────────────────────────────
                        .requestMatchers("/api/v1/books/*/download").authenticated()

                        .requestMatchers(HttpMethod.POST,   "/api/v1/books/*/reviews").authenticated()
                        .requestMatchers(HttpMethod.PUT,    "/api/v1/reviews/**").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/reviews/**").authenticated()
                        .requestMatchers(HttpMethod.GET,    "/api/v1/reviews/my").authenticated()

                        // User profile, password, dashboard
                        .requestMatchers("/api/v1/users/**").authenticated()

                        .anyRequest().authenticated()
                )
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
            throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }
}
