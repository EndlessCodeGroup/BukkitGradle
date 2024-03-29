package ru.endlesscode.bukkitgradle

import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.kotlin.dsl.getByType
import org.slf4j.LoggerFactory
import ru.endlesscode.bukkitgradle.extensions.warnSyntaxChanged
import ru.endlesscode.bukkitgradle.meta.extension.PluginMetaImpl
import ru.endlesscode.bukkitgradle.server.ServerConstants
import ru.endlesscode.bukkitgradle.server.extension.ServerConfigurationImpl

// TODO 1.0: Remove deprecated fields on release
public open class BukkitExtension(
    public final override val meta: PluginMetaImpl,
    public final override val server: ServerConfigurationImpl
) : Bukkit {

    public final override var apiVersion: String = ServerConstants.DEFAULT_VERSION

    public final override var generateMeta: Boolean = true
        private set

    private val logger = LoggerFactory.getLogger("BukkitExtension")

    @Deprecated("Use 'server' instead", ReplaceWith("server"))
    public val run: ServerConfigurationImpl
        get() {
            logger.warnSyntaxChanged("bukkit.run", "bukkit.server")
            return server
        }

    @Deprecated("Use 'server { ... }' instead", ReplaceWith("server(body)"))
    public fun run(body: Action<ServerConfigurationImpl>) {
        logger.warnSyntaxChanged("bukkit.run { ... }", "bukkit.server { ... }")
        server(body)
    }

    public fun server(body: Action<ServerConfigurationImpl>) {
        body.execute(server)
    }

    public fun meta(body: Action<PluginMetaImpl>) {
        body.execute(meta)
    }

    @Deprecated("Use apiVersion instead of version.", ReplaceWith("apiVersion = version"))
    public fun setVersion(version: String) {
        logger.warnSyntaxChanged("bukkit.version = '...'", "bukkit.apiVersion = '...'")
        apiVersion = version
    }

    /** Disabled plugin.yml generation. */
    public fun disableMetaGeneration() {
        generateMeta = false
    }
}

internal val Project.bukkit: Bukkit get() = extensions.getByType()
