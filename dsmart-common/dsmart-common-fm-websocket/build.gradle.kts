plugins {
    id("com.crowdproj.plugins.jar2npm")
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
}

val ngLibs: Configuration by configurations.creating

dependencies {
    implementation(kotlin("stdlib-js"))
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

    val createArtifact by creating {
        dependsOn(ngBuild)
        artifacts{
            add(ngLibs.name, fileTree(distDir).dir)
        }
    }
}

