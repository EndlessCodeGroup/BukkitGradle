package ru.endlesscode.bukkitgradle

import org.gradle.api.provider.Provider
import ru.endlesscode.bukkitgradle.plugin.plugin
import ru.endlesscode.bukkitgradle.server.extension.ServerConfiguration
import xyz.jpenilla.resourcefactory.bukkit.BukkitPluginYaml

public interface Bukkit {

    @Deprecated("Use 'plugin' field instead", ReplaceWith("plugin"))
    public val meta: BukkitPluginYaml
        get() = plugin

    /** Dev server configuration. */
    public val server: ServerConfiguration

    /** Bukkit version. */
    public val apiVersion: Provider<String>

    /** Plugin Meta generation enabled. */
    @Deprecated("Use 'plugin.generatePluginYaml' instead", ReplaceWith("plugin.generatePluginYaml"))
    public val generateMeta: Provider<Boolean>
        get() = generatePluginYaml

    /** Whether plugin.yml generation enabled or not. */
    public val generatePluginYaml: Provider<Boolean>
}
