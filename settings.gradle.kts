rootProject.name = "datana-smart-ui"
pluginManagement {

    val kotlinVersion: String by settings
    val jar2npmVersion: String by settings
    val dockerPluginVersion: String by settings

    plugins {

        kotlin("js") version kotlinVersion apply false
        kotlin("jvm") version kotlinVersion apply false
        kotlin("multiplatform") version kotlinVersion apply false

        id("com.crowdproj.plugins.jar2npm") version jar2npmVersion apply false
        id("com.bmuschko.docker-remote-api") version dockerPluginVersion apply false

    }
}

include("dsmart-ui-main")

