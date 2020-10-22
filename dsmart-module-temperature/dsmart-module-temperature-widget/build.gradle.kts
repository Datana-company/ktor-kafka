import com.moowork.gradle.node.npm.NpxTask

plugins {
    id("com.github.node-gradle.node")
}

group = rootProject.group
version = rootProject.version

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
val widgetLib: Configuration by configurations.creating

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

    val ngBuildRecommendations by ngLibBuild("recommendation-component")
    val ngBuildHistory by ngLibBuild("history-component") {
        dependsOn(ngBuildRecommendations)
    }

    val ngBuildStatus by ngLibBuild("teapot-status-component")
    val ngBuildCollapsible by ngLibBuild("collapsible-table-component")
    val ngBuildBoiling by ngLibBuild("teapot-boiling-component") {
        dependsOn(ngBuildCollapsible)
    }
    val ngBuildTemperature by ngLibBuild("temperature-component")
    val ngBuildWidget by ngLibBuild("temperature-widget") {
        dependsOn(ngBuildHistory)
        dependsOn(ngBuildStatus)
        dependsOn(ngBuildBoiling)
        dependsOn(ngBuildTemperature)
        dependsOn(copyCommonLibs)
    }

    val ngBuildApp by creating(com.moowork.gradle.node.npm.NpxTask::class.java) {

        dependsOn(npmInstall)
        dependsOn(ngBuildWidget)
        command = "ng"
        setArgs(
            listOf(
                "build",
                "@datana-smart/temperature-app",
                "--outputPath=$buildDir/static"
            )
        )
    }

    val build by creating {
        group = "build"
        dependsOn(ngBuildApp)
    }

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

    val ngStart by ngLibBuild("temperature-app") {
        setArgs(
            listOf(
                "serve",
                "@datana-smart/temperature-app"
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
): PolymorphicDomainObjectContainerCreatingDelegateProvider<Task, NpxTask> =
    PolymorphicDomainObjectContainerCreatingDelegateProvider.of(this, NpxTask::class.java) {
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
        outputs.dir("$buildDir/dist/$scope/$libName")
        conf()
    }
