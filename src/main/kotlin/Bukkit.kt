package ru.endlesscode.bukkitgradle

import org.gradle.api.Project
import org.gradle.kotlin.dsl.getByType
import ru.endlesscode.bukkitgradle.meta.extension.PluginMeta
import ru.endlesscode.bukkitgradle.server.extension.RunConfiguration

public interface Bukkit {
    public val meta: PluginMeta
    public val run: RunConfiguration
}

internal val Project.bukkit: Bukkit get() = extensions.getByType()
