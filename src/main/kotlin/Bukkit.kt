package ru.endlesscode.bukkitgradle

import org.gradle.api.provider.Provider
import ru.endlesscode.bukkitgradle.plugin.extension.PluginConfiguration
import ru.endlesscode.bukkitgradle.server.extension.ServerConfiguration

public interface Bukkit {

    /** Plugin plugin. */
    public val plugin: PluginConfiguration

    @Deprecated("Use 'plugin' field instead", ReplaceWith("plugin"))
    public val meta: PluginConfiguration
        get() = plugin

    /** Dev server configuration. */
    public val server: ServerConfiguration

    /** Bukkit version. */
    public val apiVersion: Provider<String>

    /** Plugin Meta generation enabled. */
    @Deprecated("Use 'plugin.generatePluginYaml' instead", ReplaceWith("plugin.generatePluginYaml"))
    public val generateMeta: Provider<Boolean>
        get() = plugin.generatePluginYaml
}
