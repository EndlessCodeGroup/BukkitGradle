package ru.endlesscode.bukkitgradle.meta

import com.charleskorn.kaml.SequenceStyle
import com.charleskorn.kaml.Yaml
import com.charleskorn.kaml.YamlConfiguration
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import meta.PluginMetaYaml
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginConvention
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.Nested
import org.gradle.api.tasks.Optional
import org.gradle.kotlin.dsl.get
import org.gradle.kotlin.dsl.getPlugin
import ru.endlesscode.bukkitgradle.bukkit
import ru.endlesscode.bukkitgradle.meta.extension.PluginMetaImpl
import java.io.File

internal class MetaFile(
    @Nested val meta: PluginMetaImpl,
    file: File
) {

    @Optional
    @InputFile
    val metaFile: File? = if (file.exists()) file else null

    private val yaml = Yaml(
        configuration = YamlConfiguration(
            encodeDefaults = false,
            sequenceStyle = SequenceStyle.Flow
        )
    )

    constructor(project: Project) : this(project.bukkit.meta as PluginMetaImpl, project.findMetaFile())

    /** Validates and writes meta and static lines to [target] file. */
    fun writeTo(target: File) {
        val metaYaml = fillMeta(readMetaFromFile())
        target.writeText(yaml.encodeToString(metaYaml))
    }

    /** Reads meta from metaFile and adds conventions for specified fields. */
    private fun readMetaFromFile(): PluginMetaYaml {
        if (metaFile == null || !metaFile.exists()) return PluginMetaYaml()

        val text = metaFile.readText()
        if (text.isBlank()) return PluginMetaYaml()

        return yaml.decodeFromString<PluginMetaYaml>(text).apply {
            main?.let(meta.main::convention)
            name?.let(meta.name::convention)
            description?.let(meta.description::convention)
            version?.let(meta.version::convention)
            website?.let(meta.url::convention)
            authors?.let(meta.authors::convention)
        }
    }

    /** Adds configured meta lines from meta. */
    private fun fillMeta(metaYaml: PluginMetaYaml): PluginMetaYaml {
        return metaYaml.copy(
            main = meta.main.get(),
            name = meta.name.get(),
            description = meta.description.orNull,
            version = meta.version.get(),
            website = meta.url.orNull,
            authors = meta.authors.get().takeIf { it.isNotEmpty() }
        )
    }

    companion object {
        @JvmStatic
        val NAME: String = "plugin.yml"

        /** Finds and returns project metaFile even if it doesn't exist. */
        private fun Project.findMetaFile(): File {
            val javaPlugin = convention.getPlugin<JavaPluginConvention>()
            val mainSourceSet = javaPlugin.sourceSets["main"]
            val resourceDir = mainSourceSet.resources.srcDirs.first()

            return File(resourceDir, NAME)
        }
    }
}
