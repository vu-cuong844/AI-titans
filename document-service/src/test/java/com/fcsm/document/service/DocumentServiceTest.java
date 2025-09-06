package com.fcsm.document.service;

import com.fcsm.document.dto.DocumentCreateRequest;
import com.fcsm.document.dto.DocumentDto;
import com.fcsm.document.dto.DocumentSearchRequest;
import com.fcsm.document.model.Document;
import com.fcsm.document.model.SharingLevel;
import com.fcsm.document.repository.DocumentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DocumentServiceTest {

    @Mock
    private DocumentRepository documentRepository;

    @InjectMocks
    private DocumentService documentService;

    private Document testDocument;
    private DocumentCreateRequest testCreateRequest;

    @BeforeEach
    void setUp() {
        testDocument = new Document();
        testDocument.setId(1L);
        testDocument.setTitle("Test Document");
        testDocument.setSummary("Test Summary");
        testDocument.setFileName("test.pdf");
        testDocument.setOriginalFileName("Test Document.pdf");
        testDocument.setFileType("application/pdf");
        testDocument.setFileSize(1024L);
        testDocument.setFilePath("/uploads/test.pdf");
        testDocument.setSharingLevel(SharingLevel.PUBLIC);
        testDocument.setDepartment("IT");
        testDocument.setCreatedBy(1L);
        testDocument.setCreatedByName("Test User");
        testDocument.setCreatedAt(LocalDateTime.now());
        testDocument.setUpdatedAt(LocalDateTime.now());
        testDocument.setTags(Arrays.asList("test", "document"));
        testDocument.setViewCount(0);
        testDocument.setStarCount(0);
        testDocument.setContent("Test content");

        testCreateRequest = new DocumentCreateRequest();
        testCreateRequest.setTitle("Test Document");
        testCreateRequest.setSummary("Test Summary");
        testCreateRequest.setFileName("test.pdf");
        testCreateRequest.setOriginalFileName("Test Document.pdf");
        testCreateRequest.setFileType("application/pdf");
        testCreateRequest.setFileSize(1024L);
        testCreateRequest.setFilePath("/uploads/test.pdf");
        testCreateRequest.setSharingLevel(SharingLevel.PUBLIC);
        testCreateRequest.setDepartment("IT");
        testCreateRequest.setTags(Arrays.asList("test", "document"));
        testCreateRequest.setContent("Test content");
    }

    @Test
    void createDocument_ShouldReturnDocumentDto() {
        // Given
        when(documentRepository.save(any(Document.class))).thenReturn(testDocument);

        // When
        DocumentDto result = documentService.createDocument(testCreateRequest, 1L, "Test User", "IT");

        // Then
        assertNotNull(result);
        assertEquals(testDocument.getTitle(), result.getTitle());
        assertEquals(testDocument.getSummary(), result.getSummary());
        assertEquals(testDocument.getFileName(), result.getFileName());
        assertEquals(testDocument.getSharingLevel(), result.getSharingLevel());
        verify(documentRepository, times(1)).save(any(Document.class));
    }

    @Test
    void getDocumentById_WithValidIdAndAccess_ShouldReturnDocumentDto() {
        // Given
        when(documentRepository.findById(1L)).thenReturn(Optional.of(testDocument));
        when(documentRepository.save(any(Document.class))).thenReturn(testDocument);

        // When
        Optional<DocumentDto> result = documentService.getDocumentById(1L, 1L, "IT");

        // Then
        assertTrue(result.isPresent());
        assertEquals(testDocument.getTitle(), result.get().getTitle());
        verify(documentRepository, times(1)).findById(1L);
        verify(documentRepository, times(1)).save(any(Document.class));
    }

    @Test
    void getDocumentById_WithInvalidId_ShouldReturnEmpty() {
        // Given
        when(documentRepository.findById(999L)).thenReturn(Optional.empty());

        // When
        Optional<DocumentDto> result = documentService.getDocumentById(999L, 1L, "IT");

        // Then
        assertFalse(result.isPresent());
        verify(documentRepository, times(1)).findById(999L);
        verify(documentRepository, never()).save(any(Document.class));
    }

    @Test
    void getDocumentById_WithNoAccess_ShouldReturnEmpty() {
        // Given
        testDocument.setSharingLevel(SharingLevel.PRIVATE);
        testDocument.setCreatedBy(2L); // Different user
        when(documentRepository.findById(1L)).thenReturn(Optional.of(testDocument));

        // When
        Optional<DocumentDto> result = documentService.getDocumentById(1L, 1L, "IT");

        // Then
        assertFalse(result.isPresent());
        verify(documentRepository, times(1)).findById(1L);
        verify(documentRepository, never()).save(any(Document.class));
    }

    @Test
    void getAccessibleDocuments_ShouldReturnPageOfDocumentDtos() {
        // Given
        Page<Document> documentPage = new PageImpl<>(Arrays.asList(testDocument));
        when(documentRepository.findAccessibleDocuments(anyLong(), anyString(), any(Pageable.class)))
                .thenReturn(documentPage);

        // When
        Page<DocumentDto> result = documentService.getAccessibleDocuments(1L, "IT", 0, 10);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(testDocument.getTitle(), result.getContent().get(0).getTitle());
        verify(documentRepository, times(1)).findAccessibleDocuments(anyLong(), anyString(), any(Pageable.class));
    }

    @Test
    void getTopDocumentsByViewCount_ShouldReturnListOfDocumentDtos() {
        // Given
        when(documentRepository.findTopDocumentsByViewCount(anyLong(), anyString(), any(Pageable.class)))
                .thenReturn(Arrays.asList(testDocument));

        // When
        List<DocumentDto> result = documentService.getTopDocumentsByViewCount(1L, "IT", 5);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testDocument.getTitle(), result.get(0).getTitle());
        verify(documentRepository, times(1)).findTopDocumentsByViewCount(anyLong(), anyString(), any(Pageable.class));
    }

    @Test
    void getTopDocumentsByStarCount_ShouldReturnListOfDocumentDtos() {
        // Given
        when(documentRepository.findTopDocumentsByStarCount(anyLong(), anyString(), any(Pageable.class)))
                .thenReturn(Arrays.asList(testDocument));

        // When
        List<DocumentDto> result = documentService.getTopDocumentsByStarCount(1L, "IT", 5);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testDocument.getTitle(), result.get(0).getTitle());
        verify(documentRepository, times(1)).findTopDocumentsByStarCount(anyLong(), anyString(), any(Pageable.class));
    }

    @Test
    void getRecentDocuments_ShouldReturnListOfDocumentDtos() {
        // Given
        when(documentRepository.findRecentDocuments(anyLong(), anyString(), any(Pageable.class)))
                .thenReturn(Arrays.asList(testDocument));

        // When
        List<DocumentDto> result = documentService.getRecentDocuments(1L, "IT", 5);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testDocument.getTitle(), result.get(0).getTitle());
        verify(documentRepository, times(1)).findRecentDocuments(anyLong(), anyString(), any(Pageable.class));
    }

    @Test
    void getUserDocuments_ShouldReturnListOfDocumentDtos() {
        // Given
        when(documentRepository.findByCreatedByOrderByCreatedAtDesc(1L))
                .thenReturn(Arrays.asList(testDocument));

        // When
        List<DocumentDto> result = documentService.getUserDocuments(1L, 5);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testDocument.getTitle(), result.get(0).getTitle());
        verify(documentRepository, times(1)).findByCreatedByOrderByCreatedAtDesc(1L);
    }

    @Test
    void searchDocuments_WithKeyword_ShouldReturnPageOfDocumentDtos() {
        // Given
        DocumentSearchRequest searchRequest = new DocumentSearchRequest();
        searchRequest.setKeyword("test");
        searchRequest.setPage(0);
        searchRequest.setSize(10);
        searchRequest.setSortBy("createdAt");
        searchRequest.setSortDirection("desc");

        Page<Document> documentPage = new PageImpl<>(Arrays.asList(testDocument));
        when(documentRepository.searchDocuments(anyLong(), anyString(), anyString(), any(Pageable.class)))
                .thenReturn(documentPage);

        // When
        Page<DocumentDto> result = documentService.searchDocuments(searchRequest, 1L, "IT");

        // Then
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(testDocument.getTitle(), result.getContent().get(0).getTitle());
        verify(documentRepository, times(1)).searchDocuments(anyLong(), anyString(), anyString(), any(Pageable.class));
    }

    @Test
    void starDocument_WithValidIdAndAccess_ShouldReturnTrue() {
        // Given
        when(documentRepository.findById(1L)).thenReturn(Optional.of(testDocument));
        when(documentRepository.save(any(Document.class))).thenReturn(testDocument);

        // When
        boolean result = documentService.starDocument(1L, 1L, "IT");

        // Then
        assertTrue(result);
        verify(documentRepository, times(1)).findById(1L);
        verify(documentRepository, times(1)).save(any(Document.class));
    }

    @Test
    void starDocument_WithInvalidId_ShouldReturnFalse() {
        // Given
        when(documentRepository.findById(999L)).thenReturn(Optional.empty());

        // When
        boolean result = documentService.starDocument(999L, 1L, "IT");

        // Then
        assertFalse(result);
        verify(documentRepository, times(1)).findById(999L);
        verify(documentRepository, never()).save(any(Document.class));
    }

    @Test
    void deleteDocument_WithValidIdAndOwner_ShouldReturnTrue() {
        // Given
        when(documentRepository.findById(1L)).thenReturn(Optional.of(testDocument));
        doNothing().when(documentRepository).delete(any(Document.class));

        // When
        boolean result = documentService.deleteDocument(1L, 1L);

        // Then
        assertTrue(result);
        verify(documentRepository, times(1)).findById(1L);
        verify(documentRepository, times(1)).delete(any(Document.class));
    }

    @Test
    void deleteDocument_WithInvalidId_ShouldReturnFalse() {
        // Given
        when(documentRepository.findById(999L)).thenReturn(Optional.empty());

        // When
        boolean result = documentService.deleteDocument(999L, 1L);

        // Then
        assertFalse(result);
        verify(documentRepository, times(1)).findById(999L);
        verify(documentRepository, never()).delete(any(Document.class));
    }

    @Test
    void deleteDocument_WithNonOwner_ShouldReturnFalse() {
        // Given
        testDocument.setCreatedBy(2L); // Different user
        when(documentRepository.findById(1L)).thenReturn(Optional.of(testDocument));

        // When
        boolean result = documentService.deleteDocument(1L, 1L);

        // Then
        assertFalse(result);
        verify(documentRepository, times(1)).findById(1L);
        verify(documentRepository, never()).delete(any(Document.class));
    }
}
