-- ============================================
-- DỮ LIỆU CƠ BẢN CHO AUTH SERVICE
-- ============================================
-- File này chứa dữ liệu cơ bản để start ứng dụng
-- Chỉ bao gồm các user hợp lệ, không có trường hợp lỗi
-- Xóa dữ liệu cũ nếu có
DELETE FROM mb_users;

-- Insert basic users (chỉ các user hợp lệ)
INSERT INTO mb_users (username, password, full_name, email, department, role, is_active) VALUES

-- ============================================
-- BASIC USERS - HAPPY PATH ONLY
-- ============================================
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', 'Administrator', 'admin@fcsm.com', 'IT', 'ADMIN', true),
('manager1', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', 'Manager One', 'manager1@fcsm.com', 'HR', 'MANAGER', true),
('employee1', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', 'Employee One', 'employee1@fcsm.com', 'IT', 'EMPLOYEE', true),
('employee2', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', 'Employee Two', 'employee2@fcsm.com', 'HR', 'EMPLOYEE', true),
('employee3', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', 'Employee Three', 'employee3@fcsm.com', 'Finance', 'EMPLOYEE', true);

-- ============================================
-- THÔNG TIN BASIC ACCOUNTS
-- ============================================
-- Password cho tất cả users: 'password' (đã được hash với BCrypt)
-- 
-- BASIC ACCOUNTS (HAPPY PATH):
-- 1. admin / password - ADMIN role, IT department
-- 2. manager1 / password - MANAGER role, HR department  
-- 3. employee1 / password - EMPLOYEE role, IT department
-- 4. employee2 / password - EMPLOYEE role, HR department
-- 5. employee3 / password - EMPLOYEE role, Finance department
--
-- Để test các trường hợp khác, sử dụng các file data riêng biệt:
-- - data_happy_path.sql: Test case 1 (đăng nhập hợp lệ)
-- - data_unhappy_path.sql: Test case 2 (đăng nhập không hợp lệ)  
-- - data_special_cases.sql: Các trường hợp đặc biệt
