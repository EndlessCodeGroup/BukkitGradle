package ru.endlesscode.bukkitgradle

import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.property
import ru.endlesscode.bukkitgradle.extensions.finalizedOnRead
import ru.endlesscode.bukkitgradle.plugin.extension.PluginConfigurationImpl
import ru.endlesscode.bukkitgradle.server.ServerConstants
import ru.endlesscode.bukkitgradle.server.extension.ServerConfigurationImpl

public open class BukkitExtension internal constructor(
    public final override val plugin: PluginConfigurationImpl,
    public final override val server: ServerConfigurationImpl,
    objects: ObjectFactory,
) : Bukkit {

    public final override val apiVersion: Property<String> = objects.property<String>()
        .convention(ServerConstants.DEFAULT_VERSION)
        .finalizedOnRead()

    public fun server(body: Action<ServerConfigurationImpl>) {
        body.execute(server)
    }

    public fun plugin(body: Action<PluginConfigurationImpl>) {
        body.execute(plugin)
    }

    @Deprecated("Use 'plugin' instead", ReplaceWith("plugin(body)"))
    public fun meta(body: Action<PluginConfigurationImpl>) {
        plugin(body)
    }

    /** Disables plugin.yml generation. */
    @Deprecated(
        "Use 'plugin.disablePluginYamlGeneration()' instead",
        ReplaceWith("plugin.disablePluginYamlGeneration()")
    )
    public fun disableMetaGeneration() {
        plugin.disablePluginYamlGeneration()
    }
}

internal val Project.bukkit: Bukkit get() = extensions.getByType()
