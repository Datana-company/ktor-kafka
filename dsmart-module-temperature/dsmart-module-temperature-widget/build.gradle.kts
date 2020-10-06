import com.moowork.gradle.node.npm.NpxTask

plugins {
    id("com.crowdproj.plugins.jar2npm")
    id("com.bmuschko.docker-remote-api")
}

group = rootProject.group
version = rootProject.version

val DOCKER_GROUP = "docker"
val dockerPort = System.getenv("DOCKER_REGISTRY_PORT")?.let { ":$it" } ?: ""
val dockerHost = System.getenv("DOCKER_REGISTRY_HOST")?.plus("$dockerPort/") ?: ""
val dockerUser = System.getenv("DOCKER_REGISTRY_USER") as String?
val dockerPass = System.getenv("DOCKER_REGISTRY_PASS") as String?
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

val staticFront: Configuration by configurations.creating {
//    isCanBeConsumed = true
//    isCanBeResolved = false
//    If you want this configuration to share the same dependencies, otherwise omit this line
//    extendsFrom(configurations["implementation"], configurations["runtimeOnly"])
}

docker {
//  url = 'https://192.168.59.103:2376'
//  certPath = new File(System.properties['user.home'], '.boot2docker/certs/boot2docker-vm')

    registryCredentials {
        url.set(dockerParams.dockerUrl)
        dockerParams.dockerUser?.also { username.set(it) }
        dockerParams.dockerPass?.also { password.set(it) }
//    email = 'benjamin.muschko@gmail.com'
    }
}

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
    val ngBuildCollapsible by ngLibBuild("collapsible-table-component")
    val ngBuildBoiling by ngLibBuild("teapot-boiling-component") {
        dependsOn(ngBuildCollapsible)
    }
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

//    val ngStart by creating(com.moowork.gradle.node.npm.NpxTask::class.java) {
//        dependsOn(jar2npm)
//        dependsOn(extractNgLibs)
//        command = "ng"
//        setArgs(
//            listOf(
//                "serve"
//            )
//        )
//    }

    val createArtifact by creating {
        dependsOn(ngBuildWidget)
        artifacts {
            add(ngLibs.name, fileTree(distDir).dir)
        }
    }
    build.get().dependsOn(createArtifact)

//    val createArtifact by creating {
//        dependsOn(ngBuild)
//        artifacts {
//            add(distConfig, fileTree(distDir).dir)
//        }
//    }
////    build.get().dependsOn(createArtifact)



    val cliInit by creating(NpxTask::class.java) {
        dependsOn(jar2npm)
        command = "npm"
        setArgs(
            listOf(
                "install",
                "@angular/cli"
            )
        )
    }

//    val ngInit by creating(com.moowork.gradle.node.npm.NpxTask::class.java) {
//        dependsOn(cliInit)
//        command = "ng"
//        setArgs(
//            listOf(
//                "new",
//                "dsmart-ui-main",
//                "--directory",
//                "./"
//            )
//        )
//    }


    val extractNgLibs by creating(Copy::class.java) {
        dependsOn(createArtifact)
        from(ngLibs.artifacts.files)
        into("$buildDir/ng-libs")
    }

    val ngBuild by creating(NpxTask::class.java) {
        dependsOn(jar2npm)
        dependsOn(extractNgLibs)
        command = "ng"
        setArgs(
            listOf(
                "build"
            )
        )
    }
    build.get().dependsOn(ngBuild)
}

fun TaskContainerScope.ngLibBuild(
    libName: String,
    scope: String = "datana-smart",
    conf: NpxTask.() -> Unit = {}
): PolymorphicDomainObjectContainerCreatingDelegateProvider<Task, NpxTask> =
    PolymorphicDomainObjectContainerCreatingDelegateProvider.of(this, NpxTask::class.java) {
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
