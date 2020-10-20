
plugins {
    id("com.github.node-gradle.node")
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
    workDir = file("${rootProject.projectDir}/.gradle/nodejs")
}

val ngLibs: Configuration by configurations.creating

val staticFront: Configuration by configurations.creating {
//    isCanBeConsumed = true
//    isCanBeResolved = false
//    If you want this configuration to share the same dependencies, otherwise omit this line
//    extendsFrom(configurations["implementation"], configurations["runtimeOnly"])
}

//sourceSets["main"].resources.srcDirs("resources", distDir)

tasks {
    val cliInit by creating(com.moowork.gradle.node.npm.NpxTask::class.java) {
        dependsOn()
        command = "npm"
        setArgs(
            listOf(
                "install",
                "@angular/cli"
            )
        )
    }

    val ngInit by creating(com.moowork.gradle.node.npm.NpxTask::class.java) {
        dependsOn(cliInit)
        command = "ng"
        setArgs(
            listOf(
                "new",
                "dsmart-ui-main",
                "--directory",
                "./"
            )
        )
    }

    val extractNgLibs by creating(Copy::class.java) {
        dependsOn(
            project(":dsmart-module-temperature:dsmart-module-temperature-widget")
                .getTasksByName("createArtifactLibs", false)
        )
        dependsOn(
            project(":dsmart-module-converter:dsmart-module-converter-widget")
                .getTasksByName("createArtifactLibs", false)
        )
        from(
            project(":dsmart-module-temperature:dsmart-module-temperature-widget")
                .configurations
                .getByName("ngLibs")
                .artifacts
                .files,
            project(":dsmart-module-converter:dsmart-module-converter-widget")
                .configurations
                .getByName("ngLibs")
                .artifacts
                .files
        )
        into("$buildDir/ng-libs")
    }

    val ngBuildApp by creating(com.moowork.gradle.node.npm.NpxTask::class.java) {
        dependsOn(npmInstall)
        dependsOn(extractNgLibs)
        command = "ng"
        setArgs(
            listOf(
                "build"
            )
        )
    }
//    yarnSetup.get().dependsOn(ngyarnSetup)
    val createArtifact by creating {
        dependsOn(ngBuildApp)
        artifacts {
            add(distConfig, fileTree(distDir).dir)
        }
    }
//    yarnSetup.get().dependsOn(createArtifact)
    val build by creating {
        group = "build"
        dependsOn(createArtifact)
    }


    val ngStart by creating(com.moowork.gradle.node.npm.NpxTask::class.java) {
        dependsOn(npmInstall)
        dependsOn(extractNgLibs)
        command = "ng"
        setArgs(
            listOf(
                "serve"
            )
        )
    }

//    val yarnSetupDockerDir by creating(Copy::class.java) {
//        dependsOn(ngyarnSetup)
//        group = DOCKER_GROUP
//        from(distDir)
//        into("$yarnSetupDir/docker/dist")
//    }
//
//    val createDockerFile by creating(com.bmuschko.gradle.docker.tasks.image.Dockerfile::class.java) {
//        dependsOn(yarnSetupDockerDir)
//        group = DOCKER_GROUP
//        from("nginx")
//        addFile("dist/", "/usr/share/nginx/html/")
//        exposePort(80)
//    }
//
//    val ngImage by creating(com.bmuschko.gradle.docker.tasks.image.DockeryarnSetupImage::class.java) {
//        dependsOn(createDockerFile)
//        group = DOCKER_GROUP
////    inputDir.set(File(distDir))
//        println("Dockder-image will be published to ${if (dockerHost.isBlank()) "localhost" else dockerHost}")
//        println("To change this value use DOCKER_REGISTRY_HOST:DOCKER_REGISTRY_PORT environment variables")
//        val imageName = "$dockerHost${project.name}"
//        images.add("$imageName:${project.version}")
//        images.add("$imageName:latest")
//    }
//
//    val ngDeploy by creating(com.bmuschko.gradle.docker.tasks.image.DockerPushImage::class.java) {
//        dependsOn(ngImage)
//        println("Dockder-image will be pushed to ${if (dockerHost.isBlank()) "localhost" else dockerHost}")
//        group = DOCKER_GROUP
//        images.set(ngImage.images)
//    }

//    val deploy by creating {
//        dependsOn(ngDeploy)
//        group = "yarnSetup"
//    }
}
