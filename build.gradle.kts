plugins {
    `kotlin-dsl`
    `maven-publish`
    groovy
    kotlin("plugin.serialization") version "1.5.21"
    id("com.gradle.plugin-publish") version "0.15.0"
    id("com.github.ben-manes.versions") version "0.39.0"
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
    withSourcesJar()
}

kotlin {
    explicitApi()
}

tasks.test.configure {
    useJUnitPlatform()
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("de.undercouch:gradle-download-task:4.1.2")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.2.2")
    implementation("com.charleskorn.kaml:kaml:0.35.0")
    testImplementation("junit:junit:4.13")
    testImplementation(platform("org.spockframework:spock-bom:2.0-groovy-3.0"))
    testImplementation("org.spockframework:spock-core")
    testImplementation("org.spockframework:spock-junit4")
}

val runningOnCi = System.getenv("CI") == "true"
publishing {
    repositories {
        if (runningOnCi) {
            maven("https://maven.pkg.github.com/EndlessCodeGroup/BukkitGradle") {
                name = "githubPackages"
                credentials(PasswordCredentials::class)
            }
        }
    }
}

gradlePlugin {
    plugins {
        create("bukkitGradle") {
            id = "ru.endlesscode.bukkitgradle"
            displayName = "BukkitGradle Plugin"
            description = "Gradle plugin providing integration for easier Bukkit plugins development."
            implementationClass = "ru.endlesscode.bukkitgradle.BukkitGradlePlugin"
        }
    }
}

pluginBundle {
    website = "https://github.com/EndlessCodeGroup/BukkitGradle"
    vcsUrl = website
    tags = listOf("minecraft", "bukkit", "plugin", "spigot", "paper")
}
