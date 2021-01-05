package ru.endlesscode.bukkitgradle

import ru.endlesscode.bukkitgradle.meta.extension.PluginMeta
import ru.endlesscode.bukkitgradle.server.extension.ServerConfiguration

public interface Bukkit {

    /** Plugin meta. */
    public val meta: PluginMeta

    /** Dev server configuration. */
    public val server: ServerConfiguration

    /** Bukkit version. */
    public val apiVersion: String

    /** Bukkit version in format "[apiVersion]-R0.1-SNAPSHOT". */
    public val fullVersion: String
}
