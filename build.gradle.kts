plugins {
    kotlin("jvm")
}

group = "ru.datana.smart.common.ktor.kafka"
version = "0.6.0"

repositories {
    mavenCentral()
}

dependencies {
    val ktorVersion: String by project

    implementation(kotlin("stdlib"))
    implementation("io.ktor:ktor-server-core:$ktorVersion")
    implementation("io.ktor:ktor-server-host-common:$ktorVersion")
    implementation("org.apache.kafka:kafka-clients:2.5.0")

    testImplementation("io.ktor:ktor-server-netty:$ktorVersion")
    testImplementation("io.ktor:ktor-server-tests:$ktorVersion")
}
