plugins {
    kotlin("jvm") version "2.0.20"
    id("com.github.johnrengelman.shadow") version "7.0.0"
}

group = "pixformer-gamefinder"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    testImplementation(project(":"))

    val ktorVersion = "3.1.0"

    implementation("io.ktor:ktor-server-netty:$ktorVersion")
    implementation("org.slf4j:slf4j-simple:2.0.16")
}

tasks.test {
    useJUnitPlatform()
}

tasks.build {
    dependsOn("shadowJar")
}
