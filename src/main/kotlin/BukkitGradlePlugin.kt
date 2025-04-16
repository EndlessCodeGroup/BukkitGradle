package ru.endlesscode.bukkitgradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaBasePlugin
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.repositories
import org.gradle.kotlin.dsl.withType
import ru.endlesscode.bukkitgradle.dependencies.Dependencies
import ru.endlesscode.bukkitgradle.extensions.java
import ru.endlesscode.bukkitgradle.plugin.configurePluginYamlFeature
import ru.endlesscode.bukkitgradle.plugin.util.parsedApiVersion
import ru.endlesscode.bukkitgradle.plugin.util.resolveMinimalJavaVersion
import ru.endlesscode.bukkitgradle.server.DevServerPlugin
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

    /** Adds needed plugins. */
    private fun Project.addPlugins() {
        val bukkit = extensions.create<BukkitExtension>(
            BukkitExtension.NAME,
            ServerConfigurationImpl(),
        )

        configurePluginYamlFeature(bukkit)
        apply<DevServerPlugin>()

        plugins.withType<JavaBasePlugin> {
            java.toolchain {
                languageVersion.convention(bukkit.parsedApiVersion.map(::resolveMinimalJavaVersion))
            }
        }
    }

    /** Sets encoding on compile to UTF-8. */
    private fun Project.configureEncoding() {
        tasks.withType<JavaCompile>().configureEach {
            options.encoding = "UTF-8"
        }
    }

    /** Adds needed repositories. */
    private fun Project.addRepositories() {
        repositories {
            mavenCentral()
        }
    }
}
