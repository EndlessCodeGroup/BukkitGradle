plugins {
    `kotlin-dsl`
    `maven-publish`
    groovy
    kotlin("jvm") version "1.4.21"
    kotlin("plugin.serialization") version "1.4.21"
    id("com.gradle.plugin-publish") version "0.12.0"
    id("com.github.ben-manes.versions") version "0.36.0"
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
    jcenter()
}

dependencies {
    implementation("de.undercouch:gradle-download-task:4.1.1")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.0.1")
    implementation("com.charleskorn.kaml:kaml:0.26.0")
    testImplementation("junit:junit:4.13")
    testImplementation(platform("org.spockframework:spock-bom:2.0-M2-groovy-2.5"))
    testImplementation("org.spockframework:spock-core")
    testImplementation("org.spockframework:spock-junit4")
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
