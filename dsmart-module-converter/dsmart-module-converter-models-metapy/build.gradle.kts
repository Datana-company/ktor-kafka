plugins {
    id("ru.vyarus.use-python") version "2.2.0"
    id("org.openapi.generator")
}

group = rootProject.group
version = rootProject.version

repositories {
    mavenCentral()
}

python {
    alwaysInstallModules = true
}

val generatedSourcesDir = "$buildDir/generated"

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
    }

    configOptions.set(mapOf(
        "dateLibrary" to "string",
        "enumPropertyNaming" to "UPPERCASE",
        "packageVersion" to project.version.toString()
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

    val publishToPiPy by creating(ru.vyarus.gradle.plugin.python.task.PythonTask::class.java) {
        dependsOn(openApiGenerate)
        dependsOn(createPypirc)
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
