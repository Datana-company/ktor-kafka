rootProject.name = "datana-smart-ui"
pluginManagement {

    val kotlinVersion: String by settings
    val nodePluginVersion: String by settings
    val dockerPluginVersion: String by settings
    val serializationPluginVersion: String by settings
    val openApiVersion: String by settings
    val dsmartLoggingVersion: String by settings
    val konveyorVersion: String by settings

    plugins {

        kotlin("js") version kotlinVersion apply false
        kotlin("jvm") version kotlinVersion apply false
        kotlin("multiplatform") version kotlinVersion apply false
        kotlin("plugin.serialization") version serializationPluginVersion

        id("com.github.node-gradle.node") version nodePluginVersion
        id("com.bmuschko.docker-remote-api") version dockerPluginVersion apply false
        id("com.bmuschko.docker-java-application") version dockerPluginVersion apply false

        id("org.openapi.generator") version openApiVersion apply false
    }
}

include(":dsmart-common")
include(":dsmart-common:dsmart-common-common")
include(":dsmart-common:dsmart-common-transport-ws")
include(":dsmart-common:dsmart-common-frontend")
include(":dsmart-common:dsmart-common-ktor-kafka")

include(":dsmart-ui-main")
include(":dsmart-ui-main:dsmart-ui-main-front")
include(":dsmart-ui-main:dsmart-gateway-app")

include(":dsmart-module-temperature")
include(":dsmart-module-temperature:dsmart-module-temperature-common")
include(":dsmart-module-temperature:dsmart-module-temperature-ws-models")
include(":dsmart-module-temperature:dsmart-module-temperature-kf-models")
include(":dsmart-module-temperature:dsmart-module-temperature-widget")
include(":dsmart-module-temperature:dsmart-module-temperature-app")
include(":dsmart-module-temperature:dsmart-module-temperature-ml-models")

include(":dsmart-module-converter")
include(":dsmart-module-converter:dsmart-module-converter-widget")
include(":dsmart-module-converter:dsmart-module-converter-app")
include(":dsmart-module-converter:dsmart-module-converter-ml-models")
include(":dsmart-module-converter:dsmart-module-converter-ws-models")
include(":dsmart-module-converter:dsmart-module-converter-common")
include(":dsmart-module-converter:dsmart-module-converter-models-meta")
include(":dsmart-module-converter:dsmart-module-converter-models-metapy")
include(":dsmart-module-converter:dsmart-module-converter-models-viml")
include(":dsmart-module-converter:dsmart-module-converter-models-vimlpy")
include(":dsmart-module-converter:dsmart-module-converter-models-mlui")
include(":dsmart-module-converter:dsmart-module-converter-models-mluipy")
include(":dsmart-module-converter:dsmart-module-converter-models-angle")
include(":dsmart-module-converter:dsmart-module-converter-mock-widget")
include(":dsmart-module-converter:dsmart-module-converter-mock-app")
include(":dsmart-module-converter:dsmart-module-converter-angle-app")
