package ru.endlesscode.bukkitgradle.meta

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.CopySpec
import ru.endlesscode.bukkitgradle.extension.Bukkit

class PluginMetaPlugin implements Plugin<Project> {
    @Override
    void apply(Project project) {
        project.with {
            extensions.create(Bukkit.NAME, Bukkit, project)

            GenerateMeta genMeta = task("generatePluginMeta", type: GenerateMeta) {
                metaFile new MetaFile(project)
            } as GenerateMeta

            tasks.processResources.dependsOn genMeta
            ((CopySpec) tasks.processResources).from genMeta.target.toFile()
        }
    }
}
