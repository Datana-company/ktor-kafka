plugins {
  id("com.crowdproj.plugins.jar2npm")
  id("com.bmuschko.docker-remote-api")
}

group = rootProject.group
version = rootProject.version

val DOCKER_GROUP = "docker"

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

  val distDir = "$projectDir/dist"

  val buildDockerDir by creating(Copy::class.java) {
    dependsOn(ngBuild)
    group = DOCKER_GROUP
    from(distDir)
    into("$buildDir/docker/dist")
  }
  val createDockerFile by creating(com.bmuschko.gradle.docker.tasks.image.Dockerfile::class.java) {
    dependsOn(buildDockerDir)
    group = DOCKER_GROUP
    from("nginx")
    addFile("dist", "/usr/share/nginx/html")
    exposePort(80)
  }

  val ngImage by creating(com.bmuschko.gradle.docker.tasks.image.DockerBuildImage::class.java) {
    dependsOn(createDockerFile)
    group = DOCKER_GROUP
//    inputDir.set(File(distDir))
    val port = System.getenv("DOCKER_REGISTRY_PORT")?.let { ":$it" } ?: ""
    val host = System.getenv("DOCKER_REGISTRY_HOST")?.plus("$port/") ?: ""
    println("Dockder-image will be published to ${if (host.isBlank()) "localhost" else host}")
    println("To change this value use DOCKER_REGISTRY_HOST:DOCKER_REGISTRY_PORT environment variables")
    val imageName = "$host${project.name}"
    images.add("$imageName:${project.version}")
    images.add("$imageName:latest")
  }
}
