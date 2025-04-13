package ru.endlesscode.bukkitgradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.kotlin.dsl.*
import ru.endlesscode.bukkitgradle.dependencies.Dependencies
import ru.endlesscode.bukkitgradle.meta.PluginMetaPlugin
import ru.endlesscode.bukkitgradle.meta.extension.PluginMetaImpl
import ru.endlesscode.bukkitgradle.meta.util.MinecraftVersion
import ru.endlesscode.bukkitgradle.meta.util.StringUtils
import ru.endlesscode.bukkitgradle.meta.util.parsedApiVersion
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
        val bukkit = extensions.create<BukkitExtension>("bukkit", configurePluginMeta(), ServerConfigurationImpl())

        with(plugins) {
            apply("java")
            apply<PluginMetaPlugin>()
            apply<DevServerPlugin>()
        }

        extensions.configure<JavaPluginExtension> {
            toolchain {
                languageVersion.convention(bukkit.parsedApiVersion.map(::resolveMinimalJavaVersion))
            }
        }
    }

    private fun Project.configurePluginMeta(): PluginMetaImpl {
        return PluginMetaImpl(objects).apply {
            name.convention(project.name)
            description.convention(provider { project.description })
            main.convention(name.map { "${project.group}.${StringUtils.toPascalCase(it)}" })
            version.convention(provider { project.version.toString() })
            apiVersion.convention(provider { bukkit.parsedApiVersion.get() }.map(::resolveDefaultApiVersion))
            url.convention(provider { providers.gradleProperty("url").orNull })
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

    private fun resolveDefaultApiVersion(version: MinecraftVersion): String = when {
        // "API version" has been introduced in Spigot 1.13
        version < MinecraftVersion.V1_13_0 -> ""
        // From 1.20.5 and onward, a patch version is supported.
        version < MinecraftVersion.V1_20_5 -> version.withoutPatch().toString()
        else -> version.toString()
    }

    private fun resolveMinimalJavaVersion(version: MinecraftVersion): JavaLanguageVersion = when {
        // https://minecraft.wiki/w/Java_Edition_1.20.5#General_2
        version >= MinecraftVersion.V1_20_5 -> JavaLanguageVersion.of(21)
        // https://minecraft.wiki/w/Java_Edition_1.18#General_2
        version >= MinecraftVersion.V1_18_0 -> JavaLanguageVersion.of(17)
        // https://minecraft.wiki/w/Java_Edition_1.17#General_2
        version >= MinecraftVersion.V1_17_0 -> JavaLanguageVersion.of(16)
        else -> JavaLanguageVersion.of(8)
    }
}
