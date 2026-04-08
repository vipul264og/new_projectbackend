package com.edulib.dto.response;

import com.edulib.entity.Review;

import java.time.LocalDateTime;

public class ReviewResponse {

    private Long id;
    private Integer rating;
    private String comment;
    private Long bookId;
    private String bookTitle;
    private Long userId;
    private String userName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public ReviewResponse() {}

    public ReviewResponse(Long id, Integer rating, String comment, Long bookId, String bookTitle,
                          Long userId, String userName, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id; this.rating = rating; this.comment = comment;
        this.bookId = bookId; this.bookTitle = bookTitle;
        this.userId = userId; this.userName = userName;
        this.createdAt = createdAt; this.updatedAt = updatedAt;
    }

    public static ReviewResponse from(Review review) {
        return new ReviewResponse(
                review.getId(), review.getRating(), review.getComment(),
                review.getBook().getId(), review.getBook().getTitle(),
                review.getUser().getId(), review.getUser().getName(),
                review.getCreatedAt(), review.getUpdatedAt());
    }

    public Long getId()                      { return id; }
    public void setId(Long id)              { this.id = id; }
    public Integer getRating()              { return rating; }
    public void setRating(Integer rating)   { this.rating = rating; }
    public String getComment()              { return comment; }
    public void setComment(String comment)  { this.comment = comment; }
    public Long getBookId()                 { return bookId; }
    public void setBookId(Long bookId)      { this.bookId = bookId; }
    public String getBookTitle()            { return bookTitle; }
    public void setBookTitle(String t)      { this.bookTitle = t; }
    public Long getUserId()                 { return userId; }
    public void setUserId(Long userId)      { this.userId = userId; }
    public String getUserName()             { return userName; }
    public void setUserName(String name)    { this.userName = name; }
    public LocalDateTime getCreatedAt()     { return createdAt; }
    public void setCreatedAt(LocalDateTime t) { this.createdAt = t; }
    public LocalDateTime getUpdatedAt()     { return updatedAt; }
    public void setUpdatedAt(LocalDateTime t) { this.updatedAt = t; }
}
