-- ============================================
-- DỮ LIỆU TEST CASE 1: ĐĂNG NHẬP VỚI THÔNG TIN HỢP LỆ
-- ============================================
-- File này chứa dữ liệu để test các trường hợp đăng nhập thành công
-- Sử dụng khi muốn test Test Case 1

-- Xóa dữ liệu cũ nếu có
DELETE FROM mb_users;

-- Insert users cho Test Case 1: Đăng nhập hợp lệ
INSERT INTO mb_users (username, password, full_name, email, department, role, is_active) VALUES

-- ============================================
-- TEST CASE 1: ĐĂNG NHẬP VỚI THÔNG TIN HỢP LỆ
-- ============================================
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', 'Administrator', 'admin@fcsm.com', 'IT', 'ADMIN', true),
('manager1', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', 'Manager One', 'manager1@fcsm.com', 'HR', 'MANAGER', true),
('employee1', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', 'Employee One', 'employee1@fcsm.com', 'IT', 'EMPLOYEE', true),
('employee2', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', 'Employee Two', 'employee2@fcsm.com', 'HR', 'EMPLOYEE', true),
('employee3', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', 'Employee Three', 'employee3@fcsm.com', 'Finance', 'EMPLOYEE', true);

-- ============================================
-- THÔNG TIN TEST ACCOUNTS - HAPPY PATH
-- ============================================
-- Password cho tất cả users: 'password' (đã được hash với BCrypt)
-- 
-- TEST ACCOUNTS (HAPPY PATH):
-- 1. admin / password - ADMIN role, IT department
-- 2. manager1 / password - MANAGER role, HR department  
-- 3. employee1 / password - EMPLOYEE role, IT department
-- 4. employee2 / password - EMPLOYEE role, HR department
-- 5. employee3 / password - EMPLOYEE role, Finance department
--
-- CÁCH SỬ DỤNG:
-- 1. Backup file data.sql hiện tại
-- 2. Copy nội dung file này vào data.sql
-- 3. Restart ứng dụng
-- 4. Test các trường hợp đăng nhập hợp lệ
--
-- TEST SCENARIOS:
-- - Đăng nhập với admin → 200 OK + JWT token
-- - Đăng nhập với manager1 → 200 OK + JWT token
-- - Đăng nhập với employee1 → 200 OK + JWT token
-- - Sử dụng token để truy cập /profile
-- - Test refresh token
-- - Test logout
