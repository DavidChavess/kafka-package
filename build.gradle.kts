plugins {
    kotlin("jvm") version "1.8.21"
    application
    `maven-publish`
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    implementation("org.apache.kafka:kafka-clients:3.5.0")
    implementation("org.slf4j:slf4j-simple:2.0.7")
    implementation("com.google.code.gson:gson:2.10.1")
}

publishing {
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/DavidChavess/kafka-package")
            credentials {
                username = project.property("usernameCredentials").toString()
                password = project.property("passwordCredentials").toString()
            }
        }
    }
    publications {
        create<MavenPublication>("maven") {
            groupId = "com.davidchavess"
            artifactId = "kafka-package"
            version = "1.4"
            from(components["java"])
        }
    }
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(17)
}

application {
    mainClass.set("MainKt")
}