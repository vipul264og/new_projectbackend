package com.edulib.dto.response;

import com.edulib.entity.Download;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

public class DownloadResponse {

    private Long   id;
    private Long   bookId;
    private String bookTitle;
    private String bookAuthor;
    private String bookFileName;
    private Long   bookFileSize;
    private List<String> bookTags;
    private Double bookAverageRating;
    private LocalDateTime downloadedAt;

    public DownloadResponse() {}

    public DownloadResponse(Long id, Long bookId, String bookTitle, String bookAuthor,
                            String bookFileName, Long bookFileSize, List<String> bookTags,
                            Double bookAverageRating, LocalDateTime downloadedAt) {
        this.id                = id;
        this.bookId            = bookId;
        this.bookTitle         = bookTitle;
        this.bookAuthor        = bookAuthor;
        this.bookFileName      = bookFileName;
        this.bookFileSize      = bookFileSize;
        this.bookTags          = bookTags;
        this.bookAverageRating = bookAverageRating;
        this.downloadedAt      = downloadedAt;
    }

    public static DownloadResponse from(Download download) {
        return new DownloadResponse(
                download.getId(),
                download.getBook().getId(),
                download.getBook().getTitle(),
                download.getBook().getAuthor(),
                download.getBook().getFileName(),
                download.getBook().getFileSize(),
                parseTags(download.getBook().getTags()),
                download.getBook().getAverageRating(),
                download.getDownloadedAt()
        );
    }

    private static List<String> parseTags(String tags) {
        if (tags == null || tags.isBlank()) return List.of();
        return Arrays.stream(tags.split(","))
                .map(String::trim).filter(t -> !t.isBlank()).toList();
    }

    public Long getId()                              { return id; }
    public void setId(Long id)                      { this.id = id; }
    public Long getBookId()                          { return bookId; }
    public void setBookId(Long bookId)              { this.bookId = bookId; }
    public String getBookTitle()                     { return bookTitle; }
    public void setBookTitle(String t)              { this.bookTitle = t; }
    public String getBookAuthor()                    { return bookAuthor; }
    public void setBookAuthor(String a)             { this.bookAuthor = a; }
    public String getBookFileName()                  { return bookFileName; }
    public void setBookFileName(String f)           { this.bookFileName = f; }
    public Long getBookFileSize()                    { return bookFileSize; }
    public void setBookFileSize(Long s)             { this.bookFileSize = s; }
    public List<String> getBookTags()               { return bookTags; }
    public void setBookTags(List<String> t)         { this.bookTags = t; }
    public Double getBookAverageRating()             { return bookAverageRating; }
    public void setBookAverageRating(Double r)      { this.bookAverageRating = r; }
    public LocalDateTime getDownloadedAt()           { return downloadedAt; }
    public void setDownloadedAt(LocalDateTime t)    { this.downloadedAt = t; }
}
