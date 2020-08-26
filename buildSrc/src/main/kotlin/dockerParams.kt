import org.gradle.api.Project

class DockerParams(
    val dockerPort: String?,
    val dockerHost: String?,
    val dockerUser: String?,
    val dockerPass: String?,
    val projectName: String,
    val projectVersion: String
) {

    val dockerFullPort
        get() = dockerPort?.let { ":$it" } ?: ""

    val dockerFullHost
        get() = dockerHost?.plus("$dockerFullPort/") ?: ""

    val dockerUrl
        get() = "https://$dockerFullHost/v1/"

    val appName
        get() = projectName

    val imageName
        get() = "$dockerFullHost$appName"

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
