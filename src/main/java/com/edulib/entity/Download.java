package com.edulib.entity;

import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "downloads", indexes = {
        @Index(name = "idx_download_user",      columnList = "user_id"),
        @Index(name = "idx_download_book",      columnList = "book_id"),
        @Index(name = "idx_download_user_book", columnList = "user_id, book_id")
})
@EntityListeners(AuditingEntityListener.class)
public class Download {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @CreatedDate
    @Column(updatable = false, nullable = false)
    private LocalDateTime downloadedAt;

    public Download() {}

    public Download(User user, Book book) {
        this.user = user;
        this.book = book;
    }

    public Long getId()                          { return id; }
    public void setId(Long id)                  { this.id = id; }
    public User getUser()                        { return user; }
    public void setUser(User user)              { this.user = user; }
    public Book getBook()                        { return book; }
    public void setBook(Book book)              { this.book = book; }
    public LocalDateTime getDownloadedAt()       { return downloadedAt; }
    public void setDownloadedAt(LocalDateTime t) { this.downloadedAt = t; }
}
