package com.fcsm.document.service;

import com.fcsm.document.dto.DocumentCreateRequest;
import com.fcsm.document.dto.DocumentDto;
import com.fcsm.document.dto.DocumentSearchRequest;
import com.fcsm.document.model.Document;
import com.fcsm.document.repository.DocumentRepository;
import com.fcsm.document.service.AIService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class DocumentService {
    
    @Autowired
    private DocumentRepository documentRepository;
    
    @Autowired
    private AIService aiService;
    
    public DocumentDto createDocument(DocumentCreateRequest request, Long userId, String userName, String department) {
        Document document = new Document();
        document.setTitle(request.getTitle());
        
        // Tự động tạo tóm tắt nếu chưa có hoặc rỗng
        String summary = request.getSummary();
        if (summary == null || summary.trim().isEmpty()) {
            summary = aiService.generateSummary(request.getContent());
        }
        document.setSummary(summary);
        
        document.setFileName(request.getFileName());
        document.setOriginalFileName(request.getOriginalFileName());
        document.setFileType(request.getFileType());
        document.setFileSize(request.getFileSize());
        document.setFilePath(request.getFilePath());
        document.setSharingLevel(request.getSharingLevel());
        document.setDepartment(department);
        document.setCreatedBy(userId);
        document.setCreatedByName(userName);
        
        // Tự động tạo tags nếu chưa có hoặc rỗng
        List<String> tags = request.getTags();
        if (tags == null || tags.isEmpty()) {
            tags = aiService.generateTags(request.getContent(), request.getTitle());
        }
        document.setTags(tags);
        
        document.setContent(request.getContent());
        document.setCreatedAt(LocalDateTime.now());
        document.setUpdatedAt(LocalDateTime.now());
        
        Document savedDocument = documentRepository.save(document);
        return convertToDto(savedDocument);
    }
    
    public Optional<DocumentDto> getDocumentById(Long id, Long userId, String department) {
        Optional<Document> document = documentRepository.findById(id);
        if (document.isPresent() && hasAccess(document.get(), userId, department)) {
            // Increment view count
            Document doc = document.get();
            doc.setViewCount(doc.getViewCount() + 1);
            documentRepository.save(doc);
            return Optional.of(convertToDto(doc));
        }
        return Optional.empty();
    }
    
    public Page<DocumentDto> getAccessibleDocuments(Long userId, String department, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Document> documents = documentRepository.findAccessibleDocuments(userId, department, pageable);
        return documents.map(this::convertToDto);
    }
    
    public List<DocumentDto> getTopDocumentsByViewCount(Long userId, String department, int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        List<Document> documents = documentRepository.findTopDocumentsByViewCount(userId, department, pageable);
        return documents.stream().map(this::convertToDto).collect(Collectors.toList());
    }
    
    public List<DocumentDto> getTopDocumentsByStarCount(Long userId, String department, int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        List<Document> documents = documentRepository.findTopDocumentsByStarCount(userId, department, pageable);
        return documents.stream().map(this::convertToDto).collect(Collectors.toList());
    }
    
    public List<DocumentDto> getRecentDocuments(Long userId, String department, int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        List<Document> documents = documentRepository.findRecentDocuments(userId, department, pageable);
        return documents.stream().map(this::convertToDto).collect(Collectors.toList());
    }
    
    public List<DocumentDto> getUserDocuments(Long userId, int limit) {
        List<Document> documents = documentRepository.findByCreatedByOrderByCreatedAtDesc(userId);
        return documents.stream()
                .limit(limit)
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    public Page<DocumentDto> searchDocuments(DocumentSearchRequest request, Long userId, String department) {
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize(), 
                Sort.by(Sort.Direction.fromString(request.getSortDirection()), request.getSortBy()));
        
        Page<Document> documents;
        
        if (request.getKeyword() != null && !request.getKeyword().trim().isEmpty()) {
            documents = documentRepository.searchDocuments(userId, department, request.getKeyword(), pageable);
        } else if (request.getTag() != null && !request.getTag().trim().isEmpty()) {
            documents = documentRepository.findByTag(userId, department, request.getTag(), pageable);
        } else if (request.getStartDate() != null && request.getEndDate() != null) {
            documents = documentRepository.findByDateRange(userId, department, 
                    request.getStartDate(), request.getEndDate(), pageable);
        } else {
            documents = documentRepository.findAccessibleDocuments(userId, department, pageable);
        }
        
        return documents.map(this::convertToDto);
    }
    
    public boolean starDocument(Long documentId, Long userId, String department) {
        Optional<Document> documentOpt = documentRepository.findById(documentId);
        if (documentOpt.isPresent()) {
            Document document = documentOpt.get();
            if (hasAccess(document, userId, department)) {
                document.setStarCount(document.getStarCount() + 1);
                documentRepository.save(document);
                return true;
            }
        }
        return false;
    }
    
    public boolean deleteDocument(Long documentId, Long userId) {
        Optional<Document> documentOpt = documentRepository.findById(documentId);
        if (documentOpt.isPresent()) {
            Document document = documentOpt.get();
            if (document.getCreatedBy().equals(userId)) {
                documentRepository.delete(document);
                return true;
            }
        }
        return false;
    }
    
    private boolean hasAccess(Document document, Long userId, String department) {
        switch (document.getSharingLevel()) {
            case PRIVATE:
                return document.getCreatedBy().equals(userId);
            case GROUP:
                return document.getDepartment().equals(department);
            case PUBLIC:
                return true;
            default:
                return false;
        }
    }
    
    private DocumentDto convertToDto(Document document) {
        DocumentDto dto = new DocumentDto();
        dto.setId(document.getId());
        dto.setTitle(document.getTitle());
        dto.setSummary(document.getSummary());
        dto.setFileName(document.getFileName());
        dto.setOriginalFileName(document.getOriginalFileName());
        dto.setFileType(document.getFileType());
        dto.setFileSize(document.getFileSize());
        dto.setFilePath(document.getFilePath());
        dto.setSharingLevel(document.getSharingLevel());
        dto.setDepartment(document.getDepartment());
        dto.setCreatedBy(document.getCreatedBy());
        dto.setCreatedByName(document.getCreatedByName());
        dto.setCreatedAt(document.getCreatedAt());
        dto.setUpdatedAt(document.getUpdatedAt());
        dto.setTags(document.getTags());
        dto.setViewCount(document.getViewCount());
        dto.setStarCount(document.getStarCount());
        dto.setContent(document.getContent());
        return dto;
    }
}
