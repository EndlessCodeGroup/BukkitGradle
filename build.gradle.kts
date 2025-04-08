plugins {
    `kotlin-dsl`
    `maven-publish`
    groovy
    kotlin("plugin.serialization") version embeddedKotlinVersion
    id("com.gradle.plugin-publish") version "1.3.1"
    id("com.github.ben-manes.versions") version "0.52.0"
}

kotlin {
    explicitApi()
    jvmToolchain(17)

    compilerOptions {
        allWarningsAsErrors = true
    }
}

tasks.test {
    useJUnitPlatform()

    jvmArgs("--add-opens=java.base/java.nio.charset=ALL-UNNAMED")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("de.undercouch:gradle-download-task:5.6.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.1")
    implementation("com.charleskorn.kaml:kaml:0.74.0")
    testImplementation("junit:junit:4.13.1")
    testImplementation(platform("org.spockframework:spock-bom:2.3-groovy-3.0"))
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
    website = "https://github.com/EndlessCodeGroup/BukkitGradle"
    vcsUrl = website

    plugins {
        create("bukkitGradle") {
            id = "ru.endlesscode.bukkitgradle"
            displayName = "BukkitGradle Plugin"
            description = "Gradle plugin providing integration for easier Bukkit plugins development."
            implementationClass = "ru.endlesscode.bukkitgradle.BukkitGradlePlugin"
            tags = listOf("minecraft", "bukkit", "plugin", "spigot", "paper")
        }
    }
}
