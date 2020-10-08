val ktorVersion: String by project
val kotlinVersion: String by project
val logbackVersion: String by project
val serializationVersion: String by project
val kafkaVersion: String by project
val projectMaintainer: String by project
val dockerBaseImage: String by project
val dsmartLoggingVersion: String by project

plugins {
    application
    kotlin("jvm")
    id("com.bmuschko.docker-java-application")
    kotlin("plugin.serialization")
}

group = rootProject.group
version = rootProject.version

val frontDist = "$buildDir/frontDist"

application {
    mainClassName = "ru.datana.smart.ui.temperature.app.ApplicationKt"
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
        baseImage.set(dockerBaseImage)
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

    implementation(project(":dsmart-module-temperature:dsmart-module-temperature-common"))
    implementation(project(":dsmart-module-temperature:dsmart-module-temperature-ws-models"))
    implementation(project(":dsmart-module-temperature:dsmart-module-temperature-ml-models"))
    implementation(project(":dsmart-common:dsmart-common-ktor-kafka"))

    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlinVersion")
    implementation("io.ktor:ktor-server-netty:$ktorVersion")
    implementation("ch.qos.logback:logback-classic:$logbackVersion")
    implementation("io.ktor:ktor-server-core:$ktorVersion")
    implementation("io.ktor:ktor-server-host-common:$ktorVersion")
    implementation("io.ktor:ktor-websockets:$ktorVersion")
    implementation("org.apache.kafka:kafka-clients:$kafkaVersion")
    implementation("ru.datana.smart:datana-smart-logging-core:$dsmartLoggingVersion")


    testImplementation("io.ktor:ktor-server-tests:$ktorVersion")
}

kotlin.sourceSets["main"].kotlin.srcDirs("src")
kotlin.sourceSets["test"].kotlin.srcDirs("test")

sourceSets["main"].resources.srcDirs(frontDist)
sourceSets["main"].resources.srcDirs("resources")
sourceSets["test"].resources.srcDirs("testresources")

tasks {
    val copyFrontApp by creating(Copy::class.java) {
        dependsOn(
            project(":dsmart-module-temperature:dsmart-module-temperature-widget")
                .getTasksByName("createArtifactStatic", false)
        )
        val frontFiles = project(":dsmart-module-temperature:dsmart-module-temperature-widget")
            .configurations
            .getByName("staticFront")
            .artifacts
            .files
        from(frontFiles)
        into("$frontDist/static")
    }
    processResources.get().dependsOn(copyFrontApp)

    val copyWidgetLib by creating(Copy::class.java) {
        dependsOn(
            project(":dsmart-module-temperature:dsmart-module-temperature-widget")
                .getTasksByName("createArtifactWidget", false)
        )
        val widgetFiles = project(":dsmart-module-temperature:dsmart-module-temperature-widget")
            .configurations
            .getByName("widgetLib")
            .artifacts
            .files
        from(widgetFiles)
        into("$frontDist/static/widget")
    }
    processResources.get().dependsOn(copyWidgetLib)

    create("deploy") {
        group = "build"
        dependsOn(dockerPushImage)
    }

    compileKotlin {
        kotlinOptions {
            targetCompatibility = "11"
        }
    }

    dockerCreateDockerfile {
        environmentVariable(
            mapOf(
                //example "KAFKA_BOOTSTRAP_SERVERS" to "172.29.40.58:9092,172.29.40.59:9092,172.29.40.60:9092",
                "KAFKA_BOOTSTRAP_SERVERS" to "",
                "KAFKA_TOPIC_RAW" to "",
                "KAFKA_TOPIC_ANALYSIS" to "",
                "KAFKA_CLIENT_ID" to "",
                "KAFKA_GROUP_ID" to "",
                "SENSOR_ID" to "",
                "LOGS_KAFKA_HOSTS" to "",
                "LOGS_KAFKA_TOPIC" to "",
                "LOGS_DIR" to "",
                "SERVICE_NAME" to "dsmart-module-temperature",
                "LOG_MAX_HISTORY_DAYS" to "3",
                "LOG_MAX_FILE_SIZE" to "10MB",
                "LOG_TOTAL_SIZE_CAP" to "24MB",
                "LOG_DATANA_LEVEL" to "info",
                "LOG_COMMON_LEVEL" to "error"
            )
        )
    }
}
