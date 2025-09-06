package com.fcsm.document.repository;

import com.fcsm.document.model.Document;
import com.fcsm.document.model.SharingLevel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {
    
    // Find documents by sharing level and user access
    @Query("SELECT d FROM Document d WHERE " +
           "(d.sharingLevel = 'PUBLIC') OR " +
           "(d.sharingLevel = 'GROUP' AND d.department = :department) OR " +
           "(d.sharingLevel = 'PRIVATE' AND d.createdBy = :userId)")
    Page<Document> findAccessibleDocuments(@Param("userId") Long userId, 
                                          @Param("department") String department, 
                                          Pageable pageable);
    
    // Find documents by creator
    List<Document> findByCreatedByOrderByCreatedAtDesc(Long createdBy);
    
    // Find documents by department
    List<Document> findByDepartmentOrderByCreatedAtDesc(String department);
    
    // Find documents by sharing level
    List<Document> findBySharingLevelOrderByCreatedAtDesc(SharingLevel sharingLevel);
    
    // Find top documents by view count
    @Query("SELECT d FROM Document d WHERE " +
           "(d.sharingLevel = 'PUBLIC') OR " +
           "(d.sharingLevel = 'GROUP' AND d.department = :department) OR " +
           "(d.sharingLevel = 'PRIVATE' AND d.createdBy = :userId) " +
           "ORDER BY d.viewCount DESC")
    List<Document> findTopDocumentsByViewCount(@Param("userId") Long userId, 
                                              @Param("department") String department, 
                                              Pageable pageable);
    
    // Find top documents by star count
    @Query("SELECT d FROM Document d WHERE " +
           "(d.sharingLevel = 'PUBLIC') OR " +
           "(d.sharingLevel = 'GROUP' AND d.department = :department) OR " +
           "(d.sharingLevel = 'PRIVATE' AND d.createdBy = :userId) " +
           "ORDER BY d.starCount DESC")
    List<Document> findTopDocumentsByStarCount(@Param("userId") Long userId, 
                                              @Param("department") String department, 
                                              Pageable pageable);
    
    // Find recent documents
    @Query("SELECT d FROM Document d WHERE " +
           "(d.sharingLevel = 'PUBLIC') OR " +
           "(d.sharingLevel = 'GROUP' AND d.department = :department) OR " +
           "(d.sharingLevel = 'PRIVATE' AND d.createdBy = :userId) " +
           "ORDER BY d.createdAt DESC")
    List<Document> findRecentDocuments(@Param("userId") Long userId, 
                                      @Param("department") String department, 
                                      Pageable pageable);
    
    // Search documents by title, tags, or content
    @Query("SELECT d FROM Document d WHERE " +
           "((d.sharingLevel = 'PUBLIC') OR " +
           "(d.sharingLevel = 'GROUP' AND d.department = :department) OR " +
           "(d.sharingLevel = 'PRIVATE' AND d.createdBy = :userId)) AND " +
           "(LOWER(d.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(d.summary) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "EXISTS (SELECT t FROM d.tags t WHERE LOWER(t) LIKE LOWER(CONCAT('%', :keyword, '%'))))")
    Page<Document> searchDocuments(@Param("userId") Long userId, 
                                  @Param("department") String department, 
                                  @Param("keyword") String keyword, 
                                  Pageable pageable);
    
    // Find documents by tag
    @Query("SELECT d FROM Document d JOIN d.tags t WHERE " +
           "((d.sharingLevel = 'PUBLIC') OR " +
           "(d.sharingLevel = 'GROUP' AND d.department = :department) OR " +
           "(d.sharingLevel = 'PRIVATE' AND d.createdBy = :userId)) AND " +
           "LOWER(t) = LOWER(:tag)")
    Page<Document> findByTag(@Param("userId") Long userId, 
                            @Param("department") String department, 
                            @Param("tag") String tag, 
                            Pageable pageable);
    
    // Find documents by date range
    @Query("SELECT d FROM Document d WHERE " +
           "((d.sharingLevel = 'PUBLIC') OR " +
           "(d.sharingLevel = 'GROUP' AND d.department = :department) OR " +
           "(d.sharingLevel = 'PRIVATE' AND d.createdBy = :userId)) AND " +
           "d.createdAt BETWEEN :startDate AND :endDate")
    Page<Document> findByDateRange(@Param("userId") Long userId, 
                                  @Param("department") String department, 
                                  @Param("startDate") LocalDateTime startDate, 
                                  @Param("endDate") LocalDateTime endDate, 
                                  Pageable pageable);
}
