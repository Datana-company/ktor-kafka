plugins {
    kotlin("js") apply false
    kotlin("jvm") apply false
    kotlin("multiplatform") apply false
    kotlin("plugin.serialization") apply false
    id("com.bmuschko.docker-remote-api") apply false
    id("com.bmuschko.docker-java-application") apply false
    id("org.openapi.generator") apply false
}

group = "ru.datana.smart.ui"
version = "0.6.12"

subprojects {
    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
//        kotlinOptions.suppressWarnings = true
        kotlinOptions.jvmTarget = "11"
    }

    repositories {
        mavenLocal()
        jcenter()
        maven { url = uri("https://kotlin.bintray.com/ktor") }
        maven { url = uri("https://nexus.datana.ru/repository/datana-release/") }
    }
}
