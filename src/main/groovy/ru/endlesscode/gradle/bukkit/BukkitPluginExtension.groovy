package ru.endlesscode.gradle.bukkit

import org.gradle.api.Project
import ru.endlesscode.gradle.bukkit.meta.PluginMeta

class BukkitPluginExtension {
    public static final String NAME = "bukkit"

    private static final String LATEST_VERSION = "+"
    private static final String VERSION_SUFFIX = "-R0.1-SNAPSHOT"

    private final project

    String version
    final PluginMeta meta

    BukkitPluginExtension(Project project) {
        this.project = project
        this.meta = new PluginMeta(project)
    }

    String getVersion() {
        return version ? "$version$VERSION_SUFFIX" : LATEST_VERSION
    }

    void meta(@DelegatesTo(PluginMeta) Closure<?> closure) {
        project.configure(meta, closure)
    }
}
