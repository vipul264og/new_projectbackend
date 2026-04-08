package com.edulib.controller;

import com.edulib.dto.request.ChangePasswordRequest;
import com.edulib.dto.request.ResetPasswordRequest;
import com.edulib.dto.response.ApiResponse;
import com.edulib.dto.response.DownloadResponse;
import com.edulib.entity.Download;
import com.edulib.repository.DownloadRepository;
import com.edulib.repository.ReviewRepository;
import com.edulib.repository.UserRepository;
import com.edulib.service.UserService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    private final UserService        userService;
    private final DownloadRepository downloadRepository;
    private final ReviewRepository   reviewRepository;
    private final UserRepository     userRepository;

    public UserController(UserService userService,
                          DownloadRepository downloadRepository,
                          ReviewRepository reviewRepository,
                          UserRepository userRepository) {
        this.userService        = userService;
        this.downloadRepository = downloadRepository;
        this.reviewRepository   = reviewRepository;
        this.userRepository     = userRepository;
    }

    /**
     * PATCH /api/v1/users/me/password
     * Authenticated — change own password (requires current password).
     */
    @PatchMapping("/users/me/password")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @Valid @RequestBody ChangePasswordRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        userService.changePassword(userDetails.getUsername(), request);
        return ResponseEntity.ok(ApiResponse.success("Password changed successfully"));
    }

    /**
     * POST /api/v1/auth/reset-password
     * Public — forgot-password reset.
     */
    @PostMapping("/auth/reset-password")
    public ResponseEntity<ApiResponse<Void>> resetPassword(
            @Valid @RequestBody ResetPasswordRequest request) {
        userService.resetPassword(request);
        return ResponseEntity.ok(ApiResponse.success(
                "If that email is registered, the password has been reset. You can now sign in."));
    }

    /**
     * GET /api/v1/users/me/downloads
     * Authenticated — paginated download history for the logged-in user.
     */
    @GetMapping("/users/me/downloads")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Page<DownloadResponse>>> getMyDownloads(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "10") int size) {

        Long userId = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"))
                .getId();

        Pageable pageable = PageRequest.of(page, Math.min(size, 50));
        Page<DownloadResponse> result = downloadRepository
                .findByUserIdOrderByDownloadedAtDesc(userId, pageable)
                .map(DownloadResponse::from);

        return ResponseEntity.ok(ApiResponse.success(result));
    }

    /**
     * GET /api/v1/users/me/stats
     * Authenticated — summary stats for the dashboard header.
     */
    @GetMapping("/users/me/stats")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Map<String, Long>>> getMyStats(
            @AuthenticationPrincipal UserDetails userDetails) {

        Long userId = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"))
                .getId();

        Map<String, Long> stats = Map.of(
                "totalDownloads", downloadRepository.countByUserId(userId),
                "totalReviews",   reviewRepository.countByUserId(userId)
        );

        return ResponseEntity.ok(ApiResponse.success(stats));
    }
}
