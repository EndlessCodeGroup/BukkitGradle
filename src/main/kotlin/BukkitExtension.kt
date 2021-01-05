package ru.endlesscode.bukkitgradle

import groovy.lang.Closure
import org.gradle.api.Project
import org.gradle.kotlin.dsl.getByType
import org.gradle.util.ConfigureUtil
import ru.endlesscode.bukkitgradle.meta.extension.PluginMeta
import ru.endlesscode.bukkitgradle.server.ServerConstants
import ru.endlesscode.bukkitgradle.server.extension.RunConfigurationImpl

public open class BukkitExtension(
    public override val meta: PluginMeta,
    public override val run: RunConfigurationImpl
) : Bukkit {

    public override var version: String = ServerConstants.FALLBACK_VERSION

    public override val fullVersion: String
        get() = "$version$REVISION_SUFFIX"

    public fun run(body: Closure<out RunConfigurationImpl>) {
        ConfigureUtil.configure(body, run)
    }

    public fun run(body: RunConfigurationImpl.() -> Unit) {
        run.run(body)
    }

    public fun meta(body: Closure<out PluginMeta>) {
        ConfigureUtil.configure(body, meta)
    }

    public fun meta(body: PluginMeta.() -> Unit) {
        meta.run(body)
    }

    public companion object {
        public const val LATEST: String = "+"
        public const val REVISION_SUFFIX: String = "-R0.1-SNAPSHOT"
    }
}

internal val Project.bukkit: Bukkit get() = extensions.getByType()
