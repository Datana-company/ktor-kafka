rootProject.name = "datana-smart-ktor-kafka"
pluginManagement {

    val kotlinVersion: String by settings
    val dokkaVersion: String by settings

    plugins {
        kotlin("jvm") version kotlinVersion apply false
        id("org.jetbrains.dokka") version dokkaVersion apply false
    }
}
