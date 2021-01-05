package ru.endlesscode.bukkitgradle

import ru.endlesscode.bukkitgradle.meta.extension.PluginMeta
import ru.endlesscode.bukkitgradle.server.extension.RunConfiguration

public interface Bukkit {

    /** Plugin meta. */
    public val meta: PluginMeta

    /** Dev server run configuration. */
    public val run: RunConfiguration

    /** Bukkit version. */
    public val version: String

    /** Bukkit version in format "[version]-R0.1-SNAPSHOT". */
    public val fullVersion: String
}
