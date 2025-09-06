-- Insert sample users for testing
INSERT INTO users (username, password, full_name, email, department, role, is_active) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', 'Administrator', 'admin@fcsm.com', 'IT', 'ADMIN', true),
('manager1', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', 'Manager One', 'manager1@fcsm.com', 'HR', 'MANAGER', true),
('employee1', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', 'Employee One', 'employee1@fcsm.com', 'IT', 'EMPLOYEE', true),
('employee2', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', 'Employee Two', 'employee2@fcsm.com', 'HR', 'EMPLOYEE', true),
('employee3', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', 'Employee Three', 'employee3@fcsm.com', 'Finance', 'EMPLOYEE', true);

-- Note: Password for all users is 'password' (hashed with BCrypt)
