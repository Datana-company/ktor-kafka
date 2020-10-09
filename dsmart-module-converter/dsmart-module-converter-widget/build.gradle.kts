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

val ngLibsConv: Configuration by configurations.creating
val staticFrontConv: Configuration by configurations.creating
val widgetLibConv: Configuration by configurations.creating

dependencies {
    implementation(kotlin("stdlib-js"))
//    implementation(project(":dsmart-module-converter:dsmart-module-converter-ws-models"))
}

tasks {
    val ngBuildVideoPlayer by ngLibBuild("video-player-component")
    val ngBuildWidget by ngLibBuild("converter-widget") {
        dependsOn(ngBuildVideoPlayer)
    }
    val ngBuildApp by creating(com.moowork.gradle.node.npm.NpxTask::class.java) {
        dependsOn(jar2npm)
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

    build.get().dependsOn(ngBuildApp)

    val createArtifactLibs by creating {
        dependsOn(ngBuildApp)
        artifacts {
            add("ngLibsConv", fileTree("$buildDir/dist").dir)
        }
    }

    val createArtifactWidget by creating {
        dependsOn(ngBuildApp)
        artifacts {
            add("widgetLibConv", fileTree("$buildDir/dist/datana-smart/converter-widget").dir)
        }
    }

    val createArtifactStatic by creating {
        dependsOn(ngBuildApp)
        artifacts {
            add("staticFrontConv", fileTree("$buildDir/static").dir)
        }
    }

    val ngStart by ngLibBuild("converter-app") {
        setArgs(
            listOf(
                "serve",
                "@datana-smart/converter-app"
            )
        )
    }
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
