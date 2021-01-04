package ru.endlesscode.bukkitgradle.meta.task

import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.Nested
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import ru.endlesscode.bukkitgradle.TASKS_GROUP_BUKKIT
import ru.endlesscode.bukkitgradle.meta.MetaFile
import java.io.File
import javax.inject.Inject

/**
 * Task that generates plugin.yml for bukkit plugin.
 *
 * @see ru.endlesscode.bukkitgradle.meta.PluginMetaPlugin
 */
public open class GenerateMeta @Inject internal constructor(
    @get:Nested internal val metaFile: MetaFile
) : DefaultTask() {

    @OutputFile
    public val target: RegularFileProperty = project.objects.fileProperty()

    init {
        group = TASKS_GROUP_BUKKIT
        description = "Generate plugin.yml file"

        val defaultTargetProvider = project.provider { File(temporaryDir, MetaFile.NAME) }
        target.convention(project.layout.file(defaultTargetProvider))
    }

    /** Writes meta to target file */
    @TaskAction
    public fun generateMeta() {
        metaFile.writeTo(target.asFile.get())
    }
}
