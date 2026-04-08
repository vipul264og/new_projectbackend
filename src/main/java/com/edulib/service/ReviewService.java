package com.edulib.service;

import com.edulib.dto.request.ReviewRequest;
import com.edulib.dto.response.ReviewResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ReviewService {
    ReviewResponse addReview(Long bookId, ReviewRequest request, String userEmail);
    ReviewResponse updateReview(Long reviewId, ReviewRequest request, String userEmail);
    void deleteReview(Long reviewId, String userEmail);
    Page<ReviewResponse> getReviewsByBook(Long bookId, Pageable pageable);
    Page<ReviewResponse> getReviewsByUser(String userEmail, Pageable pageable);
}
