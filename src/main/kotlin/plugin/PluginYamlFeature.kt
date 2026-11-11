package ru.endlesscode.bukkitgradle.plugin

import org.gradle.api.Project
import org.gradle.api.file.RegularFile
import org.gradle.api.plugins.ExtensionAware
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.SourceSet
import org.gradle.kotlin.dsl.*
import ru.endlesscode.bukkitgradle.Bukkit
import ru.endlesscode.bukkitgradle.BukkitExtension
import ru.endlesscode.bukkitgradle.extensions.finalizeAndGet
import ru.endlesscode.bukkitgradle.extensions.mapNotNull
import ru.endlesscode.bukkitgradle.extensions.resourceFactory
import ru.endlesscode.bukkitgradle.extensions.sourceSets
import ru.endlesscode.bukkitgradle.plugin.util.MinecraftVersion
import ru.endlesscode.bukkitgradle.plugin.util.StringUtils
import xyz.jpenilla.resourcefactory.ExecuteResourceFactories
import xyz.jpenilla.resourcefactory.ResourceFactoryPlugin
import xyz.jpenilla.resourcefactory.bukkit.BukkitPluginYaml
import xyz.jpenilla.resourcefactory.bukkit.bukkitPluginYaml

private const val PLUGIN_EXTENSION_NAME: String = "plugin"
private const val SOURCE_SET_NAME: String = "main" // Make it configurable, maybe?
internal const val PLUGIN_YML: String = "plugin.yml"

internal fun Project.configurePluginYamlFeature(bukkit: BukkitExtension) {
    project.apply<ResourceFactoryPlugin>()

    val bukkitPluginYaml = bukkitPluginYaml {
        setConventionsFromProjectMeta(project, bukkit)
    }
    (bukkit as ExtensionAware).extensions.add(PLUGIN_EXTENSION_NAME, bukkitPluginYaml)

    val mainSourceSet = sourceSets.named(SOURCE_SET_NAME)
    val sourcePluginYaml = findPluginYaml(mainSourceSet)
    mainSourceSet.configure {
        resourceFactory.factory(bukkitPluginYaml.resourceFactory())

        // Package only one plugin.yml: the generated one, or the source one if the generation is disabled.
        // The generated plugin.yml may be left from previous builds even if the generation is disabled.
        val sourcePluginYamlFile by lazy { sourcePluginYaml.orNull?.asFile }
        resources.exclude { element ->
            if (element.path != PLUGIN_YML) return@exclude false
            val isSource = element.file == sourcePluginYamlFile
            if (bukkit.generatePluginYaml.get()) isSource else !isSource
        }
    }

    tasks.named<ExecuteResourceFactories>("${SOURCE_SET_NAME}ResourceFactory") {
        val generatePluginYaml = bukkit.generatePluginYaml.finalizeAndGet()
        onlyIf("Flag generatePluginYaml is enabled") { generatePluginYaml }
        if (!generatePluginYaml) return@named

        val sourcePluginYamlFile = sourcePluginYaml.orNull ?: return@named
        // Don't use asText as it uses the default charset, but plugin.yml is always in UTF-8
        val content = providers.fileContents(sourcePluginYamlFile).asBytes.get()
        bukkitPluginYaml.applyConventionsFromSource(content.decodeToString())
    }
}

private fun Project.findPluginYaml(sourceSetProvider: Provider<SourceSet>): Provider<RegularFile> {
    val buildDirectory = layout.buildDirectory
    val fileProvider = sourceSetProvider.mapNotNull { sourceSet ->
        sourceSet.resources.sourceDirectories
            // Skip generated resources, otherwise the previously generated plugin.yml would be used as defaults
            .filterNot { it.startsWith(buildDirectory.get().asFile) }
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
    main.convention(name.mapNotNull {
        if (project.group.toString().isEmpty()) return@mapNotNull null
        "${project.group}.${StringUtils.toPascalCase(it)}" }
    )
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
