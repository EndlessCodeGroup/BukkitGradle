package ru.endlesscode.gradle.bukkit.extension

import org.gradle.api.Project
import ru.endlesscode.gradle.bukkit.meta.PluginMeta

class Bukkit {
    public static final String NAME = "bukkit"
    public static final String DYNAMIC_LATEST = "+"

    private static final String REVISION_SUFFIX = "-R0.1-SNAPSHOT"

    private final Project project

    String version
    final PluginMeta meta

    Bukkit(Project project) {
        this.project = project
        this.meta = new PluginMeta(project)
    }

    /**
     * Returns chosen Bukkit version in format:
     * "<version>-R0.1-SNAPSHOT"
     * If version not assigned returns dynamic latest version
     *
     * @return Chosen Bukkit version
     */
    String getVersion() {
        return version ? "$version$REVISION_SUFFIX" : DYNAMIC_LATEST
    }

    void meta(@DelegatesTo(PluginMeta) Closure<?> closure) {
        project.configure(meta, closure)
    }
}
