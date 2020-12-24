val ktorVersion: String by project
val kotlinVersion: String by project
val projectMaintainer: String by project
val kafkaVersion: String by project
val dsmartLoggingVersion: String by project
val konveyorVersion: String by project
val serializationVersion: String by project
val guavaVersion: String by project
val converterTransportVersion: String by project

plugins {
    application
    kotlin("jvm")
    id("com.bmuschko.docker-java-application")
    kotlin("plugin.serialization")
}

group = rootProject.group
version = rootProject.version

application {
    mainClassName = "ru.datana.smart.ui.converter.angle.app.ApplicationKt"
}

docker {
//  url = 'https://192.168.59.103:2376'
//  certPath = new File(System.properties['user.home'], '.boot2docker/certs/boot2docker-vm')

    registryCredentials {
        url.set(dockerParams.dockerUrl)
        dockerParams.dockerUser?.also { username.set(it) }
        dockerParams.dockerPass?.also { password.set(it) }
//    email = 'benjamin.muschko@gmail.com'
    }

    javaApplication {
        baseImage.set("adoptopenjdk/openjdk11:alpine-jre")
        maintainer.set(projectMaintainer)
        ports.set(listOf(8080))
        images.set(listOf(
            "${dockerParams.imageName}:${project.version}",
            "${dockerParams.imageName}:latest"
        ))
        jvmArgs.set(listOf("-Xms256m", "-Xmx512m"))
    }
}

repositories {
    mavenLocal()
    jcenter()
    maven { url = uri("https://kotlin.bintray.com/ktor") }
    maven { url = uri("https://nexus.datana.ru/repository/datana-release") }
}

dependencies {

    implementation(project(":dsmart-common:dsmart-common-ktor-kafka"))

    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlinVersion")
    implementation("io.ktor:ktor-server-netty:$ktorVersion")
    implementation("io.ktor:ktor-server-core:$ktorVersion")
    implementation("io.ktor:ktor-server-host-common:$ktorVersion")
    implementation("org.apache.kafka:kafka-clients:$kafkaVersion")

    implementation("ru.datana.smart:datana-smart-logging-core:$dsmartLoggingVersion")
    implementation("ru.datana.smart.converter.transport:dsmart-module-converter-models-angle:$converterTransportVersion")
//    implementation("ru.datana.smart.converter.transport:dsmart-module-converter-models-meta:$converterTransportVersion")
    implementation("ru.datana.smart.converter.transport:dsmart-module-converter-models-mlui:$converterTransportVersion")

    implementation("codes.spectrum:konveyor:$konveyorVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:$serializationVersion")
    implementation("com.google.guava:guava:$guavaVersion")
    implementation(kotlin("test-junit"))

    testImplementation("io.ktor:ktor-server-tests:$ktorVersion")
}

kotlin.sourceSets["main"].kotlin.srcDirs("src")
kotlin.sourceSets["test"].kotlin.srcDirs("test")

sourceSets["main"].resources.srcDirs("resources")
sourceSets["test"].resources.srcDirs("testresources")

tasks {

    dockerCreateDockerfile {
        runCommand("mkdir -p logs")
        environmentVariable(
            mapOf(
                // example "KAFKA_BOOTSTRAP_SERVERS" to "172.29.40.58:9092,172.29.40.59:9092,172.29.40.60:9092",
                "KAFKA_BOOTSTRAP_SERVERS" to "",
                "KAFKA_TOPIC_ANGLE" to "",
                "KAFKA_TOPIC_META" to "",
                "KAFKA_CLIENT_ID" to "",
                "KAFKA_GROUP_ID" to "",
                "LOGS_KAFKA_HOSTS" to "",
                "LOGS_KAFKA_TOPIC" to "",
                "SCHEDULE_BASE_PATH" to "",
                "LOGS_DIR" to "",
                "SERVICE_NAME" to "dsmart-module-converter-angle",
                "LOG_MAX_HISTORY_DAYS" to "3",
                "LOG_MAX_FILE_SIZE" to "10MB",
                "LOG_TOTAL_SIZE_CAP" to "24MB",
                "LOG_DATANA_LEVEL" to "info",
                "LOG_COMMON_LEVEL" to "error"
            )
        )
    }

    create("deploy") {
        group = "build"
        dependsOn(dockerPushImage)
    }
}
