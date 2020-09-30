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

    implementation(kotlin("stdlib"))

    implementation("io.ktor:ktor-server-core:$ktorVersion")
    implementation("io.ktor:ktor-server-host-common:$ktorVersion")

    implementation("ch.qos.logback:logback-classic:1.2.3")

    //Kafka
    implementation("org.apache.kafka:kafka-clients:2.5.0")

    //kostya
    api("io.ktor:ktor-gson:$ktorVersion")

    testImplementation("io.ktor:ktor-server-tests:$ktorVersion")
    //end kostya
}
