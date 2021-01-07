package ru.endlesscode.bukkitgradle

import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginConvention
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.kotlin.dsl.*
import ru.endlesscode.bukkitgradle.dependencies.Dependencies
import ru.endlesscode.bukkitgradle.meta.PluginMetaPlugin
import ru.endlesscode.bukkitgradle.meta.extension.PluginMetaImpl
import ru.endlesscode.bukkitgradle.meta.util.StringUtils
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
        extensions.create<BukkitExtension>("bukkit", configurePluginMeta(), ServerConfigurationImpl())

        with(plugins) {
            apply("java")
            apply<PluginMetaPlugin>()
            apply<DevServerPlugin>()
        }

        with(convention.getPlugin<JavaPluginConvention>()) {
            sourceCompatibility = JavaVersion.VERSION_1_8
            targetCompatibility = JavaVersion.VERSION_1_8
        }
    }

    private fun Project.configurePluginMeta(): PluginMetaImpl {
        return PluginMetaImpl(objects).apply {
            name.convention(project.name)
            description.convention(provider { project.description })
            main.convention(name.map { "${project.group}.${StringUtils.toPascalCase(it)}" })
            version.convention(provider { project.version.toString() })
            apiVersion.convention(provider { StringUtils.parseApiVersion(bukkit.apiVersion) })
            url.convention(provider { findProperty("url")?.toString() })
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
            jcenter()
        }
    }
}
