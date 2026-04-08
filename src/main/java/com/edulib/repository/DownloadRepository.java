package com.edulib.repository;

import com.edulib.entity.Download;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface DownloadRepository extends JpaRepository<Download, Long> {

    // All downloads for a user, most recent first
    Page<Download> findByUserIdOrderByDownloadedAtDesc(Long userId, Pageable pageable);

    // Total download count for a book
    Long countByBookId(Long bookId);

    // Total download count for a user
    Long countByUserId(Long userId);

    // Check if user has downloaded a specific book before
    boolean existsByUserIdAndBookId(Long userId, Long bookId);

    // Latest download timestamp per book for a user (for "last downloaded" label)
    @Query("""
        SELECT d FROM Download d
        WHERE d.user.id = :userId
        AND d.book.id = :bookId
        ORDER BY d.downloadedAt DESC
        """)
    Page<Download> findLatestByUserAndBook(
            @Param("userId") Long userId,
            @Param("bookId") Long bookId,
            Pageable pageable);
}
