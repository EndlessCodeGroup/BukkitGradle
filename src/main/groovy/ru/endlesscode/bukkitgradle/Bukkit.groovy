package ru.endlesscode.bukkitgradle

import org.gradle.util.ConfigureUtil
import ru.endlesscode.bukkitgradle.meta.extension.PluginMeta
import ru.endlesscode.bukkitgradle.server.extension.RunConfiguration

class Bukkit {
    public static final String NAME = "bukkit"
    public static final String LATEST = "+"
    public static final String REVISION_SUFFIX = "-R0.1-SNAPSHOT"

    String version

    final PluginMeta meta
    final RunConfiguration run

    Bukkit(PluginMeta meta, RunConfiguration run) {
        this.meta = meta
        this.run = run
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

    void meta(@DelegatesTo(PluginMeta) Closure<?> closure) {
        ConfigureUtil.configure(closure, meta)
    }

    void run(@DelegatesTo(RunConfiguration) Closure<?> closure) {
        ConfigureUtil.configure(closure, run)
    }
}
