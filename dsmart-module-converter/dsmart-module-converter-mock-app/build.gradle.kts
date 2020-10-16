import org.jetbrains.kotlin.gradle.dsl.Coroutines
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val ktorVersion: String by project
val kotlinVersion: String by project
val logbackVersion: String by project
val projectMaintainer: String by project

plugins {
    application
    kotlin("jvm")
    id("com.bmuschko.docker-java-application")
}

group = rootProject.group
version = rootProject.version

val frontDist = "$buildDir/frontDist"

application {
    mainClassName = "ru.datana.smart.ui.converter.mock.app.Application"
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
}

dependencies {

    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlinVersion")
    implementation("io.ktor:ktor-server-netty:$ktorVersion")
    implementation("ch.qos.logback:logback-classic:$logbackVersion")
    implementation("io.ktor:ktor-server-core:$ktorVersion")
    implementation("io.ktor:ktor-server-host-common:$ktorVersion")
    implementation("io.ktor:ktor-websockets:$ktorVersion")
    testImplementation("io.ktor:ktor-server-tests:$ktorVersion")
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

    create("deploy") {
        group = "build"
        dependsOn(dockerPushImage)
    }
}
