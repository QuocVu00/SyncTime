plugins {
    kotlin("jvm")
    application
}

group = "com.example.synctimebackend"
version = "1.0.0"

application {
    mainClass.set("com.example.synctimebackend.ApplicationKt")
    applicationDefaultJvmArgs = listOf("-Duser.timezone=Asia/Ho_Chi_Minh")

}

dependencies {
    // Ktor server
    implementation("io.ktor:ktor-server-core-jvm:2.3.12")
    implementation("io.ktor:ktor-server-netty-jvm:2.3.12")
    implementation("io.ktor:ktor-server-content-negotiation-jvm:2.3.12")
    implementation("io.ktor:ktor-serialization-gson-jvm:2.3.12")
    implementation("io.ktor:ktor-server-call-logging-jvm:2.3.12")

    // PostgreSQL + connection pool
    implementation("com.zaxxer:HikariCP:5.1.0")
    implementation("org.postgresql:postgresql:42.7.3")

    // Log
    implementation("ch.qos.logback:logback-classic:1.5.6")
}