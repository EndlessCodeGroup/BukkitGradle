package ru.endlesscode.bukkitgradle.meta.task

import com.charleskorn.kaml.Yaml
import kotlinx.serialization.encodeToString
import meta.PluginMetaYaml
import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.gradle.api.tasks.*
import org.gradle.kotlin.dsl.property
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
public open class MergePluginMeta @Inject internal constructor(
    objects: ObjectFactory
) : DefaultTask() {

    @get:Internal
    internal lateinit var yaml: Yaml

    @get:Nested
    internal lateinit var meta: PluginMeta

    @get:Input
    internal val metaYaml: Property<PluginMetaYaml> = objects.property()

    @OutputFile
    public val target: RegularFileProperty = objects.fileProperty()

    init {
        group = TASKS_GROUP_BUKKIT
        description = "Generate plugin.yml file"

        val defaultTargetProvider = project.provider { File(temporaryDir, PluginMetaPlugin.FILE_NAME) }
        target.convention(project.layout.file(defaultTargetProvider))
    }

    /** Writes meta to target file */
    @TaskAction
    public fun mergePluginMeta() {
        val mergedMeta = metaYaml.get().copy(
            main = meta.main.get(),
            name = meta.name.get(),
            description = meta.description.orNull,
            version = meta.version.get(),
            website = meta.url.orNull,
            authors = meta.authors.get().takeIf { it.isNotEmpty() }
        )

        target.get()
            .asFile
            .writeText(yaml.encodeToString(mergedMeta))
    }
}
