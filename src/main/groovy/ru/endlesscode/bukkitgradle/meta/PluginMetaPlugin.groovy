package ru.endlesscode.bukkitgradle.meta

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.CopySpec
import ru.endlesscode.bukkitgradle.BukkitGradlePlugin
import ru.endlesscode.bukkitgradle.extension.Bukkit
import ru.endlesscode.bukkitgradle.task.GenerateMeta

class PluginMetaPlugin implements Plugin<Project> {
    @Override
    void apply(Project project) {
        project.with {
            extensions.create(Bukkit.NAME, Bukkit, project)

            def genMeta = task("generatePluginMeta", type: GenerateMeta) {
                metaFile new MetaFile(project)
            } as GenerateMeta

            genMeta.configure {
                group = BukkitGradlePlugin.GROUP
                description = 'Generate plugin.yml file'
            }

            tasks.processResources.dependsOn genMeta
            (tasks.processResources as CopySpec).from genMeta.target.toFile()
        }
    }
}
