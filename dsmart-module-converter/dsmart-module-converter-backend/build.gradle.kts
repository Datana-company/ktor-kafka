val konveyorVersion: String by project
val dsmartLoggingVersion: String by project

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

    implementation(kotlin("stdlib-jdk8"))
    implementation("codes.spectrum:konveyor:$konveyorVersion")
    testImplementation(kotlin("test"))
    testImplementation(kotlin("test-junit"))
}

tasks {

}

