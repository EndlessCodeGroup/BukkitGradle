package ru.endlesscode.bukkitgradle

import groovy.lang.Closure
import org.gradle.api.Project
import org.gradle.kotlin.dsl.getByType
import org.gradle.util.ConfigureUtil
import ru.endlesscode.bukkitgradle.meta.extension.PluginMetaImpl
import ru.endlesscode.bukkitgradle.server.ServerConstants
import ru.endlesscode.bukkitgradle.server.extension.ServerConfigurationImpl

// TODO 1.0: Remove deprecated fields on release
public open class BukkitExtension(
    public override val meta: PluginMetaImpl,
    public override val server: ServerConfigurationImpl
) : Bukkit {

    public override var apiVersion: String = ServerConstants.DEFAULT_VERSION

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

    public fun meta(body: Closure<out PluginMetaImpl>) {
        ConfigureUtil.configure(body, meta)
    }

    public fun meta(body: PluginMetaImpl.() -> Unit) {
        meta.run(body)
    }

    @Deprecated("Use apiVersion instead of version.", ReplaceWith("apiVersion = version"))
    public fun setVersion(version: String) {
        apiVersion = version
    }
}

internal val Project.bukkit: Bukkit get() = extensions.getByType()
