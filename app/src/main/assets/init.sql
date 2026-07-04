-- ==========================================
-- SyncTime Database Initialization
-- init.sql
-- ==========================================

PRAGMA foreign_keys = ON;

-- BẢNG BRANCHES
CREATE TABLE IF NOT EXISTS branches (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL,
    address TEXT NOT NULL,
    wifi_bssid TEXT NOT NULL UNIQUE,
    reward_rate REAL DEFAULT 1.0,
    created_at INTEGER NOT NULL,
    updated_at INTEGER NOT NULL
);

-- BẢNG USERS
CREATE TABLE IF NOT EXISTS users (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    full_name TEXT NOT NULL,
    email TEXT NOT NULL UNIQUE,
    password_hash TEXT NOT NULL,
    role TEXT NOT NULL, -- ADMIN, MANAGER, STAFF
    position TEXT NOT NULL, -- SERVER, BARISTA, etc.
    branch_id INTEGER,
    is_active INTEGER DEFAULT 1, -- 1: true, 0: false
    created_by INTEGER,
    created_at INTEGER NOT NULL,
    updated_at INTEGER NOT NULL,
    FOREIGN KEY(branch_id) REFERENCES branches(id) ON DELETE SET NULL,
    FOREIGN KEY(created_by) REFERENCES users(id) ON DELETE SET NULL
);

-- BẢNG SHIFTS
CREATE TABLE IF NOT EXISTS shifts (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL,
    start_time TEXT NOT NULL,
    end_time TEXT NOT NULL,
    is_active INTEGER DEFAULT 1,
    created_at INTEGER NOT NULL
);

-- BẢNG SCHEDULES
CREATE TABLE IF NOT EXISTS schedules (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id INTEGER NOT NULL,
    shift_id INTEGER NOT NULL,
    branch_id INTEGER NOT NULL,
    work_date TEXT NOT NULL, -- YYYY-MM-DD
    position TEXT NOT NULL,
    start_time TEXT NOT NULL,
    end_time TEXT NOT NULL,
    status TEXT NOT NULL, -- SCHEDULED, PENDING, COMPLETED, ABSENT, CANCELLED
    created_by INTEGER,
    created_at INTEGER NOT NULL,
    FOREIGN KEY(user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY(shift_id) REFERENCES shifts(id) ON DELETE CASCADE,
    FOREIGN KEY(branch_id) REFERENCES branches(id) ON DELETE CASCADE,
    FOREIGN KEY(created_by) REFERENCES users(id) ON DELETE SET NULL
);

-- BẢNG ATTENDANCES
CREATE TABLE IF NOT EXISTS attendances (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id INTEGER NOT NULL,
    schedule_id INTEGER NOT NULL UNIQUE,
    branch_id INTEGER NOT NULL,
    check_in_time INTEGER,
    check_out_time INTEGER,
    check_in_bssid TEXT,
    check_out_bssid TEXT,
    check_in_status TEXT NOT NULL, -- VALID, LATE, etc.
    check_out_status TEXT NOT NULL,
    late_minutes INTEGER DEFAULT 0,
    overtime_minutes INTEGER DEFAULT 0,
    total_hours REAL DEFAULT 0.0,
    is_kitchen INTEGER DEFAULT 0,
    created_at INTEGER NOT NULL,
    updated_at INTEGER NOT NULL,
    FOREIGN KEY(user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY(schedule_id) REFERENCES schedules(id) ON DELETE CASCADE,
    FOREIGN KEY(branch_id) REFERENCES branches(id) ON DELETE CASCADE
);

-- BẢNG REQUESTS
CREATE TABLE IF NOT EXISTS requests (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id INTEGER NOT NULL,
    type TEXT NOT NULL, -- LEAVE, CHANGE_SHIFT, UPDATE_BSSID
    reason TEXT NOT NULL,
    target_date TEXT NOT NULL,
    status TEXT NOT NULL, -- PENDING, APPROVED, REJECTED
    manager_id INTEGER,
    response_note TEXT,
    created_at INTEGER NOT NULL,
    updated_at INTEGER NOT NULL,
    FOREIGN KEY(user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY(manager_id) REFERENCES users(id) ON DELETE SET NULL
);

-- BẢNG POSITION_SALARIES
CREATE TABLE IF NOT EXISTS position_salaries (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    position TEXT NOT NULL UNIQUE,
    position_name TEXT NOT NULL,
    hourly_rate REAL NOT NULL,
    updated_by INTEGER,
    updated_at INTEGER NOT NULL,
    FOREIGN KEY(updated_by) REFERENCES users(id) ON DELETE SET NULL
);

-- BẢNG DEVICES
CREATE TABLE IF NOT EXISTS devices (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id INTEGER NOT NULL,
    android_id TEXT NOT NULL,
    device_name TEXT,
    is_active INTEGER DEFAULT 1,
    registered_at INTEGER NOT NULL,
    FOREIGN KEY(user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- BẢNG NOTIFICATIONS
CREATE TABLE IF NOT EXISTS notifications (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id INTEGER NOT NULL,
    title TEXT NOT NULL,
    content TEXT NOT NULL,
    type TEXT NOT NULL DEFAULT 'SYSTEM',
    is_read INTEGER DEFAULT 0,
    created_at INTEGER NOT NULL,
    FOREIGN KEY(user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- INDEXES
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_schedules_user_date ON schedules(user_id, work_date);
CREATE INDEX idx_attendances_schedule ON attendances(schedule_id);
CREATE INDEX idx_requests_user ON requests(user_id);
