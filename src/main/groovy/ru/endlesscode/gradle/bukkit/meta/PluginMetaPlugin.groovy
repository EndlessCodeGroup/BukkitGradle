package ru.endlesscode.gradle.bukkit.meta

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.CopySpec
import org.gradle.api.plugins.JavaPluginConvention
import ru.endlesscode.gradle.bukkit.BukkitPluginExtension

class PluginMetaPlugin implements Plugin<Project> {


    @Override
    void apply(Project project) {
        project.with {
            extensions.create(BukkitPluginExtension.NAME, BukkitPluginExtension, project)
            MetaFile meta = new MetaFile(project)

            GenerateMeta genMeta = task("generatePluginMeta", type: GenerateMeta) {
                metaFile meta
            } as GenerateMeta

            tasks.processResources.dependsOn genMeta
            ((CopySpec) tasks.processResources).from genMeta.target.toFile()
        }
    }

    /**
     * Removes meta information from project meta if meta file
     * exists for project
     *
     * @param project The project
     */
    static void processMetaFile(Project project) {
        File metaFile = getMetaFile(project)

        if (metaFile.exists()) {
            removeAllMeta(metaFile)
        }
    }

    /**
     * Gets project meta file even if it doesn't exist
     *
     * @param project The project
     * @return The meta file of project
     */
    static File getMetaFile(Project project) {
        def java = project.convention.getPlugin(JavaPluginConvention)
        def resourceDir = java.sourceSets.main.resources.srcDirs[0]
        def metaFile = new File(resourceDir, MetaFile.META_FILE)

        return metaFile
    }

    /**
     * Removes all meta lines from file, also removes all lead
     * empty lines
     *
     * @param file The file with meta
     */
    static void removeAllMeta(File file) {
        StringWriter tempWriter = new StringWriter()
        boolean firstLine = true
        file.filterLine(tempWriter) { String line ->
            line = line.trim()
            if (!isMetaLine(line) && (!line.isEmpty() || !firstLine)) {
                firstLine = false
                return true
            }

            return false
        }

        file.withWriter {
            it.write(tempWriter.toString())
        }
    }

    /**
     * Checks if line contains meta attributes
     *
     * @param line The line to check
     * @return true if line is meta attribute, otherwise false
     */
    static boolean isMetaLine(String line) {
        for (String attribute in MetaFile.META_ATTRIBUTES) {
            if (line.startsWith("$attribute:")) {
                return true
            }
        }

        return false
    }
}
