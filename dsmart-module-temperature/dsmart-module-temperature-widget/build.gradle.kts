plugins {
    id("com.crowdproj.plugins.jar2npm")
//    id("com.bmuschko.docker-remote-api")
}

group = rootProject.group
version = rootProject.version

//val DOCKER_GROUP = "docker"
//val dockerPort = System.getenv("DOCKER_REGISTRY_PORT")?.let { ":$it" } ?: ""
//val dockerHost = System.getenv("DOCKER_REGISTRY_HOST")?.plus("$dockerPort/") ?: ""
//val dockerUser = System.getenv("DOCKER_REGISTRY_USER") as String?
//val dockerPass = System.getenv("DOCKER_REGISTRY_PASS") as String?
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
    val ngBuild by creating(com.moowork.gradle.node.npm.NpxTask::class.java) {
        dependsOn(jar2npm)
        command = "ng"
        setArgs(
            listOf(
                "build"
            )
        )
        outputs.dir(distDir)
    }

//    val npmPublish by creating(com.moowork.gradle.node.npm.NpxTask::class.java) {
//        dependsOn(ngBuild)
//        command = "npm"
//        setArgs(
//            listOf(
//                "publish"
////                "--registry"
////                "http://nexus.datana.ru:8081/repository/npm-private/"
//            )
//        )
//    }

    val createArtifact by creating {
        dependsOn(ngBuild)
        artifacts{
            add(ngLibs.name, fileTree(distDir).dir)
        }
    }
//    build.get().dependsOn(createArtifact)

//    create("publish") {
//        group = "build"
//        dependsOn(npmPublish)
//    }

}
