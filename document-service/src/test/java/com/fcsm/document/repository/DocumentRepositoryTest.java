package com.fcsm.document.repository;

import com.fcsm.document.model.Document;
import com.fcsm.document.model.SharingLevel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class DocumentRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private DocumentRepository documentRepository;

    private Document publicDocument;
    private Document groupDocument;
    private Document privateDocument;

    @BeforeEach
    void setUp() {
        // Create test documents
        publicDocument = new Document();
        publicDocument.setTitle("Public Document");
        publicDocument.setSummary("Public summary");
        publicDocument.setFileName("public.pdf");
        publicDocument.setOriginalFileName("Public Document.pdf");
        publicDocument.setFileType("application/pdf");
        publicDocument.setFileSize(1024L);
        publicDocument.setFilePath("/uploads/public.pdf");
        publicDocument.setSharingLevel(SharingLevel.PUBLIC);
        publicDocument.setDepartment("IT");
        publicDocument.setCreatedBy(1L);
        publicDocument.setCreatedByName("User 1");
        publicDocument.setCreatedAt(LocalDateTime.now());
        publicDocument.setUpdatedAt(LocalDateTime.now());
        publicDocument.setTags(Arrays.asList("public", "test"));
        publicDocument.setViewCount(10);
        publicDocument.setStarCount(5);
        publicDocument.setContent("Public content");

        groupDocument = new Document();
        groupDocument.setTitle("Group Document");
        groupDocument.setSummary("Group summary");
        groupDocument.setFileName("group.pdf");
        groupDocument.setOriginalFileName("Group Document.pdf");
        groupDocument.setFileType("application/pdf");
        groupDocument.setFileSize(2048L);
        groupDocument.setFilePath("/uploads/group.pdf");
        groupDocument.setSharingLevel(SharingLevel.GROUP);
        groupDocument.setDepartment("HR");
        groupDocument.setCreatedBy(2L);
        groupDocument.setCreatedByName("User 2");
        groupDocument.setCreatedAt(LocalDateTime.now());
        groupDocument.setUpdatedAt(LocalDateTime.now());
        groupDocument.setTags(Arrays.asList("group", "hr"));
        groupDocument.setViewCount(5);
        groupDocument.setStarCount(2);
        groupDocument.setContent("Group content");

        privateDocument = new Document();
        privateDocument.setTitle("Private Document");
        privateDocument.setSummary("Private summary");
        privateDocument.setFileName("private.pdf");
        privateDocument.setOriginalFileName("Private Document.pdf");
        privateDocument.setFileType("application/pdf");
        privateDocument.setFileSize(512L);
        privateDocument.setFilePath("/uploads/private.pdf");
        privateDocument.setSharingLevel(SharingLevel.PRIVATE);
        privateDocument.setDepartment("IT");
        privateDocument.setCreatedBy(1L);
        privateDocument.setCreatedByName("User 1");
        privateDocument.setCreatedAt(LocalDateTime.now());
        privateDocument.setUpdatedAt(LocalDateTime.now());
        privateDocument.setTags(Arrays.asList("private", "personal"));
        privateDocument.setViewCount(1);
        privateDocument.setStarCount(0);
        privateDocument.setContent("Private content");

        // Persist test data
        entityManager.persistAndFlush(publicDocument);
        entityManager.persistAndFlush(groupDocument);
        entityManager.persistAndFlush(privateDocument);
    }

    @Test
    void findAccessibleDocuments_WithPublicAccess_ShouldReturnAllPublicDocuments() {
        // Given
        Long userId = 1L;
        String department = "IT";
        Pageable pageable = PageRequest.of(0, 10);

        // When
        Page<Document> result = documentRepository.findAccessibleDocuments(userId, department, pageable);

        // Then
        assertNotNull(result);
        assertTrue(result.getContent().size() >= 1);
        assertTrue(result.getContent().stream()
                .anyMatch(doc -> doc.getSharingLevel() == SharingLevel.PUBLIC));
    }

    @Test
    void findAccessibleDocuments_WithGroupAccess_ShouldReturnGroupAndPublicDocuments() {
        // Given
        Long userId = 2L;
        String department = "HR";
        Pageable pageable = PageRequest.of(0, 10);

        // When
        Page<Document> result = documentRepository.findAccessibleDocuments(userId, department, pageable);

        // Then
        assertNotNull(result);
        assertTrue(result.getContent().size() >= 2);
        assertTrue(result.getContent().stream()
                .anyMatch(doc -> doc.getSharingLevel() == SharingLevel.PUBLIC));
        assertTrue(result.getContent().stream()
                .anyMatch(doc -> doc.getSharingLevel() == SharingLevel.GROUP && 
                        doc.getDepartment().equals("HR")));
    }

    @Test
    void findAccessibleDocuments_WithPrivateAccess_ShouldReturnOwnPrivateDocuments() {
        // Given
        Long userId = 1L;
        String department = "IT";
        Pageable pageable = PageRequest.of(0, 10);

        // When
        Page<Document> result = documentRepository.findAccessibleDocuments(userId, department, pageable);

        // Then
        assertNotNull(result);
        assertTrue(result.getContent().stream()
                .anyMatch(doc -> doc.getSharingLevel() == SharingLevel.PRIVATE && 
                        doc.getCreatedBy().equals(userId)));
    }

    @Test
    void findByCreatedByOrderByCreatedAtDesc_ShouldReturnUserDocuments() {
        // Given
        Long userId = 1L;

        // When
        List<Document> result = documentRepository.findByCreatedByOrderByCreatedAtDesc(userId);

        // Then
        assertNotNull(result);
        assertTrue(result.size() >= 2); // publicDocument and privateDocument
        assertTrue(result.stream().allMatch(doc -> doc.getCreatedBy().equals(userId)));
    }

    @Test
    void findByDepartmentOrderByCreatedAtDesc_ShouldReturnDepartmentDocuments() {
        // Given
        String department = "IT";

        // When
        List<Document> result = documentRepository.findByDepartmentOrderByCreatedAtDesc(department);

        // Then
        assertNotNull(result);
        assertTrue(result.size() >= 2); // publicDocument and privateDocument
        assertTrue(result.stream().allMatch(doc -> doc.getDepartment().equals(department)));
    }

    @Test
    void findBySharingLevelOrderByCreatedAtDesc_ShouldReturnDocumentsBySharingLevel() {
        // Given
        SharingLevel sharingLevel = SharingLevel.PUBLIC;

        // When
        List<Document> result = documentRepository.findBySharingLevelOrderByCreatedAtDesc(sharingLevel);

        // Then
        assertNotNull(result);
        assertTrue(result.size() >= 1);
        assertTrue(result.stream().allMatch(doc -> doc.getSharingLevel() == sharingLevel));
    }

    @Test
    void findTopDocumentsByViewCount_ShouldReturnDocumentsOrderedByViewCount() {
        // Given
        Long userId = 1L;
        String department = "IT";
        Pageable pageable = PageRequest.of(0, 5);

        // When
        List<Document> result = documentRepository.findTopDocumentsByViewCount(userId, department, pageable);

        // Then
        assertNotNull(result);
        assertTrue(result.size() >= 1);
        // Verify ordering by view count (descending)
        for (int i = 0; i < result.size() - 1; i++) {
            assertTrue(result.get(i).getViewCount() >= result.get(i + 1).getViewCount());
        }
    }

    @Test
    void findTopDocumentsByStarCount_ShouldReturnDocumentsOrderedByStarCount() {
        // Given
        Long userId = 1L;
        String department = "IT";
        Pageable pageable = PageRequest.of(0, 5);

        // When
        List<Document> result = documentRepository.findTopDocumentsByStarCount(userId, department, pageable);

        // Then
        assertNotNull(result);
        assertTrue(result.size() >= 1);
        // Verify ordering by star count (descending)
        for (int i = 0; i < result.size() - 1; i++) {
            assertTrue(result.get(i).getStarCount() >= result.get(i + 1).getStarCount());
        }
    }

    @Test
    void findRecentDocuments_ShouldReturnDocumentsOrderedByCreatedAt() {
        // Given
        Long userId = 1L;
        String department = "IT";
        Pageable pageable = PageRequest.of(0, 5);

        // When
        List<Document> result = documentRepository.findRecentDocuments(userId, department, pageable);

        // Then
        assertNotNull(result);
        assertTrue(result.size() >= 1);
        // Verify ordering by created date (descending)
        for (int i = 0; i < result.size() - 1; i++) {
            assertTrue(result.get(i).getCreatedAt().isAfter(result.get(i + 1).getCreatedAt()) ||
                    result.get(i).getCreatedAt().isEqual(result.get(i + 1).getCreatedAt()));
        }
    }

    @Test
    void searchDocuments_WithKeyword_ShouldReturnMatchingDocuments() {
        // Given
        Long userId = 1L;
        String department = "IT";
        String keyword = "Public";
        Pageable pageable = PageRequest.of(0, 10);

        // When
        Page<Document> result = documentRepository.searchDocuments(userId, department, keyword, pageable);

        // Then
        assertNotNull(result);
        assertTrue(result.getContent().size() >= 1);
        assertTrue(result.getContent().stream()
                .anyMatch(doc -> doc.getTitle().toLowerCase().contains(keyword.toLowerCase())));
    }

    @Test
    void findByTag_ShouldReturnDocumentsWithMatchingTag() {
        // Given
        Long userId = 1L;
        String department = "IT";
        String tag = "test";
        Pageable pageable = PageRequest.of(0, 10);

        // When
        Page<Document> result = documentRepository.findByTag(userId, department, tag, pageable);

        // Then
        assertNotNull(result);
        assertTrue(result.getContent().size() >= 1);
        assertTrue(result.getContent().stream()
                .anyMatch(doc -> doc.getTags().contains(tag)));
    }
}
