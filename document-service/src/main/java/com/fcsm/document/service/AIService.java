package com.fcsm.document.service;

import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Arrays;

@Service
public class AIService {
    
    /**
     * Tạo tóm tắt nội dung tài liệu bằng AI
     * @param content Nội dung tài liệu
     * @return Tóm tắt không quá 500 từ
     */
    public String generateSummary(String content) {
        if (content == null || content.trim().isEmpty()) {
            return "Nội dung tài liệu không có thông tin để tóm tắt.";
        }
        
        // Mock AI implementation - trong thực tế sẽ gọi AI service
        String summary = content.length() > 500 ? content.substring(0, 500) + "..." : content;
        
        // Giả lập tóm tắt thông minh hơn
        if (content.toLowerCase().contains("hợp đồng")) {
            summary = "Tài liệu hợp đồng chứa các điều khoản và điều kiện quan trọng về thỏa thuận giữa các bên liên quan.";
        } else if (content.toLowerCase().contains("báo cáo")) {
            summary = "Báo cáo tổng hợp các thông tin và dữ liệu quan trọng trong khoảng thời gian nhất định.";
        } else if (content.toLowerCase().contains("quy định")) {
            summary = "Tài liệu quy định chứa các nguyên tắc và hướng dẫn cần tuân thủ trong tổ chức.";
        } else if (content.toLowerCase().contains("hướng dẫn")) {
            summary = "Tài liệu hướng dẫn cung cấp các bước và quy trình thực hiện công việc cụ thể.";
        } else {
            // Tóm tắt đơn giản
            String[] sentences = content.split("[.!?]+");
            if (sentences.length > 0) {
                summary = sentences[0].trim();
                if (summary.length() > 200) {
                    summary = summary.substring(0, 200) + "...";
                }
            }
        }
        
        // Đảm bảo không vượt quá 500 từ
        String[] words = summary.split("\\s+");
        if (words.length > 500) {
            summary = String.join(" ", Arrays.copyOf(words, 500)) + "...";
        }
        
        return summary;
    }
    
    /**
     * Tạo tags tự động bằng AI dựa trên nội dung tài liệu
     * @param content Nội dung tài liệu
     * @param title Tiêu đề tài liệu
     * @return Danh sách tags được đề xuất
     */
    public List<String> generateTags(String content, String title) {
        // Mock AI implementation - trong thực tế sẽ gọi AI service
        List<String> tags = new java.util.ArrayList<>();
        
        String textToAnalyze = (title + " " + (content != null ? content : "")).toLowerCase();
        
        // Phân tích từ khóa và tạo tags
        if (textToAnalyze.contains("hợp đồng") || textToAnalyze.contains("contract")) {
            tags.add("hợp đồng");
        }
        if (textToAnalyze.contains("báo cáo") || textToAnalyze.contains("report")) {
            tags.add("báo cáo");
        }
        if (textToAnalyze.contains("quy định") || textToAnalyze.contains("regulation")) {
            tags.add("quy định");
        }
        if (textToAnalyze.contains("hướng dẫn") || textToAnalyze.contains("guide")) {
            tags.add("hướng dẫn");
        }
        if (textToAnalyze.contains("tài chính") || textToAnalyze.contains("finance")) {
            tags.add("tài chính");
        }
        if (textToAnalyze.contains("nhân sự") || textToAnalyze.contains("hr") || textToAnalyze.contains("human resource")) {
            tags.add("nhân sự");
        }
        if (textToAnalyze.contains("công nghệ") || textToAnalyze.contains("technology") || textToAnalyze.contains("it")) {
            tags.add("công nghệ");
        }
        if (textToAnalyze.contains("marketing") || textToAnalyze.contains("tiếp thị")) {
            tags.add("marketing");
        }
        if (textToAnalyze.contains("kinh doanh") || textToAnalyze.contains("business")) {
            tags.add("kinh doanh");
        }
        if (textToAnalyze.contains("pháp lý") || textToAnalyze.contains("legal")) {
            tags.add("pháp lý");
        }
        if (textToAnalyze.contains("dự án") || textToAnalyze.contains("project")) {
            tags.add("dự án");
        }
        if (textToAnalyze.contains("khách hàng") || textToAnalyze.contains("customer")) {
            tags.add("khách hàng");
        }
        if (textToAnalyze.contains("nội bộ") || textToAnalyze.contains("internal")) {
            tags.add("nội bộ");
        }
        if (textToAnalyze.contains("công khai") || textToAnalyze.contains("public")) {
            tags.add("công khai");
        }
        
        // Thêm tags dựa trên loại file
        if (textToAnalyze.contains(".pdf")) {
            tags.add("pdf");
        }
        if (textToAnalyze.contains(".doc") || textToAnalyze.contains(".docx")) {
            tags.add("word");
        }
        if (textToAnalyze.contains(".jpg") || textToAnalyze.contains(".jpeg") || textToAnalyze.contains(".png")) {
            tags.add("hình ảnh");
        }
        
        // Giới hạn số lượng tags
        if (tags.size() > 10) {
            tags = tags.subList(0, 10);
        }
        
        return tags;
    }
}
