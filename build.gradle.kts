import java.net.URI

plugins {
    java
    `maven-publish`
    kotlin("jvm")
    id("org.jetbrains.dokka")
    maven
}

group = "ru.datana.smart.common.ktor.kafka"
version = "0.7.1"

repositories {
    jcenter()
    mavenCentral()
}

dependencies {
    val ktorVersion: String by project

    implementation(kotlin("stdlib"))
    implementation("io.ktor:ktor-server-core:$ktorVersion")
    implementation("io.ktor:ktor-server-host-common:$ktorVersion")
    implementation("org.apache.kafka:kafka-clients:2.5.0")
    implementation("org.slf4j:slf4j-api:1.7.30")

    testImplementation("io.ktor:ktor-server-netty:$ktorVersion")
    testImplementation("io.ktor:ktor-server-tests:$ktorVersion")
}

kotlin {
}

val sourcesJar by tasks.creating(Jar::class) {
    archiveClassifier.set("sources")
    from(sourceSets.getByName("main").allSource)
}

tasks.dokka {
    outputFormat = "html"
    outputDirectory = "$buildDir/javadoc"
}

val dokkaJar by tasks.creating(Jar::class) {
    group = JavaBasePlugin.DOCUMENTATION_GROUP
    description = "Assembles Kotlin docs with Dokka"
    archiveClassifier.set("javadoc")
    from(tasks.dokka)
}

publishing {
    publications {
        create<MavenPublication>("default") {
            from(components["java"])
            artifact(dokkaJar)
            artifact(sourcesJar)
        }
    }
    repositories {
        val nexusHost: String? = System.getenv("NEXUS_HOST")
        val nexusUser: String? = System.getenv("NEXUS_USER")
        val nexusPass: String? = System.getenv("NEXUS_PASS")
        if (nexusHost != null && nexusUser != null && nexusPass != null) {
            maven {
                url = URI(nexusHost)
                credentials {
                    username = nexusUser
                    password = nexusPass
                }
            }
        }
    }
}

tasks {
    create("deploy") {
        group = "build"
        dependsOn(build)
        dependsOn(publish)
    }
}
