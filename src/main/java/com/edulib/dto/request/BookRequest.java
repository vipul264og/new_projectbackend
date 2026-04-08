package com.edulib.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class BookRequest {

    @NotBlank(message = "Title is required")
    @Size(max = 255, message = "Title must not exceed 255 characters")
    private String title;

    @NotBlank(message = "Author is required")
    @Size(max = 150, message = "Author must not exceed 150 characters")
    private String author;

    @Size(max = 5000, message = "Description must not exceed 5000 characters")
    private String description;

    @Size(max = 500, message = "Tags must not exceed 500 characters")
    private String tags;

    public BookRequest() {}

    public String getTitle()               { return title; }
    public void setTitle(String title)    { this.title = title; }
    public String getAuthor()             { return author; }
    public void setAuthor(String author)  { this.author = author; }
    public String getDescription()        { return description; }
    public void setDescription(String d)  { this.description = d; }
    public String getTags()               { return tags; }
    public void setTags(String tags)      { this.tags = tags; }
}
