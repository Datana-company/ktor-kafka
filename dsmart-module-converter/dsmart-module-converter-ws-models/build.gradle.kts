val serializationVersion: String by project

plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
}

group = rootProject.group
version = rootProject.version

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    api("org.jetbrains.kotlinx:kotlinx-serialization-core:$serializationVersion")
    api(project(":dsmart-common:dsmart-common-transport-ws"))
    api(project(":dsmart-module-converter:dsmart-module-converter-common"))

    testImplementation(kotlin("test"))
    testImplementation(kotlin("test-junit"))
}

tasks {

}
