package com.edulib.repository;

import com.edulib.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    @Query("""
            SELECT b FROM Book b
            WHERE (:keyword IS NULL OR :keyword = ''
                OR LOWER(b.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
                OR LOWER(b.author) LIKE LOWER(CONCAT('%', :keyword, '%'))
                OR LOWER(b.tags) LIKE LOWER(CONCAT('%', :keyword, '%')))
            """)
    Page<Book> searchBooks(@Param("keyword") String keyword, Pageable pageable);

    @Query("""
            SELECT b FROM Book b
            WHERE (:title IS NULL OR LOWER(b.title) LIKE LOWER(CONCAT('%', :title, '%')))
            AND (:author IS NULL OR LOWER(b.author) LIKE LOWER(CONCAT('%', :author, '%')))
            AND (:tag IS NULL OR LOWER(b.tags) LIKE LOWER(CONCAT('%', :tag, '%')))
            """)
    Page<Book> searchByFilters(
            @Param("title") String title,
            @Param("author") String author,
            @Param("tag") String tag,
            Pageable pageable
    );
}
