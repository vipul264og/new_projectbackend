package com.edulib.entity;

import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "books", indexes = {
        @Index(name = "idx_book_title", columnList = "title"),
        @Index(name = "idx_book_author", columnList = "author")
})
@EntityListeners(AuditingEntityListener.class)
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(nullable = false, length = 150)
    private String author;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private String filePath;

    @Column(nullable = false, length = 255)
    private String fileName;

    @Column(nullable = false)
    private Long fileSize;

    @Column(length = 100)
    private String contentType;

    @Column(length = 500)
    private String tags;

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Review> reviews = new ArrayList<>();

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    public Book() {}

    public Book(Long id, String title, String author, String description,
                String filePath, String fileName, Long fileSize, String contentType,
                String tags, List<Review> reviews, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id; this.title = title; this.author = author; this.description = description;
        this.filePath = filePath; this.fileName = fileName; this.fileSize = fileSize;
        this.contentType = contentType; this.tags = tags;
        this.reviews = reviews != null ? reviews : new ArrayList<>();
        this.createdAt = createdAt; this.updatedAt = updatedAt;
    }

    public Double getAverageRating() {
        if (reviews == null || reviews.isEmpty()) return 0.0;
        return reviews.stream().mapToInt(Review::getRating).average().orElse(0.0);
    }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id; private String title; private String author;
        private String description; private String filePath; private String fileName;
        private Long fileSize; private String contentType; private String tags;
        private List<Review> reviews = new ArrayList<>();
        private LocalDateTime createdAt; private LocalDateTime updatedAt;

        public Builder id(Long id)                    { this.id = id; return this; }
        public Builder title(String title)            { this.title = title; return this; }
        public Builder author(String author)          { this.author = author; return this; }
        public Builder description(String desc)       { this.description = desc; return this; }
        public Builder filePath(String filePath)      { this.filePath = filePath; return this; }
        public Builder fileName(String fileName)      { this.fileName = fileName; return this; }
        public Builder fileSize(Long fileSize)        { this.fileSize = fileSize; return this; }
        public Builder contentType(String ct)         { this.contentType = ct; return this; }
        public Builder tags(String tags)              { this.tags = tags; return this; }
        public Builder reviews(List<Review> reviews)  { this.reviews = reviews; return this; }

        public Book build() {
            return new Book(id, title, author, description, filePath, fileName,
                    fileSize, contentType, tags, reviews, createdAt, updatedAt);
        }
    }

    public Long getId()                       { return id; }
    public void setId(Long id)               { this.id = id; }
    public String getTitle()                  { return title; }
    public void setTitle(String title)       { this.title = title; }
    public String getAuthor()                 { return author; }
    public void setAuthor(String author)     { this.author = author; }
    public String getDescription()           { return description; }
    public void setDescription(String desc)  { this.description = desc; }
    public String getFilePath()              { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }
    public String getFileName()              { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }
    public Long getFileSize()                { return fileSize; }
    public void setFileSize(Long fileSize)   { this.fileSize = fileSize; }
    public String getContentType()           { return contentType; }
    public void setContentType(String ct)    { this.contentType = ct; }
    public String getTags()                  { return tags; }
    public void setTags(String tags)         { this.tags = tags; }
    public List<Review> getReviews()         { return reviews; }
    public void setReviews(List<Review> r)   { this.reviews = r; }
    public LocalDateTime getCreatedAt()      { return createdAt; }
    public void setCreatedAt(LocalDateTime t) { this.createdAt = t; }
    public LocalDateTime getUpdatedAt()      { return updatedAt; }
    public void setUpdatedAt(LocalDateTime t) { this.updatedAt = t; }
}
