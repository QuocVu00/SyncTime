-- ==========================================
-- Attendance Management Database
-- init.sql
-- ==========================================

PRAGMA foreign_keys = ON;

-- =========================
-- BẢNG BRANCHES (Chi nhánh)
-- =========================
CREATE TABLE branches (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL,           -- Tên chi nhánh
    address TEXT NOT NULL,        -- Địa chỉ
    wifi_bssid TEXT NOT NULL UNIQUE, -- Địa chỉ MAC của Wifi chi nhánh (để kiểm tra vị trí)
    reward_rate REAL,             -- Hệ số thưởng (nếu có)
    created_at INTEGER NOT NULL   -- Ngày tạo
);

-- =========================
-- BẢNG USERS (Người dùng)
-- =========================
CREATE TABLE users (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    full_name TEXT NOT NULL,
    email TEXT NOT NULL UNIQUE,
    password_hash TEXT NOT NULL,
    role TEXT NOT NULL CHECK(role IN ('ADMIN', 'MANAGER', 'STAFF')), -- Chỉ cho phép 3 vai trò
    branch_id INTEGER NOT NULL,
    base_salary REAL NOT NULL CHECK(base_salary >= 0),
    device_id TEXT UNIQUE,        -- Mỗi người gắn với 1 mã thiết bị duy nhất
    status TEXT NOT NULL CHECK(status IN ('ACTIVE', 'INACTIVE')),
    created_at INTEGER NOT NULL,

    FOREIGN KEY(branch_id)
        REFERENCES branches(id)
        ON DELETE CASCADE         -- Xóa chi nhánh sẽ xóa sạch user thuộc chi nhánh đó
);

-- =========================
-- SHIFTS
-- =========================
CREATE TABLE shifts (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL,
    start_time TEXT NOT NULL, -- Format: HH:mm
    end_time TEXT NOT NULL    -- Format: HH:mm
);

-- =========================
-- SCHEDULES
-- =========================
CREATE TABLE schedules (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id INTEGER NOT NULL,
    shift_id INTEGER NOT NULL,
    work_date TEXT NOT NULL, -- Format: YYYY-MM-DD
    status TEXT NOT NULL CHECK(status IN ('PENDING', 'COMPLETED', 'CANCELLED')),
    created_by INTEGER NOT NULL,

    FOREIGN KEY(user_id)
        REFERENCES users(id)
        ON DELETE CASCADE,

    FOREIGN KEY(shift_id)
        REFERENCES shifts(id)
        ON DELETE CASCADE,

    FOREIGN KEY(created_by)
        REFERENCES users(id)
        ON DELETE CASCADE,

    UNIQUE(user_id, work_date)
);

-- =========================
-- ATTENDANCES
-- =========================
CREATE TABLE attendances (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id INTEGER NOT NULL,
    schedule_id INTEGER NOT NULL UNIQUE,
    check_in_time INTEGER,  -- Timestamp
    check_out_time INTEGER, -- Timestamp
    check_in_bssid TEXT,
    check_out_bssid TEXT,

    status TEXT NOT NULL
        CHECK(status IN (
            'VALID',
            'LATE',
            'INVALID_DEVICE',
            'INVALID_WIFI',
            'MISSING_CHECKOUT'
        )),

    created_at INTEGER NOT NULL,

    FOREIGN KEY(user_id)
        REFERENCES users(id)
        ON DELETE CASCADE,

    FOREIGN KEY(schedule_id)
        REFERENCES schedules(id)
        ON DELETE CASCADE
);

-- =========================
-- REQUESTS
-- =========================
CREATE TABLE requests (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id INTEGER NOT NULL,

    type TEXT NOT NULL
        CHECK(type IN (
            'LEAVE',
            'CHANGE_SHIFT',
            'UPDATE_BSSID'
        )),

    reason TEXT NOT NULL,

    target_date TEXT NOT NULL,

    status TEXT NOT NULL
        CHECK(status IN (
            'PENDING',
            'APPROVED',
            'REJECTED'
        )),

    manager_id INTEGER,

    created_at INTEGER NOT NULL,

    updated_at INTEGER NOT NULL,

    FOREIGN KEY(user_id)
        REFERENCES users(id)
        ON DELETE CASCADE,

    FOREIGN KEY(manager_id)
        REFERENCES users(id)
        ON DELETE SET NULL
);

-- =========================
-- INDEXES
-- =========================

CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_branch ON users(branch_id);
CREATE INDEX idx_users_device ON users(device_id);

CREATE INDEX idx_schedule_user ON schedules(user_id);
CREATE INDEX idx_schedule_date ON schedules(work_date);
CREATE INDEX idx_schedule_shift ON schedules(shift_id);

CREATE INDEX idx_attendance_user ON attendances(user_id);
CREATE INDEX idx_attendance_schedule ON attendances(schedule_id);

CREATE INDEX idx_request_user ON requests(user_id);
CREATE INDEX idx_request_status ON requests(status);
CREATE INDEX idx_request_date ON requests(target_date);
