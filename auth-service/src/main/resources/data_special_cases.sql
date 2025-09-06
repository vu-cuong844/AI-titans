-- ============================================
-- DỮ LIỆU CÁC TRƯỜNG HỢP ĐẶC BIỆT
-- ============================================
-- File này chứa dữ liệu để test các trường hợp đặc biệt
-- Sử dụng khi muốn test các edge cases và boundary conditions

-- Xóa dữ liệu cũ nếu có
DELETE FROM mb_users;

-- Insert users cho các trường hợp đặc biệt
INSERT INTO mb_users (username, password, full_name, email, department, role, is_active) VALUES

-- ============================================
-- BASIC USERS (để so sánh)
-- ============================================
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', 'Administrator', 'admin@fcsm.com', 'IT', 'ADMIN', true),
('employee1', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', 'Employee One', 'employee1@fcsm.com', 'IT', 'EMPLOYEE', true),

-- ============================================
-- SPECIAL CASES
-- ============================================

-- User với tên dài (test boundary conditions)
('longname_user', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', 'Very Long Name User For Testing Purposes And Boundary Conditions', 'longname@fcsm.com', 'IT', 'EMPLOYEE', true),

-- User với department đặc biệt
('special_dept', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', 'Special Department User', 'special@fcsm.com', 'R&D', 'EMPLOYEE', true),

-- User với role khác nhau
('supervisor', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', 'Supervisor User', 'supervisor@fcsm.com', 'Operations', 'SUPERVISOR', true),

-- User với email có ký tự đặc biệt
('special_email', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', 'Special Email User', 'user.name+tag@company-domain.co.uk', 'IT', 'EMPLOYEE', true),

-- User với username có số
('user123', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', 'User With Numbers', 'user123@fcsm.com', 'IT', 'EMPLOYEE', true),

-- User với username có dấu gạch ngang
('user-name', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', 'User With Dash', 'user-name@fcsm.com', 'IT', 'EMPLOYEE', true),

-- User với username có dấu chấm
('user.name', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', 'User With Dot', 'user.name@fcsm.com', 'IT', 'EMPLOYEE', true);

-- ============================================
-- THÔNG TIN TEST ACCOUNTS - SPECIAL CASES
-- ============================================
-- Password cho tất cả users: 'password' (đã được hash với BCrypt)
-- 
-- TEST ACCOUNTS (SPECIAL CASES):
-- 1. admin / password - ADMIN role, IT department (BASIC)
-- 2. employee1 / password - EMPLOYEE role, IT department (BASIC)
-- 3. longname_user / password - EMPLOYEE role, IT department (LONG NAME)
-- 4. special_dept / password - EMPLOYEE role, R&D department (SPECIAL DEPT)
-- 5. supervisor / password - SUPERVISOR role, Operations department (SPECIAL ROLE)
-- 6. special_email / password - EMPLOYEE role, IT department (SPECIAL EMAIL)
-- 7. user123 / password - EMPLOYEE role, IT department (USERNAME WITH NUMBERS)
-- 8. user-name / password - EMPLOYEE role, IT department (USERNAME WITH DASH)
-- 9. user.name / password - EMPLOYEE role, IT department (USERNAME WITH DOT)
--
-- CÁCH SỬ DỤNG:
-- 1. Backup file data.sql hiện tại
-- 2. Copy nội dung file này vào data.sql
-- 3. Restart ứng dụng
-- 4. Test các trường hợp đặc biệt
--
-- TEST SCENARIOS:
-- ✅ Đăng nhập với các username có ký tự đặc biệt
-- ✅ Đăng nhập với user có tên dài
-- ✅ Đăng nhập với user có department đặc biệt
-- ✅ Đăng nhập với user có role đặc biệt
-- ✅ Đăng nhập với user có email phức tạp
-- ✅ Test JWT token generation cho các trường hợp đặc biệt
-- ✅ Test profile endpoint với các user đặc biệt
-- ✅ Test get users by department với department đặc biệt
--
-- EXPECTED RESULTS:
-- - Tất cả trường hợp đặc biệt phải hoạt động bình thường
-- - JWT token được tạo thành công
-- - Profile endpoint trả về đúng thông tin
-- - Không có lỗi validation hoặc encoding
