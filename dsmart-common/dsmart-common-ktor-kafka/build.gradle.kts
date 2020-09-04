plugins {
    kotlin("jvm")
}

group = rootProject
version = rootProject.version

repositories {
    mavenCentral()
}

dependencies {
    val ktorVersion: String by project

    implementation(kotlin("stdlib"))

    implementation("io.ktor:ktor-server-core:$ktorVersion")
    implementation("io.ktor:ktor-server-host-common:$ktorVersion")

    implementation("ch.qos.logback:logback-classic:1.2.3")

    //Kafka
    implementation("org.apache.kafka:kafka-clients:2.5.0")

    //kostya
    //compile "org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlin_version"
    api("org.apache.kafka:kafka-streams:2.6.0")
    api("io.ktor:ktor-server-netty:$ktorVersion")
    api("io.ktor:ktor-gson:$ktorVersion")
    //end kostya
}
