package ru.endlesscode.bukkitgradle

import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.kotlin.dsl.assign
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.property
import org.slf4j.LoggerFactory
import ru.endlesscode.bukkitgradle.extensions.finalizedOnRead
import ru.endlesscode.bukkitgradle.extensions.warnSyntaxChanged
import ru.endlesscode.bukkitgradle.meta.extension.PluginMetaImpl
import ru.endlesscode.bukkitgradle.server.ServerConstants
import ru.endlesscode.bukkitgradle.server.extension.ServerConfigurationImpl

// TODO 1.0: Remove deprecated fields on release
public open class BukkitExtension internal constructor(
    public final override val meta: PluginMetaImpl,
    public final override val server: ServerConfigurationImpl,
    objects: ObjectFactory,
) : Bukkit {

    public final override val apiVersion: Property<String> = objects.property<String>()
        .convention(ServerConstants.DEFAULT_VERSION)
        .finalizedOnRead()

    private val _generateMeta: Property<Boolean> = objects.property<Boolean>()
        .convention(true)
        .finalizedOnRead()

    public final override val generateMeta: Provider<Boolean> = _generateMeta

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
        _generateMeta = false
    }
}

internal val Project.bukkit: Bukkit get() = extensions.getByType()
