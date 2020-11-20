import org.jetbrains.kotlin.gradle.dsl.Coroutines
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val ktorVersion: String by project
val kotlinVersion: String by project
val projectMaintainer: String by project
val kafkaVersion: String by project
val dsmartLoggingVersion: String by project
val konveyorVersion: String by project
val jacksonVersion: String by project

plugins {
    application
    kotlin("jvm")
    id("com.bmuschko.docker-java-application")
}

group = rootProject.group
version = rootProject.version

val frontDist = "$buildDir/frontDist"

application {
    mainClassName = "ru.datana.smart.ui.converter.mock.app.ApplicationKt"
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
        images.set(
            listOf(
                "${dockerParams.imageName}:${project.version}",
                "${dockerParams.imageName}:latest"
            )
        )
        jvmArgs.set(listOf("-Xms256m", "-Xmx512m"))
    }
}

repositories {
    mavenLocal()
    jcenter()
    maven { url = uri("https://kotlin.bintray.com/ktor") }
    maven { url = uri("https://nexus.datana.ru/repository/datana-release/") }
}

dependencies {

    implementation(project(":dsmart-module-converter:dsmart-module-converter-common"))
    implementation(project(":dsmart-module-converter:dsmart-module-converter-models-meta"))

    implementation("org.apache.kafka:kafka-clients:$kafkaVersion")
    implementation("ru.datana.smart:datana-smart-logging-core:$dsmartLoggingVersion")
    implementation("codes.spectrum:konveyor:$konveyorVersion")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:$jacksonVersion")
    testImplementation("io.ktor:ktor-server-tests:$ktorVersion")

    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlinVersion")
    implementation("io.ktor:ktor-server-netty:$ktorVersion")
    implementation("io.ktor:ktor-server-core:$ktorVersion")
    implementation("io.ktor:ktor-server-host-common:$ktorVersion")
//    implementation("io.ktor:ktor-client-serialization-jvm:$ktorVersion")
    implementation("io.ktor:ktor-jackson:$ktorVersion")
    testImplementation("io.ktor:ktor-server-tests:$ktorVersion")
    implementation( "io.ktor:ktor-locations:$ktorVersion")
}

kotlin.sourceSets["main"].kotlin.srcDirs("src")
kotlin.sourceSets["test"].kotlin.srcDirs("test")

sourceSets["main"].resources.srcDirs("resources")
sourceSets["main"].resources.srcDirs(frontDist)
sourceSets["test"].resources.srcDirs("testresources")

tasks {

    val copyFrontApp by creating(Copy::class.java) {
        dependsOn(
            project(":dsmart-module-converter:dsmart-module-converter-mock-widget")
                .getTasksByName("createArtifactStatic", false)
        )
        val frontFiles = project(":dsmart-module-converter:dsmart-module-converter-mock-widget")
            .configurations
            .getByName("staticFront")
            .artifacts
            .files
        from(frontFiles)
        into("$frontDist/static")
    }
    processResources.get().dependsOn(copyFrontApp)

    val copyArtifactLibs by creating(Copy::class.java) {
        dependsOn(
            project(":dsmart-module-converter:dsmart-module-converter-mock-widget")
                .getTasksByName("createArtifactLibs", false)
        )
        val widgetFiles = project(":dsmart-module-converter:dsmart-module-converter-mock-widget")
            .configurations
            .getByName("ngLibs")
            .artifacts
            .files
        from(widgetFiles)
        into("$frontDist/static/widget")
    }
    processResources.get().dependsOn(copyArtifactLibs)

    dockerCreateDockerfile {
        environmentVariable(
            mapOf(
                //example "KAFKA_BOOTSTRAP_SERVERS" to "172.29.40.58:9092,172.29.40.59:9092,172.29.40.60:9092",
                "KAFKA_BOOTSTRAP_SERVERS" to "",
                "KAFKA_TOPIC_META" to "",
                "LOGS_KAFKA_HOSTS" to "",
                "LOGS_KAFKA_TOPIC" to "",
                "LOGS_DIR" to "",
                "SERVICE_NAME" to project.name,
                "CATALOG_TESTCASES_PATH" to "./",
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
