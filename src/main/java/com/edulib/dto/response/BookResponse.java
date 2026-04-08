package com.edulib.dto.response;

import com.edulib.entity.Book;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

public class BookResponse {

    private Long id;
    private String title;
    private String author;
    private String description;
    private String fileName;
    private Long fileSize;
    private List<String> tags;
    private Double averageRating;
    private Long reviewCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public BookResponse() {}

    public BookResponse(Long id, String title, String author, String description,
                        String fileName, Long fileSize, List<String> tags,
                        Double averageRating, Long reviewCount,
                        LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id; this.title = title; this.author = author;
        this.description = description; this.fileName = fileName;
        this.fileSize = fileSize; this.tags = tags;
        this.averageRating = averageRating; this.reviewCount = reviewCount;
        this.createdAt = createdAt; this.updatedAt = updatedAt;
    }

    public static BookResponse from(Book book) {
        return new BookResponse(
                book.getId(), book.getTitle(), book.getAuthor(), book.getDescription(),
                book.getFileName(), book.getFileSize(), parseTags(book.getTags()),
                book.getAverageRating(), (long) book.getReviews().size(),
                book.getCreatedAt(), book.getUpdatedAt());
    }

    public static BookResponse from(Book book, Double averageRating, Long reviewCount) {
        BookResponse r = from(book);
        r.setAverageRating(averageRating != null ? averageRating : 0.0);
        r.setReviewCount(reviewCount != null ? reviewCount : 0L);
        return r;
    }

    private static List<String> parseTags(String tags) {
        if (tags == null || tags.isBlank()) return List.of();
        return Arrays.stream(tags.split(","))
                .map(String::trim).filter(t -> !t.isBlank()).toList();
    }

    public Long getId()                       { return id; }
    public void setId(Long id)               { this.id = id; }
    public String getTitle()                  { return title; }
    public void setTitle(String title)       { this.title = title; }
    public String getAuthor()                 { return author; }
    public void setAuthor(String author)     { this.author = author; }
    public String getDescription()           { return description; }
    public void setDescription(String d)     { this.description = d; }
    public String getFileName()              { return fileName; }
    public void setFileName(String f)        { this.fileName = f; }
    public Long getFileSize()                { return fileSize; }
    public void setFileSize(Long fileSize)   { this.fileSize = fileSize; }
    public List<String> getTags()            { return tags; }
    public void setTags(List<String> tags)   { this.tags = tags; }
    public Double getAverageRating()         { return averageRating; }
    public void setAverageRating(Double avg) { this.averageRating = avg; }
    public Long getReviewCount()             { return reviewCount; }
    public void setReviewCount(Long rc)      { this.reviewCount = rc; }
    public LocalDateTime getCreatedAt()      { return createdAt; }
    public void setCreatedAt(LocalDateTime t) { this.createdAt = t; }
    public LocalDateTime getUpdatedAt()      { return updatedAt; }
    public void setUpdatedAt(LocalDateTime t) { this.updatedAt = t; }
}
