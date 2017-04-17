package ru.endlesscode.gradle.bukkit

import org.gradle.api.Project
import ru.endlesscode.gradle.bukkit.meta.PluginMeta

class BukkitGradlePluginExtension {
    public static final String NAME = "bukkit"

    private static final String LATEST_VERSION = "+"
    private static final String REVISION_SUFFIX = "-R0.1-SNAPSHOT"

    private final project

    String version
    final PluginMeta meta

    BukkitGradlePluginExtension(Project project) {
        this.project = project
        this.meta = new PluginMeta(project)
    }

    /**
     * Returns chosen Bukkit version in format:
     * "<version>-R0.1-SNAPSHOT"
     * If version not assigned returns latest version
     *
     * @return Chosen Bukkit version
     */
    String getVersion() {
        return version ? "$version$REVISION_SUFFIX" : LATEST_VERSION
    }

    void meta(@DelegatesTo(PluginMeta) Closure<?> closure) {
        project.configure(meta, closure)
    }
}
