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

include(":dsmart-common")
include(":dsmart-common:dsmart-common-common")
include(":dsmart-common:dsmart-common-transport-ws")
include(":dsmart-common:dsmart-common-fm-websocket")

include(":dsmart-ui-main")
include(":dsmart-ui-main:dsmart-ui-main-front")
include(":dsmart-ui-main:dsmart-gateway-app")

include(":dsmart-module-temperature")
include(":dsmart-module-temperature:dsmart-module-temperature-common")
include(":dsmart-module-temperature:dsmart-module-temperature-ws-models")
include(":dsmart-module-temperature:dsmart-module-temperature-widget")
include(":dsmart-module-temperature:dsmart-module-temperature-app")
