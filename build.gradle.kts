plugins {
    java
    id("maven-publish")
}

group = "de.skyslycer"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
        }
    }
    repositories {
        maven {
            url = uri("https://repo.skyslycer.de/releases/")
            credentials {
                username = System.getenv("REPO_USERNAME") ?: ""
                password = System.getenv("REPO_PASSWORD") ?: ""
            }
        }
    }
}
