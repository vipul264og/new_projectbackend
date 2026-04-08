package com.edulib.entity;

import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "reviews",
        indexes = {
                @Index(name = "idx_review_book", columnList = "book_id"),
                @Index(name = "idx_review_user", columnList = "user_id")
        },
        uniqueConstraints = {
                @UniqueConstraint(name = "uq_user_book_review", columnNames = {"user_id", "book_id"})
        })
@EntityListeners(AuditingEntityListener.class)
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer rating;

    @Column(columnDefinition = "TEXT")
    private String comment;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    public Review() {}

    public Review(Long id, Integer rating, String comment, User user, Book book,
                  LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id; this.rating = rating; this.comment = comment;
        this.user = user; this.book = book;
        this.createdAt = createdAt; this.updatedAt = updatedAt;
    }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id; private Integer rating; private String comment;
        private User user; private Book book;
        private LocalDateTime createdAt; private LocalDateTime updatedAt;

        public Builder id(Long id)           { this.id = id; return this; }
        public Builder rating(Integer rating) { this.rating = rating; return this; }
        public Builder comment(String comment){ this.comment = comment; return this; }
        public Builder user(User user)       { this.user = user; return this; }
        public Builder book(Book book)       { this.book = book; return this; }

        public Review build() {
            return new Review(id, rating, comment, user, book, createdAt, updatedAt);
        }
    }

    public Long getId()                      { return id; }
    public void setId(Long id)              { this.id = id; }
    public Integer getRating()              { return rating; }
    public void setRating(Integer rating)   { this.rating = rating; }
    public String getComment()              { return comment; }
    public void setComment(String comment)  { this.comment = comment; }
    public User getUser()                   { return user; }
    public void setUser(User user)          { this.user = user; }
    public Book getBook()                   { return book; }
    public void setBook(Book book)          { this.book = book; }
    public LocalDateTime getCreatedAt()     { return createdAt; }
    public void setCreatedAt(LocalDateTime t) { this.createdAt = t; }
    public LocalDateTime getUpdatedAt()     { return updatedAt; }
    public void setUpdatedAt(LocalDateTime t) { this.updatedAt = t; }
}
