package ru.endlesscode.bukkitgradle.meta

import com.charleskorn.kaml.SequenceStyle
import com.charleskorn.kaml.Yaml
import com.charleskorn.kaml.YamlConfiguration
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.CopySpec
import org.gradle.api.plugins.JavaPluginConvention
import org.gradle.kotlin.dsl.get
import org.gradle.kotlin.dsl.getPlugin
import org.gradle.kotlin.dsl.named
import org.gradle.kotlin.dsl.register
import ru.endlesscode.bukkitgradle.bukkit
import ru.endlesscode.bukkitgradle.meta.extension.PluginMetaImpl
import ru.endlesscode.bukkitgradle.meta.task.MergePluginMeta
import ru.endlesscode.bukkitgradle.meta.task.ParsePluginMetaFile
import java.io.File

public class PluginMetaPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        val yaml = Yaml(
            configuration = YamlConfiguration(
                encodeDefaults = false,
                sequenceStyle = SequenceStyle.Flow
            )
        )
        val metaFile = project.findMetaFile()

        val parsePluginMeta = project.tasks.register<ParsePluginMetaFile>("parsePluginMetaFile") {
            this.yaml = yaml
            this.meta = project.bukkit.meta as PluginMetaImpl
            this.metaFile.set(metaFile)
        }

        val mergePluginMeta = project.tasks.register<MergePluginMeta>("mergePluginMeta") {
            this.yaml = yaml
            meta = project.bukkit.meta
            metaYaml.set(parsePluginMeta.map { it.pluginMetaYaml.get() })
            dependsOn(parsePluginMeta)
        }

        project.tasks.named<CopySpec>("processResources").configure {
            from(mergePluginMeta.map { it.target })
        }
    }

    /** Finds and returns project metaFile if it exists. */
    private fun Project.findMetaFile(): File? {
        val javaPlugin = convention.getPlugin<JavaPluginConvention>()
        val mainSourceSet = javaPlugin.sourceSets["main"]
        val resourceDir = mainSourceSet.resources.srcDirs.first()

        return File(resourceDir, FILE_NAME).takeIf { it.isFile }
    }

    internal companion object {
        const val FILE_NAME: String = "plugin.yml"
    }
}
