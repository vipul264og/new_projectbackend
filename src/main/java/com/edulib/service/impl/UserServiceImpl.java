package com.edulib.service.impl;

import com.edulib.dto.request.ChangePasswordRequest;
import com.edulib.dto.request.ResetPasswordRequest;
import com.edulib.entity.User;
import com.edulib.exception.AppException;
import com.edulib.exception.ResourceNotFoundException;
import com.edulib.repository.UserRepository;
import com.edulib.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // ── Change password (authenticated) ───────────────────────────────────────
    // Requires the user to know their current password.
    @Override
    @Transactional
    public void changePassword(String email, ChangePasswordRequest request) {
        User user = findByEmailOrThrow(email);

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new AppException("Current password is incorrect", HttpStatus.BAD_REQUEST);
        }
        if (passwordEncoder.matches(request.getNewPassword(), user.getPassword())) {
            throw new AppException(
                    "New password must be different from the current password", HttpStatus.BAD_REQUEST);
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        log.info("Password changed for user: {}", email);
    }

    // ── Reset password (unauthenticated / forgot password) ────────────────────
    // Verifies the email exists, checks confirm password matches, then resets.
    // NOTE: In production, gate this behind a time-limited email token sent to
    // the user's inbox. Here we keep it simple (no email server required).
    @Override
    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        // Deliberately use a vague not-found message to avoid user enumeration
        User user = userRepository.findByEmail(request.getEmail().toLowerCase())
                .orElseThrow(() -> new AppException(
                        "If that email is registered, the password has been reset.",
                        HttpStatus.OK));

        if (!user.isEnabled()) {
            throw new AppException("Account is disabled. Contact support.", HttpStatus.FORBIDDEN);
        }

        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new AppException(
                    "Passwords do not match", HttpStatus.BAD_REQUEST);
        }

        if (passwordEncoder.matches(request.getNewPassword(), user.getPassword())) {
            throw new AppException(
                    "New password must be different from the current password", HttpStatus.BAD_REQUEST);
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        log.info("Password reset for user: {}", request.getEmail());
    }

    private User findByEmailOrThrow(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + email));
    }
}
