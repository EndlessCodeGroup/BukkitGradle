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

            def genMeta = task("generatePluginMeta", type: GenerateMeta) {
                metaFile new MetaFile(project)
            } as GenerateMeta

            tasks.processResources.dependsOn genMeta
            (tasks.processResources as CopySpec).from genMeta.target.toFile()
        }
    }
}
