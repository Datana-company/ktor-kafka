import org.gradle.api.Project

class DockerParams(
    dockerPort: String?,
    dockerHost: String?,
    dockerUser: String?,
    dockerPass: String?,
    projectName: String,
    projectVersion: String
) {

    val dockerFullPort = dockerPort?.trim()?.let { ":$it" } ?: ""

    val dockerFullHost = dockerHost?.trim()?.plus("$dockerFullPort/") ?: ""

    val dockerUrl
        get() = "https://$dockerFullHost/v1/"

    val appName = projectName.trim()

    val imageName = "$dockerFullHost$appName"

    val dockerUser = dockerUser?.trim()

    val dockerPass = dockerPass?.trim()

}

val Project.dockerParams
    get() = DockerParams(
        System.getenv("DOCKER_REGISTRY_PORT"),
        System.getenv("DOCKER_REGISTRY_HOST"),
        System.getenv("DOCKER_REGISTRY_USER"),
        System.getenv("DOCKER_REGISTRY_PASS"),
        this.name,
        this.version.toString()
    )
