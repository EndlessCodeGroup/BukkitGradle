package ru.endlesscode.bukkitgradle.meta

import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginConvention
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.Nested
import org.gradle.api.tasks.Optional
import ru.endlesscode.bukkitgradle.meta.extension.PluginMeta

class MetaFile {
    public static final String NAME = "plugin.yml"

    private static final String[] KNOWN_FIELDS = [
            "name", "description", "version", "author", "authors", "website", "main"
    ]

    private final List<String> metaLines = []
    private final List<String> extraLines = []

    @Nested
    final PluginMeta meta

    @Optional
    @InputFile
    final File metaFile

    MetaFile(Project project) {
        this(project.bukkit.meta as PluginMeta, findMetaFile(project))
    }

    MetaFile(PluginMeta meta, File file) {
        this.meta = meta
        this.metaFile = file?.exists() ? file : null
    }

    /** Finds and returns project metaFile even if it doesn't exist. */
    private static File findMetaFile(Project project) {
        def javaPlugin = project.convention.getPlugin(JavaPluginConvention)
        def mainSourceSet = javaPlugin.sourceSets.main
        def resourceDir = mainSourceSet.resources.srcDirs[0]

        return new File(resourceDir, NAME)
    }

    /**
     * Validates and writes meta and static lines to target file
     * @param target
     */
    void writeTo(File target) {
        filterMetaLines()
        generateMetaLines()

        writeLinesTo(target, metaLines, extraLines)
    }

    /**
     * Removes all meta lines from metaFile, and saves extra lines to
     * list. If metaFile not exists only clears extra lines.
     */
    private void filterMetaLines() {
        extraLines.clear()
        if (!metaFile?.exists()) {
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
        meta.items.each { id, property ->
            if (isPropertyFilled(property)) {
                metaLines << "$id: ${property.get()}".toString()
            }
        }
    }

    private static boolean isPropertyFilled(Provider<?> property) {
        if (property instanceof ListProperty) {
            return !(property as ListProperty<?>).get().isEmpty()
        }

        return property.isPresent()
    }

    /**
     * Writes lines to target file
     *
     * @param target The target to write
     * @param lineLists Lines to write
     */
    private static void writeLinesTo(File target, List<String>... lineLists) {
        target.withWriter("UTF-8") { writer ->
            lineLists.flatten().each { line -> writer.println line }
        }
    }
}
