package ru.endlesscode.bukkitgradle.plugin

import com.charleskorn.kaml.SequenceStyle
import com.charleskorn.kaml.Yaml
import com.charleskorn.kaml.YamlConfiguration
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.CopySpec
import org.gradle.api.file.DuplicatesStrategy
import org.gradle.kotlin.dsl.get
import org.gradle.kotlin.dsl.named
import org.gradle.kotlin.dsl.register
import ru.endlesscode.bukkitgradle.extensions.java
import ru.endlesscode.bukkitgradle.plugin.task.MergePluginYaml
import ru.endlesscode.bukkitgradle.plugin.task.ParsePluginYaml
import java.io.File

public class PluginConfigurationPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        val yaml = Yaml(
            configuration = YamlConfiguration(
                encodeDefaults = false,
                sequenceStyle = SequenceStyle.Flow
            )
        )

        val parsePluginYaml = project.tasks.register<ParsePluginYaml>("parsePluginYaml") {
            this.yaml = yaml
            //val bukkit = project.bukkit
            //this.plugin = bukkit.plugin as PluginConfigurationImpl
            this.pluginYamlFile.set(project.findPluginYaml())

            //val predicate = bukkit.plugin.generatePluginYaml
            //onlyIf { predicate.get() }
        }

        val mergePluginYaml = project.tasks.register<MergePluginYaml>("mergePluginYaml") {
            this.yaml = yaml
            //val bukkit = project.bukkit
            //this.plugin = bukkit.plugin
            pluginYaml.set(parsePluginYaml.map { it.pluginYaml.get() })

            //val predicate = bukkit.plugin.generatePluginYaml
            //onlyIf { predicate.get() }
        }

        project.tasks.named<CopySpec>("processResources") {
            from(mergePluginYaml)
            duplicatesStrategy = DuplicatesStrategy.INCLUDE
        }
    }

    /** Finds and returns project's plugin.yaml if it exists. */
    private fun Project.findPluginYaml(): File? {
        val mainSourceSet = java.sourceSets["main"]
        val resourceDir = mainSourceSet.resources.srcDirs.first()

        return File(resourceDir, FILE_NAME).takeIf { it.isFile }
    }

    internal companion object {
        const val FILE_NAME: String = "plugin.yml"
    }
}
