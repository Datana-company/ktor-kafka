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
        id("com.bmuschko.docker-java-application") version dockerPluginVersion apply false

    }
}

include("dsmart-ui-main")
include("dsmart-common")
include("dsmart-transport-models-mp")
include("dsmart-gateway-app")
include("dsmart-module-temerature")
include(":dsmart-module-temerature:dsmart-module-temperature-widget")
