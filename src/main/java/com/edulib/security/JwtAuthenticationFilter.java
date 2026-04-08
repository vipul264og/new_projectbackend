package com.edulib.security;

import com.edulib.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtUtil jwtUtil,
                                   CustomUserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        // Skip JWT processing for /error — this is an internal Spring forward
        // that happens when an exception is thrown inside a controller or service.
        // There is no Authorization header on a forwarded request; attempting to
        // process it would either silently fail or pollute the security context.
        final String requestUri = request.getRequestURI();
        if ("/error".equals(requestUri)) {
            filterChain.doFilter(request, response);
            return;
        }

        final String authHeader = request.getHeader("Authorization");

        // No Bearer token — let the request pass through unauthenticated.
        // Security rules will reject it if the endpoint requires authentication.
        if (!StringUtils.hasText(authHeader) || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            final String jwt       = authHeader.substring(7);
            final String userEmail = jwtUtil.extractUsername(jwt);

            if (StringUtils.hasText(userEmail)
                    && SecurityContextHolder.getContext().getAuthentication() == null) {

                UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);

                if (jwtUtil.isTokenValid(jwt, userDetails)) {
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails, null, userDetails.getAuthorities());
                    authToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    log.debug("Authenticated user: {} for URI: {}", userEmail, requestUri);
                } else {
                    log.warn("Invalid JWT token for URI: {}", requestUri);
                }
            }
        } catch (Exception ex) {
            // Log the exception clearly so you can see the REAL cause in logs.
            // Do NOT rethrow — let the request continue; Security will handle it.
            log.warn("JWT processing failed for URI: {} — {}", requestUri, ex.getMessage());
        }

        filterChain.doFilter(request, response);
    }
}
