plugins {
    kotlin("jvm")
    id("org.openapi.generator")
}

group = rootProject.group
version = rootProject.version

repositories {
    mavenCentral()
}

dependencies {
    val jacksonVersion: String by project
    implementation(kotlin("stdlib"))
    api("com.fasterxml.jackson.module:jackson-module-kotlin:$jacksonVersion")

    testImplementation(kotlin("test"))
    testImplementation(kotlin("test-junit"))
}

val generatedSourcesDir = "$buildDir/generated"
kotlin.sourceSets["main"].kotlin.srcDirs("$generatedSourcesDir/src")

openApiGenerate {
    generatorName.set("kotlin")
    inputSpec.set("${project(":dsmart-module-temperature").projectDir}/spec-temperature-ml.yaml")
    outputDir.set(generatedSourcesDir)
    val basePackage = "${project.group}.ml"
    apiPackage.set("$basePackage.api")
    invokerPackage.set("$basePackage.invoker")
    modelPackage.set("$basePackage.models")
    systemProperties.apply {
        put("models", "")
        put("modelDocs", "false")
        put("invoker", "false")
        put("apis", "false")
    }
    configOptions.set(mapOf(
        "dateLibrary" to "string",
        "enumPropertyNaming" to "UPPERCASE",
        "library" to "multiplatform",
        "serializationLibrary" to "jackson"
    ))
}

tasks {
    compileKotlin {
        dependsOn(openApiGenerate)
        kotlinOptions {
            jvmTarget = "11"
        }
    }
}
