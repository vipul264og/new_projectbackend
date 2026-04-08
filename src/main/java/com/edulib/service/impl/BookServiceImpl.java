package com.edulib.service.impl;

import com.edulib.config.FileStorageConfig;
import com.edulib.dto.request.BookRequest;
import com.edulib.dto.response.BookResponse;
import com.edulib.entity.Book;
import com.edulib.entity.Download;
import com.edulib.entity.User;
import com.edulib.exception.ResourceNotFoundException;
import com.edulib.repository.BookRepository;
import com.edulib.repository.DownloadRepository;
import com.edulib.repository.ReviewRepository;
import com.edulib.repository.UserRepository;
import com.edulib.service.BookService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.util.Objects;

@Service
public class BookServiceImpl implements BookService {

    private static final Logger log = LoggerFactory.getLogger(BookServiceImpl.class);

    private final BookRepository     bookRepository;
    private final ReviewRepository   reviewRepository;
    private final DownloadRepository downloadRepository;
    private final UserRepository     userRepository;
    private final FileStorageConfig  fileStorageConfig;

    public BookServiceImpl(BookRepository bookRepository,
                           ReviewRepository reviewRepository,
                           DownloadRepository downloadRepository,
                           UserRepository userRepository,
                           FileStorageConfig fileStorageConfig) {
        this.bookRepository     = bookRepository;
        this.reviewRepository   = reviewRepository;
        this.downloadRepository = downloadRepository;
        this.userRepository     = userRepository;
        this.fileStorageConfig  = fileStorageConfig;
    }

    @Override
    @Transactional
    public BookResponse createBook(BookRequest request, MultipartFile file) {
        String storedFileName   = fileStorageConfig.storeFile(file);
        String originalFileName = StringUtils.cleanPath(
                Objects.requireNonNull(file.getOriginalFilename()));

        Book book = Book.builder()
                .title(request.getTitle())
                .author(request.getAuthor())
                .description(request.getDescription())
                .tags(request.getTags())
                .filePath(storedFileName)
                .fileName(originalFileName)
                .fileSize(file.getSize())
                .contentType(file.getContentType())
                .build();

        Book saved = bookRepository.save(book);
        log.info("Book created: id={}, title={}", saved.getId(), saved.getTitle());
        return buildResponse(saved);
    }

    @Override
    @Transactional
    public BookResponse updateBook(Long id, BookRequest request) {
        Book book = findBookOrThrow(id);
        book.setTitle(request.getTitle());
        book.setAuthor(request.getAuthor());
        book.setDescription(request.getDescription());
        book.setTags(request.getTags());

        Book updated = bookRepository.save(book);
        log.info("Book updated: id={}", id);
        return buildResponse(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public BookResponse getBookById(Long id) {
        return buildResponse(findBookOrThrow(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BookResponse> getAllBooks(Pageable pageable) {
        return bookRepository.findAll(pageable).map(this::buildResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BookResponse> searchBooks(String keyword, String title, String author,
                                          String tag, Pageable pageable) {
        boolean hasFilters = StringUtils.hasText(title)
                || StringUtils.hasText(author)
                || StringUtils.hasText(tag);

        Page<Book> books = hasFilters
                ? bookRepository.searchByFilters(
                        StringUtils.hasText(title)  ? title  : null,
                        StringUtils.hasText(author) ? author : null,
                        StringUtils.hasText(tag)    ? tag    : null,
                        pageable)
                : bookRepository.searchBooks(keyword, pageable);

        return books.map(this::buildResponse);
    }

    @Override
    @Transactional
    public void deleteBook(Long id) {
        Book book = findBookOrThrow(id);
        fileStorageConfig.deleteFile(book.getFilePath());
        bookRepository.delete(book);
        log.info("Book deleted: id={}", id);
    }

    @Override
    @Transactional
    public Resource downloadBook(Long id, String userEmail) {
        Book book = findBookOrThrow(id);
        Path filePath = fileStorageConfig.loadFile(book.getFilePath());

        Resource resource;
        try {
            resource = new UrlResource(filePath.toUri());
            if (!resource.exists() || !resource.isReadable()) {
                throw new ResourceNotFoundException("File not readable for book: " + id);
            }
        } catch (MalformedURLException ex) {
            throw new ResourceNotFoundException("File not found for book: " + id);
        }

        // Record the download — find user and persist a Download row.
        // Done inside the same transaction so if the file is unreadable
        // the record is never written.
        userRepository.findByEmail(userEmail).ifPresent(user -> {
            Download record = new Download(user, book);
            downloadRepository.save(record);
            log.info("Download recorded: bookId={}, userId={}", id, user.getId());
        });

        return resource;
    }

    @Override
    @Transactional(readOnly = true)
    public String getBookFileName(Long id) {
        return findBookOrThrow(id).getFileName();
    }

    private Book findBookOrThrow(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book", id));
    }

    private BookResponse buildResponse(Book book) {
        Double avgRating  = reviewRepository.findAverageRatingByBookId(book.getId());
        Long   reviewCount = reviewRepository.countByBookId(book.getId());
        return BookResponse.from(book, avgRating, reviewCount);
    }
}
