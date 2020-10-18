plugins {
    id("ru.vyarus.use-python") version "2.2.0"
    id("org.openapi.generator")
}

group = rootProject.group
version = rootProject.version

repositories {
    mavenCentral()
//    pyGradlePyPi()
}

python {
    alwaysInstallModules = true
}

dependencies {
//    val jacksonVersion: String by project
//    implementation(kotlin("stdlib"))
//    api("com.fasterxml.jackson.module:jackson-module-kotlin:$jacksonVersion")

//    testImplementation(kotlin("test"))
//    testImplementation(kotlin("test-junit"))
//    implementation("pypi:requests:2.9.1")
//    implementation("pypi:mock:2.0.0")
}

val generatedSourcesDir = "$buildDir/generated"
//kotlin.sourceSets["main"].kotlin.srcDirs("$generatedSourcesDir/src/main/kotlin")

openApiGenerate {
    generatorName.set("python-flask")
    inputSpec.set("${project(":dsmart-module-converter").projectDir}/spec-converter-meta.yaml")
    outputDir.set(generatedSourcesDir)
    val basePackage = "${project.group.toString().replace(".", "_")}_meta"
    packageName.set(basePackage)
    apiPackage.set("api")
    invokerPackage.set("invoker")
    modelPackage.set("models")
    ignoreFileOverride.set("$projectDir/.")
    systemProperties.apply {
//        put("models", "")
//        put("modelDocs", "false")
//        put("invoker", "")
////        put("invoker", "false")
//        put("apis", "false")
    }

    configOptions.set(mapOf(
        "dateLibrary" to "string",
        "enumPropertyNaming" to "UPPERCASE",
        "packageVersion" to project.version.toString()
//        "library" to "multiplatform",
//        "serializationLibrary" to "jackson"
    ))
}

tasks {

    val pypircPath = "${System.getenv("HOME")}/.pypirc"

    val createPypirc by creating {
        doLast {
            println("pypircPath=$pypircPath")
            val fileContent = """
            [distutils]
            index-servers =
                datana-nexus

            [datana-nexus]
            repository = https://nexus.datana.ru/repository/datana-pypi/
            username = ${dockerParams.dockerUser}
            password = ${dockerParams.dockerPass}
            """.trimIndent()
            File(pypircPath).writeText(fileContent)
            println("Saved: \n$fileContent")
        }
    }

    val registerToPiPy by creating(ru.vyarus.gradle.plugin.python.task.PythonTask::class.java) {
        dependsOn(openApiGenerate)
        dependsOn(createPypirc)
        workDir = "$buildDir/generated"
        command = "setup.py register"
    }

    val publishToPiPy by creating(ru.vyarus.gradle.plugin.python.task.PythonTask::class.java) {
        dependsOn(openApiGenerate)
        dependsOn(createPypirc)
//        dependsOn(registerToPiPy)
        workDir = "$buildDir/generated"
        command = "setup.py sdist upload --repository=datana-nexus --show-response"
    }

    val deletePypirc by creating {
        dependsOn(publishToPiPy)
        file(pypircPath).delete()
    }

    val deploy by creating {
        group = "build"
        dependsOn(deletePypirc)
        dependsOn(publishToPiPy)
    }
}
