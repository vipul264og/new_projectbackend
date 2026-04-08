package com.edulib.service;

import com.edulib.dto.request.BookRequest;
import com.edulib.dto.response.BookResponse;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

public interface BookService {
    BookResponse createBook(BookRequest request, MultipartFile file);
    BookResponse updateBook(Long id, BookRequest request);
    BookResponse getBookById(Long id);
    Page<BookResponse> getAllBooks(Pageable pageable);
    Page<BookResponse> searchBooks(String keyword, String title, String author, String tag, Pageable pageable);
    void deleteBook(Long id);

    // userEmail added so download can be recorded against the user
    Resource downloadBook(Long id, String userEmail);
    String getBookFileName(Long id);
}
