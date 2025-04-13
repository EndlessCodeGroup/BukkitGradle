package ru.endlesscode.bukkitgradle.plugin.task

import com.charleskorn.kaml.Yaml
import kotlinx.serialization.encodeToString
import org.gradle.api.DefaultTask
import org.gradle.api.file.ProjectLayout
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.ProviderFactory
import org.gradle.api.tasks.*
import ru.endlesscode.bukkitgradle.TASKS_GROUP_BUKKIT
import ru.endlesscode.bukkitgradle.plugin.PluginConfigurationPlugin
import ru.endlesscode.bukkitgradle.plugin.PluginYaml
import ru.endlesscode.bukkitgradle.plugin.extension.PluginConfiguration
import java.io.File
import javax.inject.Inject

/**
 * Task that generates plugin.yml for bukkit plugin.
 *
 * @see ru.endlesscode.bukkitgradle.plugin.PluginConfigurationPlugin
 */
public abstract class MergePluginYaml @Inject internal constructor(
    projectLayout: ProjectLayout,
    providers: ProviderFactory
): DefaultTask() {

    @get:Internal
    internal lateinit var yaml: Yaml

    @get:Nested
    internal lateinit var plugin: PluginConfiguration

    @get:Input
    internal abstract val pluginYaml: Property<PluginYaml>

    @get:OutputFile
    public abstract val target: RegularFileProperty

    init {
        group = TASKS_GROUP_BUKKIT
        description = "Generate plugin.yml file"

        val defaultTargetProvider = providers.provider { File(temporaryDir, PluginConfigurationPlugin.FILE_NAME) }
        target.convention(projectLayout.file(defaultTargetProvider))
    }

    /** Writes plugin to a target file */
    @TaskAction
    public fun mergePluginYaml() {
        val merged = pluginYaml.get().copy(
            main = plugin.main.get(),
            name = plugin.name.get(),
            description = plugin.description.orNull,
            version = plugin.version.get(),
            apiVersion = plugin.apiVersion.orNull?.takeIf { it.isNotEmpty() },
            website = plugin.url.orNull,
            authors = plugin.authors.get().takeIf { it.isNotEmpty() }
        )

        target.get()
            .asFile
            .writeText(yaml.encodeToString(merged))
    }
}
