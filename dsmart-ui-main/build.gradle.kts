plugins {
    id("com.crowdproj.plugins.jar2npm")
}

group = rootProject.group
version = rootProject.version

repositories {
    mavenCentral()
}

node {
    download = true
    version = "14.8.0"
}

dependencies {
    implementation(kotlin("stdlib-js"))


}

tasks {
    val cliInit by creating(com.moowork.gradle.node.npm.NpxTask::class.java) {
        dependsOn(jar2npm)
        command = "npm"
        setArgs(listOf(
                "install",
                "@angular/cli"
        ))
    }

    val ngInit by creating(com.moowork.gradle.node.npm.NpxTask::class.java) {
        dependsOn(cliInit)
        command = "ng"
        setArgs(listOf(
                "new",
                "dsmart-ui-main",
                "--directory",
                "./"
        ))
    }

    val ngBuild by creating(com.moowork.gradle.node.npm.NpxTask::class.java) {
        dependsOn(jar2npm)
        command = "ng"
        setArgs(listOf(
                "build"
        ))
    }
    build.get().dependsOn(ngBuild)

    val ngStart by creating(com.moowork.gradle.node.npm.NpxTask::class.java) {
        dependsOn(jar2npm)
        command = "ng"
        setArgs(listOf(
                "serve"
        ))
    }

}
