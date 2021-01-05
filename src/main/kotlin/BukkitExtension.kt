package ru.endlesscode.bukkitgradle

import groovy.lang.Closure
import org.gradle.api.Project
import org.gradle.kotlin.dsl.getByType
import org.gradle.util.ConfigureUtil
import ru.endlesscode.bukkitgradle.meta.extension.PluginMeta
import ru.endlesscode.bukkitgradle.server.ServerConstants
import ru.endlesscode.bukkitgradle.server.extension.ServerConfigurationImpl

// TODO 1.0: Remove deprecated fields on release
public open class BukkitExtension(
    public override val meta: PluginMeta,
    public override val server: ServerConfigurationImpl
) : Bukkit {

    public override var apiVersion: String = ServerConstants.DEFAULT_VERSION

    public override val fullVersion: String
        get() = "$apiVersion$REVISION_SUFFIX"

    @Deprecated("Use 'server' instead", ReplaceWith("server"))
    public val run: ServerConfigurationImpl
        get() = server

    @Deprecated("Use 'server { ... }' instead", ReplaceWith("server(body)"))
    public fun run(body: Closure<out ServerConfigurationImpl>) {
        ConfigureUtil.configure(body, server)
    }

    public fun server(body: Closure<out ServerConfigurationImpl>) {
        ConfigureUtil.configure(body, server)
    }

    public fun server(body: ServerConfigurationImpl.() -> Unit) {
        server.run(body)
    }

    public fun meta(body: Closure<out PluginMeta>) {
        ConfigureUtil.configure(body, meta)
    }

    public fun meta(body: PluginMeta.() -> Unit) {
        meta.run(body)
    }

    @Deprecated("Use apiVersion instead of version.", ReplaceWith("apiVersion = version"))
    public fun setVersion(version: String) {
        apiVersion = version
    }

    private companion object {
        const val REVISION_SUFFIX: String = "-R0.1-SNAPSHOT"
    }
}

internal val Project.bukkit: Bukkit get() = extensions.getByType()
