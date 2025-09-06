-- Insert sample documents for testing
INSERT INTO documents (title, summary, file_name, original_file_name, file_type, file_size, file_path, sharing_level, department, created_by, created_by_name, created_at, updated_at, view_count, star_count, content) VALUES
('Hướng dẫn sử dụng hệ thống', 'Tài liệu hướng dẫn chi tiết về cách sử dụng các tính năng của hệ thống quản lý nhân sự', 'huong_dan_su_dung.pdf', 'Hướng dẫn sử dụng hệ thống.pdf', 'application/pdf', 2048576, '/uploads/huong_dan_su_dung.pdf', 'PUBLIC', 'IT', 1, 'Administrator', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 25, 8, 'Nội dung chi tiết về hướng dẫn sử dụng hệ thống...'),

('Quy trình tuyển dụng', 'Quy trình và các bước thực hiện tuyển dụng nhân viên mới', 'quy_trinh_tuyen_dung.docx', 'Quy trình tuyển dụng.docx', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document', 1536000, '/uploads/quy_trinh_tuyen_dung.docx', 'GROUP', 'HR', 2, 'Manager One', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 15, 5, 'Quy trình tuyển dụng bao gồm các bước...'),

('Báo cáo tài chính Q1', 'Báo cáo tài chính quý 1 năm 2024', 'bao_cao_tai_chinh_q1.xlsx', 'Báo cáo tài chính Q1.xlsx', 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet', 3072000, '/uploads/bao_cao_tai_chinh_q1.xlsx', 'PRIVATE', 'Finance', 5, 'Employee Three', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 3, 1, 'Dữ liệu tài chính chi tiết...'),

('Chính sách bảo mật', 'Chính sách bảo mật thông tin và dữ liệu của công ty', 'chinh_sach_bao_mat.pdf', 'Chính sách bảo mật.pdf', 'application/pdf', 1024000, '/uploads/chinh_sach_bao_mat.pdf', 'PUBLIC', 'IT', 1, 'Administrator', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 45, 12, 'Chính sách bảo mật thông tin...'),

('Hướng dẫn onboarding', 'Tài liệu hướng dẫn cho nhân viên mới', 'huong_dan_onboarding.docx', 'Hướng dẫn onboarding.docx', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document', 2560000, '/uploads/huong_dan_onboarding.docx', 'GROUP', 'HR', 2, 'Manager One', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 20, 7, 'Quy trình onboarding cho nhân viên mới...');

-- Insert sample tags
INSERT INTO document_tags (document_id, tag) VALUES
(1, 'hướng dẫn'),
(1, 'hệ thống'),
(1, 'IT'),
(2, 'tuyển dụng'),
(2, 'HR'),
(2, 'quy trình'),
(3, 'tài chính'),
(3, 'báo cáo'),
(3, 'Q1'),
(4, 'bảo mật'),
(4, 'chính sách'),
(4, 'IT'),
(5, 'onboarding'),
(5, 'nhân viên mới'),
(5, 'HR');
