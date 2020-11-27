val konveyorVersion: String by project
val dsmartLoggingVersion: String by project
val coroutinesVersion: String by project

plugins {
    kotlin("jvm")
}

group = rootProject.group
version = rootProject.version

repositories {
    mavenLocal()
    jcenter()
    maven { url = uri("https://kotlin.bintray.com/ktor") }
    maven { url = uri("https://nexus.datana.ru/repository/datana-release/") }
}

dependencies {
    implementation(project(":dsmart-module-converter:dsmart-module-converter-common"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion")
    implementation(kotlin("stdlib-jdk8"))
    implementation("codes.spectrum:konveyor:$konveyorVersion")
    implementation("ru.datana.smart:datana-smart-logging-core:$dsmartLoggingVersion")
    implementation("org.slf4j:slf4j-api:1.7.30")
    testImplementation(kotlin("test"))
    testImplementation(kotlin("test-junit"))
    testImplementation(project(":dsmart-module-converter:dsmart-module-converter-repo-inmemory"))
    testImplementation(project(":dsmart-module-converter:dsmart-module-converter-app"))
}

tasks {

}

