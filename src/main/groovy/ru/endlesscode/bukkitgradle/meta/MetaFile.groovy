package ru.endlesscode.bukkitgradle.meta

import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginConvention
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Nested
import ru.endlesscode.bukkitgradle.meta.extension.MetaItem
import ru.endlesscode.bukkitgradle.meta.extension.PluginMeta

import java.nio.file.Files
import java.nio.file.Path

class MetaFile {
    public static final String NAME = "plugin.yml"

    private static final String[] KNOWN_FIELDS = [
            "name", "description", "version", "author", "authors", "website", "main"
    ]

    private final List<String> metaLines = []
    private final List<String> extraLines = []

    @Nested
    private final PluginMeta meta
    @Input
    private final Path metaFile

    MetaFile(Project project) {
        this(project.bukkit.meta as PluginMeta, findMetaFile(project))
    }

    MetaFile(PluginMeta meta, Path file) {
        this.meta = meta
        this.metaFile = file
    }

    /**
     * Finds and returns project metaFile even if it doesn't exist
     *
     * @return The File
     */
    private static Path findMetaFile(Project project) {
        def javaPlugin = project.convention.getPlugin(JavaPluginConvention)
        def mainSourceSet = javaPlugin.sourceSets.main
        def resourceDir = mainSourceSet.resources.srcDirs[0].toPath()

        return resourceDir.resolve(NAME)
    }

    /**
     * Validates and writes meta and static lines to target file
     * @param target
     */
    void writeTo(Path target) {
        validateMeta()
        filterMetaLines()
        generateMetaLines()

        writeLinesTo(target, metaLines, extraLines)
    }

    /**
     * Validates that meta contains all required fields
     * If it isn't throw GradleException
     *
     * @param metaItems List of MetaItem
     */
    private validateMeta() {
        for (MetaItem item in meta.items) {
            if (!item.valid) {
                throw new GradleException("Plugin metadata parse error: '$item.id' must not be null")
            }
        }
    }

    /**
     * Removes all meta lines from metaFile, and saves extra lines to
     * list. If metaFile not exists only clears extra lines.
     */
    private void filterMetaLines() {
        extraLines.clear()
        if (Files.notExists(metaFile)) {
            return
        }

        metaFile.eachLine("UTF-8") { line ->
            if (isExtraLine(line)) {
                extraLines << line
            }

            return
        }

        writeLinesTo(metaFile, extraLines)
    }

    /**
     * Checks if line isn't dynamic meta or if line empty it isn't first
     *
     * @param line The line to check
     * @return true if line is static
     */
    private boolean isExtraLine(String line) {
        return !(line.empty && extraLines.empty) && !isMetaLine(line)
    }

    /**
     * Checks if line contains meta attributes
     *
     * @param line The line to check
     * @return true if line is dynamic meta, otherwise false
     */
    private static boolean isMetaLine(String line) {
        return KNOWN_FIELDS.any { line.startsWith("$it:") }
    }

    /**
     * Generates meta lines from meta
     */
    private void generateMetaLines() {
        metaLines.clear()
        meta.items.each { item ->
            if (item.value != null) {
                metaLines << item.entry
            }
        }
    }

    /**
     * Writes lines to target file
     *
     * @param target The target to write
     * @param lineLists Lines to write
     */
    private static void writeLinesTo(Path target, List<String>... lineLists) {
        target.withWriter("UTF-8") { writer ->
            lineLists.flatten().each { line -> writer.println line }
        }
    }
}
