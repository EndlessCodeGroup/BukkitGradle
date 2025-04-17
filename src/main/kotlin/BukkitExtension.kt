package ru.endlesscode.bukkitgradle

import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.property
import ru.endlesscode.bukkitgradle.extensions.finalizedOnRead
import ru.endlesscode.bukkitgradle.plugin.plugin
import ru.endlesscode.bukkitgradle.server.ServerConstants
import ru.endlesscode.bukkitgradle.server.extension.ServerConfigurationImpl
import xyz.jpenilla.resourcefactory.bukkit.BukkitPluginYaml

public open class BukkitExtension internal constructor(
    public final override val server: ServerConfigurationImpl,
    objects: ObjectFactory,
) : Bukkit {

    override val generatePluginYaml: Property<Boolean> = objects.property<Boolean>()
        .convention(true)

    public final override val apiVersion: Property<String> = objects.property<String>()
        .convention(ServerConstants.DEFAULT_VERSION)
        .finalizedOnRead()

    public fun server(body: Action<ServerConfigurationImpl>) {
        body.execute(server)
    }

    @Deprecated("Use 'plugin' instead", ReplaceWith("plugin(body)"))
    public fun meta(body: Action<BukkitPluginYaml>) {
        body.execute(plugin)
    }

    /** Disables plugin.yml generation. */
    @Deprecated(
        "Use 'generatePluginYaml.set(false)' instead",
        ReplaceWith("generatePluginYaml.set(false)")
    )
    public fun disableMetaGeneration() {
        generatePluginYaml.set(false)
    }

    public companion object {
        public const val NAME: String = "bukkit"
    }
}

internal val Project.bukkit: Bukkit get() = extensions.getByType()
