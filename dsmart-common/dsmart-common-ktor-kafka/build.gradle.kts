plugins {
    kotlin("jvm")
}

group = rootProject.group
version = rootProject.version

repositories {
    mavenCentral()

}

dependencies {
    val ktorVersion: String by project
    val dsmartLoggingVersion: String by project

    implementation(kotlin("stdlib"))
    implementation("io.ktor:ktor-server-core:$ktorVersion")
    implementation("io.ktor:ktor-server-host-common:$ktorVersion")
    implementation("org.apache.kafka:kafka-clients:2.5.0")
    implementation("ru.datana.smart:datana-smart-logging-core:$dsmartLoggingVersion")
    implementation("org.slf4j:slf4j-api:1.7.30")


    testImplementation("io.ktor:ktor-server-netty:$ktorVersion")
    testImplementation("io.ktor:ktor-server-tests:$ktorVersion")
}
