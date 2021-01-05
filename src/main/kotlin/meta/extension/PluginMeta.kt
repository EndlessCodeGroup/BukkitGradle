package ru.endlesscode.bukkitgradle.meta.extension

import org.gradle.api.provider.Provider
import org.gradle.api.tasks.Internal

public interface PluginMeta {
    public val name: Provider<String>
    public val description: Provider<String>
    public val main: Provider<String>
    public val version: Provider<String>
    public val url: Provider<String>
    public val authors: Provider<List<String>>
}

@get:Internal
internal val PluginMeta.items: Map<String, Provider<*>>
    get() = mapOf(
        "name" to name,
        "description" to description,
        "main" to main,
        "version" to version,
        "website" to url,
        "authors" to authors
    )
