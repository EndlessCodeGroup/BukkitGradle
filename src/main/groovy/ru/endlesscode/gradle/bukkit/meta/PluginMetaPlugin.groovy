package ru.endlesscode.gradle.bukkit.meta

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.CopySpec
import ru.endlesscode.gradle.bukkit.BukkitPluginExtension

class PluginMetaPlugin implements Plugin<Project> {
    @Override
    void apply(Project project) {
        project.with {
            extensions.create(BukkitPluginExtension.NAME, BukkitPluginExtension, project)

            GenerateMeta genMeta = task("generatePluginMeta", type: GenerateMeta) {
                metaFile new MetaFile(project)
            } as GenerateMeta

            tasks.processResources.dependsOn genMeta
            ((CopySpec) tasks.processResources).from genMeta.target.toFile()
        }
    }
}
