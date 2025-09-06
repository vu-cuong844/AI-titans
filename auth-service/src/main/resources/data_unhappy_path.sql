-- ============================================
-- DỮ LIỆU TEST CASE 2: XỬ LÝ THÔNG TIN ĐĂNG NHẬP KHÔNG HỢP LỆ
-- ============================================
-- File này chứa dữ liệu để test các trường hợp đăng nhập thất bại
-- Sử dụng khi muốn test Test Case 2

-- Xóa dữ liệu cũ nếu có
DELETE FROM mb_users;

-- Insert users cho Test Case 2: Đăng nhập không hợp lệ
INSERT INTO mb_users (username, password, full_name, email, department, role, is_active) VALUES

-- ============================================
-- TEST CASE 2: CÁC TRƯỜNG HỢP ĐĂNG NHẬP KHÔNG HỢP LỆ
-- ============================================

-- User hợp lệ để so sánh
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', 'Administrator', 'admin@fcsm.com', 'IT', 'ADMIN', true),

-- User bị vô hiệu hóa (is_active = false)
('disabled_user', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', 'Disabled User', 'disabled@fcsm.com', 'IT', 'EMPLOYEE', false),

-- User với email không hợp lệ format
('invalid_email', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', 'Invalid Email User', 'invalid-email', 'IT', 'EMPLOYEE', true);

-- ============================================
-- THÔNG TIN TEST ACCOUNTS - UNHAPPY PATH
-- ============================================
-- Password cho tất cả users: 'password' (đã được hash với BCrypt)
-- 
-- TEST ACCOUNTS (UNHAPPY PATH):
-- 1. admin / password - ADMIN role, IT department (HỢP LỆ - để so sánh)
-- 2. disabled_user / password - EMPLOYEE role, IT department (INACTIVE - KHÔNG HỢP LỆ)
-- 3. invalid_email / password - EMPLOYEE role, IT department (EMAIL KHÔNG HỢP LỆ)
--
-- INVALID ACCOUNTS (không tồn tại trong DB - để test):
-- - wronguser / password
-- - nonexistent / anypassword
-- - admin / wrongpassword
--
-- CÁCH SỬ DỤNG:
-- 1. Backup file data.sql hiện tại
-- 2. Copy nội dung file này vào data.sql
-- 3. Restart ứng dụng
-- 4. Test các trường hợp đăng nhập không hợp lệ
--
-- TEST SCENARIOS:
-- ✅ admin / password → 200 OK (hợp lệ)
-- ❌ disabled_user / password → 401 Unauthorized (user bị vô hiệu hóa)
-- ❌ invalid_email / password → 401 Unauthorized (email không hợp lệ)
-- ❌ wronguser / password → 401 Unauthorized (user không tồn tại)
-- ❌ admin / wrongpassword → 401 Unauthorized (password sai)
-- ❌ "" / "" → 400 Bad Request (empty credentials)
--
-- EXPECTED RESULTS:
-- - Tất cả trường hợp không hợp lệ phải trả về 401 hoặc 400
-- - Error message phải rõ ràng và có ý nghĩa
-- - Không được tạo JWT token cho các trường hợp không hợp lệ
