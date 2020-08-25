plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
    jcenter()
}

dependencies {
    api("com.bmuschko:gradle-docker-plugin:6.6.1")
//    implementation("com.bmuschko:gradle-docker-plugin:6.6.1")
}
