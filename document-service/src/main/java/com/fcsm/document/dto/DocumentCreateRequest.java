package com.fcsm.document.dto;

import com.fcsm.document.model.SharingLevel;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentCreateRequest {
    @NotBlank(message = "Tiêu đề là bắt buộc")
    @Size(max = 200, message = "Tiêu đề không được vượt quá 200 ký tự")
    private String title;

    @Size(max = 1000, message = "Tóm tắt không được vượt quá 1000 ký tự")
    private String summary;

    @Size(max = 255, message = "Tên file không được vượt quá 255 ký tự")
    private String fileName;

    @Size(max = 255, message = "Tên file gốc không được vượt quá 255 ký tự")
    private String originalFileName;

    private String fileType;

    @Max(value = 10485760, message = "Kích thước file không được vượt quá 10MB")
    private Long fileSize;

    private String filePath;

    @NotNull(message = "Mức độ chia sẻ là bắt buộc")
    private SharingLevel sharingLevel;

    private String department;

    private List<String> tags;

    @Size(max = 10000, message = "Nội dung không được vượt quá 10000 ký tự")
    private String content; // Nội dung không bắt buộc nhưng có giới hạn độ dài

    private MultipartFile file;
}