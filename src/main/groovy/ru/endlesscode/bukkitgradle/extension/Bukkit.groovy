package ru.endlesscode.bukkitgradle.extension

import org.gradle.api.Project
import ru.endlesscode.bukkitgradle.meta.PluginMeta

class Bukkit {
    public static final String NAME = "bukkit"
    public static final String LATEST = "latest"
    public static final String REVISION_SUFFIX = "-R0.1-SNAPSHOT"

    private final Project project

    String version
    String buildtools = ''

    final PluginMeta meta
    final RunConfiguration run

    Bukkit(Project project) {
        this.project = project
        this.meta = new PluginMeta(project)
        this.run = new RunConfiguration(project)
    }

    /**
     * Returns chosen Bukkit version in format:
     * "<version>-R0.1-SNAPSHOT"
     * If version not assigned returns dynamic latest version
     *
     * @return Chosen Bukkit version
     */
    String getVersion() {
        return version ? "$version$REVISION_SUFFIX" : LATEST
    }

    String getDependencyVersion() {
        return getVersion().replace(LATEST, '+')
    }

    void meta(@DelegatesTo(PluginMeta) Closure<?> closure) {
        project.configure(meta, closure)
    }

    void run(@DelegatesTo(RunConfiguration) Closure<?> closure) {
        project.configure(run, closure)
    }
}
