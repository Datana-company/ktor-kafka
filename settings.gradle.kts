rootProject.name = "datana-smart-ui"
pluginManagement {

    val kotlinVersion: String by settings
    val jar2npmVersion: String by settings

    plugins {

        kotlin("js") version kotlinVersion apply false
        kotlin("jvm") version kotlinVersion apply false
        kotlin("multiplatform") version kotlinVersion apply false

        id("com.crowdproj.plugins.jar2npm") version jar2npmVersion
    }
}

include("dmart-ui-main")

