package ru.endlesscode.bukkitgradle

import org.gradle.api.Project
import org.gradle.kotlin.dsl.getByType
import ru.endlesscode.bukkitgradle.meta.extension.PluginMeta

public interface Bukkit {
    public val meta: PluginMeta
}

internal val Project.bukkit: Bukkit get() = extensions.getByType()
