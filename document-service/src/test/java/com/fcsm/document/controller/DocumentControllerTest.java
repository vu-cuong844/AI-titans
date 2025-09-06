package com.fcsm.document.controller;

import com.fcsm.document.dto.DocumentCreateRequest;
import com.fcsm.document.dto.DocumentDto;
import com.fcsm.document.dto.DocumentSearchRequest;
import com.fcsm.document.model.Document;
import com.fcsm.document.model.SharingLevel;
import com.fcsm.document.service.DocumentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DocumentController.class)
class DocumentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DocumentService documentService;

    @Autowired
    private ObjectMapper objectMapper;

    private DocumentDto testDocumentDto;
    private DocumentCreateRequest testCreateRequest;

    @BeforeEach
    void setUp() {
        testDocumentDto = new DocumentDto();
        testDocumentDto.setId(1L);
        testDocumentDto.setTitle("Test Document");
        testDocumentDto.setSummary("Test Summary");
        testDocumentDto.setFileName("test.pdf");
        testDocumentDto.setOriginalFileName("Test Document.pdf");
        testDocumentDto.setFileType("application/pdf");
        testDocumentDto.setFileSize(1024L);
        testDocumentDto.setFilePath("/uploads/test.pdf");
        testDocumentDto.setSharingLevel(SharingLevel.PUBLIC);
        testDocumentDto.setDepartment("IT");
        testDocumentDto.setCreatedBy(1L);
        testDocumentDto.setCreatedByName("Test User");
        testDocumentDto.setCreatedAt(LocalDateTime.now());
        testDocumentDto.setUpdatedAt(LocalDateTime.now());
        testDocumentDto.setTags(Arrays.asList("test", "document"));
        testDocumentDto.setViewCount(0);
        testDocumentDto.setStarCount(0);
        testDocumentDto.setContent("Test content");

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
    void createDocument_ShouldReturnDocumentDto() throws Exception {
        // Given
        when(documentService.createDocument(any(DocumentCreateRequest.class), anyLong(), anyString(), anyString()))
                .thenReturn(testDocumentDto);

        // When & Then
        mockMvc.perform(post("/api/documents")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testCreateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value("Test Document"))
                .andExpect(jsonPath("$.summary").value("Test Summary"))
                .andExpect(jsonPath("$.sharingLevel").value("PUBLIC"));
    }

    @Test
    void getDocument_WithValidId_ShouldReturnDocumentDto() throws Exception {
        // Given
        when(documentService.getDocumentById(eq(1L), anyLong(), anyString()))
                .thenReturn(Optional.of(testDocumentDto));

        // When & Then
        mockMvc.perform(get("/api/documents/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value("Test Document"))
                .andExpect(jsonPath("$.summary").value("Test Summary"));
    }

    @Test
    void getDocument_WithInvalidId_ShouldReturnNotFound() throws Exception {
        // Given
        when(documentService.getDocumentById(eq(999L), anyLong(), anyString()))
                .thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/api/documents/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getDocuments_ShouldReturnPageOfDocumentDtos() throws Exception {
        // Given
        Page<DocumentDto> documentPage = new PageImpl<>(Arrays.asList(testDocumentDto));
        when(documentService.getAccessibleDocuments(anyLong(), anyString(), eq(0), eq(10)))
                .thenReturn(documentPage);

        // When & Then
        mockMvc.perform(get("/api/documents")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].id").value(1L))
                .andExpect(jsonPath("$.content[0].title").value("Test Document"));
    }

    @Test
    void getTopViewedDocuments_ShouldReturnListOfDocumentDtos() throws Exception {
        // Given
        when(documentService.getTopDocumentsByViewCount(anyLong(), anyString(), eq(5)))
                .thenReturn(Arrays.asList(testDocumentDto));

        // When & Then
        mockMvc.perform(get("/api/documents/top/viewed")
                        .param("limit", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].title").value("Test Document"));
    }

    @Test
    void getTopStarredDocuments_ShouldReturnListOfDocumentDtos() throws Exception {
        // Given
        when(documentService.getTopDocumentsByStarCount(anyLong(), anyString(), eq(5)))
                .thenReturn(Arrays.asList(testDocumentDto));

        // When & Then
        mockMvc.perform(get("/api/documents/top/starred")
                        .param("limit", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].title").value("Test Document"));
    }

    @Test
    void getRecentDocuments_ShouldReturnListOfDocumentDtos() throws Exception {
        // Given
        when(documentService.getRecentDocuments(anyLong(), anyString(), eq(5)))
                .thenReturn(Arrays.asList(testDocumentDto));

        // When & Then
        mockMvc.perform(get("/api/documents/recent")
                        .param("limit", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].title").value("Test Document"));
    }

    @Test
    void getMyDocuments_ShouldReturnListOfDocumentDtos() throws Exception {
        // Given
        when(documentService.getUserDocuments(anyLong(), eq(5)))
                .thenReturn(Arrays.asList(testDocumentDto));

        // When & Then
        mockMvc.perform(get("/api/documents/my-documents")
                        .param("limit", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].title").value("Test Document"));
    }

    @Test
    void searchDocuments_ShouldReturnPageOfDocumentDtos() throws Exception {
        // Given
        DocumentSearchRequest searchRequest = new DocumentSearchRequest();
        searchRequest.setKeyword("test");
        searchRequest.setPage(0);
        searchRequest.setSize(10);
        searchRequest.setSortBy("createdAt");
        searchRequest.setSortDirection("desc");

        Page<DocumentDto> documentPage = new PageImpl<>(Arrays.asList(testDocumentDto));
        when(documentService.searchDocuments(any(DocumentSearchRequest.class), anyLong(), anyString()))
                .thenReturn(documentPage);

        // When & Then
        mockMvc.perform(post("/api/documents/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(searchRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].id").value(1L))
                .andExpect(jsonPath("$.content[0].title").value("Test Document"));
    }

    @Test
    void starDocument_WithValidId_ShouldReturnSuccess() throws Exception {
        // Given
        when(documentService.starDocument(eq(1L), anyLong(), anyString()))
                .thenReturn(true);

        // When & Then
        mockMvc.perform(post("/api/documents/1/star"))
                .andExpect(status().isOk())
                .andExpect(content().string("Document starred successfully"));
    }

    @Test
    void starDocument_WithInvalidId_ShouldReturnBadRequest() throws Exception {
        // Given
        when(documentService.starDocument(eq(999L), anyLong(), anyString()))
                .thenReturn(false);

        // When & Then
        mockMvc.perform(post("/api/documents/999/star"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Failed to star document"));
    }

    @Test
    void deleteDocument_WithValidId_ShouldReturnSuccess() throws Exception {
        // Given
        when(documentService.deleteDocument(eq(1L), anyLong()))
                .thenReturn(true);

        // When & Then
        mockMvc.perform(delete("/api/documents/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Document deleted successfully"));
    }

    @Test
    void deleteDocument_WithInvalidId_ShouldReturnBadRequest() throws Exception {
        // Given
        when(documentService.deleteDocument(eq(999L), anyLong()))
                .thenReturn(false);

        // When & Then
        mockMvc.perform(delete("/api/documents/999"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Failed to delete document"));
    }
}
