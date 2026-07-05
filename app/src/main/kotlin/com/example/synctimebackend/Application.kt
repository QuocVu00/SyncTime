package com.example.synctimebackend

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.gson.gson
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.callloging.CallLogging
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import io.ktor.server.routing.routing
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.Statement
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

fun main() {
    embeddedServer(
        factory = Netty,
        port = 8080,
        host = "0.0.0.0",
        module = Application::module
    ).start(wait = true)
}

fun Application.module() {
    install(CallLogging)

    install(ContentNegotiation) {
        gson {
            setPrettyPrinting()
        }
    }

    Db.init()
    Db.createTables()
    Db.seedData()

    routing {
        get("/health") {
            call.respond(mapOf("status" to "OK", "service" to "SyncTime Backend"))
        }

        // ================= AUTH =================

        post("/login") {
            val body = call.receive<LoginRequest>()
            call.respond(AuthService.login(body))
        }

        post("/api/auth/login") {
            val body = call.receive<LoginRequest>()
            call.respond(AuthService.login(body))
        }

        // ================= STAFF API CŨ CỦA KHOA =================

        post("/create-request") {
            val body = call.receive<OldLeaveRequest>()
            val result = RequestService.createFromOldStaff(body)
            call.respond(result)
        }

        post("/my-requests") {
            val body = call.receive<Map<String, String>>()
            val androidId = body["androidId"]
            call.respond(RequestService.getMyRequests(androidId))
        }

        post("/schedule") {
            call.receive<Map<String, String>>()
            call.respond(ScheduleResponse(success = true, schedules = ScheduleService.getMockSchedules()))
        }

        post("/checkin") {
            val body = call.receive<CheckInRequest>()
            call.respond(AttendanceService.checkIn(body))
        }

        post("/checkout") {
            val body = call.receive<CheckOutRequest>()
            call.respond(AttendanceService.checkOut(body))
        }

        post("/status") {
            call.receive<Map<String, String>>()
            call.respond(
                AttendanceStatusResponse(
                    success = true,
                    status = "Not started",
                    checkInTime = null,
                    checkOutTime = null
                )
            )
        }

        // ================= STAFF API MỚI =================

        post("/api/staff/requests") {
            val body = call.receive<StaffCreateRequest>()
            val result = RequestService.createFromNewStaff(body)
            call.respond(result)
        }

        post("/api/staff/requests/my") {
            val body = call.receive<Map<String, String>>()
            val androidId = body["androidId"]
            call.respond(RequestService.getMyRequests(androidId))
        }

        post("/api/staff/check-in") {
            val body = call.receive<CheckInRequest>()
            call.respond(AttendanceService.checkIn(body))
        }

        post("/api/staff/check-out") {
            val body = call.receive<CheckOutRequest>()
            call.respond(AttendanceService.checkOut(body))
        }

        post("/api/staff/status") {
            call.receive<Map<String, String>>()
            call.respond(
                AttendanceStatusResponse(
                    success = true,
                    status = "Not started",
                    checkInTime = null,
                    checkOutTime = null
                )
            )
        }

        post("/api/staff/schedules") {
            call.receive<Map<String, String>>()
            call.respond(ScheduleResponse(success = true, schedules = ScheduleService.getMockSchedules()))
        }

        // ================= MANAGER =================

        get("/api/manager/staff") {
            call.respond(ManagerService.getStaff())
        }

        post("/api/manager/staff") {
            val body = call.receive<CreateStaffRequest>()
            call.respond(ManagerService.createStaff(body))
        }

        get("/api/manager/requests") {
            call.respond(RequestService.getManagerRequests())
        }

        post("/api/manager/requests/{id}/approve") {
            val id = call.parameters["id"]?.toIntOrNull()
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, ApiMessage("ID không hợp lệ"))
                return@post
            }

            call.respond(RequestService.updateRequestStatus(id, "APPROVED"))
        }

        post("/api/manager/requests/{id}/reject") {
            val id = call.parameters["id"]?.toIntOrNull()
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, ApiMessage("ID không hợp lệ"))
                return@post
            }

            call.respond(RequestService.updateRequestStatus(id, "REJECTED"))
        }

        get("/api/manager/attendance") {
            call.respond(AttendanceService.getManagerAttendance())
        }

        post("/api/manager/schedules") {
            val body = call.receive<CreateScheduleRequest>()
            call.respond(ScheduleService.createOneSchedule(body))
        }

        post("/api/manager/schedules/multiple") {
            val body = call.receive<CreateMultiScheduleRequest>()
            call.respond(ScheduleService.createMultiSchedule(body))
        }

        // ================= ADMIN =================

        get("/api/admin/branches") {
            call.respond(AdminService.getBranches())
        }

        post("/api/admin/branches") {
            val body = call.receive<BranchRequest>()
            call.respond(AdminService.createBranch(body))
        }

        put("/api/admin/branches/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, ApiMessage("ID chi nhánh không hợp lệ"))
                return@put
            }

            val body = call.receive<BranchRequest>()
            call.respond(AdminService.updateBranch(id, body))
        }

        get("/api/admin/position-salaries") {
            call.respond(AdminService.getPositionSalaries())
        }

        put("/api/admin/position-salaries/{position}") {
            val position = call.parameters["position"]
            if (position.isNullOrBlank()) {
                call.respond(HttpStatusCode.BadRequest, ApiMessage("Chức vụ không hợp lệ"))
                return@put
            }

            val body = call.receive<UpdatePositionSalaryRequest>()
            call.respond(AdminService.updatePositionSalary(position, body))
        }

        get("/api/admin/salary-report") {
            call.respond(AdminService.getSalaryReport())
        }

        get("/api/admin/attendance-report") {
            call.respond(AttendanceService.getManagerAttendance())
        }
    }
}

// ================= DATABASE =================

object Db {
    private lateinit var dataSource: HikariDataSource

    fun init() {
        val config = HikariConfig().apply {
            jdbcUrl = System.getenv("DB_URL") ?: "jdbc:postgresql://localhost:5432/synctime"
            username = System.getenv("DB_USER") ?: "synctime"
            password = System.getenv("DB_PASSWORD") ?: "synctime123"
            driverClassName = "org.postgresql.Driver"
            maximumPoolSize = 10
        }

        dataSource = HikariDataSource(config)
    }

    fun connection(): Connection = dataSource.connection

    fun createTables() {
        connection().use { conn ->
            conn.createStatement().use { st ->
                st.executeUpdate(
                    """
                    CREATE TABLE IF NOT EXISTS branches (
                        id SERIAL PRIMARY KEY,
                        name VARCHAR(100) NOT NULL,
                        address VARCHAR(255) NOT NULL,
                        wifi_bssid VARCHAR(100) NOT NULL,
                        reward_rate DOUBLE PRECISION DEFAULT 1.0,
                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                        updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                    );
                    """.trimIndent()
                )

                st.executeUpdate(
                    """
                    CREATE TABLE IF NOT EXISTS users (
                        id SERIAL PRIMARY KEY,
                        full_name VARCHAR(100) NOT NULL,
                        email VARCHAR(100) NOT NULL UNIQUE,
                        password VARCHAR(100) NOT NULL,
                        role VARCHAR(30) NOT NULL DEFAULT 'STAFF',
                        position VARCHAR(30) DEFAULT 'SERVER',
                        branch_id INT REFERENCES branches(id),
                        android_id VARCHAR(100),
                        is_active BOOLEAN DEFAULT TRUE,
                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                    );
                    """.trimIndent()
                )

                st.executeUpdate(
                    """
                    CREATE TABLE IF NOT EXISTS position_salaries (
                        id SERIAL PRIMARY KEY,
                        position VARCHAR(30) NOT NULL UNIQUE,
                        position_name VARCHAR(50) NOT NULL,
                        hourly_rate DOUBLE PRECISION NOT NULL,
                        updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                    );
                    """.trimIndent()
                )

                st.executeUpdate(
                    """
                    CREATE TABLE IF NOT EXISTS shifts (
                        id SERIAL PRIMARY KEY,
                        name VARCHAR(100) NOT NULL,
                        start_time VARCHAR(10) NOT NULL,
                        end_time VARCHAR(10) NOT NULL,
                        is_active BOOLEAN DEFAULT TRUE
                    );
                    """.trimIndent()
                )

                st.executeUpdate(
                    """
                    CREATE TABLE IF NOT EXISTS schedules (
                        id SERIAL PRIMARY KEY,
                        user_id INT NOT NULL REFERENCES users(id),
                        shift_id INT NOT NULL REFERENCES shifts(id),
                        branch_id INT NOT NULL REFERENCES branches(id),
                        work_date DATE NOT NULL,
                        position VARCHAR(30) NOT NULL,
                        start_time VARCHAR(10) NOT NULL,
                        end_time VARCHAR(10) NOT NULL,
                        status VARCHAR(30) DEFAULT 'SCHEDULED',
                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                    );
                    """.trimIndent()
                )

                st.executeUpdate(
                    """
                    CREATE TABLE IF NOT EXISTS requests (
                        id SERIAL PRIMARY KEY,
                        user_id INT NOT NULL REFERENCES users(id),
                        type VARCHAR(30) NOT NULL,
                        reason TEXT NOT NULL,
                        target_date DATE NOT NULL,
                        status VARCHAR(30) DEFAULT 'PENDING',
                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                        updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                    );
                    """.trimIndent()
                )

                st.executeUpdate(
                    """
                    CREATE TABLE IF NOT EXISTS attendance_logs (
                        id SERIAL PRIMARY KEY,
                        user_id INT NOT NULL REFERENCES users(id),
                        schedule_id INT,
                        branch_id INT REFERENCES branches(id),
                        check_in_time TIMESTAMP,
                        check_out_time TIMESTAMP,
                        check_in_bssid VARCHAR(100),
                        check_out_bssid VARCHAR(100),
                        status VARCHAR(50) DEFAULT 'VALID',
                        late_minutes INT DEFAULT 0,
                        overtime_minutes INT DEFAULT 0,
                        total_hours DOUBLE PRECISION DEFAULT 0,
                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                        updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                    );
                    """.trimIndent()
                )
            }
        }
    }

    fun seedData() {
        connection().use { conn ->
            conn.prepareStatement(
                """
                INSERT INTO branches (id, name, address, wifi_bssid, reward_rate)
                VALUES (1, 'Chi nhánh Quận 12', 'Quận 12, TP.HCM', 'A1:B2:C3:D4:E5:F6', 1.0)
                ON CONFLICT (id) DO NOTHING;
                """.trimIndent()
            ).use { it.executeUpdate() }

            conn.prepareStatement(
                """
                INSERT INTO users (full_name, email, password, role, position, branch_id, android_id)
                VALUES
                ('Admin SyncTime', 'admin@synctime.com', '123456', 'ADMIN', 'SUPERVISOR', 1, NULL),
                ('Manager SyncTime', 'manager@synctime.com', '123456', 'MANAGER', 'SUPERVISOR', 1, NULL),
                ('Staff Demo', 'staff@synctime.com', '123456', 'STAFF', 'SERVER', 1, 'demo_android_id'),
                ('Pha Chế Demo', 'barista@synctime.com', '123456', 'STAFF', 'BARISTA', 1, NULL),
                ('Tiếp Thực Demo', 'runner@synctime.com', '123456', 'STAFF', 'RUNNER', 1, NULL)
                ON CONFLICT (email) DO NOTHING;
                """.trimIndent()
            ).use { it.executeUpdate() }

            conn.prepareStatement(
                """
                INSERT INTO position_salaries (position, position_name, hourly_rate)
                VALUES
                ('SERVER', 'Phục vụ', 25000),
                ('BARISTA', 'Pha chế', 30000),
                ('CASHIER', 'Thu ngân', 28000),
                ('SUPERVISOR', 'Giám sát', 35000),
                ('KITCHEN', 'Bếp', 32000),
                ('RUNNER', 'Tiếp thực', 24000)
                ON CONFLICT (position) DO NOTHING;
                """.trimIndent()
            ).use { it.executeUpdate() }

            conn.prepareStatement(
                """
                INSERT INTO shifts (id, name, start_time, end_time)
                VALUES
                (1, 'Ca sáng', '08:00', '12:00'),
                (2, 'Ca chiều', '13:00', '17:00'),
                (3, 'Ca tối', '18:00', '22:00'),
                (4, 'Ca nguyên ngày', '08:00', '17:00')
                ON CONFLICT (id) DO NOTHING;
                """.trimIndent()
            ).use { it.executeUpdate() }
        }
    }

    fun update(sql: String, vararg params: Any?): Int {
        connection().use { conn ->
            conn.prepareStatement(sql).use { ps ->
                bindParams(ps, params)
                return ps.executeUpdate()
            }
        }
    }

    fun insert(sql: String, vararg params: Any?): Int {
        connection().use { conn ->
            conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS).use { ps ->
                bindParams(ps, params)
                ps.executeUpdate()
                ps.generatedKeys.use { rs ->
                    return if (rs.next()) rs.getInt(1) else 0
                }
            }
        }
    }

    fun <T> query(sql: String, vararg params: Any?, mapper: (ResultSet) -> T): List<T> {
        connection().use { conn ->
            conn.prepareStatement(sql).use { ps ->
                bindParams(ps, params)
                ps.executeQuery().use { rs ->
                    val list = mutableListOf<T>()
                    while (rs.next()) {
                        list.add(mapper(rs))
                    }
                    return list
                }
            }
        }
    }

    private fun bindParams(ps: PreparedStatement, params: Array<out Any?>) {
        params.forEachIndexed { index, value ->
            ps.setObject(index + 1, value)
        }
    }
}

// ================= SERVICES =================

object AuthService {
    fun login(body: LoginRequest): LoginResponse {
        val username = body.username.trim()
        val password = body.password.trim()

        val email = when (username.lowercase(Locale.getDefault())) {
            "staff" -> "staff@synctime.com"
            "manager" -> "manager@synctime.com"
            "admin" -> "admin@synctime.com"
            else -> username
        }

        val users = Db.query(
            """
            SELECT id, full_name, email, role, position, branch_id
            FROM users
            WHERE email = ? AND password = ? AND is_active = true
            LIMIT 1
            """.trimIndent(),
            email,
            password
        ) { rs ->
            LoginUser(
                id = rs.getInt("id"),
                fullName = rs.getString("full_name"),
                email = rs.getString("email"),
                role = rs.getString("role"),
                position = rs.getString("position"),
                branchId = rs.getInt("branch_id")
            )
        }

        if (users.isEmpty()) {
            return LoginResponse(
                success = false,
                message = "Sai tài khoản hoặc mật khẩu",
                token = null,
                user = null
            )
        }

        val user = users.first()

        return LoginResponse(
            success = true,
            message = "Đăng nhập thành công",
            token = "demo-token-${user.id}-${user.role}",
            user = user
        )
    }
}

object RequestService {
    fun createFromOldStaff(body: OldLeaveRequest): OldLeaveResponse {
        val type = normalizeRequestType(body.type)
        val date = normalizeDate(body.date)
        val userId = findStaffUserId(body.androidId)

        Db.insert(
            """
            INSERT INTO requests (user_id, type, reason, target_date, status)
            VALUES (?, ?, ?, ?::date, 'PENDING')
            """.trimIndent(),
            userId,
            type,
            body.reason,
            date
        )

        return OldLeaveResponse(success = true, message = "Gửi đơn thành công")
    }

    fun createFromNewStaff(body: StaffCreateRequest): StaffCreateRequestResponse {
        val type = normalizeRequestType(body.type)
        val date = normalizeDate(body.targetDate)
        val userId = getDefaultStaffUserId()

        Db.insert(
            """
            INSERT INTO requests (user_id, type, reason, target_date, status)
            VALUES (?, ?, ?, ?::date, 'PENDING')
            """.trimIndent(),
            userId,
            type,
            body.reason,
            date
        )

        return StaffCreateRequestResponse(success = true, message = "Gửi đơn thành công")
    }

    fun getMyRequests(androidId: String?): StaffRequestsResponse {
        val userId = findStaffUserId(androidId)

        val requests = Db.query(
            """
            SELECT id, type, reason, target_date, status, EXTRACT(EPOCH FROM created_at) * 1000 AS timestamp
            FROM requests
            WHERE user_id = ?
            ORDER BY id DESC
            """.trimIndent(),
            userId
        ) { rs ->
            StaffRequestItem(
                id = rs.getInt("id").toString(),
                type = rs.getString("type"),
                date = rs.getDate("target_date").toString(),
                reason = rs.getString("reason"),
                status = rs.getString("status"),
                timestamp = rs.getLong("timestamp")
            )
        }

        return StaffRequestsResponse(success = true, requests = requests)
    }

    fun getManagerRequests(): List<RequestDto> {
        return Db.query(
            """
            SELECT r.id, r.user_id, u.full_name, r.type, r.reason, r.target_date, r.status
            FROM requests r
            JOIN users u ON u.id = r.user_id
            ORDER BY r.id DESC
            """.trimIndent()
        ) { rs ->
            RequestDto(
                id = rs.getInt("id"),
                userId = rs.getInt("user_id"),
                fullName = rs.getString("full_name"),
                type = rs.getString("type"),
                reason = rs.getString("reason"),
                targetDate = rs.getDate("target_date").toString(),
                status = rs.getString("status")
            )
        }
    }

    fun updateRequestStatus(id: Int, status: String): ApiMessage {
        val count = Db.update(
            """
            UPDATE requests
            SET status = ?, updated_at = CURRENT_TIMESTAMP
            WHERE id = ?
            """.trimIndent(),
            status,
            id
        )

        return if (count > 0) {
            ApiMessage("Cập nhật đơn thành công")
        } else {
            ApiMessage("Không tìm thấy đơn")
        }
    }

    private fun findStaffUserId(androidId: String?): Int {
        if (!androidId.isNullOrBlank()) {
            val users = Db.query(
                "SELECT id FROM users WHERE android_id = ? LIMIT 1",
                androidId
            ) { rs -> rs.getInt("id") }

            if (users.isNotEmpty()) return users.first()
        }

        return getDefaultStaffUserId()
    }

    private fun getDefaultStaffUserId(): Int {
        val users = Db.query(
            "SELECT id FROM users WHERE role = 'STAFF' ORDER BY id LIMIT 1"
        ) { rs -> rs.getInt("id") }

        return users.firstOrNull() ?: 1
    }

    private fun normalizeRequestType(type: String): String {
        return when (type.trim().uppercase(Locale.getDefault())) {
            "LEAVE", "XIN NGHỈ", "XIN NGHI" -> "LEAVE"
            "SHIFT CHANGE", "CHANGE_SHIFT", "ĐỔI CA", "DOI CA" -> "CHANGE_SHIFT"
            else -> "CHANGE_SHIFT"
        }
    }

    private fun normalizeDate(input: String): String {
        val value = input.trim()

        return try {
            if (value.contains("/")) {
                val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
                LocalDate.parse(value, formatter).toString()
            } else {
                LocalDate.parse(value).toString()
            }
        } catch (e: Exception) {
            LocalDate.now().toString()
        }
    }
}

object ManagerService {
    fun getStaff(): List<StaffDto> {
        return Db.query(
            """
            SELECT u.id, u.full_name, u.email, u.role, u.branch_id, u.position,
                   b.name AS branch_name,
                   ps.position_name
            FROM users u
            LEFT JOIN branches b ON b.id = u.branch_id
            LEFT JOIN position_salaries ps ON ps.position = u.position
            WHERE u.role = 'STAFF'
            ORDER BY u.id
            """.trimIndent()
        ) { rs ->
            StaffDto(
                id = rs.getInt("id"),
                fullName = rs.getString("full_name"),
                email = rs.getString("email"),
                role = rs.getString("role"),
                branchId = rs.getInt("branch_id"),
                position = rs.getString("position"),
                positionName = rs.getString("position_name") ?: "Phục vụ",
                branchName = rs.getString("branch_name") ?: "Chi nhánh chính"
            )
        }
    }

    fun createStaff(body: CreateStaffRequest): ApiMessage {
        val position = body.position.ifBlank { "SERVER" }
        val branchId = body.branchId ?: 1

        Db.insert(
            """
            INSERT INTO users (full_name, email, password, role, position, branch_id)
            VALUES (?, ?, ?, ?, ?, ?)
            """.trimIndent(),
            body.fullName,
            body.email,
            body.password,
            body.role.ifBlank { "STAFF" },
            position,
            branchId
        )

        return ApiMessage("Tạo nhân viên thành công")
    }
}

object AdminService {
    fun getBranches(): List<BranchDto> {
        return Db.query(
            """
            SELECT id, name, address, wifi_bssid, reward_rate
            FROM branches
            ORDER BY id
            """.trimIndent()
        ) { rs ->
            BranchDto(
                id = rs.getInt("id"),
                name = rs.getString("name"),
                address = rs.getString("address"),
                wifiBssid = rs.getString("wifi_bssid"),
                rewardRate = rs.getDouble("reward_rate")
            )
        }
    }

    fun createBranch(body: BranchRequest): ApiMessage {
        Db.insert(
            """
            INSERT INTO branches (name, address, wifi_bssid, reward_rate)
            VALUES (?, ?, ?, ?)
            """.trimIndent(),
            body.name,
            body.address,
            body.wifiBssid,
            body.rewardRate ?: 1.0
        )

        return ApiMessage("Tạo chi nhánh thành công")
    }

    fun updateBranch(id: Int, body: BranchRequest): ApiMessage {
        Db.update(
            """
            UPDATE branches
            SET name = ?, address = ?, wifi_bssid = ?, reward_rate = ?, updated_at = CURRENT_TIMESTAMP
            WHERE id = ?
            """.trimIndent(),
            body.name,
            body.address,
            body.wifiBssid,
            body.rewardRate ?: 1.0,
            id
        )

        return ApiMessage("Cập nhật chi nhánh thành công")
    }

    fun getPositionSalaries(): List<PositionSalaryDto> {
        return Db.query(
            """
            SELECT position, position_name, hourly_rate
            FROM position_salaries
            ORDER BY id
            """.trimIndent()
        ) { rs ->
            PositionSalaryDto(
                position = rs.getString("position"),
                positionName = rs.getString("position_name"),
                hourlyRate = rs.getDouble("hourly_rate")
            )
        }
    }

    fun updatePositionSalary(position: String, body: UpdatePositionSalaryRequest): ApiMessage {
        Db.update(
            """
            UPDATE position_salaries
            SET hourly_rate = ?, updated_at = CURRENT_TIMESTAMP
            WHERE position = ?
            """.trimIndent(),
            body.hourlyRate,
            position
        )

        return ApiMessage("Cập nhật lương chức vụ thành công")
    }

    fun getSalaryReport(): List<SalaryDto> {
        return Db.query(
            """
            SELECT u.id AS user_id, u.full_name, u.position,
                   COALESCE(ps.position_name, u.position) AS position_name,
                   COALESCE(ps.hourly_rate, 0) AS hourly_rate,
                   COALESCE(SUM(a.total_hours), 0) AS total_hours,
                   COALESCE(SUM(a.late_minutes), 0) AS late_minutes,
                   COALESCE(SUM(a.overtime_minutes), 0) AS overtime_minutes
            FROM users u
            LEFT JOIN attendance_logs a ON a.user_id = u.id
            LEFT JOIN position_salaries ps ON ps.position = u.position
            WHERE u.role = 'STAFF'
            GROUP BY u.id, u.full_name, u.position, ps.position_name, ps.hourly_rate
            ORDER BY u.id
            """.trimIndent()
        ) { rs ->
            val totalHours = rs.getDouble("total_hours")
            val hourlyRate = rs.getDouble("hourly_rate")

            SalaryDto(
                userId = rs.getInt("user_id"),
                fullName = rs.getString("full_name"),
                totalHours = totalHours,
                salary = totalHours * hourlyRate,
                position = rs.getString("position"),
                positionName = rs.getString("position_name"),
                hourlyRate = hourlyRate,
                lateMinutes = rs.getInt("late_minutes"),
                overtimeMinutes = rs.getInt("overtime_minutes")
            )
        }
    }
}

object ScheduleService {
    fun getMockSchedules(): List<ScheduleItem> {
        return listOf(
            ScheduleItem(
                date = LocalDate.now().toString(),
                shift = "Ca sáng",
                startTime = "08:00",
                endTime = "12:00",
                note = "Làm đúng giờ"
            ),
            ScheduleItem(
                date = LocalDate.now().plusDays(1).toString(),
                shift = "Ca chiều",
                startTime = "13:00",
                endTime = "17:00",
                note = "Có thể đổi ca nếu cần"
            )
        )
    }

    fun createOneSchedule(body: CreateScheduleRequest): ApiMessage {
        val shift = getShift(body.shiftId)
        val user = getUserPositionAndBranch(body.userId)

        Db.insert(
            """
            INSERT INTO schedules (user_id, shift_id, branch_id, work_date, position, start_time, end_time)
            VALUES (?, ?, ?, ?::date, ?, ?, ?)
            """.trimIndent(),
            body.userId,
            body.shiftId,
            user.branchId,
            body.workDate,
            user.position,
            shift.startTime,
            shift.endTime
        )

        return ApiMessage("Tạo lịch làm thành công")
    }

    fun createMultiSchedule(body: CreateMultiScheduleRequest): ApiMessage {
        if (body.userIds.size < 2) {
            return ApiMessage("Một ca làm cần ít nhất 2 nhân viên")
        }

        val staff = body.userIds.map { getUserPositionAndBranch(it) }
        val hasServer = staff.any { it.position == "SERVER" }
        val hasBarista = staff.any { it.position == "BARISTA" }

        if (!hasServer || !hasBarista) {
            return ApiMessage("Ca làm cần ít nhất 1 Phục vụ và 1 Pha chế")
        }

        val shift = getShift(body.shiftId)

        body.userIds.forEach { userId ->
            val user = getUserPositionAndBranch(userId)

            Db.insert(
                """
                INSERT INTO schedules (user_id, shift_id, branch_id, work_date, position, start_time, end_time)
                VALUES (?, ?, ?, ?::date, ?, ?, ?)
                """.trimIndent(),
                userId,
                body.shiftId,
                user.branchId,
                body.workDate,
                user.position,
                shift.startTime,
                shift.endTime
            )
        }

        return ApiMessage("Tạo lịch làm thành công")
    }

    private fun getShift(id: Int): ShiftData {
        val shifts = Db.query(
            "SELECT id, name, start_time, end_time FROM shifts WHERE id = ? LIMIT 1",
            id
        ) { rs ->
            ShiftData(
                id = rs.getInt("id"),
                name = rs.getString("name"),
                startTime = rs.getString("start_time"),
                endTime = rs.getString("end_time")
            )
        }

        return shifts.firstOrNull()
            ?: ShiftData(id = 1, name = "Ca sáng", startTime = "08:00", endTime = "12:00")
    }

    private fun getUserPositionAndBranch(userId: Int): UserMini {
        val users = Db.query(
            "SELECT id, position, COALESCE(branch_id, 1) AS branch_id FROM users WHERE id = ? LIMIT 1",
            userId
        ) { rs ->
            UserMini(
                id = rs.getInt("id"),
                position = rs.getString("position"),
                branchId = rs.getInt("branch_id")
            )
        }

        return users.firstOrNull() ?: UserMini(userId, "SERVER", 1)
    }
}

object AttendanceService {
    fun checkIn(body: CheckInRequest): CheckInResponse {
        val userId = 3

        Db.insert(
            """
            INSERT INTO attendance_logs (user_id, branch_id, check_in_time, check_in_bssid, status)
            VALUES (?, 1, CURRENT_TIMESTAMP, ?, 'VALID')
            """.trimIndent(),
            userId,
            body.bssid
        )

        return CheckInResponse(success = true, message = "Vào ca thành công")
    }

    fun checkOut(body: CheckOutRequest): CheckOutResponse {
        val userId = 3

        Db.update(
            """
            UPDATE attendance_logs
            SET check_out_time = CURRENT_TIMESTAMP,
                check_out_bssid = ?,
                total_hours = 4,
                updated_at = CURRENT_TIMESTAMP
            WHERE id = (
                SELECT id FROM attendance_logs
                WHERE user_id = ?
                ORDER BY id DESC
                LIMIT 1
            )
            """.trimIndent(),
            body.bssid,
            userId
        )

        return CheckOutResponse(success = true, message = "Ra ca thành công")
    }

    fun getManagerAttendance(): List<AttendanceDto> {
        return Db.query(
            """
            SELECT a.id, a.user_id, u.full_name, a.check_in_time, a.check_out_time,
                   a.status, a.check_in_bssid, a.check_out_bssid,
                   u.position, COALESCE(ps.position_name, u.position) AS position_name,
                   a.late_minutes, a.overtime_minutes, a.total_hours
            FROM attendance_logs a
            JOIN users u ON u.id = a.user_id
            LEFT JOIN position_salaries ps ON ps.position = u.position
            ORDER BY a.id DESC
            """.trimIndent()
        ) { rs ->
            AttendanceDto(
                id = rs.getInt("id"),
                userId = rs.getInt("user_id"),
                fullName = rs.getString("full_name"),
                checkInTime = rs.getTimestamp("check_in_time")?.toString(),
                checkOutTime = rs.getTimestamp("check_out_time")?.toString(),
                status = rs.getString("status"),
                checkInBssid = rs.getString("check_in_bssid"),
                checkOutBssid = rs.getString("check_out_bssid"),
                position = rs.getString("position"),
                positionName = rs.getString("position_name"),
                lateMinutes = rs.getInt("late_minutes"),
                overtimeMinutes = rs.getInt("overtime_minutes"),
                totalHours = rs.getDouble("total_hours")
            )
        }
    }
}

// ================= DATA CLASSES =================

data class ApiMessage(
    val message: String? = null
)

data class LoginRequest(
    val username: String,
    val password: String
)

data class LoginResponse(
    val success: Boolean,
    val message: String,
    val token: String?,
    val user: LoginUser? = null
)

data class LoginUser(
    val id: Int,
    val fullName: String,
    val email: String,
    val role: String,
    val position: String?,
    val branchId: Int?
)

data class OldLeaveRequest(
    val androidId: String? = null,
    val type: String,
    val date: String,
    val reason: String,
    val timestamp: Long? = null
)

data class OldLeaveResponse(
    val success: Boolean,
    val message: String
)

data class StaffCreateRequest(
    val type: String,
    val reason: String,
    val targetDate: String
)

data class StaffCreateRequestResponse(
    val success: Boolean,
    val message: String
)

data class StaffRequestItem(
    val id: String,
    val type: String,
    val date: String,
    val reason: String,
    val status: String,
    val timestamp: Long
)

data class StaffRequestsResponse(
    val success: Boolean,
    val requests: List<StaffRequestItem>
)

data class CheckInRequest(
    val androidId: String? = null,
    val bssid: String,
    val timestamp: Long? = null
)

data class CheckInResponse(
    val success: Boolean,
    val message: String
)

data class CheckOutRequest(
    val androidId: String? = null,
    val bssid: String,
    val timestamp: Long? = null
)

data class CheckOutResponse(
    val success: Boolean,
    val message: String
)

data class AttendanceStatusResponse(
    val success: Boolean,
    val status: String,
    val checkInTime: String?,
    val checkOutTime: String?
)

data class ScheduleItem(
    val date: String,
    val shift: String,
    val startTime: String,
    val endTime: String,
    val note: String?
)

data class ScheduleResponse(
    val success: Boolean,
    val schedules: List<ScheduleItem>
)

data class StaffDto(
    val id: Int,
    val fullName: String,
    val email: String,
    val role: String,
    val branchId: Int?,
    val position: String = "SERVER",
    val positionName: String = "Phục vụ",
    val branchName: String = "Chi nhánh chính"
)

data class RequestDto(
    val id: Int,
    val userId: Int,
    val fullName: String?,
    val type: String,
    val reason: String,
    val targetDate: String,
    val status: String
)

data class AttendanceDto(
    val id: Int,
    val userId: Int,
    val fullName: String?,
    val checkInTime: String?,
    val checkOutTime: String?,
    val status: String,
    val checkInBssid: String?,
    val checkOutBssid: String?,
    val position: String = "SERVER",
    val positionName: String = "Phục vụ",
    val lateMinutes: Int = 0,
    val overtimeMinutes: Int = 0,
    val totalHours: Double = 0.0
)

data class BranchDto(
    val id: Int,
    val name: String,
    val address: String,
    val wifiBssid: String,
    val rewardRate: Double?
)

data class BranchRequest(
    val name: String,
    val address: String,
    val wifiBssid: String,
    val rewardRate: Double?
)

data class SalaryDto(
    val userId: Int,
    val fullName: String,
    val totalHours: Double,
    val salary: Double,
    val position: String = "SERVER",
    val positionName: String = "Phục vụ",
    val hourlyRate: Double = 0.0,
    val lateMinutes: Int = 0,
    val overtimeMinutes: Int = 0
)

data class CreateStaffRequest(
    val fullName: String,
    val email: String,
    val password: String,
    val role: String = "STAFF",
    val position: String,
    val branchId: Int?
)

data class CreateScheduleRequest(
    val userId: Int,
    val shiftId: Int,
    val workDate: String
)

data class CreateMultiScheduleRequest(
    val userIds: List<Int>,
    val shiftId: Int,
    val workDate: String
)

data class PositionSalaryDto(
    val position: String,
    val positionName: String,
    val hourlyRate: Double
)

data class UpdatePositionSalaryRequest(
    val hourlyRate: Double
)

data class ShiftData(
    val id: Int,
    val name: String,
    val startTime: String,
    val endTime: String
)

data class UserMini(
    val id: Int,
    val position: String,
    val branchId: Int
)