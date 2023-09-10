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
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("org.slf4j:slf4j-simple:2.0.7")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.4")
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
            version = "1.7.1"
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
tasks {
    withType<Jar> {
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
        manifest {
            attributes["Main-Class"] = application.mainClass
        }
        from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
    }
}