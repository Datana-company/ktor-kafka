rootProject.name = "datana-smart-ktor-kafka"
pluginManagement {

    val kotlinVersion: String by settings

    plugins {
        kotlin("jvm") version kotlinVersion apply false
    }
}
