package ru.endlesscode.gradle.bukkit.meta

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.CopySpec
import org.gradle.api.plugins.JavaPluginConvention
import ru.endlesscode.gradle.bukkit.BukkitPluginExtension

class PluginMetaPlugin implements Plugin<Project> {
    public static final String META_FILE = "plugin.yml"

    private static final String[] META_ATTRIBUTES = [
            "name", "description", "version", "author", "authors", "website", "main"
    ]

    @Override
    void apply(Project project) {
        project.with {
            extensions.create(BukkitPluginExtension.NAME, BukkitPluginExtension, project)
            processMetaFile(project)

            GenerateMeta genMeta = task("generatePluginMeta", type: GenerateMeta) {
                meta = bukkit.meta
            } as GenerateMeta

            tasks.processResources.dependsOn genMeta
            ((CopySpec) tasks.processResources).from genMeta.target.toFile()
        }
    }

    static def processMetaFile(Project project) {
        File metaFile = getMetaFile(project)

        if (metaFile.exists()) {
            removeAllMeta(metaFile)
        }
    }

    static File getMetaFile(Project project) {
        def java = project.convention.getPlugin(JavaPluginConvention)
        def resourceDir = java.sourceSets.main.resources.srcDirs[0]
        def metaFile = new File(resourceDir, META_FILE)

        return metaFile
    }

    static def removeAllMeta(File file) {
        StringWriter noMetaWriter = new StringWriter()
        boolean firstLine = true
        file.filterLine(noMetaWriter) { String line ->
            if (!isMetaLine(line) && (!line.isEmpty() || !firstLine)) {
                firstLine = false
                return true
            }

            return false
        }

        file.withWriter {
            it.write(noMetaWriter.toString())
        }
    }

    static boolean isMetaLine(String line) {
        for (String attribute in META_ATTRIBUTES) {
            if (line.startsWith("$attribute:")) {
                return true
            }
        }

        return false
    }
}
