package ru.endlesscode.bukkitgradle.meta.task

import com.charleskorn.kaml.Yaml
import kotlinx.serialization.encodeToString
import meta.PluginMetaYaml
import org.gradle.api.DefaultTask
import org.gradle.api.file.ProjectLayout
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.ProviderFactory
import org.gradle.api.tasks.*
import ru.endlesscode.bukkitgradle.TASKS_GROUP_BUKKIT
import ru.endlesscode.bukkitgradle.meta.PluginMetaPlugin
import ru.endlesscode.bukkitgradle.meta.extension.PluginMeta
import java.io.File
import javax.inject.Inject

/**
 * Task that generates plugin.yml for bukkit plugin.
 *
 * @see ru.endlesscode.bukkitgradle.meta.PluginMetaPlugin
 */
public abstract class MergePluginMeta @Inject internal constructor(
    projectLayout: ProjectLayout,
    providers: ProviderFactory
): DefaultTask() {

    @get:Internal
    internal lateinit var yaml: Yaml

    @get:Nested
    internal lateinit var meta: PluginMeta

    @get:Input
    internal abstract val metaYaml: Property<PluginMetaYaml>

    @get:OutputFile
    public abstract val target: RegularFileProperty

    init {
        group = TASKS_GROUP_BUKKIT
        description = "Generate plugin.yml file"

        val defaultTargetProvider = providers.provider { File(temporaryDir, PluginMetaPlugin.FILE_NAME) }
        target.convention(projectLayout.file(defaultTargetProvider))
    }

    /** Writes meta to target file */
    @TaskAction
    public fun mergePluginMeta() {
        val mergedMeta = metaYaml.get().copy(
            main = meta.main.get(),
            name = meta.name.get(),
            description = meta.description.orNull,
            version = meta.version.get(),
            apiVersion = meta.apiVersion.orNull,
            website = meta.url.orNull,
            authors = meta.authors.get().takeIf { it.isNotEmpty() }
        )

        target.get()
            .asFile
            .writeText(yaml.encodeToString(mergedMeta))
    }
}
