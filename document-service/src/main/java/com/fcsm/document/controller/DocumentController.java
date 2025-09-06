package com.fcsm.document.controller;

import com.fcsm.document.dto.DocumentCreateRequest;
import com.fcsm.document.dto.DocumentDto;
import com.fcsm.document.dto.DocumentSearchRequest;
import com.fcsm.document.service.DocumentService;
import com.fcsm.document.service.AIService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/documents")
@CrossOrigin(origins = "*")
public class DocumentController {
    
    @Autowired
    private DocumentService documentService;
    
    @Autowired
    private AIService aiService;
    
    // Mock user data - in real implementation, get from JWT token
    private Long getCurrentUserId() {
        return 1L; // Mock user ID
    }
    
    private String getCurrentUserName() {
        return "John Doe"; // Mock user name
    }
    
    private String getCurrentUserDepartment() {
        return "IT"; // Mock department
    }

    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<DocumentDto> createDocument(
            @Valid @ModelAttribute DocumentCreateRequest request,
            @RequestPart(value = "file", required = false) MultipartFile file) {
        if (request.getTitle() == null || request.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Tiêu đề là bắt buộc");
        }

        if (file == null && (request.getContent() == null || request.getContent().trim().isEmpty())) {
            throw new IllegalArgumentException("Phải cung cấp ít nhất nội dung hoặc file");
        }

        if (file != null && !isValidFileType(file.getContentType())) {
            throw new IllegalArgumentException("Chỉ chấp nhận file doc, PDF hoặc image");
        }

        if (file != null && file.getSize() > 10 * 1024 * 1024) {
            throw new IllegalArgumentException("Kích thước file không được vượt quá 10MB");
        }

        DocumentDto document = documentService.createDocument(request, file, getCurrentUserId(), getCurrentUserName(), getCurrentUserDepartment());
        return ResponseEntity.ok(document);
    }

    // Phương thức kiểm tra định dạng file
    private boolean isValidFileType(String fileType) {
        if (fileType == null) return true; // Cho phép null vì file không bắt buộc
        String normalizedType = fileType.toLowerCase();
        System.out.println(normalizedType);
        return normalizedType.equals("application/doc") ||
                normalizedType.equals("application/docx") ||
                normalizedType.equals("application/pdf") ||
                normalizedType.startsWith("application/image/");
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<DocumentDto> getDocument(@PathVariable("id") Long id) {
        return documentService.getDocumentById(id, getCurrentUserId(), getCurrentUserDepartment())
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping
    public ResponseEntity<Page<DocumentDto>> getDocuments(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {
        Page<DocumentDto> documents = documentService.getAccessibleDocuments(
            getCurrentUserId(), 
            getCurrentUserDepartment(), 
            page, 
            size
        );
        return ResponseEntity.ok(documents);
    }
    
    @GetMapping("/top/viewed")
    public ResponseEntity<List<DocumentDto>> getTopViewedDocuments(
            @RequestParam(value = "limit", defaultValue = "5") int limit) {
        List<DocumentDto> documents = documentService.getTopDocumentsByViewCount(
            getCurrentUserId(), 
            getCurrentUserDepartment(), 
            limit
        );
        return ResponseEntity.ok(documents);
    }
    
    @GetMapping("/top/starred")
    public ResponseEntity<List<DocumentDto>> getTopStarredDocuments(
            @RequestParam(value = "limit", defaultValue = "5") int limit) {
        List<DocumentDto> documents = documentService.getTopDocumentsByStarCount(
            getCurrentUserId(), 
            getCurrentUserDepartment(), 
            limit
        );
        return ResponseEntity.ok(documents);
    }
    
    @GetMapping("/recent")
    public ResponseEntity<List<DocumentDto>> getRecentDocuments(
            @RequestParam(value = "limit", defaultValue = "5") int limit) {
        List<DocumentDto> documents = documentService.getRecentDocuments(
            getCurrentUserId(), 
            getCurrentUserDepartment(), 
            limit
        );
        return ResponseEntity.ok(documents);
    }
    
    @GetMapping("/my-documents")
    public ResponseEntity<List<DocumentDto>> getMyDocuments(
            @RequestParam(value = "limit", defaultValue = "5") int limit) {
        List<DocumentDto> documents = documentService.getUserDocuments(getCurrentUserId(), limit);
        return ResponseEntity.ok(documents);
    }
    
    @PostMapping("/search")
    public ResponseEntity<Page<DocumentDto>> searchDocuments(@RequestBody DocumentSearchRequest request) {
        Page<DocumentDto> documents = documentService.searchDocuments(
            request, 
            getCurrentUserId(), 
            getCurrentUserDepartment()
        );
        return ResponseEntity.ok(documents);
    }
    
    @PostMapping("/{id}/star")
    public ResponseEntity<String> starDocument(@PathVariable("id") Long id) {
        boolean success = documentService.starDocument(id, getCurrentUserId(), getCurrentUserDepartment());
        if (success) {
            return ResponseEntity.ok("Document starred successfully");
        }
        return ResponseEntity.badRequest().body("Failed to star document");
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteDocument(@PathVariable Long id) {
        boolean success = documentService.deleteDocument(id, getCurrentUserId());
        if (success) {
            return ResponseEntity.ok("Document deleted successfully");
        }
        return ResponseEntity.badRequest().body("Failed to delete document");
    }
    
    @PostMapping("/ai/generate-summary")
    public ResponseEntity<String> generateSummary(@RequestBody String content) {
        String summary = aiService.generateSummary(content);
        return ResponseEntity.ok(summary);
    }
    
    @PostMapping("/ai/generate-tags")
    public ResponseEntity<List<String>> generateTags(@RequestParam String content, @RequestParam String title) {
        List<String> tags = aiService.generateTags(content, title);
        return ResponseEntity.ok(tags);
    }
}
