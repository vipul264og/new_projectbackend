package com.edulib.controller;

import com.edulib.dto.request.ReviewRequest;
import com.edulib.dto.response.ApiResponse;
import com.edulib.dto.response.ReviewResponse;
import com.edulib.service.ReviewService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class ReviewController {

    private static final Logger log = LoggerFactory.getLogger(ReviewController.class);

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    /**
     * POST /api/v1/books/{bookId}/reviews
     * Authenticated — Add a review for a book (one per user per book).
     */
    @PostMapping("/books/{bookId}/reviews")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<ReviewResponse>> addReview(
            @PathVariable Long bookId,
            @Valid @RequestBody ReviewRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        ReviewResponse response = reviewService.addReview(bookId, request, userDetails.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Review submitted successfully", response));
    }

    /**
     * PUT /api/v1/reviews/{reviewId}
     * Authenticated — Update your own review.
     */
    @PutMapping("/reviews/{reviewId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<ReviewResponse>> updateReview(
            @PathVariable Long reviewId,
            @Valid @RequestBody ReviewRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        ReviewResponse response = reviewService.updateReview(
                reviewId, request, userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success("Review updated successfully", response));
    }

    /**
     * DELETE /api/v1/reviews/{reviewId}
     * Authenticated — Delete your own review; ADMIN can delete any review.
     */
    @DeleteMapping("/reviews/{reviewId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Void>> deleteReview(
            @PathVariable Long reviewId,
            @AuthenticationPrincipal UserDetails userDetails) {
        reviewService.deleteReview(reviewId, userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success("Review deleted successfully"));
    }

    /**
     * GET /api/v1/books/{bookId}/reviews
     * Public — Get all reviews for a specific book (paginated).
     */
    @GetMapping("/books/{bookId}/reviews")
    public ResponseEntity<ApiResponse<Page<ReviewResponse>>> getBookReviews(
            @PathVariable Long bookId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sort,
            @RequestParam(defaultValue = "DESC") String direction) {
        Sort.Direction dir = Sort.Direction.fromOptionalString(direction)
                .orElse(Sort.Direction.DESC);
        Pageable pageable = PageRequest.of(page, Math.min(size, 50), Sort.by(dir, sort));
        return ResponseEntity.ok(
                ApiResponse.success(reviewService.getReviewsByBook(bookId, pageable)));
    }

    /**
     * GET /api/v1/reviews/my
     * Authenticated — Get all reviews submitted by the logged-in user.
     */
    @GetMapping("/reviews/my")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Page<ReviewResponse>>> getMyReviews(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, Math.min(size, 50),
                Sort.by(Sort.Direction.DESC, "createdAt"));
        return ResponseEntity.ok(
                ApiResponse.success(reviewService.getReviewsByUser(
                        userDetails.getUsername(), pageable)));
    }
}
