package ru.endlesscode.bukkitgradle.plugin

import com.charleskorn.kaml.Yaml
import com.charleskorn.kaml.YamlConfiguration
import com.charleskorn.kaml.YamlNamingStrategy
import org.gradle.api.Project
import org.gradle.api.file.RegularFile
import org.gradle.api.plugins.ExtensionAware
import org.gradle.api.plugins.JavaBasePlugin
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.SourceSet
import org.gradle.kotlin.dsl.*
import ru.endlesscode.bukkitgradle.Bukkit
import ru.endlesscode.bukkitgradle.BukkitExtension
import ru.endlesscode.bukkitgradle.extensions.mapNotNull
import ru.endlesscode.bukkitgradle.extensions.resourceFactory
import ru.endlesscode.bukkitgradle.extensions.sourceSets
import ru.endlesscode.bukkitgradle.plugin.task.ParsePluginYaml
import ru.endlesscode.bukkitgradle.plugin.util.MinecraftVersion
import ru.endlesscode.bukkitgradle.plugin.util.StringUtils
import ru.endlesscode.bukkitgradle.plugin.util.parsedApiVersion
import xyz.jpenilla.resourcefactory.ExecuteResourceFactories
import xyz.jpenilla.resourcefactory.ResourceFactoryPlugin
import xyz.jpenilla.resourcefactory.bukkit.BukkitPluginYaml
import xyz.jpenilla.resourcefactory.bukkit.bukkitPluginYaml

private const val PLUGIN_EXTENSION_NAME: String = "plugin"
private const val SOURCE_SET_NAME: String = "main" // Make it configurable, maybe?
internal const val PLUGIN_YML: String = "plugin.yml"

private val defaultYaml by lazy {
    Yaml(
        configuration = YamlConfiguration(
            strictMode = false,
            decodeEnumCaseInsensitive = true,
            yamlNamingStrategy = YamlNamingStrategy.KebabCase,
        )
    )
}

internal fun Project.configurePluginYamlFeature(bukkit: BukkitExtension) {
    project.apply<ResourceFactoryPlugin>()

    val bukkitPluginYaml = bukkitPluginYaml {
        setConventionsFromProjectMeta(project, bukkit)
    }
    (bukkit as ExtensionAware).extensions.add(PLUGIN_EXTENSION_NAME, bukkitPluginYaml)

    plugins.withType<JavaBasePlugin> {
        val mainSourceSet = sourceSets.named(SOURCE_SET_NAME) {
            resourceFactory.factory(bukkitPluginYaml.resourceFactory())
        }

        val parsePluginYamlProvider = tasks.register<ParsePluginYaml>("parsePluginYaml") {
            yaml.set(defaultYaml)
            pluginYaml.set(bukkitPluginYaml)
            pluginYamlFile.set(findPluginYaml(mainSourceSet))

            onlyIf { bukkit.generatePluginYaml.get() }
        }

        tasks.named<ExecuteResourceFactories>("${SOURCE_SET_NAME}ResourceFactory") {
            val generatePluginYaml = bukkit.generatePluginYaml.get()
            onlyIf("Flag generatePluginYaml is enabled") { generatePluginYaml }
            if (!generatePluginYaml) return@named

            val parsePluginYaml = parsePluginYamlProvider.get()

            // Switch to in-place generation mode if the plugin.yml exists
            val pluginYamlFile = parsePluginYaml.pluginYamlFile.orNull?.asFile
            if (pluginYamlFile != null) {
                dependsOn(parsePluginYaml)

                outputDir.set(pluginYamlFile.parentFile)
                // Deduplicate resource dirs after changing outputDir
                mainSourceSet.configure {
                    resources.setSrcDirs(resources.srcDirs.distinct())
                }
            }
        }
    }
}

private fun Project.findPluginYaml(sourceSetProvider: Provider<SourceSet>): Provider<RegularFile> {
    val fileProvider = sourceSetProvider.mapNotNull { sourceSet ->
        sourceSet.resources.sourceDirectories
            .map { it.resolve(PLUGIN_YML) }
            .find { it.isFile }
    }

    return layout.file(fileProvider)
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
