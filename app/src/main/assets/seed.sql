-- ==========================================
-- Sample Data for SyncTime
-- seed.sql
-- ==========================================

-- Insert Branches
INSERT INTO branches (name, address, wifi_bssid, reward_rate, created_at, updated_at) VALUES
('Main Office', '123 Tech Avenue, City', '00:11:22:33:44:55', 1.0, 1704067200000, 1704067200000),
('West Branch', '456 Garden Road, City', 'AA:BB:CC:DD:EE:FF', 1.1, 1704067200000, 1704067200000);

-- Insert Position Salaries
INSERT INTO position_salaries (position, position_name, hourly_rate, updated_at) VALUES
('SERVER', 'Phục vụ', 20000.0, 1704067200000),
('BARISTA', 'Pha chế', 25000.0, 1704067200000),
('CASHIER', 'Thu ngân', 22000.0, 1704067200000);

-- Insert Users
-- role: ADMIN, MANAGER, STAFF
-- position: SERVER, BARISTA, CASHIER, etc.
INSERT INTO users (full_name, email, password_hash, role, position, branch_id, is_active, created_at, updated_at) VALUES
('Admin User', 'admin@synctime.com', 'hashed_pass', 'ADMIN', 'CASHIER', 1, 1, 1704067200000, 1704067200000),
('Manager One', 'manager1@synctime.com', 'hashed_pass', 'MANAGER', 'CASHIER', 1, 1, 1704067200000, 1704067200000),
('Staff John', 'john@synctime.com', 'hashed_pass', 'STAFF', 'SERVER', 1, 1, 1704067200000, 1704067200000),
('Staff Jane', 'jane@synctime.com', 'hashed_pass', 'STAFF', 'BARISTA', 2, 1, 1704067200000, 1704067200000);

-- Insert Shifts
INSERT INTO shifts (name, start_time, end_time) VALUES
('Morning Shift', '08:00', '12:00'),
('Afternoon Shift', '13:00', '17:00'),
('Night Shift', '18:00', '22:00');

-- Insert Schedules
-- status: SCHEDULED, PENDING, COMPLETED, ABSENT, CANCELLED
INSERT INTO schedules (user_id, shift_id, branch_id, work_date, position, start_time, end_time, status, created_by, created_at) VALUES
(3, 1, 1, '2024-05-20', 'SERVER', '08:00', '12:00', 'COMPLETED', 2, 1716188400000),
(3, 2, 1, '2024-05-21', 'SERVER', '13:00', '17:00', 'SCHEDULED', 2, 1716188400000),
(4, 1, 2, '2024-05-20', 'BARISTA', '08:00', '12:00', 'COMPLETED', 2, 1716188400000);

-- Insert Attendances
-- check_in_status/check_out_status: VALID, LATE, INVALID_WIFI, etc.
INSERT INTO attendances (user_id, schedule_id, branch_id, check_in_time, check_out_time, check_in_bssid, check_out_bssid, check_in_status, check_out_status, created_at, updated_at) VALUES
(3, 1, 1, 1716188400000, 1716202800000, '00:11:22:33:44:55', '00:11:22:33:44:55', 'VALID', 'VALID', 1716202800000, 1716202800000),
(4, 3, 2, 1716188400000, 1716202800000, 'AA:BB:CC:DD:EE:FF', 'AA:BB:CC:DD:EE:FF', 'VALID', 'VALID', 1716202800000, 1716202800000);

-- Insert Requests
-- type: LEAVE, CHANGE_SHIFT, UPDATE_BSSID
-- status: PENDING, APPROVED, REJECTED
INSERT INTO requests (user_id, type, reason, target_date, status, manager_id, created_at, updated_at) VALUES
(3, 'LEAVE', 'Sick leave', '2024-05-25', 'PENDING', 2, 1716188400000, 1716188400000),
(4, 'CHANGE_SHIFT', 'Personal business', '2024-05-22', 'APPROVED', 2, 1716188400000, 1716190000000);
