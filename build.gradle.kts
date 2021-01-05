plugins {
    `kotlin-dsl`
    kotlin("jvm") version "1.4.21"
    id("groovy")
    id("maven-publish")
    id("com.gradle.plugin-publish") version "0.12.0"
    id("com.github.ben-manes.versions") version "0.36.0"
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

kotlin {
    explicitApi()
}

tasks.test {
    useJUnitPlatform()
}

// TODO: Remove after migration to Kotlin
tasks.compileGroovy {
    dependsOn(tasks.compileKotlin)
    classpath += files(tasks.compileKotlin.get().destinationDir)
}

repositories {
    jcenter()
}

dependencies {
    implementation(gradleApi())
    implementation(localGroovy())
    implementation("de.undercouch:gradle-download-task:4.1.1")
    testImplementation("junit:junit:4.13")
    testImplementation(platform("org.spockframework:spock-bom:2.0-M2-groovy-2.5"))
    testImplementation("org.spockframework:spock-core")
    testImplementation("org.spockframework:spock-junit4")
}

gradlePlugin {
    plugins {
        create("bukkitGradle") {
            id = "ru.endlesscode.bukkitgradle"
            implementationClass = "ru.endlesscode.bukkitgradle.BukkitGradlePlugin"
        }
    }
}

pluginBundle {
    website = "https://github.com/EndlessCodeGroup/BukkitGradle"
    vcsUrl = "$website.git"
    description = project.description
    tags = listOf("minecraft", "bukkit", "plugin", "spigot", "paper")

    (plugins) {
        "bukkitGradle" {
            displayName = "Bukkit Plugin development Gradle integration"
            description = "Gradle plugin providing integration for easier Bukkit plugins developement."
        }
    }
}
