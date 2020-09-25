import com.moowork.gradle.node.npm.NpxTask

plugins {
    id("com.crowdproj.plugins.jar2npm")
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
}

val ngLibs: Configuration by configurations.creating

dependencies {
    implementation(kotlin("stdlib-js"))
//    implementation(project(":dsmart-module-temperature:dsmart-module-temperature-ws-models"))
}

tasks {
    val ngBuildRecommendations by ngLibBuild("recommendation-component")
    val ngBuildHistory by ngLibBuild("history-component") {
        dependsOn(ngBuildRecommendations)
    }
    val ngBuildStatus by ngLibBuild("teapot-status-component")
    val ngBuildBoiling by ngLibBuild("teapot-boiling-component")
    val ngBuildTemperature by ngLibBuild("temperature-component")
    val ngBuildWebsocket by ngLibBuild("websocket")
    val ngBuildWidget by ngLibBuild("temperature-widget") {
        dependsOn(ngBuildHistory)
        dependsOn(ngBuildStatus)
        dependsOn(ngBuildBoiling)
        dependsOn(ngBuildTemperature)
        dependsOn(ngBuildWebsocket)
    }
//    val ngBuildApp by ngLibBuild("temperature-app")

    val ngStart by ngLibBuild("temperature-app") {
        setArgs(
            listOf(
                "serve",
                "@datana-smart/temperature-app"
            )
        )
    }

    val createArtifact by creating {
        dependsOn(ngBuildWidget)
        artifacts {
            add(ngLibs.name, fileTree(distDir).dir)
        }
    }
    build.get().dependsOn(createArtifact)
}

fun TaskContainerScope.ngLibBuild(
    libName: String,
    scope: String = "datana-smart",
    conf: NpxTask.() -> Unit = {}
): PolymorphicDomainObjectContainerCreatingDelegateProvider<Task, NpxTask> = PolymorphicDomainObjectContainerCreatingDelegateProvider.of(this, NpxTask::class.java) {
    dependsOn(jar2npm)
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
        file("yarn.lock")
    )
    inputs.dir("projects/$scope/$libName")
    outputs.dir("$distDir/$scope/$libName")
    conf()
}
