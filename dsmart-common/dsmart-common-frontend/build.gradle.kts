import com.moowork.gradle.node.npm.NpxTask

plugins {
    id("com.github.node-gradle.node")
}

group = rootProject.group
version = rootProject.version

val distDir = "$buildDir/dist"

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

tasks {
    val ngBuildWebsocket by ngLibBuild("websocket")

    val ngBuildLibs by creating {
        dependsOn(ngBuildWebsocket)
    }

    val ngBuildApp by creating(com.moowork.gradle.node.npm.NpxTask::class.java) {
        dependsOn(npmInstall)
        dependsOn(ngBuildLibs)
        command = "ng"
        setArgs(
            listOf(
                "build",
                "@datana-smart/frontend-app",
                "--outputPath=$buildDir/static"
            )
        )
    }
//    build.get().dependsOn(ngBuildApp)
    val build by creating {
        group = "build"
        dependsOn(ngBuildApp)
    }

    val createArtifactLibs by creating {
        dependsOn(ngBuildWebsocket)
        artifacts {
            add("ngLibs", fileTree("$buildDir/dist").dir)
        }
    }

    val ngStart by ngLibBuild("frontend-app") {
        setArgs(
            listOf(
                "serve",
                "@datana-smart/frontend-app"
            )
        )
    }

    create("clean", Delete::class.java) {
        group = "build"
        delete(buildDir)
        delete("$projectDir/node_modules")
        delete("$projectDir/package-lock.json")
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

