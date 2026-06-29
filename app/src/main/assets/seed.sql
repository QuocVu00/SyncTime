-- ==========================================
-- Sample Data for SyncTime
-- seed.sql
-- ==========================================

-- Insert Branches
INSERT INTO branches (name, address, wifi_bssid, reward_rate, created_at) VALUES
('Main Office', '123 Tech Avenue, City', '00:11:22:33:44:55', 1.0, 1704067200000),
('West Branch', '456 Garden Road, City', 'AA:BB:CC:DD:EE:FF', 1.1, 1704067200000);

-- Insert Users (Passwords are 'password123' hashed - placeholder)
INSERT INTO users (full_name, email, password_hash, role, branch_id, base_salary, device_id, status, created_at) VALUES
('Admin User', 'admin@synctime.com', '$2a$10$xyz', 'ADMIN', 1, 5000.0, 'DEVICE_001', 'ACTIVE', 1704067200000),
('Manager One', 'manager1@synctime.com', '$2a$10$xyz', 'MANAGER', 1, 3500.0, 'DEVICE_002', 'ACTIVE', 1704067200000),
('Staff John', 'john@synctime.com', '$2a$10$xyz', 'STAFF', 1, 2000.0, 'DEVICE_003', 'ACTIVE', 1704067200000),
('Staff Jane', 'jane@synctime.com', '$2a$10$xyz', 'STAFF', 2, 2100.0, 'DEVICE_004', 'ACTIVE', 1704067200000);

-- Insert Shifts
INSERT INTO shifts (name, start_time, end_time) VALUES
('Morning Shift', '08:00', '12:00'),
('Afternoon Shift', '13:00', '17:00'),
('Night Shift', '18:00', '22:00');

-- Insert Schedules
INSERT INTO schedules (user_id, shift_id, work_date, status, created_by) VALUES
(3, 1, '2024-05-20', 'COMPLETED', 2),
(3, 2, '2024-05-21', 'PENDING', 2),
(4, 1, '2024-05-20', 'COMPLETED', 2);

-- Insert Attendances
INSERT INTO attendances (user_id, schedule_id, check_in_time, check_out_time, check_in_bssid, check_out_bssid, status, created_at) VALUES
(3, 1, 1716188400000, 1716202800000, '00:11:22:33:44:55', '00:11:22:33:44:55', 'VALID', 1716202800000),
(4, 3, 1716188400000, 1716202800000, 'AA:BB:CC:DD:EE:FF', 'AA:BB:CC:DD:EE:FF', 'VALID', 1716202800000);

-- Insert Requests
INSERT INTO requests (user_id, type, reason, target_date, status, manager_id, created_at, updated_at) VALUES
(3, 'LEAVE', 'Sick leave', '2024-05-25', 'PENDING', 2, 1716188400000, 1716188400000),
(4, 'CHANGE_SHIFT', 'Personal business', '2024-05-22', 'APPROVED', 2, 1716188400000, 1716190000000);
