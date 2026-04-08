package com.edulib.service;

import com.edulib.dto.request.AuthRequest;
import com.edulib.dto.response.AuthResponse;

public interface AuthService {
    AuthResponse.TokenResponse register(AuthRequest.Register request);
    AuthResponse.TokenResponse login(AuthRequest.Login request);
}
