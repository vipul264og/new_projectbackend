package com.edulib.service;

import com.edulib.dto.request.ChangePasswordRequest;
import com.edulib.dto.request.ResetPasswordRequest;

public interface UserService {

    // Authenticated: requires current password verification
    void changePassword(String email, ChangePasswordRequest request);

    // Unauthenticated: reset by email — no current password needed
    // In production you would gate this behind an email OTP/token.
    // Here it resets directly by email so it works without an email server.
    void resetPassword(ResetPasswordRequest request);
}
