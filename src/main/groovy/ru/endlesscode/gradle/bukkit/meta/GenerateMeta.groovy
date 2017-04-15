package ru.endlesscode.gradle.bukkit.meta

import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.tasks.TaskAction

class GenerateMeta extends DefaultTask {
    PluginMeta meta

    @TaskAction
    def generateMeta() {
        List<String> lines = []
        File file = PluginMetaPlugin.createMetaFileMaybe(project)
        PluginMetaPlugin.removeAllMeta(file)
        print file.path

        def metaItems = meta.items
        validateMeta(metaItems)

        lines.addAll(convertToYaml(metaItems))
        lines.addAll(file.readLines())

        file.withWriter { Writer writer ->
            lines.each { String line ->
                writer.write("$line\n")
            }
        }
    }

    private static def validateMeta(List<MetaItem> metaItems) {
        for (MetaItem item in metaItems) {
            if (item.required && !metaItems.value) {
                throw new GradleException("Plugin metadata parse error: '$item.id' must not be null")
            }
        }
    }

    private static List<String> convertToYaml(List<MetaItem> metaItems) {
        List<String> yaml = []
        metaItems.each { MetaItem item ->
            if (item.value != null) {
                yaml.add(item.entry)
            }
        }

        return yaml
    }
}
