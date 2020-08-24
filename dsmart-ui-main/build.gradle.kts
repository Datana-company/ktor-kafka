plugins {
  id("com.crowdproj.plugins.jar2npm")
  id("com.bmuschko.docker-remote-api")
}

group = rootProject.group
version = rootProject.version

val DOCKER_GROUP = "docker"
val dockerPort = System.getenv("DOCKER_REGISTRY_PORT")?.let { ":$it" } ?: ""
val dockerHost = System.getenv("DOCKER_REGISTRY_HOST")?.plus("$dockerPort/") ?: ""
val dockerUser = System.getenv("DOCKER_REGISTRY_USER")
val dockerPass = System.getenv("DOCKER_REGISTRY_PASS")

repositories {
  mavenCentral()
}

node {
  val nodeVersion: String by project
    download = true
    version = nodeVersion
}

docker {
//  url = 'https://192.168.59.103:2376'
//  certPath = new File(System.properties['user.home'], '.boot2docker/certs/boot2docker-vm')

  registryCredentials {
    url.set("https://$dockerHost/v1/")
    dockerUser?.also { username.set(it) }
    dockerPass?.also { password.set(it) }
//    email = 'benjamin.muschko@gmail.com'
  }
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

  val distDir = "$projectDir/dist/${project.name}"

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
    addFile("dist/", "/usr/share/nginx/html/")
    exposePort(80)
  }

  val ngImage by creating(com.bmuschko.gradle.docker.tasks.image.DockerBuildImage::class.java) {
    dependsOn(createDockerFile)
    group = DOCKER_GROUP
//    inputDir.set(File(distDir))
    println("Dockder-image will be published to ${if (dockerHost.isBlank()) "localhost" else dockerHost}")
    println("To change this value use DOCKER_REGISTRY_HOST:DOCKER_REGISTRY_PORT environment variables")
    val imageName = "$dockerHost${project.name}"
    images.add("$imageName:${project.version}")
    images.add("$imageName:latest")
  }

  val ngDeploy by creating (com.bmuschko.gradle.docker.tasks.image.DockerPushImage::class.java) {
    dependsOn(ngImage)
    println("Dockder-image will be pushed to ${if (dockerHost.isBlank()) "localhost" else dockerHost}")
    group = DOCKER_GROUP
    images.set(ngImage.images)
  }

  val deploy by creating {
    dependsOn(ngDeploy)
    group = "build"
  }
}
