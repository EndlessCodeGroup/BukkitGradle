package ru.endlesscode.bukkitgradle.meta

import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginConvention
import ru.endlesscode.bukkitgradle.meta.extension.MetaItem
import ru.endlesscode.bukkitgradle.meta.extension.PluginMeta

import java.nio.file.Files
import java.nio.file.Path

class MetaFile {
    public static final String NAME = "plugin.yml"

    private static final String[] KNOWN_FIELDS = [
            "name", "description", "version", "author", "authors", "website", "main"
    ]

    final List<String> metaLines = []
    final List<String> staticLines = []

    private final PluginMeta meta
    private final Path metaFile

    MetaFile(Project project) {
        this(project.bukkit.meta as PluginMeta, findMetaFile(project))
    }

    MetaFile(PluginMeta meta, Path file) {
        this.meta = meta
        this.metaFile = file

        this.filterMetaLines()
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
        this.validateMeta()
        this.filterMetaLines()
        this.generateMetaLines()

        writeLinesTo(target, metaLines, staticLines)
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
     * Removes all meta lines from metaFile, and saves static lines to
     * list. If metaFile not exists only clears staticLines
     */
    private void filterMetaLines() {
        staticLines.clear()
        if (Files.notExists(metaFile)) {
            return
        }

        metaFile.eachLine { line ->
            if (isStaticLine(line)) {
                staticLines << line
            }

            return
        }

        writeLinesTo(metaFile, staticLines)
    }

    /**
     * Checks if line isn't dynamic meta or if line empty it isn't first
     *
     * @param line The line to check
     * @return true if line is static
     */
    private boolean isStaticLine(String line) {
        return !isMetaLine(line) && (!line.empty || !staticLines.empty)
    }

    /**
     * Checks if line contains meta attributes
     *
     * @param line The line to check
     * @return true if line is dynamic meta, otherwise false
     */
    private static boolean isMetaLine(String line) {
        for (String field in KNOWN_FIELDS) {
            if (line.startsWith("$field:")) {
                return true
            }
        }

        return false
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
            lineLists.each { lineList ->
                lineList.each { line -> writer.println line }
            }
        }
    }
}
