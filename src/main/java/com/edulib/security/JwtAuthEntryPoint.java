package com.edulib.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.edulib.dto.response.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class JwtAuthEntryPoint implements AuthenticationEntryPoint {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthEntryPoint.class);

    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {

        // Log the ORIGINAL request URI (before any forward), not just the
        // current URI. This makes debugging much clearer — you'll see both
        // the real endpoint and the /error forward if one occurred.
        String requestUri  = request.getRequestURI();
        String originalUri = (String) request.getAttribute(
                "jakarta.servlet.error.request_uri");

        if (originalUri != null) {
            log.warn("Unauthorized — original URI: {} forwarded to: {}  reason: {}",
                    originalUri, requestUri, authException.getMessage());
        } else {
            log.warn("Unauthorized request to: {}  reason: {}",
                    requestUri, authException.getMessage());
        }

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        objectMapper.writeValue(
                response.getOutputStream(),
                ApiResponse.error("Authentication required. Please provide a valid JWT token.")
        );
    }
}
