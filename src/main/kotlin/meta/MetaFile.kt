package ru.endlesscode.bukkitgradle.meta

import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginConvention
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.Nested
import org.gradle.api.tasks.Optional
import org.gradle.kotlin.dsl.get
import org.gradle.kotlin.dsl.getPlugin
import ru.endlesscode.bukkitgradle.bukkit
import ru.endlesscode.bukkitgradle.meta.extension.PluginMeta
import ru.endlesscode.bukkitgradle.meta.extension.items
import java.io.File

internal class MetaFile(
    @Nested val meta: PluginMeta,
    file: File
) {

    @Optional
    @InputFile
    val metaFile: File? = if (file.exists()) file else null

    constructor(project: Project) : this(project.bukkit.meta, project.findMetaFile())

    /** Validates and writes meta and static lines to [target] file. */
    fun writeTo(target: File) {
        val extraLines = filterMetaLines()
        val metaLines = generateMetaLines()

        target.writeLines(metaLines + extraLines)
    }

    /**
     * Removes all meta lines from metaFile, and saves extra lines to list.
     * If metaFile not exists only clears extra lines.
     */
    private fun filterMetaLines(): List<String> {
        if (metaFile == null || !metaFile.exists()) return emptyList()

        val extraLines = metaFile.readLines().asSequence()
            .mapNotNull { line ->
                if (!isMetaLine(line)) {
                    line
                } else {
                    null
                }
            }
            .dropWhile(String::isBlank)
            .toList()

        metaFile.writeLines(extraLines)
        return extraLines
    }

    /** Checks if [line] contains meta attributes. */
    private fun isMetaLine(line: String): Boolean = line.substringBefore(':') in KNOWN_FIELDS

    /** Generates meta lines from meta. */
    private fun generateMetaLines(): List<String> {
        return meta.items.mapNotNull { (id, property) ->
            if (property.isPropertyFilled()) {
                "$id: ${property.get()}"
            } else {
                null
            }
        }
    }

    companion object {
        @JvmStatic
        val NAME: String = "plugin.yml"

        private val KNOWN_FIELDS: Array<String> = arrayOf(
            "name", "description", "version", "author", "authors", "website", "main"
        )

        /** Finds and returns project metaFile even if it doesn't exist. */
        private fun Project.findMetaFile(): File {
            val javaPlugin = convention.getPlugin<JavaPluginConvention>()
            val mainSourceSet = javaPlugin.sourceSets["main"]
            val resourceDir = mainSourceSet.resources.srcDirs.first()

            return File(resourceDir, NAME)
        }

        private fun Provider<*>.isPropertyFilled(): Boolean {
            return if (this is ListProperty<*>) get().isNotEmpty() else isPresent
        }

        /** Writes [lines] to [this] file. */
        private fun File.writeLines(lines: List<String>) {
            writer().use { writer ->
                for (line in lines) writer.appendln(line)
            }
        }
    }
}
