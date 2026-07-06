package com.example.synctimebackend

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.gson.gson
import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.plugins.cors.routing.CORS
import io.ktor.server.request.receive
import io.ktor.server.request.receiveText
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import io.ktor.server.routing.routing
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import java.sql.Connection
import java.sql.ResultSet
import java.time.LocalDate
import java.util.UUID

fun main() {
    val port = System.getenv("PORT")?.toIntOrNull() ?: 8080
    embeddedServer(
        factory = Netty,
        port = port,
        host = "0.0.0.0",
        module = Application::module
    ).start(wait = true)
}

fun Application.module() {
    Db.init()

    install(ContentNegotiation) {
        gson {
            setPrettyPrinting()
        }
    }

    install(CORS) {
        anyHost()
        allowMethod(HttpMethod.Get)
        allowMethod(HttpMethod.Post)
        allowMethod(HttpMethod.Put)
        allowHeader(HttpHeaders.Authorization)
        allowHeader(HttpHeaders.ContentType)
    }

    routing {
        get("/health") {
            call.respondText(
                text = """{"success":true,"message":"SyncTime backend is running"}""",
                contentType = ContentType.Application.Json
            )
        }

        post("/auth/login") {
            val rawText = call.receiveText()

            val jsonObject = try {
                Json.parseToJsonElement(rawText).jsonObject
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    MessageResponse(
                        success = false,
                        message = "JSON login không hợp lệ. Body nhận được: $rawText. Lỗi: ${e.message}"
                    )
                )
                return@post
            }

            fun getString(vararg keys: String): String {
                for (key in keys) {
                    val value = jsonObject[key]?.jsonPrimitive?.contentOrNull
                    if (!value.isNullOrBlank()) return value
                }
                return ""
            }

            val email = getString("email").trim()
            val password = getString("password").trim()
            val androidId = getString("androidId", "deviceId", "android_id").trim()
            val fcmToken = getString("fcmToken", "fcm_token").ifBlank { null }

            if (email.isBlank()) {
                call.respond(HttpStatusCode.BadRequest, MessageResponse(false, "Thiếu email. Body: $rawText"))
                return@post
            }

            if (password.isBlank()) {
                call.respond(HttpStatusCode.BadRequest, MessageResponse(false, "Thiếu mật khẩu. Body: $rawText"))
                return@post
            }

            if (androidId.isBlank()) {
                call.respond(HttpStatusCode.BadRequest, MessageResponse(false, "Thiếu Android ID hoặc deviceId. Body: $rawText"))
                return@post
            }

            val body = LoginRequest(
                email = email,
                password = password,
                androidId = androidId,
                deviceId = androidId,
                android_id = androidId,
                fcmToken = fcmToken
            )

            val result = try {
                Db.login(body)
            } catch (e: Exception) {
                e.printStackTrace()

                call.respond(
                    HttpStatusCode.InternalServerError,
                    MessageResponse(
                        success = false,
                        message = "Lỗi server khi login: ${e::class.simpleName}: ${e.message}"
                    )
                )
                return@post
            }

            when (result) {
                is LoginResult.Success -> call.respond(result.response)
                is LoginResult.Fail -> call.respond(result.status, MessageResponse(false, result.message))
            }
        }
        get("/me") {
            val user = call.requireUser() ?: return@get
            call.respond(user)
        }

        post("/attendance/check-in") {
            val user = call.requireUser("STAFF", "MANAGER") ?: return@post
            val body = call.receive<AttendanceRequest>()

            if (!call.verifyDeviceAndWifi(user, body.deviceId, body.currentBssid)) {
                return@post
            }

            val result = Db.checkIn(user.id)
            call.respond(result.status, MessageResponse(result.success, result.message))
        }

        post("/attendance/check-out") {
            val user = call.requireUser("STAFF", "MANAGER") ?: return@post
            val body = call.receive<AttendanceRequest>()

            if (!call.verifyDeviceAndWifi(user, body.deviceId, body.currentBssid)) {
                return@post
            }

            val result = Db.checkOut(user.id)
            call.respond(result.status, MessageResponse(result.success, result.message))
        }

        get("/attendance/status") {
            val user = call.requireUser("STAFF", "MANAGER") ?: return@get
            call.respond(Db.getAttendanceStatus(user.id))
        }

        get("/branches") {
            call.requireUser("ADMIN", "MANAGER") ?: return@get
            call.respond(Db.getBranches())
        }

        post("/branches") {
            call.requireUser("ADMIN") ?: return@post
            val body = call.receive<BranchRequest>()
            call.respond(Db.createBranch(body))
        }

        put("/branches/{id}") {
            call.requireUser("ADMIN") ?: return@put
            val id = call.parameters["id"]?.toIntOrNull()
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, MessageResponse(false, "ID chi nhánh không hợp lệ"))
                return@put
            }

            val body = call.receive<BranchRequest>()
            call.respond(Db.updateBranch(id, body))
        }

        get("/shifts") {
            call.requireUser() ?: return@get
            call.respond(Db.getShifts())
        }

        post("/shifts") {
            call.requireUser("ADMIN", "MANAGER") ?: return@post
            val body = call.receive<ShiftRequest>()
            call.respond(Db.createShift(body))
        }

        get("/staff") {
            val user = call.requireUser("ADMIN", "MANAGER") ?: return@get
            call.respond(Db.getStaff(user))
        }

        post("/staff") {
            val user = call.requireUser("ADMIN", "MANAGER") ?: return@post
            val body = call.receive<CreateStaffRequest>()
            call.respond(Db.createStaff(user, body))
        }

        get("/schedules/my") {
            val user = call.requireUser("STAFF", "MANAGER") ?: return@get
            call.respond(Db.getMySchedules(user.id))
        }

        post("/schedules/multi") {
            val user = call.requireUser("ADMIN", "MANAGER") ?: return@post
            val body = call.receive<CreateMultiScheduleRequest>()
            call.respond(Db.createSchedules(user, body))
        }

        get("/requests") {
            val user = call.requireUser() ?: return@get
            call.respond(Db.getRequests(user))
        }

        post("/requests") {
            val user = call.requireUser("STAFF", "MANAGER") ?: return@post
            val body = call.receive<CreateRequestBody>()
            call.respond(Db.createRequest(user, body))
        }

        post("/requests/{id}/approve") {
            val user = call.requireUser("ADMIN", "MANAGER") ?: return@post
            val id = call.parameters["id"]?.toIntOrNull()
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, MessageResponse(false, "ID yêu cầu không hợp lệ"))
                return@post
            }

            call.respond(Db.updateRequestStatus(user, id, "APPROVED"))
        }

        post("/requests/{id}/reject") {
            val user = call.requireUser("ADMIN", "MANAGER") ?: return@post
            val id = call.parameters["id"]?.toIntOrNull()
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, MessageResponse(false, "ID yêu cầu không hợp lệ"))
                return@post
            }

            call.respond(Db.updateRequestStatus(user, id, "REJECTED"))
        }

        get("/salary") {
            val user = call.requireUser("ADMIN", "MANAGER") ?: return@get
            call.respond(Db.getSalaryReport(user))
        }
    }
}

object Db {
    private lateinit var dataSource: HikariDataSource

    fun init() {
        val jdbcUrl = System.getenv("JDBC_URL") ?: "jdbc:postgresql://localhost:5432/synctime"
        val dbUser = System.getenv("DB_USER") ?: "synctime"
        val dbPassword = System.getenv("DB_PASSWORD") ?: "123456"

        val config = HikariConfig().apply {
            this.jdbcUrl = jdbcUrl
            this.username = dbUser
            this.password = dbPassword
            this.driverClassName = "org.postgresql.Driver"

            maximumPoolSize = 10
            minimumIdle = 2
            isAutoCommit = true

            // Sửa lỗi: PostgreSQL không dùng Asia/Saigon
            connectionInitSql = "SET TIME ZONE 'Asia/Ho_Chi_Minh'"
        }

        dataSource = HikariDataSource(config)
        createSchema()
        seedData()
    }

    private fun <T> useConnection(block: (Connection) -> T): T {
        return dataSource.connection.use { connection ->
            block(connection)
        }
    }

    private fun createSchema() {
        useConnection { conn ->
            conn.exec(
                """
            CREATE TABLE IF NOT EXISTS branches (
                id SERIAL PRIMARY KEY,
                name TEXT NOT NULL,
                address TEXT NOT NULL,
                wifi_bssid TEXT NOT NULL,
                reward_rate DOUBLE PRECISION NOT NULL DEFAULT 1.0,
                created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
            )
            """.trimIndent()
            )

            conn.exec(
                """
            CREATE TABLE IF NOT EXISTS users (
                id SERIAL PRIMARY KEY,
                full_name TEXT NOT NULL,
                email TEXT UNIQUE NOT NULL,
                password_hash TEXT NOT NULL,
                role TEXT NOT NULL CHECK (role IN ('ADMIN', 'MANAGER', 'STAFF')),
                branch_id INT REFERENCES branches(id),
                position TEXT,
                base_salary DOUBLE PRECISION NOT NULL DEFAULT 0,
                device_id TEXT,
                fcm_token TEXT,
                active BOOLEAN NOT NULL DEFAULT TRUE,
                created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
            )
            """.trimIndent()
            )

            conn.exec(
                """
            CREATE TABLE IF NOT EXISTS shifts (
                id SERIAL PRIMARY KEY,
                name TEXT NOT NULL,
                start_time TEXT NOT NULL,
                end_time TEXT NOT NULL,
                created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
            )
            """.trimIndent()
            )

            conn.exec(
                """
            CREATE TABLE IF NOT EXISTS schedules (
                id SERIAL PRIMARY KEY,
                user_id INT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                shift_id INT NOT NULL REFERENCES shifts(id) ON DELETE CASCADE,
                work_date DATE NOT NULL,
                created_by INT REFERENCES users(id),
                status TEXT NOT NULL DEFAULT 'APPROVED',
                created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
                UNIQUE(user_id, shift_id, work_date)
            )
            """.trimIndent()
            )

            conn.exec(
                """
            CREATE TABLE IF NOT EXISTS attendances (
                id SERIAL PRIMARY KEY,
                user_id INT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                check_in_time TIMESTAMPTZ NOT NULL DEFAULT NOW(),
                check_out_time TIMESTAMPTZ,
                status TEXT NOT NULL DEFAULT 'VALID',
                created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
                updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
            )
            """.trimIndent()
            )

            conn.exec(
                """
            CREATE TABLE IF NOT EXISTS requests (
                id SERIAL PRIMARY KEY,
                user_id INT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                type TEXT NOT NULL,
                reason TEXT NOT NULL,
                status TEXT NOT NULL DEFAULT 'PENDING',
                manager_id INT REFERENCES users(id),
                created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
                updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
            )
            """.trimIndent()
            )

            conn.exec(
                """
            CREATE TABLE IF NOT EXISTS sessions (
                token TEXT PRIMARY KEY,
                user_id INT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                expires_at TIMESTAMPTZ NOT NULL DEFAULT NOW() + INTERVAL '30 days',
                created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
            )
            """.trimIndent()
            )
        }
    }
    private fun seedData() {
        useConnection { conn ->
            conn.exec(
                """
                INSERT INTO branches(name, address, wifi_bssid, reward_rate)
                VALUES 
                    ('Chi nhánh Quận 12', 'Quận 12, TP.HCM', 'A1:B2:C3:D4:E5:F6', 1.0),
                    ('Chi nhánh Gò Vấp', 'Gò Vấp, TP.HCM', 'AA:BB:CC:DD:EE:FF', 1.0)
                ON CONFLICT DO NOTHING
                """.trimIndent()
            )

            conn.exec(
                """
                INSERT INTO users(full_name, email, password_hash, role, branch_id, position, base_salary)
                VALUES
                    ('Admin SyncTime', 'admin@synctime.vn', '123456', 'ADMIN', NULL, 'ADMIN', 0),
                    ('Manager Quận 12', 'manager@synctime.vn', '123456', 'MANAGER', 1, 'MANAGER', 12000000),
                    ('Nguyễn Văn An', 'staff@synctime.vn', '123456', 'STAFF', 1, 'SERVER', 8000000)
                ON CONFLICT(email) DO NOTHING
                """.trimIndent()
            )

            conn.exec(
                """
                INSERT INTO shifts(name, start_time, end_time)
                VALUES
                    ('Ca sáng', '08:00', '12:00'),
                    ('Ca chiều', '13:00', '17:00'),
                    ('Ca tối', '18:00', '22:00')
                ON CONFLICT DO NOTHING
                """.trimIndent()
            )
        }
    }

    fun login(body: LoginRequest): LoginResult {
        return useConnection { conn ->
            val user = conn.prepareStatement(
                """
            SELECT id, full_name, email, role, branch_id, position, device_id, active
            FROM users
            WHERE email = ? AND password_hash = ?
            """.trimIndent()
            ).use { ps ->
                ps.setString(1, body.email.trim())
                ps.setString(2, body.password.trim())
                ps.executeQuery().use { rs ->
                    if (rs.next()) rs.toAuthUserWithDevice() else null
                }
            } ?: return@useConnection LoginResult.Fail(
                HttpStatusCode.Unauthorized,
                "Sai email hoặc mật khẩu"
            )

            if (!user.active) {
                return@useConnection LoginResult.Fail(
                    HttpStatusCode.Locked,
                    "Tài khoản đã bị khóa"
                )
            }

            /*
                Bản dev: không khóa tài khoản khi đổi thiết bị.
                Vì bạn đang test bằng curl + emulator nên nếu khóa device sẽ rất dễ lỗi 423.
                Khi app ổn định rồi mới bật lại chống gian lận device.
            */
            conn.prepareStatement(
                """
            UPDATE users
            SET device_id = ?, fcm_token = ?, active = TRUE
            WHERE id = ?
            """.trimIndent()
            ).use { ps ->
                ps.setString(1, body.androidId.ifBlank { body.deviceId.ifBlank { body.android_id } })
                ps.setString(2, body.fcmToken)
                ps.setInt(3, user.id)
                ps.executeUpdate()
            }

            val token = UUID.randomUUID().toString()

            conn.prepareStatement(
                """
            INSERT INTO sessions(token, user_id)
            VALUES (?, ?)
            """.trimIndent()
            ).use { ps ->
                ps.setString(1, token)
                ps.setInt(2, user.id)
                ps.executeUpdate()
            }

            val safeUser = AuthUser(
                id = user.id,
                fullName = user.fullName,
                email = user.email,
                role = user.role,
                branchId = user.branchId,
                position = user.position
            )

            LoginResult.Success(
                LoginResponse(
                    success = true,
                    message = "Đăng nhập thành công",
                    token = token,
                    user = safeUser
                )
            )
        }
    }

    fun findUserByToken(token: String): AuthUser? {
        return useConnection { conn ->
            conn.prepareStatement(
                """
                SELECT u.id, u.full_name, u.email, u.role, u.branch_id, u.position
                FROM sessions s
                JOIN users u ON u.id = s.user_id
                WHERE s.token = ?
                  AND s.expires_at > NOW()
                  AND u.active = TRUE
                """.trimIndent()
            ).use { ps ->
                ps.setString(1, token)
                ps.executeQuery().use { rs ->
                    if (rs.next()) rs.toAuthUser() else null
                }
            }
        }
    }

    fun checkDeviceAndWifi(user: AuthUser, deviceId: String, currentBssid: String): CheckResult {
        if (deviceId.isBlank()) {
            return CheckResult(false, "Không đọc được Android ID")
        }

        if (currentBssid.isBlank() || currentBssid == "02:00:00:00:00:00") {
            return CheckResult(false, "Không đọc được BSSID Wi-Fi. Hãy bật Wi-Fi, GPS và cấp quyền vị trí")
        }

        if (user.branchId == null) {
            return CheckResult(false, "Tài khoản chưa được gán chi nhánh")
        }

        return useConnection { conn ->
            val dbDeviceId = conn.prepareStatement("SELECT device_id FROM users WHERE id = ?").use { ps ->
                ps.setInt(1, user.id)
                ps.executeQuery().use { rs ->
                    if (rs.next()) rs.getString("device_id") else null
                }
            }

            if (dbDeviceId != deviceId) {
                conn.prepareStatement("UPDATE users SET active = FALSE WHERE id = ?").use { ps ->
                    ps.setInt(1, user.id)
                    ps.executeUpdate()
                }
                return@useConnection CheckResult(false, "Sai thiết bị. Tài khoản đã bị khóa")
            }

            val branchBssid = conn.prepareStatement("SELECT wifi_bssid FROM branches WHERE id = ?").use { ps ->
                ps.setInt(1, user.branchId)
                ps.executeQuery().use { rs ->
                    if (rs.next()) rs.getString("wifi_bssid") else null
                }
            }

            if (normalizeBssid(branchBssid) != normalizeBssid(currentBssid)) {
                return@useConnection CheckResult(false, "Sai Wi-Fi chi nhánh")
            }

            CheckResult(true, "OK")
        }
    }

    fun checkIn(userId: Int): ActionResult {
        return useConnection { conn ->
            val hasOpen = conn.prepareStatement(
                """
                SELECT id FROM attendances
                WHERE user_id = ?
                  AND check_out_time IS NULL
                ORDER BY id DESC
                LIMIT 1
                """.trimIndent()
            ).use { ps ->
                ps.setInt(1, userId)
                ps.executeQuery().use { it.next() }
            }

            if (hasOpen) {
                return@useConnection ActionResult(false, HttpStatusCode.Conflict, "Bạn đã chấm công vào, chưa chấm công ra")
            }

            conn.prepareStatement(
                """
                INSERT INTO attendances(user_id, check_in_time, status)
                VALUES (?, NOW(), 'VALID')
                """.trimIndent()
            ).use { ps ->
                ps.setInt(1, userId)
                ps.executeUpdate()
            }

            ActionResult(true, HttpStatusCode.OK, "Chấm công vào thành công")
        }
    }

    fun checkOut(userId: Int): ActionResult {
        return useConnection { conn ->
            val openId = conn.prepareStatement(
                """
                SELECT id FROM attendances
                WHERE user_id = ?
                  AND check_out_time IS NULL
                ORDER BY id DESC
                LIMIT 1
                """.trimIndent()
            ).use { ps ->
                ps.setInt(1, userId)
                ps.executeQuery().use { rs ->
                    if (rs.next()) rs.getInt("id") else null
                }
            } ?: return@useConnection ActionResult(false, HttpStatusCode.Conflict, "Bạn chưa chấm công vào")

            conn.prepareStatement(
                """
                UPDATE attendances
                SET check_out_time = NOW(), updated_at = NOW()
                WHERE id = ?
                """.trimIndent()
            ).use { ps ->
                ps.setInt(1, openId)
                ps.executeUpdate()
            }

            ActionResult(true, HttpStatusCode.OK, "Chấm công ra thành công")
        }
    }

    fun getAttendanceStatus(userId: Int): AttendanceStatusResponse {
        return useConnection { conn ->
            conn.prepareStatement(
                """
                SELECT id, check_in_time::TEXT AS check_in_time, check_out_time::TEXT AS check_out_time, status
                FROM attendances
                WHERE user_id = ?
                ORDER BY id DESC
                LIMIT 1
                """.trimIndent()
            ).use { ps ->
                ps.setInt(1, userId)
                ps.executeQuery().use { rs ->
                    if (rs.next()) {
                        AttendanceStatusResponse(
                            checkedIn = rs.getString("check_out_time") == null,
                            checkInTime = rs.getString("check_in_time"),
                            checkOutTime = rs.getString("check_out_time"),
                            status = rs.getString("status")
                        )
                    } else {
                        AttendanceStatusResponse(false, null, null, "NO_ATTENDANCE")
                    }
                }
            }
        }
    }

    fun getBranches(): List<BranchDto> {
        return useConnection { conn ->
            conn.prepareStatement(
                """
                SELECT id, name, address, wifi_bssid, reward_rate
                FROM branches
                ORDER BY id
                """.trimIndent()
            ).use { ps ->
                ps.executeQuery().use { rs ->
                    buildList {
                        while (rs.next()) {
                            add(
                                BranchDto(
                                    id = rs.getInt("id"),
                                    name = rs.getString("name"),
                                    address = rs.getString("address"),
                                    wifiBssid = rs.getString("wifi_bssid"),
                                    rewardRate = rs.getDouble("reward_rate")
                                )
                            )
                        }
                    }
                }
            }
        }
    }

    fun createBranch(body: BranchRequest): BranchDto {
        return useConnection { conn ->
            conn.prepareStatement(
                """
                INSERT INTO branches(name, address, wifi_bssid, reward_rate)
                VALUES (?, ?, ?, ?)
                RETURNING id, name, address, wifi_bssid, reward_rate
                """.trimIndent()
            ).use { ps ->
                ps.setString(1, body.name)
                ps.setString(2, body.address)
                ps.setString(3, body.wifiBssid)
                ps.setDouble(4, body.rewardRate)
                ps.executeQuery().use { rs ->
                    rs.next()
                    BranchDto(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("address"),
                        rs.getString("wifi_bssid"),
                        rs.getDouble("reward_rate")
                    )
                }
            }
        }
    }

    fun updateBranch(id: Int, body: BranchRequest): BranchDto {
        return useConnection { conn ->
            conn.prepareStatement(
                """
                UPDATE branches
                SET name = ?, address = ?, wifi_bssid = ?, reward_rate = ?
                WHERE id = ?
                RETURNING id, name, address, wifi_bssid, reward_rate
                """.trimIndent()
            ).use { ps ->
                ps.setString(1, body.name)
                ps.setString(2, body.address)
                ps.setString(3, body.wifiBssid)
                ps.setDouble(4, body.rewardRate)
                ps.setInt(5, id)
                ps.executeQuery().use { rs ->
                    rs.next()
                    BranchDto(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("address"),
                        rs.getString("wifi_bssid"),
                        rs.getDouble("reward_rate")
                    )
                }
            }
        }
    }

    fun getShifts(): List<ShiftDto> {
        return useConnection { conn ->
            conn.prepareStatement("SELECT id, name, start_time, end_time FROM shifts ORDER BY id").use { ps ->
                ps.executeQuery().use { rs ->
                    buildList {
                        while (rs.next()) {
                            add(
                                ShiftDto(
                                    rs.getInt("id"),
                                    rs.getString("name"),
                                    rs.getString("start_time"),
                                    rs.getString("end_time")
                                )
                            )
                        }
                    }
                }
            }
        }
    }

    fun createShift(body: ShiftRequest): ShiftDto {
        return useConnection { conn ->
            conn.prepareStatement(
                """
                INSERT INTO shifts(name, start_time, end_time)
                VALUES (?, ?, ?)
                RETURNING id, name, start_time, end_time
                """.trimIndent()
            ).use { ps ->
                ps.setString(1, body.name)
                ps.setString(2, body.startTime)
                ps.setString(3, body.endTime)
                ps.executeQuery().use { rs ->
                    rs.next()
                    ShiftDto(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("start_time"),
                        rs.getString("end_time")
                    )
                }
            }
        }
    }

    fun getStaff(currentUser: AuthUser): List<StaffDto> {
        return useConnection { conn ->
            val sql = if (currentUser.role == "ADMIN") {
                """
                SELECT u.id, u.full_name, u.email, u.role, u.branch_id, u.position, b.name AS branch_name
                FROM users u
                LEFT JOIN branches b ON b.id = u.branch_id
                WHERE u.role IN ('STAFF', 'MANAGER')
                ORDER BY u.id
                """.trimIndent()
            } else {
                """
                SELECT u.id, u.full_name, u.email, u.role, u.branch_id, u.position, b.name AS branch_name
                FROM users u
                LEFT JOIN branches b ON b.id = u.branch_id
                WHERE u.branch_id = ?
                ORDER BY u.id
                """.trimIndent()
            }

            conn.prepareStatement(sql).use { ps ->
                if (currentUser.role != "ADMIN") ps.setInt(1, currentUser.branchId ?: -1)

                ps.executeQuery().use { rs ->
                    buildList {
                        while (rs.next()) {
                            add(
                                StaffDto(
                                    id = rs.getInt("id"),
                                    fullName = rs.getString("full_name"),
                                    email = rs.getString("email"),
                                    role = rs.getString("role"),
                                    branchId = rs.getIntOrNull("branch_id"),
                                    position = rs.getString("position") ?: "",
                                    positionName = positionName(rs.getString("position") ?: ""),
                                    branchName = rs.getString("branch_name") ?: ""
                                )
                            )
                        }
                    }
                }
            }
        }
    }

    fun createStaff(currentUser: AuthUser, body: CreateStaffRequest): StaffDto {
        val branchId = if (currentUser.role == "ADMIN") body.branchId else currentUser.branchId

        return useConnection { conn ->
            conn.prepareStatement(
                """
                INSERT INTO users(full_name, email, password_hash, role, branch_id, position, base_salary)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                RETURNING id, full_name, email, role, branch_id, position
                """.trimIndent()
            ).use { ps ->
                ps.setString(1, body.fullName)
                ps.setString(2, body.email)
                ps.setString(3, body.password)
                ps.setString(4, body.role.ifBlank { "STAFF" })
                if (branchId == null) ps.setNull(5, java.sql.Types.INTEGER) else ps.setInt(5, branchId)
                ps.setString(6, body.position)
                ps.setDouble(7, body.baseSalary)
                ps.executeQuery().use { rs ->
                    rs.next()
                    StaffDto(
                        id = rs.getInt("id"),
                        fullName = rs.getString("full_name"),
                        email = rs.getString("email"),
                        role = rs.getString("role"),
                        branchId = rs.getIntOrNull("branch_id"),
                        position = rs.getString("position") ?: "",
                        positionName = positionName(rs.getString("position") ?: ""),
                        branchName = ""
                    )
                }
            }
        }
    }

    fun getMySchedules(userId: Int): List<ScheduleDto> {
        return useConnection { conn ->
            conn.prepareStatement(
                """
                SELECT s.id, s.user_id, u.full_name, s.shift_id, sh.name AS shift_name, 
                       sh.start_time, sh.end_time, s.work_date::TEXT AS work_date, s.status
                FROM schedules s
                JOIN users u ON u.id = s.user_id
                JOIN shifts sh ON sh.id = s.shift_id
                WHERE s.user_id = ?
                ORDER BY s.work_date DESC, s.id DESC
                """.trimIndent()
            ).use { ps ->
                ps.setInt(1, userId)
                ps.executeQuery().use { rs ->
                    buildList {
                        while (rs.next()) add(rs.toScheduleDto())
                    }
                }
            }
        }
    }

    fun createSchedules(currentUser: AuthUser, body: CreateMultiScheduleRequest): MessageResponse {
        if (body.items.isEmpty()) {
            return MessageResponse(false, "Danh sách lịch trống")
        }

        return useConnection { conn ->
            body.items.forEach { item ->
                if (item.workDate.isBlank()) return@forEach

                conn.prepareStatement(
                    """
                    INSERT INTO schedules(user_id, shift_id, work_date, created_by, status)
                    VALUES (?, ?, ?, ?, 'APPROVED')
                    ON CONFLICT(user_id, shift_id, work_date)
                    DO UPDATE SET shift_id = EXCLUDED.shift_id, created_by = EXCLUDED.created_by
                    """.trimIndent()
                ).use { ps ->
                    ps.setInt(1, item.userId)
                    ps.setInt(2, item.shiftId)
                    ps.setObject(3, LocalDate.parse(item.workDate))
                    ps.setInt(4, currentUser.id)
                    ps.executeUpdate()
                }
            }

            MessageResponse(true, "Đã tạo lịch làm")
        }
    }

    fun getRequests(currentUser: AuthUser): List<RequestDto> {
        return useConnection { conn ->
            val sql = when (currentUser.role) {
                "ADMIN" -> """
                    SELECT r.id, r.user_id, u.full_name, r.type, r.reason, r.status, r.created_at::TEXT AS created_at
                    FROM requests r
                    JOIN users u ON u.id = r.user_id
                    ORDER BY r.id DESC
                """.trimIndent()

                "MANAGER" -> """
                    SELECT r.id, r.user_id, u.full_name, r.type, r.reason, r.status, r.created_at::TEXT AS created_at
                    FROM requests r
                    JOIN users u ON u.id = r.user_id
                    WHERE u.branch_id = ?
                    ORDER BY r.id DESC
                """.trimIndent()

                else -> """
                    SELECT r.id, r.user_id, u.full_name, r.type, r.reason, r.status, r.created_at::TEXT AS created_at
                    FROM requests r
                    JOIN users u ON u.id = r.user_id
                    WHERE r.user_id = ?
                    ORDER BY r.id DESC
                """.trimIndent()
            }

            conn.prepareStatement(sql).use { ps ->
                when (currentUser.role) {
                    "MANAGER" -> ps.setInt(1, currentUser.branchId ?: -1)
                    "STAFF" -> ps.setInt(1, currentUser.id)
                }

                ps.executeQuery().use { rs ->
                    buildList {
                        while (rs.next()) {
                            add(
                                RequestDto(
                                    id = rs.getInt("id"),
                                    userId = rs.getInt("user_id"),
                                    fullName = rs.getString("full_name"),
                                    type = rs.getString("type"),
                                    reason = rs.getString("reason"),
                                    status = rs.getString("status"),
                                    createdAt = rs.getString("created_at")
                                )
                            )
                        }
                    }
                }
            }
        }
    }

    fun createRequest(currentUser: AuthUser, body: CreateRequestBody): MessageResponse {
        return useConnection { conn ->
            conn.prepareStatement(
                """
                INSERT INTO requests(user_id, type, reason, status)
                VALUES (?, ?, ?, 'PENDING')
                """.trimIndent()
            ).use { ps ->
                ps.setInt(1, currentUser.id)
                ps.setString(2, body.type)
                ps.setString(3, body.reason)
                ps.executeUpdate()
            }

            MessageResponse(true, "Đã gửi yêu cầu")
        }
    }

    fun updateRequestStatus(currentUser: AuthUser, requestId: Int, status: String): MessageResponse {
        return useConnection { conn ->
            conn.prepareStatement(
                """
                UPDATE requests
                SET status = ?, manager_id = ?, updated_at = NOW()
                WHERE id = ?
                """.trimIndent()
            ).use { ps ->
                ps.setString(1, status)
                ps.setInt(2, currentUser.id)
                ps.setInt(3, requestId)
                val count = ps.executeUpdate()

                if (count > 0) {
                    MessageResponse(true, "Đã cập nhật yêu cầu")
                } else {
                    MessageResponse(false, "Không tìm thấy yêu cầu")
                }
            }
        }
    }

    fun getSalaryReport(currentUser: AuthUser): List<SalaryDto> {
        return useConnection { conn ->
            val sql = if (currentUser.role == "ADMIN") {
                """
                SELECT u.id AS user_id, u.full_name, COUNT(a.id) AS attendance_count, u.base_salary
                FROM users u
                LEFT JOIN attendances a ON a.user_id = u.id
                WHERE u.role = 'STAFF'
                GROUP BY u.id, u.full_name, u.base_salary
                ORDER BY u.id
                """.trimIndent()
            } else {
                """
                SELECT u.id AS user_id, u.full_name, COUNT(a.id) AS attendance_count, u.base_salary
                FROM users u
                LEFT JOIN attendances a ON a.user_id = u.id
                WHERE u.role = 'STAFF' AND u.branch_id = ?
                GROUP BY u.id, u.full_name, u.base_salary
                ORDER BY u.id
                """.trimIndent()
            }

            conn.prepareStatement(sql).use { ps ->
                if (currentUser.role != "ADMIN") ps.setInt(1, currentUser.branchId ?: -1)

                ps.executeQuery().use { rs ->
                    buildList {
                        while (rs.next()) {
                            val baseSalary = rs.getDouble("base_salary")
                            val attendanceCount = rs.getInt("attendance_count")
                            add(
                                SalaryDto(
                                    userId = rs.getInt("user_id"),
                                    fullName = rs.getString("full_name"),
                                    attendanceCount = attendanceCount,
                                    estimatedSalary = baseSalary / 26.0 * attendanceCount
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

private suspend fun ApplicationCall.requireUser(vararg roles: String): AuthUser? {
    val header = request.headers[HttpHeaders.Authorization].orEmpty()
    val token = header.removePrefix("Bearer").trim()

    if (token.isBlank()) {
        respond(HttpStatusCode.Unauthorized, MessageResponse(false, "Thiếu token"))
        return null
    }

    val user = Db.findUserByToken(token)
    if (user == null) {
        respond(HttpStatusCode.Unauthorized, MessageResponse(false, "Token không hợp lệ hoặc đã hết hạn"))
        return null
    }

    if (roles.isNotEmpty() && user.role !in roles) {
        respond(HttpStatusCode.Forbidden, MessageResponse(false, "Không có quyền truy cập"))
        return null
    }

    return user
}

private suspend fun ApplicationCall.verifyDeviceAndWifi(
    user: AuthUser,
    deviceId: String,
    currentBssid: String
): Boolean {
    val result = Db.checkDeviceAndWifi(user, deviceId, currentBssid)
    if (!result.success) {
        respond(HttpStatusCode.Forbidden, MessageResponse(false, result.message))
        return false
    }
    return true
}

private fun Connection.exec(sql: String) {
    createStatement().use { it.execute(sql) }
}

private fun ResultSet.getIntOrNull(column: String): Int? {
    val value = getInt(column)
    return if (wasNull()) null else value
}

private fun ResultSet.toAuthUser(): AuthUser {
    return AuthUser(
        id = getInt("id"),
        fullName = getString("full_name"),
        email = getString("email"),
        role = getString("role"),
        branchId = getIntOrNull("branch_id"),
        position = getString("position")
    )
}

private fun ResultSet.toAuthUserWithDevice(): AuthUserWithDevice {
    return AuthUserWithDevice(
        id = getInt("id"),
        fullName = getString("full_name"),
        email = getString("email"),
        role = getString("role"),
        branchId = getIntOrNull("branch_id"),
        position = getString("position"),
        deviceId = getString("device_id"),
        active = getBoolean("active")
    )
}

private fun ResultSet.toScheduleDto(): ScheduleDto {
    return ScheduleDto(
        id = getInt("id"),
        userId = getInt("user_id"),
        fullName = getString("full_name"),
        shiftId = getInt("shift_id"),
        shiftName = getString("shift_name"),
        startTime = getString("start_time"),
        endTime = getString("end_time"),
        workDate = getString("work_date"),
        status = getString("status")
    )
}

private fun normalizeBssid(value: String?): String {
    return value.orEmpty().trim().uppercase()
}

private fun positionName(position: String): String {
    return when (position.uppercase()) {
        "SERVER" -> "Phục vụ"
        "BARISTA" -> "Pha chế"
        "KITCHEN" -> "Bếp"
        "CASHIER" -> "Thu ngân"
        "MANAGER" -> "Quản lý"
        else -> position
    }
}

sealed class LoginResult {
    data class Success(val response: LoginResponse) : LoginResult()
    data class Fail(val status: HttpStatusCode, val message: String) : LoginResult()
}

data class ActionResult(
    val success: Boolean,
    val status: HttpStatusCode,
    val message: String
)

data class CheckResult(
    val success: Boolean,
    val message: String
)

@Serializable
data class MessageResponse(
    val success: Boolean,
    val message: String
)

@Serializable
data class LoginRequest(
    val email: String = "",
    val password: String = "",
    val androidId: String = "",
    val deviceId: String = "",
    val android_id: String = "",
    val fcmToken: String? = null
)

@Serializable
data class LoginResponse(
    val success: Boolean,
    val message: String,
    val token: String,
    val user: AuthUser
)

@Serializable
data class AuthUser(
    val id: Int,
    val fullName: String,
    val email: String,
    val role: String,
    val branchId: Int? = null,
    val position: String? = null
)

data class AuthUserWithDevice(
    val id: Int,
    val fullName: String,
    val email: String,
    val role: String,
    val branchId: Int?,
    val position: String?,
    val deviceId: String?,
    val active: Boolean
)

@Serializable
data class AttendanceRequest(
    val deviceId: String,
    val currentBssid: String
)

@Serializable
data class AttendanceStatusResponse(
    val checkedIn: Boolean,
    val checkInTime: String?,
    val checkOutTime: String?,
    val status: String
)

@Serializable
data class BranchDto(
    val id: Int,
    val name: String,
    val address: String,
    val wifiBssid: String,
    val rewardRate: Double = 1.0
)

@Serializable
data class BranchRequest(
    val name: String,
    val address: String,
    val wifiBssid: String,
    val rewardRate: Double = 1.0
)

@Serializable
data class ShiftDto(
    val id: Int,
    val name: String,
    val startTime: String,
    val endTime: String
)

@Serializable
data class ShiftRequest(
    val name: String,
    val startTime: String,
    val endTime: String
)

@Serializable
data class StaffDto(
    val id: Int,
    val fullName: String,
    val email: String,
    val role: String,
    val branchId: Int?,
    val position: String,
    val positionName: String,
    val branchName: String
)

@Serializable
data class CreateStaffRequest(
    val fullName: String,
    val email: String,
    val password: String,
    val role: String = "STAFF",
    val branchId: Int? = null,
    val position: String,
    val baseSalary: Double = 0.0
)

@Serializable
data class ScheduleDto(
    val id: Int,
    val userId: Int,
    val fullName: String,
    val shiftId: Int,
    val shiftName: String,
    val startTime: String,
    val endTime: String,
    val workDate: String,
    val status: String
)

@Serializable
data class CreateScheduleRequest(
    val userId: Int,
    val shiftId: Int,
    val workDate: String
)

@Serializable
data class CreateMultiScheduleRequest(
    val items: List<CreateScheduleRequest>
)

@Serializable
data class RequestDto(
    val id: Int,
    val userId: Int,
    val fullName: String,
    val type: String,
    val reason: String,
    val status: String,
    val createdAt: String
)

@Serializable
data class CreateRequestBody(
    val type: String,
    val reason: String
)

@Serializable
data class SalaryDto(
    val userId: Int,
    val fullName: String,
    val attendanceCount: Int,
    val estimatedSalary: Double
)