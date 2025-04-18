package ru.endlesscode.bukkitgradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaBasePlugin
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.hasPlugin
import org.gradle.kotlin.dsl.withType
import ru.endlesscode.bukkitgradle.dependencies.Dependencies.configureDependencyExtensions
import ru.endlesscode.bukkitgradle.extensions.java
import ru.endlesscode.bukkitgradle.plugin.configurePluginYamlFeature
import ru.endlesscode.bukkitgradle.plugin.util.parsedApiVersion
import ru.endlesscode.bukkitgradle.plugin.util.resolveMinimalJavaVersion
import ru.endlesscode.bukkitgradle.server.configureDevServerFeature
import ru.endlesscode.bukkitgradle.server.extension.ServerConfigurationImpl

public class BukkitGradlePlugin : Plugin<Project> {

    override fun apply(target: Project): Unit = with(target) {
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

        configureJavaCompilation(bukkit)
        configureDependencyExtensions(bukkit)
    }

    private fun Project.configureJavaCompilation(bukkit: Bukkit) {
        java.toolchain {
            languageVersion.convention(bukkit.parsedApiVersion.map(::resolveMinimalJavaVersion))
        }

        tasks.withType<JavaCompile>().configureEach {
            options.encoding = "UTF-8"
        }
    }
}
