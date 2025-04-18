package ru.endlesscode.bukkitgradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaBasePlugin
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.kotlin.dsl.*
import ru.endlesscode.bukkitgradle.dependencies.Dependencies
import ru.endlesscode.bukkitgradle.extensions.java
import ru.endlesscode.bukkitgradle.plugin.configurePluginYamlFeature
import ru.endlesscode.bukkitgradle.plugin.util.parsedApiVersion
import ru.endlesscode.bukkitgradle.plugin.util.resolveMinimalJavaVersion
import ru.endlesscode.bukkitgradle.server.configureDevServerFeature
import ru.endlesscode.bukkitgradle.server.extension.ServerConfigurationImpl

public class BukkitGradlePlugin : Plugin<Project> {

    override fun apply(target: Project) {
        target.configureProject()
    }

    private fun Project.configureProject() {
        addRepositories()
        addPlugins()
        configureEncoding()
        Dependencies.configureProject(project)
    }

    private fun Project.addPlugins() {
        // Apply Java plugin, but only if another JVM-language plugin wasn't applied before
        if (!plugins.hasPlugin(JavaBasePlugin::class)) {
            plugins.apply(JavaPlugin::class)
        }

        val bukkit = extensions.create<BukkitExtension>(
            BukkitExtension.NAME,
            ServerConfigurationImpl(),
        )

        configurePluginYamlFeature(bukkit)
        configureDevServerFeature(bukkit)

        java.toolchain {
            languageVersion.convention(bukkit.parsedApiVersion.map(::resolveMinimalJavaVersion))
        }
    }

    private fun Project.configureEncoding() {
        tasks.withType<JavaCompile>().configureEach {
            options.encoding = "UTF-8"
        }
    }

    private fun Project.addRepositories() {
        repositories {
            mavenCentral()
        }
    }
}
