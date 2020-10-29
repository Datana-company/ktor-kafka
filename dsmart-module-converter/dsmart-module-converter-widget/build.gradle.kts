import com.moowork.gradle.node.npm.NpxTask

plugins {
    id("com.github.node-gradle.node")
}

group = rootProject.group
version = rootProject.version

val distDir = "$buildDir/dist"
val distConfig = "staticFront"

repositories {
    mavenCentral()
}

node {
    val nodeVersion: String by project
    download = true
    version = nodeVersion
    workDir = file("${rootProject.projectDir}/.gradle/nodejs")
}

val ngLibs: Configuration by configurations.creating
val staticFront: Configuration by configurations.creating

tasks {
    val copyCommonLibs by creating(Copy::class.java) {
        dependsOn(
            project(":dsmart-common:dsmart-common-frontend")
                .getTasksByName("createArtifactLibs", false)
        )
        val frontFiles = project(":dsmart-common:dsmart-common-frontend")
            .configurations
            .getByName("ngLibs")
            .artifacts
            .files
        from(frontFiles)
        into("$buildDir/dist")
    }
//    processResources.get().dependsOn(copyCommonLibs)

    val ngBuildWidget by ngLibBuild("converter-widget") {
        dependsOn(copyCommonLibs)
    }

    val ngBuildApp by creating(com.moowork.gradle.node.npm.NpxTask::class.java) {
        dependsOn(npmInstall)
        dependsOn(ngBuildWidget)
        command = "ng"
        setArgs(
            listOf(
                "build",
                "@datana-smart/converter-app",
                "--outputPath=$buildDir/static"
            )
        )
    }

    val build by creating {
        group = "build"
        dependsOn(ngBuildApp)
    }
//    build.get().dependsOn(ngBuildApp)

    val createArtifactLibs by creating {
        dependsOn(ngBuildWidget)
        artifacts {
            add("ngLibs", fileTree("$buildDir/dist").dir)
        }
    }

    val createArtifactStatic by creating {
        dependsOn(ngBuildApp)
        artifacts {
            add("staticFront", fileTree("$buildDir/static").dir)
        }
    }

    val ngStart by ngLibBuild("converter-app") {
        dependsOn(ngBuildWidget)
        setArgs(
            listOf(
                "serve",
                "@datana-smart/converter-app"
            )
        )
    }

    create("clean", Delete::class.java) {
        group = "build"
        delete(buildDir)
        delete("$projectDir/node_modules")
        delete("$projectDir/package-lock.json")
        delete("$projectDir/yarn.lock")
    }
}

fun TaskContainerScope.ngLibBuild(
    libName: String,
    scope: String = "datana-smart",
    conf: NpxTask.() -> Unit = {}
): PolymorphicDomainObjectContainerCreatingDelegateProvider<Task, NpxTask> = PolymorphicDomainObjectContainerCreatingDelegateProvider.of(this, NpxTask::class.java) {
    dependsOn(npmInstall)
    command = "ng"
    setArgs(
        listOf(
            "build",
            "@$scope/$libName"
        )
    )
    inputs.files(
        file("angular.json"),
        file("tsconfig.base.json"),
        file("package.json"),
        file("tsconfig.json"),
        file("tslint.json"),
        file("yarn.lock"),
        file("package-lock.json")
    )
    inputs.dir("projects/$scope/$libName")
    outputs.dir("$distDir/$scope/$libName")
    conf()
}
