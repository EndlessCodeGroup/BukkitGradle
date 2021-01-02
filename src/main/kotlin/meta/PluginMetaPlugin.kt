package ru.endlesscode.bukkitgradle.meta

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.CopySpec
import org.gradle.kotlin.dsl.named
import org.gradle.kotlin.dsl.register
import ru.endlesscode.bukkitgradle.meta.task.GenerateMeta

public class PluginMetaPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        val generatePluginMeta = project.tasks.register<GenerateMeta>("generatePluginMeta", MetaFile(project))
        project.tasks.named<CopySpec>("processResources").configure {
            from(generatePluginMeta.map { it.target })
        }
    }
}
