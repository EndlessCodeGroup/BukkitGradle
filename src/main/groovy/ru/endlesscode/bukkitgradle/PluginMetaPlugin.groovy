package ru.endlesscode.bukkitgradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.CopySpec
import org.gradle.api.tasks.TaskProvider
import ru.endlesscode.bukkitgradle.meta.MetaFile
import ru.endlesscode.bukkitgradle.meta.task.GenerateMeta

class PluginMetaPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        def genMeta = project.tasks.register("generatePluginMeta", GenerateMeta) {
            metaFile.set(project.provider { new MetaFile(project) })
        } as TaskProvider<GenerateMeta>

        genMeta.configure {
            group = BukkitGradlePlugin.GROUP
            description = 'Generate plugin.yml file'
        }

        project.tasks.named("processResources").configure { CopySpec copySpec ->
            copySpec.from(genMeta.get().target)
        }
    }
}
