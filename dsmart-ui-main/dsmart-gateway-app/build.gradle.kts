val ktorVersion: String by project
val kotlinVersion: String by project
val logbackVersion: String by project
val serializationVersion: String by project
val kafkaVersion: String by project
val frontConfig = "staticFront"
val projectMaintainer: String by project

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
    mainClassName = "ru.datana.smart.ui.ApplicationKt"
//    mainClassName = "io.ktor.server.netty.EngineMain"
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

    // TODO Временная зависимость. Должна уйти в dsmart-module-temperature
    implementation(project(":dsmart-module-temperature:dsmart-module-temperature-ws-models"))
//    implementation(project(":dsmart-module-temperature:dsmart-module-temperature-kf-models"))
    implementation(project(":dsmart-module-temperature:dsmart-module-temperature-ml-models"))
    implementation(project(":dsmart-common:dsmart-common-ktor-kafka"))

    api("ru.datana.smart:datana-smart-logging-core:0.0.5")

    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlinVersion")
    implementation("io.ktor:ktor-server-netty:$ktorVersion")
    implementation("ch.qos.logback:logback-classic:$logbackVersion")
    implementation("io.ktor:ktor-server-core:$ktorVersion")
    implementation("io.ktor:ktor-server-host-common:$ktorVersion")
    implementation("io.ktor:ktor-websockets:$ktorVersion")
    implementation("org.apache.kafka:kafka-clients:$kafkaVersion")

    testImplementation("io.ktor:ktor-server-tests:$ktorVersion")
}

kotlin.sourceSets["main"].kotlin.srcDirs("src")
kotlin.sourceSets["test"].kotlin.srcDirs("test")

//sourceSets["main"].resources.srcDirs("resources", frontDist)
sourceSets["main"].resources.srcDirs("resources")
sourceSets["main"].resources.srcDirs(frontDist)
sourceSets["test"].resources.srcDirs("testresources")

tasks {
    val copyFront by creating(Copy::class.java) {
        dependsOn(
            project(":dsmart-ui-main:dsmart-ui-main-front")
                .getTasksByName("createArtifact", false)
        )
        val frontFiles = project(":dsmart-ui-main:dsmart-ui-main-front")
            .configurations
            .getByName(frontConfig)
            .artifacts
            .files
        from(frontFiles)
        into("$frontDist/static")
    }
//    compileKotlin.get().dependsOn(copyFront)
    processResources.get().dependsOn(copyFront)

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
                "KAFKA_BOOTSTRAP_SERVER" to "172.29.40.58:9092",
                "KAFKA_BOOTSTRAP_SERVER_1" to "172.29.40.58:9092",
                "KAFKA_BOOTSTRAP_SERVER_2" to "172.29.40.58:9092",
                "KAFKA_TOPIC_RAW" to "ui-temperature",
                "KAFKA_TOPIC_ANALYSIS" to "temperature-ui-ml",
                "SENSOR_ID" to "8e630dd0-5796-45e0-8d85-8a14c5d872dd",
                "LOGS_KAFKA_HOSTS" to "172.29.40.58:9092",
                "LOGS_KAFKA_TOPIC" to "gitlab-ci-logs",
                "LOGS_DIR" to "./logs",
                "SERVICE_NAME" to "adapter-jms"
            )
        )
    }
}
