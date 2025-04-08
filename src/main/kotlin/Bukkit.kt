package ru.endlesscode.bukkitgradle

import org.gradle.api.provider.Provider
import ru.endlesscode.bukkitgradle.meta.extension.PluginMeta
import ru.endlesscode.bukkitgradle.server.extension.ServerConfiguration

public interface Bukkit {

    /** Plugin meta. */
    public val meta: PluginMeta

    /** Dev server configuration. */
    public val server: ServerConfiguration

    /** Bukkit version. */
    public val apiVersion: Provider<String>

    /** Plugin Meta generation enabled. */
    public val generateMeta: Provider<Boolean>
}
