package com.edulib.service.impl;

import com.edulib.dto.request.ReviewRequest;
import com.edulib.dto.response.ReviewResponse;
import com.edulib.entity.Book;
import com.edulib.entity.Review;
import com.edulib.entity.User;
import com.edulib.exception.DuplicateResourceException;
import com.edulib.exception.ResourceNotFoundException;
import com.edulib.exception.UnauthorizedException;
import com.edulib.repository.BookRepository;
import com.edulib.repository.ReviewRepository;
import com.edulib.repository.UserRepository;
import com.edulib.service.ReviewService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReviewServiceImpl implements ReviewService {

    private static final Logger log = LoggerFactory.getLogger(ReviewServiceImpl.class);

    private final ReviewRepository reviewRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    public ReviewServiceImpl(ReviewRepository reviewRepository,
                             BookRepository bookRepository,
                             UserRepository userRepository) {
        this.reviewRepository = reviewRepository;
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public ReviewResponse addReview(Long bookId, ReviewRequest request, String userEmail) {
        User user = findUserOrThrow(userEmail);
        Book book = findBookOrThrow(bookId);

        if (reviewRepository.existsByUserIdAndBookId(user.getId(), bookId)) {
            throw new DuplicateResourceException(
                    "You have already reviewed this book. Use update instead.");
        }

        Review review = Review.builder()
                .rating(request.getRating())
                .comment(request.getComment())
                .user(user)
                .book(book)
                .build();

        Review saved = reviewRepository.save(review);
        log.info("Review added: bookId={}, userId={}", bookId, user.getId());
        return ReviewResponse.from(saved);
    }

    @Override
    @Transactional
    public ReviewResponse updateReview(Long reviewId, ReviewRequest request, String userEmail) {
        User user = findUserOrThrow(userEmail);
        Review review = findReviewOrThrow(reviewId);
        assertOwnership(review, user);

        review.setRating(request.getRating());
        review.setComment(request.getComment());

        Review updated = reviewRepository.save(review);
        log.info("Review updated: reviewId={}, userId={}", reviewId, user.getId());
        return ReviewResponse.from(updated);
    }

    @Override
    @Transactional
    public void deleteReview(Long reviewId, String userEmail) {
        User user = findUserOrThrow(userEmail);
        Review review = findReviewOrThrow(reviewId);

        boolean isAdmin = user.getRole() == User.Role.ADMIN;
        if (!isAdmin) {
            assertOwnership(review, user);
        }

        reviewRepository.delete(review);
        log.info("Review deleted: reviewId={}, by userId={}", reviewId, user.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ReviewResponse> getReviewsByBook(Long bookId, Pageable pageable) {
        if (!bookRepository.existsById(bookId)) {
            throw new ResourceNotFoundException("Book", bookId);
        }
        return reviewRepository.findByBookId(bookId, pageable).map(ReviewResponse::from);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ReviewResponse> getReviewsByUser(String userEmail, Pageable pageable) {
        User user = findUserOrThrow(userEmail);
        return reviewRepository.findByUserId(user.getId(), pageable).map(ReviewResponse::from);
    }

    private void assertOwnership(Review review, User user) {
        if (!review.getUser().getId().equals(user.getId())) {
            throw new UnauthorizedException("You are not authorized to modify this review");
        }
    }

    private User findUserOrThrow(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + email));
    }

    private Book findBookOrThrow(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book", id));
    }

    private Review findReviewOrThrow(Long id) {
        return reviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Review", id));
    }
}
