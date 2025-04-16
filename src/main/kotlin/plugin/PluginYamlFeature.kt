package ru.endlesscode.bukkitgradle.plugin

import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionAware
import org.gradle.api.plugins.JavaBasePlugin
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.the
import org.gradle.kotlin.dsl.withType
import ru.endlesscode.bukkitgradle.Bukkit
import ru.endlesscode.bukkitgradle.BukkitExtension
import ru.endlesscode.bukkitgradle.extensions.resourceFactory
import ru.endlesscode.bukkitgradle.extensions.sourceSets
import ru.endlesscode.bukkitgradle.plugin.util.MinecraftVersion
import ru.endlesscode.bukkitgradle.plugin.util.StringUtils
import ru.endlesscode.bukkitgradle.plugin.util.parsedApiVersion
import xyz.jpenilla.resourcefactory.ResourceFactoryPlugin
import xyz.jpenilla.resourcefactory.bukkit.BukkitPluginYaml
import xyz.jpenilla.resourcefactory.bukkit.bukkitPluginYaml

private const val PLUGIN_EXTENSION_NAME: String = "plugin"

internal fun Project.configurePluginYamlFeature(bukkit: BukkitExtension) {
    project.apply<ResourceFactoryPlugin>()

    val bukkitPluginYaml = bukkitPluginYaml {
        setConventionsFromProjectMeta(project, bukkit)
    }
    (bukkit as ExtensionAware).extensions.add(PLUGIN_EXTENSION_NAME, bukkitPluginYaml)

    plugins.withType<JavaBasePlugin> {
        sourceSets.named("main") {
            resourceFactory.factory(bukkitPluginYaml.resourceFactory())
        }
    }
}

/**
 * We use custom implementation of `setConventionsFromProjectMeta` that uses providers to defer project fields reading.
 */
private fun BukkitPluginYaml.setConventionsFromProjectMeta(project: Project, bukkit: BukkitExtension) {
    name.convention(project.name) // Project name is immutable, no need to wrap it with provider
    description.convention(project.provider { project.description })
    version.convention(project.provider { project.version.toString() })
    apiVersion.convention(bukkit.parsedApiVersion.map(::resolveDefaultApiVersion))

    // Kept for backward compatibility
    // TODO: This is a bit unobvious behavior, so probably we should remove defaults for these properties
    main.convention(name.map { "${project.group}.${StringUtils.toPascalCase(it)}" })
    website.convention(
        project.providers.gradleProperty("url")
            .orElse(project.providers.gradleProperty("website"))
    )
}

private fun resolveDefaultApiVersion(version: MinecraftVersion): String = when {
    // "API version" has been introduced in Spigot 1.13
    version < MinecraftVersion.V1_13_0 -> ""
    // From 1.20.5 and onward, a patch version is supported.
    version < MinecraftVersion.V1_20_5 -> version.withoutPatch().toString()
    else -> version.toString()
}

public val Bukkit.plugin: BukkitPluginYaml
    get() = (this as ExtensionAware).the()

public fun Bukkit.plugin(configure: BukkitPluginYaml.() -> Unit) {
    (this as ExtensionAware).configure(configure)
}
