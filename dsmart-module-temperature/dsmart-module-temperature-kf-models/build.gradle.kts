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
    val serializationVersion: String by project

    implementation(kotlin("stdlib"))
    api("org.jetbrains.kotlinx:kotlinx-serialization-core:$serializationVersion")
}
