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
    implementation("io.ktor:ktor-server-core-jvm:2.3.12")
    implementation("io.ktor:ktor-server-netty-jvm:2.3.12")
    implementation("io.ktor:ktor-server-content-negotiation-jvm:2.3.12")
    implementation("io.ktor:ktor-serialization-gson-jvm:2.3.12")
    implementation("io.ktor:ktor-server-cors-jvm:2.3.12")

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")

    implementation("org.postgresql:postgresql:42.7.3")
    implementation("com.zaxxer:HikariCP:5.1.0")
    implementation("ch.qos.logback:logback-classic:1.5.6")

    testImplementation("org.jetbrains.kotlin:kotlin-test")
}